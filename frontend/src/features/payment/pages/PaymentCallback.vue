<template>
  <div class="min-h-screen bg-gradient-to-b from-slate-50 to-white flex items-center justify-center px-4">
    <div class="w-full max-w-md bg-white rounded-2xl shadow-xl border border-slate-100 p-6">
      <div class="flex items-center gap-3">
        <div
          class="h-10 w-10 rounded-full flex items-center justify-center"
          :class="statusClass"
        >
          <svg v-if="status === 'success'" class="h-5 w-5 text-white" viewBox="0 0 24 24" fill="currentColor">
            <path d="M9 16.2l-3.5-3.5-1.4 1.4L9 19 20.3 7.7l-1.4-1.4z" />
          </svg>
          <svg v-else-if="status === 'error'" class="h-5 w-5 text-white" viewBox="0 0 24 24" fill="currentColor">
            <path d="M12 2C6.5 2 2 6.5 2 12s4.5 10 10 10 10-4.5 10-10S17.5 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z" />
          </svg>
          <div v-else class="h-5 w-5 border-2 border-white border-t-transparent rounded-full animate-spin"></div>
        </div>
        <div>
          <h1 class="text-lg font-semibold text-slate-900">Payment Status</h1>
          <p class="text-xs text-slate-500">Transaction reference: {{ txRef || 'N/A' }}</p>
        </div>
      </div>

      <div class="mt-5">
        <p class="text-sm text-slate-700">{{ statusMessage }}</p>
        <div v-if="order" class="mt-4 p-3 rounded-lg bg-slate-50 border border-slate-100">
          <div class="flex justify-between text-xs text-slate-600">
            <span>Service</span>
            <span class="text-slate-900 font-medium">{{ order.serviceName || 'Service' }}</span>
          </div>
          <div class="flex justify-between text-xs text-slate-600 mt-2">
            <span>Amount</span>
            <span class="text-slate-900 font-medium">ETB {{ formatAmount(order.amount) }}</span>
          </div>
        </div>
      </div>

      <div class="mt-6 flex gap-2">
        <button
          class="flex-1 px-4 py-2 rounded-lg bg-[#3C3C9E] text-white text-sm font-medium hover:opacity-90 transition"
          @click="goToOrders"
        >
          Go to Orders
        </button>
        <button
          class="px-4 py-2 rounded-lg border border-slate-200 text-sm text-slate-700 hover:bg-slate-50"
          @click="retryVerify"
          :disabled="loading"
        >
          Retry
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { chapaApi } from '@/features/client-service/api/ChapaApi'

const route = useRoute()
const router = useRouter()

const loading = ref(true)
const status = ref('loading')
const statusMessage = ref('Verifying your payment with Chapa...')
const txRef = computed(() => route.query.tx_ref)
const order = ref(null)

const statusClass = computed(() => {
  if (status.value === 'success') return 'bg-emerald-500'
  if (status.value === 'error') return 'bg-rose-500'
  return 'bg-slate-400'
})

const formatAmount = (amount) => {
  if (amount == null) return '0.00'
  return Number(amount).toFixed(2)
}

const loadPendingOrder = () => {
  try {
    const raw = sessionStorage.getItem('pending_payment')
    if (!raw) return
    const parsed = JSON.parse(raw)
    if (parsed?.order) {
      order.value = {
        serviceName: parsed.order?.serviceName,
        amount: parsed.order?.amount
      }
    }
  } catch (error) {
    console.warn('Failed to read pending payment', error)
  }
}

const handleVerification = async () => {
  const reference = txRef.value
  if (!reference) {
    status.value = 'error'
    statusMessage.value = 'Missing transaction reference. Please contact support.'
    loading.value = false
    return
  }

  try {
    loading.value = true
    const response = await chapaApi.verifyPayment(reference)

    const overallStatus = response?.status || response?.data?.status
    const remoteStatus = response?.data?.status || response?.data?.data?.status

    if (overallStatus === 'success' && remoteStatus === 'success') {
      status.value = 'success'
      statusMessage.value = 'Payment verified successfully. Redirecting you to your orders...'
      sessionStorage.removeItem('pending_payment')
      setTimeout(() => {
        router.push('/service/order')
      }, 1800)
    } else {
      status.value = 'error'
      statusMessage.value = 'Payment could not be verified. Please retry or contact support.'
    }
  } catch (error) {
    console.error('Verification failed:', error)
    status.value = 'error'
    statusMessage.value = error?.message || 'Verification failed. Please retry or contact support.'
  } finally {
    loading.value = false
  }
}

const retryVerify = () => {
  handleVerification()
}

const goToOrders = () => {
  router.push('/service/order')
}

onMounted(() => {
  loadPendingOrder()
  handleVerification()
})
</script>
