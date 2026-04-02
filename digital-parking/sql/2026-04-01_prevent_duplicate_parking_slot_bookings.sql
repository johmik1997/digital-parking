-- Prevent duplicate parking slot reservations for the same service/time window/location.
-- This protects the system even if an older backend build is still running.

CREATE OR REPLACE FUNCTION prevent_duplicate_parking_slot_booking()
RETURNS trigger AS $$
DECLARE
  booking_time TEXT;
  parsed_duration_hours NUMERIC;
  new_start TIMESTAMP;
  new_end TIMESTAMP;
  has_actual_entry BOOLEAN;
  conflicting_order_id BIGINT;
BEGIN
  IF COALESCE(NEW.service_type, '') <> 'PARKING' THEN
    RETURN NEW;
  END IF;

  IF NEW.status = 'CANCELLED' THEN
    RETURN NEW;
  END IF;

  IF NEW.parking_date IS NULL
     OR NEW.parking_level_code IS NULL
     OR NEW.parking_zone IS NULL
     OR NEW.selected_slot IS NULL THEN
    RETURN NEW;
  END IF;

  booking_time := COALESCE(
    NULLIF(BTRIM(NEW.scheduled_entry_time), ''),
    CASE
      WHEN NEW.entry_time ~ '^[0-2][0-9]:[0-5][0-9]$' THEN NEW.entry_time
      ELSE NULL
    END
  );

  IF booking_time IS NULL THEN
    RETURN NEW;
  END IF;

  parsed_duration_hours := COALESCE(
    NEW.duration_hours,
    NULLIF(regexp_replace(COALESCE(NEW.duration, ''), '[^0-9.]', '', 'g'), '')::NUMERIC,
    1
  );

  has_actual_entry := COALESCE(NEW.entry_time, '') ~ '^[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}(:[0-9]{2}(\.[0-9]+)?)?$';
  new_start := CASE
    WHEN has_actual_entry THEN NEW.entry_time::timestamp
    ELSE (NEW.parking_date::date + booking_time::time)
  END;
  new_end := CASE
    WHEN has_actual_entry AND NEW.completed_at IS NOT NULL AND NEW.completed_at > new_start
      THEN NEW.completed_at
    ELSE new_start + make_interval(mins => GREATEST(60, CEIL(parsed_duration_hours * 60))::INT)
  END;

  SELECT o.id
  INTO conflicting_order_id
  FROM service_orders o
  CROSS JOIN LATERAL (
    SELECT
      CASE
        WHEN COALESCE(o.entry_time, '') ~ '^[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}(:[0-9]{2}(\.[0-9]+)?)?$'
          THEN o.entry_time::timestamp
        ELSE (
          o.parking_date::date
          + COALESCE(
            NULLIF(BTRIM(o.scheduled_entry_time), ''),
            CASE
              WHEN o.entry_time ~ '^[0-2][0-9]:[0-5][0-9]$' THEN o.entry_time
              ELSE NULL
            END
          )::time
        )
      END AS existing_start,
      GREATEST(
        60,
        CEIL(
          COALESCE(
            o.duration_hours,
            NULLIF(regexp_replace(COALESCE(o.duration, ''), '[^0-9.]', '', 'g'), '')::NUMERIC,
            1
          ) * 60
        )
      )::INT AS existing_duration_minutes
  ) overlap_window
  WHERE o.id <> COALESCE(NEW.id, -1)
    AND o.service_id = NEW.service_id
    AND o.status <> 'CANCELLED'
    AND o.parking_date BETWEEN
      ((NEW.parking_date::date - INTERVAL '1 day')::date)::text
      AND
      ((NEW.parking_date::date + INTERVAL '1 day')::date)::text
    AND UPPER(COALESCE(o.parking_level_code, '')) = UPPER(NEW.parking_level_code)
    AND UPPER(COALESCE(o.parking_zone, '')) = UPPER(NEW.parking_zone)
    AND UPPER(COALESCE(o.selected_slot, '')) = UPPER(NEW.selected_slot)
    AND overlap_window.existing_start IS NOT NULL
    AND overlap_window.existing_start < new_end
    AND new_start < CASE
      WHEN COALESCE(o.entry_time, '') ~ '^[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}(:[0-9]{2}(\.[0-9]+)?)?$'
        AND o.completed_at IS NOT NULL
        AND o.completed_at > overlap_window.existing_start
        THEN o.completed_at
      ELSE overlap_window.existing_start + make_interval(mins => overlap_window.existing_duration_minutes)
    END
  LIMIT 1;

  IF conflicting_order_id IS NOT NULL THEN
    RAISE EXCEPTION 'Parking slot % is already reserved during the selected booking window on %',
      NEW.selected_slot,
      NEW.parking_date;
  END IF;

  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_prevent_duplicate_parking_slot_booking ON service_orders;

CREATE TRIGGER trg_prevent_duplicate_parking_slot_booking
BEFORE INSERT OR UPDATE ON service_orders
FOR EACH ROW
EXECUTE FUNCTION prevent_duplicate_parking_slot_booking();
