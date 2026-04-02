export const PARKING_LEVELS = [
  {
    code: 'G',
    type: 'GROUND',
    label: 'Ground Floor',
    entranceName: 'Main Entrance',
    zones: ['A', 'B'],
  },
  {
    code: 'B1',
    type: 'BASEMENT',
    label: 'Basement B1',
    entranceName: 'Basement Gate',
    zones: ['A', 'B', 'C'],
  },
  {
    code: 'B2',
    type: 'BASEMENT',
    label: 'Basement B2',
    entranceName: 'Basement Gate',
    zones: ['C', 'D'],
  },
  {
    code: 'F1',
    type: 'FLOOR',
    label: 'Floor 1',
    entranceName: 'Upper Ramp Entrance',
    zones: ['A', 'B'],
  },
]

export const getParkingLevel = (levelCode) => {
  return PARKING_LEVELS.find((level) => level.code === levelCode) || null
}

export const getParkingZones = (levelCode) => {
  return getParkingLevel(levelCode)?.zones || []
}

export const getParkingSlots = (levelCode, zoneCode, capacity = 24) => {
  if (!levelCode || !zoneCode) return []

  const normalizedCapacity = Math.max(8, Number(capacity || 0) || 24)
  const slotsPerZone = Math.max(6, Math.min(12, Math.ceil(normalizedCapacity / 3)))

  return Array.from({ length: slotsPerZone }, (_, index) => {
    const code = `${zoneCode}${String(index + 1).padStart(2, '0')}`
    return {
      code,
      label: `Slot ${code}`,
    }
  })
}

export const buildParkingLocationDisplay = ({
  parkingLevelCode,
  parkingLevelType,
  parkingZone,
  selectedSlot,
}) => {
  const level = getParkingLevel(parkingLevelCode)
  const levelLabel = level?.label || inferLevelLabel(parkingLevelType, parkingLevelCode)
  const parts = [levelLabel, parkingZone ? `Zone ${parkingZone}` : null, selectedSlot ? `Slot ${selectedSlot}` : null]
    .filter(Boolean)

  return parts.join(' / ')
}

export const resolveEntranceName = ({ parkingLevelCode, parkingLevelType, entranceName }) => {
  if (entranceName) return entranceName
  const level = getParkingLevel(parkingLevelCode)
  if (level?.entranceName) return level.entranceName

  const normalizedType = `${parkingLevelType || ''}`.toUpperCase()
  const normalizedCode = `${parkingLevelCode || ''}`.toUpperCase()

  if (normalizedType === 'BASEMENT' || normalizedCode.startsWith('B')) return 'Basement Gate'
  if (normalizedType === 'GROUND' || normalizedCode === 'G') return 'Main Entrance'
  return 'Upper Ramp Entrance'
}

export const buildParkingGoogleMapsUrl = ({ entranceName }) => {
  const destination = entranceName
    ? `Meskel Square Parking ${entranceName}, Addis Ababa`
    : 'Meskel Square Parking, Addis Ababa'

  return `https://www.google.com/maps/dir/?api=1&destination=${encodeURIComponent(destination)}&travelmode=driving`
}

export const buildParkingInstructions = (parkingDetails) => {
  const entrance = resolveEntranceName(parkingDetails)
  const locationDisplay = buildParkingLocationDisplay(parkingDetails)

  if (entrance && locationDisplay) {
    return `Navigate to ${entrance}, then follow signs to ${locationDisplay}.`
  }

  if (locationDisplay) {
    return `Follow internal parking signs to ${locationDisplay}.`
  }

  return entrance ? `Navigate to ${entrance}.` : ''
}

const inferLevelLabel = (parkingLevelType, parkingLevelCode) => {
  const type = `${parkingLevelType || ''}`.toUpperCase()
  const code = `${parkingLevelCode || ''}`.toUpperCase()

  if (type === 'BASEMENT' || code.startsWith('B')) return `Basement ${code}`
  if (type === 'GROUND' || code === 'G') return 'Ground Floor'
  if (type === 'FLOOR' || code.startsWith('F')) return `Floor ${code.replace(/^F/, '')}`
  return code
}
