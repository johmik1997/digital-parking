# Slot Navigation Plan

This document captures the requested enhancement for customer parking appointments: time-based booking, structured slot selection, and navigation support.

## Requirement Summary

The customer should be able to:

- choose the appointment date
- choose the appointment time
- choose a specific parking area
- choose a slot grouped by floor or basement
- get navigation support to reach the parking location

Example:

`Meskel Square Parking -> Basement B1 -> Zone A -> Slot 12`

## Recommended Product Behavior

Use this customer flow:

1. The customer selects the parking date.
2. The customer selects the entry time.
3. The system shows available levels such as `Ground`, `Floor 1`, `Basement B1`, `Basement B2`.
4. The customer selects a zone inside that level.
5. The customer selects an available slot.
6. The booking confirmation includes:
   - facility name
   - entrance or gate
   - floor or basement
   - zone
   - slot label
   - Google Maps button
7. Google Maps guides the customer to the parking facility or the correct gate.
8. The app shows the final internal guidance to the selected level and slot.

## Important Google Maps Clarification

Google Maps is a good fit for:

- navigating to the parking facility
- navigating to a specific gate or entrance
- opening turn-by-turn directions from the customer device

Google Maps should not be the only promised solution for:

- exact indoor routing to a basement slot
- reliable navigation between internal parking levels
- detailed garage wayfinding unless the venue already has supported indoor mapping in Google Maps

Best practical approach:

- Google Maps for arrival to the site or gate
- internal parking logic in our app for `floor/basement -> zone -> slot`

## Proposed Parking Hierarchy

The slot model should be hierarchical:

- Facility
- Entrance or Gate
- Level Type: `FLOOR` or `BASEMENT`
- Level Code: `G`, `F1`, `F2`, `B1`, `B2`
- Zone: `A`, `B`, `C`
- Slot: `01`, `02`, `03`

Display examples:

- `Basement B1 / Zone A / Slot 12`
- `Floor 2 / Zone C / Slot 07`
- `Ground / Zone VIP / Slot 03`

## Suggested Data Model Changes

The current order model already contains `selectedSlot`, but the parking structure should be expanded.

Recommended new parking-slot fields:

- `facilityName`
- `entranceName`
- `levelType`
- `levelCode`
- `zoneCode`
- `slotCode`
- `slotDisplayName`
- `mapsDestinationName`
- `mapsDestinationLat`
- `mapsDestinationLng`
- `active`

Recommended order fields:

- `parkingDate`
- `entryTime`
- `selectedSlotId`
- `selectedSlotDisplay`
- `entranceName`
- `levelCode`
- `zoneCode`

## Suggested API Enhancements

Useful endpoints for this feature:

- `GET /client/services/{serviceUuid}/availability?parkingDate=...&entryTime=...`
- `GET /parking/levels`
- `GET /parking/levels/{levelCode}/zones`
- `GET /parking/slots?levelCode=B1&zoneCode=A&parkingDate=...&entryTime=...`
- `GET /parking/slots/{slotId}`
- `GET /parking/slots/{slotId}/navigation`

The navigation endpoint can return:

- parking facility name
- entrance name
- destination coordinates
- Google Maps URL
- user-facing slot instructions

## Suggested Frontend Experience

Customer booking UI:

- Step 1: Select date
- Step 2: Select time
- Step 3: Select level or basement
- Step 4: Select zone
- Step 5: Select slot
- Step 6: Confirm booking

Booking confirmation card:

- appointment date and time
- assigned level or basement
- assigned zone
- assigned slot
- `Open in Google Maps` button
- text directions such as `Enter Gate 2, proceed to Basement B1, follow Zone A signs`

## Demo Wording For Stakeholders

Use this wording with your boss or stakeholders:

"We can support slot-based appointment booking by dividing parking into floors, basements, zones, and individual slots. Google Maps can bring the customer to the parking facility or the correct entrance, and our system can handle the final in-parking guidance to the exact slot."

## Delivery Recommendation

Implement this in phases:

### Phase 1

- add floor or basement
- add zone
- add slot master data
- allow customer slot selection
- show slot details on booking confirmation
- open Google Maps to the facility or gate

### Phase 2

- add entrance-specific routing
- add QR or NFC check-in by entrance
- add occupancy-based slot suggestions
- add admin slot management screen

### Phase 3

- add internal visual map
- add lane-by-lane guidance
- add digital signage integration by level and zone
