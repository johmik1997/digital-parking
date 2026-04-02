<template>
  <div v-if="show && order" class="fixed inset-0 z-50 overflow-y-auto">
    <div class="fixed inset-0" @click="$emit('close')">
      <div class="absolute inset-0 bg-slate-950/60 backdrop-blur-sm"></div>
    </div>

    <div class="relative flex min-h-screen items-end justify-center px-0 sm:items-center sm:px-4">
      <transition
        enter-active-class="transition duration-300 ease-out"
        enter-from-class="opacity-0 translate-y-4 sm:translate-y-0 sm:scale-95"
        enter-to-class="opacity-100 translate-y-0 sm:scale-100"
        leave-active-class="transition duration-200 ease-in"
        leave-from-class="opacity-100 translate-y-0 sm:scale-100"
        leave-to-class="opacity-0 translate-y-4 sm:translate-y-0 sm:scale-95"
      >
        <div class="relative w-full overflow-hidden rounded-t-[28px] bg-white shadow-2xl sm:my-8 sm:max-w-5xl sm:rounded-[32px]">
          <div class="bg-gradient-to-r from-[#0f1c34] via-[#1a2b4b] to-[#214b76] px-5 py-5 text-white sm:px-6">
            <div class="flex items-start justify-between gap-4">
              <div>
                <p class="text-xs font-semibold uppercase tracking-[0.3em] text-sky-100/80">Order Details</p>
                <h3 class="mt-2 text-2xl font-semibold">{{ order.serviceName || 'Service Order' }}</h3>
                <p class="mt-1 text-sm text-slate-200/80">
                  Order #{{ shortOrderNumber }} • {{ formatOrderDate(order.createdAt) }}
                </p>
              </div>

              <div class="flex items-start gap-3">
                <span
                  class="rounded-full px-3 py-1 text-xs font-semibold uppercase tracking-[0.18em]"
                  :class="statusClasses"
                >
                  {{ order.status }}
                </span>
                <button
                  type="button"
                  @click="$emit('close')"
                  class="inline-flex h-10 w-10 items-center justify-center rounded-2xl border border-white/10 bg-white/10 text-white transition hover:bg-white/20"
                >
                  <svg class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                  </svg>
                </button>
              </div>
            </div>
          </div>

          <div class="max-h-[78vh] overflow-y-auto bg-slate-50 p-5 sm:p-6">
            <div v-if="isRideReady" class="grid gap-5 xl:grid-cols-[minmax(0,1fr)_minmax(0,0.9fr)]">
              <div class="overflow-hidden rounded-[30px] border border-[#dfd8cc] bg-[#f4efe5] p-4 shadow-[0_28px_80px_rgba(148,163,184,0.18)] sm:p-5">
                <div class="flex items-center justify-between">
                  <div>
                    <p class="text-xs font-semibold uppercase tracking-[0.28em] text-slate-400">Ride Preview</p>
                    <h4 class="mt-1 text-xl font-semibold text-slate-900">{{ order.serviceName }}</h4>
                  </div>
                  <span class="rounded-full border border-slate-300 bg-white/80 px-3 py-1 text-[11px] font-semibold uppercase tracking-[0.22em] text-slate-600">
                    {{ order.scheduledEntryTime || order.entryTime || 'Ready' }}
                  </span>
                </div>

                <div class="relative mt-6 h-[340px] overflow-hidden rounded-[28px] border border-[#e1dbcf] bg-[#ece7dd]">
                  <div class="parking-map-surface absolute inset-0"></div>

                  <div class="absolute left-7 top-6 rounded-full bg-white/85 px-3 py-1.5 text-xs font-medium text-slate-600 shadow-sm">
                    {{ parkingRouteAddress }}
                  </div>
                  <div class="absolute right-7 top-20 rounded-full bg-slate-900/85 px-3 py-1.5 text-xs font-medium text-white shadow-lg">
                    {{ order.entranceName || 'Main Entrance' }}
                  </div>
                  <div class="absolute bottom-6 left-7 rounded-full bg-white/90 px-3 py-1.5 text-xs font-medium text-slate-700 shadow-sm">
                    Your Current Location
                  </div>

                  <svg
                    class="pointer-events-none absolute inset-0 h-full w-full"
                    viewBox="0 0 400 320"
                    fill="none"
                    xmlns="http://www.w3.org/2000/svg"
                  >
                    <path d="M282 92C282 110 267.941 124 250.5 124C233.059 124 219 110 219 92C219 74 233.059 60 250.5 60C267.941 60 282 74 282 92Z" fill="#1E293B" fill-opacity="0.14" />
                    <path d="M145 246C145 263.673 159.327 278 177 278C194.673 278 209 263.673 209 246C209 228.327 194.673 214 177 214C159.327 214 145 228.327 145 246Z" fill="#1677FF" fill-opacity="0.16" />
                    <path
                      d="M177 246C203 228 236 218 252 196C269 173 268 137 251 92"
                      stroke="#1677FF"
                      stroke-width="6"
                      stroke-linecap="round"
                      stroke-dasharray="14 12"
                    />
                    <circle cx="177" cy="246" r="18" fill="white" stroke="#1677FF" stroke-width="6" />
                    <circle cx="177" cy="246" r="6" fill="#1677FF" />
                    <circle cx="251" cy="92" r="17" fill="#1E293B" />
                    <path
                      d="M251 78C244.925 78 240 82.925 240 89C240 97.25 251 109 251 109C251 109 262 97.25 262 89C262 82.925 257.075 78 251 78Z"
                      fill="white"
                    />
                    <circle cx="251" cy="89" r="4" fill="#1E293B" />
                  </svg>
                </div>
              </div>

              <div class="rounded-[30px] border border-slate-200 bg-white p-5 shadow-[0_22px_60px_rgba(15,23,42,0.08)] sm:p-6">
                <div class="flex flex-wrap items-start justify-between gap-3">
                  <div>
                    <p class="text-xs font-semibold uppercase tracking-[0.24em] text-slate-400">Assigned Parking</p>
                    <p class="mt-2 text-xl font-semibold text-slate-900">
                      {{ order.parkingLocationDisplay || 'Parking slot assigned' }}
                    </p>
                  </div>
                  <span class="rounded-full bg-sky-50 px-3 py-1 text-xs font-semibold text-sky-700">
                    Ride ready
                  </span>
                </div>

                <div class="mt-5 space-y-3">
                  <div class="rounded-[20px] bg-slate-50 p-4">
                    <p class="text-xs font-semibold uppercase tracking-[0.2em] text-slate-400">Schedule</p>
                    <p class="mt-1 text-sm font-medium text-slate-900">{{ parkingScheduleSummary }}</p>
                  </div>

                  <div class="rounded-[20px] bg-slate-50 p-4">
                    <p class="text-xs font-semibold uppercase tracking-[0.2em] text-slate-400">Entrance</p>
                    <p class="mt-1 text-sm font-medium text-slate-900">{{ order.entranceName || 'Main Entrance' }}</p>
                  </div>

                  <div class="rounded-[20px] bg-slate-50 p-4">
                    <p class="text-xs font-semibold uppercase tracking-[0.2em] text-slate-400">Guidance</p>
                    <p class="mt-1 text-sm font-medium text-slate-900">
                      {{ order.navigationInstructions || 'Navigation details will appear here.' }}
                    </p>
                  </div>

                  <div class="grid gap-3 sm:grid-cols-2">
                    <div class="rounded-[20px] bg-slate-50 p-4">
                      <p class="text-xs font-semibold uppercase tracking-[0.2em] text-slate-400">Vehicle Plate</p>
                      <p class="mt-1 text-sm font-medium text-slate-900">{{ order.vehiclePlate || 'Not provided' }}</p>
                    </div>
                    <div class="rounded-[20px] bg-slate-50 p-4">
                      <p class="text-xs font-semibold uppercase tracking-[0.2em] text-slate-400">Total Paid</p>
                      <p class="mt-1 text-sm font-medium text-slate-900">ETB {{ formatAmount(order.totalAmount) }}</p>
                    </div>
                  </div>
                </div>

                <a
                  :href="order.googleMapsUrl"
                  target="_blank"
                  rel="noopener noreferrer"
                  class="mt-6 inline-flex w-full items-center justify-center rounded-[22px] bg-gradient-to-r from-[#0a84ff] to-[#1967ff] px-5 py-4 text-base font-semibold text-white shadow-[0_20px_40px_rgba(10,132,255,0.24)] transition hover:brightness-105"
                >
                  Ride Now
                </a>
              </div>
            </div>

            <div v-else class="grid gap-5 xl:grid-cols-[minmax(0,0.95fr)_minmax(0,1.05fr)]">
              <div class="rounded-[30px] border border-slate-200 bg-white p-5 shadow-[0_22px_60px_rgba(15,23,42,0.08)] sm:p-6">
                <p class="text-xs font-semibold uppercase tracking-[0.24em] text-slate-400">Summary</p>
                <div class="mt-4 grid gap-3 sm:grid-cols-2">
                  <div class="rounded-[20px] bg-slate-50 p-4">
                    <p class="text-xs font-semibold uppercase tracking-[0.18em] text-slate-400">Date</p>
                    <p class="mt-1 text-sm font-medium text-slate-900">{{ formatOrderDate(order.createdAt) }}</p>
                  </div>
                  <div class="rounded-[20px] bg-slate-50 p-4">
                    <p class="text-xs font-semibold uppercase tracking-[0.18em] text-slate-400">Total</p>
                    <p class="mt-1 text-sm font-medium text-slate-900">ETB {{ formatAmount(order.totalAmount) }}</p>
                  </div>
                  <div class="rounded-[20px] bg-slate-50 p-4">
                    <p class="text-xs font-semibold uppercase tracking-[0.18em] text-slate-400">Duration</p>
                    <p class="mt-1 text-sm font-medium text-slate-900">{{ formatDuration(order) }}</p>
                  </div>
                  <div class="rounded-[20px] bg-slate-50 p-4">
                    <p class="text-xs font-semibold uppercase tracking-[0.18em] text-slate-400">Rate</p>
                    <p class="mt-1 text-sm font-medium text-slate-900">ETB {{ formatAmount(order.rateApplied) }}</p>
                  </div>
                </div>
              </div>

              <div class="rounded-[30px] border border-slate-200 bg-white p-5 shadow-[0_22px_60px_rgba(15,23,42,0.08)] sm:p-6">
                <p class="text-xs font-semibold uppercase tracking-[0.24em] text-slate-400">Order Notes</p>
                <div class="mt-4 space-y-3">
                  <div v-if="order.parkingLocationDisplay" class="rounded-[20px] bg-slate-50 p-4">
                    <p class="text-xs font-semibold uppercase tracking-[0.18em] text-slate-400">Parking</p>
                    <p class="mt-1 text-sm font-medium text-slate-900">{{ order.parkingLocationDisplay }}</p>
                  </div>
                  <div v-if="parkingScheduleSummary" class="rounded-[20px] bg-slate-50 p-4">
                    <p class="text-xs font-semibold uppercase tracking-[0.18em] text-slate-400">Schedule</p>
                    <p class="mt-1 text-sm font-medium text-slate-900">{{ parkingScheduleSummary }}</p>
                  </div>
                  <div class="rounded-[20px] border border-dashed border-slate-200 bg-slate-50/70 p-4">
                    <p class="text-sm font-medium text-slate-900">
                      {{ statusMessage }}
                    </p>
                    <p v-if="order.navigationInstructions" class="mt-2 text-sm text-slate-600">
                      {{ order.navigationInstructions }}
                    </p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </transition>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  show: {
    type: Boolean,
    default: false,
  },
  order: {
    type: Object,
    default: null,
  },
})

defineEmits(['close'])

const parkingRouteAddress = 'Meskel Square Parking, Addis Ababa'

const isParkingOrder = computed(() => `${props.order?.serviceType || ''}`.toUpperCase() === 'PARKING')

const isRideReady = computed(() =>
  isParkingOrder.value &&
  `${props.order?.status || ''}`.toUpperCase() === 'COMPLETED' &&
  !!props.order?.googleMapsUrl
)

const shortOrderNumber = computed(() =>
  props.order?.orderUuid?.slice(-6)?.toUpperCase() || props.order?.id || 'N/A'
)

const parkingScheduleSummary = computed(() => {
  if (!props.order) return ''

  if (isParkingOrder.value) {
    const parkingDate = formatPlainDate(props.order.parkingDate)
    const scheduledTime = props.order.scheduledEntryTime || props.order.entryTime
    if (parkingDate && scheduledTime) return `${parkingDate} at ${scheduledTime}`
    if (parkingDate) return parkingDate
    if (scheduledTime) return scheduledTime
  }

  if (props.order.appointmentDate || props.order.appointmentTime) {
    return `${formatPlainDate(props.order.appointmentDate)} ${props.order.appointmentTime || ''}`.trim()
  }

  if (props.order.visitDate) {
    return formatPlainDate(props.order.visitDate)
  }

  return ''
})

const statusClasses = computed(() => {
  const status = `${props.order?.status || ''}`.toUpperCase()
  if (status === 'COMPLETED') return 'bg-emerald-400/15 text-emerald-100 border border-emerald-300/30'
  if (status === 'PENDING') return 'bg-amber-400/15 text-amber-100 border border-amber-300/30'
  if (status === 'PROCESSING') return 'bg-sky-400/15 text-sky-100 border border-sky-300/30'
  if (status === 'CANCELLED') return 'bg-white/10 text-slate-100 border border-white/10'
  return 'bg-rose-400/15 text-rose-100 border border-rose-300/30'
})

const statusMessage = computed(() => {
  if (!props.order) return ''
  if (isRideReady.value) return 'Your ride page is ready. Use the map preview and drive straight to your assigned entrance.'
  if (isParkingOrder.value) return 'Ride guidance unlocks after the parking order is completed. This detail page will show the route once payment verification finishes.'
  return 'This order does not require a ride page, but the service summary is still available here.'
})

const formatAmount = (amount) => {
  if (amount == null) return '0.00'
  return Number(amount).toFixed(2)
}

const formatPlainDate = (value) => {
  if (!value) return ''

  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return value

  return date.toLocaleDateString('en-US', {
    month: 'short',
    day: 'numeric',
    year: 'numeric',
  })
}

const formatOrderDate = (value) => {
  if (!value) return 'Unknown date'

  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return `${value}`

  return date.toLocaleDateString('en-US', {
    month: 'short',
    day: 'numeric',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  })
}

const formatDuration = (order) => {
  if (!order?.duration) return 'Fixed'
  return `${order.duration}h`
}
</script>

<style scoped>
@media (max-width: 640px) {
  .fixed.inset-0 {
    padding-bottom: env(safe-area-inset-bottom);
  }
}

.parking-map-surface {
  background-color: #efe9dd;
  background-image:
    linear-gradient(120deg, rgba(255, 255, 255, 0.78) 0, rgba(255, 255, 255, 0.78) 2px, transparent 2px, transparent 42px),
    linear-gradient(30deg, rgba(255, 255, 255, 0.75) 0, rgba(255, 255, 255, 0.75) 2px, transparent 2px, transparent 54px),
    radial-gradient(circle at 68% 30%, rgba(255, 255, 255, 0.95), transparent 22%);
  background-position: 0 0, 30px 18px, center;
  background-size: 120px 120px, 140px 140px, auto;
}
</style>
