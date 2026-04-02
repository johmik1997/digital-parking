-- Preserve the scheduled parking time separately from the real arrival timestamp.

ALTER TABLE service_orders
  ADD COLUMN IF NOT EXISTS scheduled_entry_time VARCHAR(32);
