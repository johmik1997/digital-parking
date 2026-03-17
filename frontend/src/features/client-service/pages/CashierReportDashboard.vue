<template>
  <div class="min-h-screen bg-gradient-to-br from-blue-50 via-white to-indigo-50">
    <header class="sticky top-0 z-10 border-b border-gray-200 bg-white/90 backdrop-blur">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 py-4 flex items-center justify-between">
        <div>
          <h1 class="text-xl font-bold text-gray-900">Cashier Report</h1>
          <p class="text-xs text-gray-500">Summary for {{ cashierName }} • ID {{ cashierId }}</p>
        </div>
        <div class="flex items-center gap-3 text-[10px] text-gray-500">
          <button
            class="px-3 py-1.5 rounded-full border border-gray-200 text-[10px] font-semibold text-gray-600 hover:bg-gray-50"
            @click="refreshReport"
            :disabled="isLoading"
          >
            {{ isLoading ? 'Refreshing...' : 'Refresh' }}
          </button>
          <span v-if="isLoading">Refreshing report...</span>
          <span v-else>Last updated: {{ updatedAt }}</span>
        </div>
      </div>
    </header>

    <main class="max-w-7xl mx-auto px-4 sm:px-6 py-6 space-y-4">
      <section class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-6 gap-3">
        <div class="bg-white rounded-xl border border-gray-200 p-4 shadow-sm">
          <p class="text-[10px] text-gray-500 uppercase tracking-wider">Total Payments</p>
          <p class="mt-2 text-2xl font-black text-gray-900">{{ totalPayments }}</p>
          <p class="text-[10px] text-gray-400 mt-1">Processed by you</p>
        </div>
        <div class="bg-white rounded-xl border border-gray-200 p-4 shadow-sm">
          <p class="text-[10px] text-gray-500 uppercase tracking-wider">Total Revenue</p>
          <p class="mt-2 text-2xl font-black text-green-600">ETB {{ formatAmount(totalRevenue) }}</p>
          <p class="text-[10px] text-gray-400 mt-1">All time</p>
        </div>
        <div class="bg-white rounded-xl border border-gray-200 p-4 shadow-sm">
          <p class="text-[10px] text-gray-500 uppercase tracking-wider">Today Revenue</p>
          <p class="mt-2 text-2xl font-black text-indigo-600">ETB {{ formatAmount(todayRevenue) }}</p>
          <p class="text-[10px] text-gray-400 mt-1">Since midnight</p>
        </div>
        <div class="bg-white rounded-xl border border-gray-200 p-4 shadow-sm">
          <p class="text-[10px] text-gray-500 uppercase tracking-wider">Avg. Ticket</p>
          <p class="mt-2 text-2xl font-black text-gray-900">ETB {{ formatAmount(averagePayment) }}</p>
          <p class="text-[10px] text-gray-400 mt-1">Per payment</p>
        </div>
        <div class="bg-white rounded-xl border border-gray-200 p-4 shadow-sm">
          <p class="text-[10px] text-gray-500 uppercase tracking-wider">Telebirr Share</p>
          <p class="mt-2 text-2xl font-black text-emerald-600">{{ telebirrShare }}%</p>
          <p class="text-[10px] text-gray-400 mt-1">Of payments</p>
        </div>
        <div class="bg-white rounded-xl border border-gray-200 p-4 shadow-sm">
          <p class="text-[10px] text-gray-500 uppercase tracking-wider">Cash Payments</p>
          <p class="mt-2 text-2xl font-black text-gray-900">{{ paymentMethodCounts.cash }}</p>
          <p class="text-[10px] text-gray-400 mt-1">Cash transactions</p>
        </div>
      </section>

      <section class="grid grid-cols-1 lg:grid-cols-3 gap-3">
        <div class="lg:col-span-2 bg-white rounded-xl border border-gray-200 p-4 shadow-sm">
          <div class="flex items-center justify-between">
            <h2 class="text-sm font-bold text-gray-800">Recent Payments</h2>
            <span class="text-[10px] text-gray-400">Latest 5</span>
          </div>
          <div v-if="isLoading" class="mt-3 text-[11px] text-gray-500">Loading payments...</div>
          <div v-else-if="recentPayments.length === 0" class="mt-3 text-[11px] text-gray-500">
            No payments yet.
          </div>
          <div v-else class="mt-3 overflow-x-auto">
            <table class="min-w-full text-[11px]">
              <thead>
                <tr class="text-left text-gray-500">
                  <th class="py-1.5 pr-3">Plate</th>
                  <th class="py-1.5 pr-3">Method</th>
                  <th class="py-1.5 pr-3">Amount</th>
                  <th class="py-1.5">Time</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="payment in recentPayments" :key="payment.id" class="border-t border-gray-100">
                  <td class="py-1.5 pr-3 font-mono text-gray-800">{{ payment.plate }}</td>
                  <td class="py-1.5 pr-3">
                    <span
                      class="px-2 py-0.5 rounded-full text-[10px] font-semibold"
                      :class="payment.paymentMethod === 'Telebirr' ? 'bg-green-100 text-green-700' : 'bg-blue-100 text-blue-700'"
                    >
                      {{ payment.paymentMethod }}
                    </span>
                  </td>
                  <td class="py-1.5 pr-3 text-gray-700">ETB {{ formatAmount(payment.amount) }}</td>
                  <td class="py-1.5 text-gray-500">{{ payment.time }}</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

        <div class="bg-white rounded-xl border border-gray-200 p-4 shadow-sm">
          <h2 class="text-sm font-bold text-gray-800">Payment Methods</h2>
          <div class="mt-3 space-y-3 text-xs">
            <div class="flex items-center justify-between">
              <span class="text-gray-500">Cash</span>
              <span class="font-bold text-gray-800">{{ paymentMethodCounts.cash }}</span>
            </div>
            <div class="w-full h-2 bg-gray-100 rounded-full overflow-hidden">
              <div class="h-full bg-blue-500" :style="{ width: methodPercent(paymentMethodCounts.cash) }"></div>
            </div>
            <div class="flex items-center justify-between">
              <span class="text-gray-500">Telebirr</span>
              <span class="font-bold text-gray-800">{{ paymentMethodCounts.telebirr }}</span>
            </div>
            <div class="w-full h-2 bg-gray-100 rounded-full overflow-hidden">
              <div class="h-full bg-green-500" :style="{ width: methodPercent(paymentMethodCounts.telebirr) }"></div>
            </div>
            <p class="text-[10px] text-gray-400">Based on your recent payments.</p>
          </div>
        </div>
      </section>

      <section class="grid grid-cols-1 lg:grid-cols-3 gap-3">
        <div class="lg:col-span-2 bg-white rounded-xl border border-gray-200 p-4 shadow-sm">
          <div class="flex items-center justify-between">
            <h2 class="text-sm font-bold text-gray-800">Revenue (Last 7 Days)</h2>
            <span class="text-[10px] text-gray-400">ETB</span>
          </div>
          <div class="mt-4 grid grid-cols-7 gap-2 items-end h-36">
            <div
              v-for="day in last7DaysRevenue"
              :key="day.date"
              class="flex flex-col items-center gap-1"
            >
              <div class="w-full flex-1 flex items-end">
                <div
                  class="w-full rounded-md bg-gradient-to-t from-indigo-600 to-blue-400"
                  :style="{ height: barHeight(day.total) }"
                ></div>
              </div>
              <span class="text-[9px] text-gray-500">{{ day.label }}</span>
            </div>
          </div>
          <p class="text-[10px] text-gray-400 mt-3">Based on cashier payment history.</p>
        </div>

        <div class="bg-white rounded-xl border border-gray-200 p-4 shadow-sm">
          <h2 class="text-sm font-bold text-gray-800">Payment Split</h2>
          <div class="mt-4 flex items-center justify-center">
            <svg class="w-32 h-32" viewBox="0 0 36 36">
              <circle
                cx="18"
                cy="18"
                r="16"
                fill="none"
                stroke="#e5e7eb"
                stroke-width="4"
              />
              <circle
                cx="18"
                cy="18"
                r="16"
                fill="none"
                stroke="#10b981"
                stroke-width="4"
                stroke-linecap="round"
                :stroke-dasharray="pieDash"
                stroke-dashoffset="25"
              />
            </svg>
          </div>
          <div class="mt-3 space-y-2 text-xs">
            <div class="flex items-center justify-between">
              <span class="text-gray-500">Telebirr</span>
              <span class="font-bold text-gray-800">{{ paymentMethodCounts.telebirr }}</span>
            </div>
            <div class="flex items-center justify-between">
              <span class="text-gray-500">Cash</span>
              <span class="font-bold text-gray-800">{{ paymentMethodCounts.cash }}</span>
            </div>
          </div>
        </div>
      </section>
    </main>
  </div>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useAuth } from '@/stores/auth'
import { useCashierPayments } from '../composables/useCashierPayments'

const authStore = useAuth()
const { cashierPayments, cashierId, isLoading, updatedAt, fetchPayments } = useCashierPayments()

const cashierName = computed(() => {
  const user = authStore.auth?.user
  return user?.firstName || user?.fullName || user?.email || 'Cashier'
})

const totalPayments = computed(() => cashierPayments.value.length)

const totalRevenue = computed(() =>
  cashierPayments.value.reduce((sum, scan) => sum + Number(scan.amount || 0), 0)
)

const todayRevenue = computed(() => {
  const today = new Date()
  return cashierPayments.value.reduce((sum, scan) => {
    if (!scan.createdAt) return sum
    const date = new Date(scan.createdAt)
    const isSameDay =
      date.getFullYear() === today.getFullYear() &&
      date.getMonth() === today.getMonth() &&
      date.getDate() === today.getDate()
    return isSameDay ? sum + Number(scan.amount || 0) : sum
  }, 0)
})

const paymentMethodCounts = computed(() => {
  return cashierPayments.value.reduce(
    (acc, scan) => {
      if (scan.paymentMethod === 'Telebirr') acc.telebirr += 1
      else acc.cash += 1
      return acc
    },
    { cash: 0, telebirr: 0 }
  )
})

const recentPayments = computed(() => cashierPayments.value.slice(0, 5))

const averagePayment = computed(() => {
  if (!cashierPayments.value.length) return 0
  return totalRevenue.value / cashierPayments.value.length
})

const telebirrShare = computed(() => {
  const total = paymentMethodCounts.value.cash + paymentMethodCounts.value.telebirr
  if (!total) return 0
  return Math.round((paymentMethodCounts.value.telebirr / total) * 100)
})

const formatAmount = (value) => {
  const numberValue = Number(value || 0)
  return numberValue.toLocaleString()
}

const methodPercent = (count) => {
  const total = paymentMethodCounts.value.cash + paymentMethodCounts.value.telebirr
  if (!total) return '0%'
  return `${Math.round((count / total) * 100)}%`
}

onMounted(() => {
  fetchPayments()
})

const refreshReport = () => {
  fetchPayments()
}

const last7DaysRevenue = computed(() => {
  const today = new Date()
  const days = []
  for (let i = 6; i >= 0; i -= 1) {
    const day = new Date(today)
    day.setDate(today.getDate() - i)
    const label = day.toLocaleDateString('en-ET', { weekday: 'short' })
    const total = cashierPayments.value.reduce((sum, scan) => {
      if (!scan.createdAt) return sum
      const date = new Date(scan.createdAt)
      const isSameDay =
        date.getFullYear() === day.getFullYear() &&
        date.getMonth() === day.getMonth() &&
        date.getDate() === day.getDate()
      return isSameDay ? sum + Number(scan.amount || 0) : sum
    }, 0)
    days.push({ date: day.toISOString().slice(0, 10), label, total })
  }
  return days
})

const maxDayRevenue = computed(() => {
  const values = last7DaysRevenue.value.map((d) => d.total)
  return Math.max(1, ...values)
})

const barHeight = (value) => {
  const percent = (Number(value || 0) / maxDayRevenue.value) * 100
  const clamped = Math.max(6, Math.min(100, Math.round(percent)))
  return `${clamped}%`
}

const pieDash = computed(() => {
  const total = paymentMethodCounts.value.cash + paymentMethodCounts.value.telebirr
  if (!total) return '0 100'
  const telebirr = Math.round((paymentMethodCounts.value.telebirr / total) * 100)
  return `${telebirr} ${100 - telebirr}`
})
</script>
