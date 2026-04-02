<template>
  <div class="min-h-screen bg-gradient-to-br from-gray-50 via-white to-purple-50/30 overflow-x-hidden">
    <!-- Animated Background Elements -->
    <div class="fixed inset-0 overflow-hidden pointer-events-none">
      <div class="absolute -top-40 -right-40 w-80 h-80 bg-[#3C3C9E]/5 rounded-full blur-3xl animate-pulse"></div>
      <div class="absolute top-60 -left-40 w-96 h-96 bg-purple-500/5 rounded-full blur-3xl animate-pulse animation-delay-2000"></div>
      <div class="absolute bottom-0 right-20 w-72 h-72 bg-blue-500/5 rounded-full blur-3xl animate-pulse animation-delay-4000"></div>
    </div>

    <!-- Header - Mobile First -->
    <div class="relative bg-white/95 backdrop-blur-xl border-b border-gray-200/80 sticky top-0 z-20 shadow-sm">
      <div class="px-4 sm:px-6 py-3 sm:py-4">
        <div class="flex flex-col sm:flex-row sm:justify-between sm:items-center gap-3">
          <!-- Logo & Title -->
          <div class="flex items-center space-x-3">
            <div class="h-10 w-10 sm:h-12 sm:w-12 bg-gradient-to-br from-[#3C3C9E] to-purple-600 rounded-xl flex items-center justify-center shadow-lg shadow-[#3C3C9E]/20">
              <svg class="h-5 w-5 sm:h-6 sm:w-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" />
              </svg>
            </div>
            <div>
              <h1 class="text-xl sm:text-2xl font-bold bg-gradient-to-r from-[#3C3C9E] to-purple-600 bg-clip-text text-transparent">              </h1>
              <p class="text-xs text-gray-500">Meskel Square Parking Services</p>
            </div>
          </div>

          <!-- Mobile Tab Switcher - Horizontal Scroll -->
          <div class="sm:hidden overflow-x-auto pb-1 -mx-1 px-1">
            <div class="flex space-x-2 min-w-max">
              <button
                v-for="tab in tabs"
                :key="tab.value"
                @click="activeTab = tab.value"
                class="relative px-5 py-2.5 text-sm font-medium rounded-xl transition-all duration-300 flex items-center space-x-2"
                :class="[
                  activeTab === tab.value
                    ? 'bg-gradient-to-r from-[#3C3C9E] to-purple-600 text-white shadow-md'
                    : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                ]"
              >
                <component :is="tab.icon" class="h-4 w-4" />
                <span>{{ tab.label }}</span>
              </button>
            </div>
          </div>

          <!-- Desktop Tab Switcher -->
          <div class="hidden sm:flex space-x-2 bg-gray-100/80 backdrop-blur-sm p-1 rounded-2xl">
            <button
              v-for="tab in tabs"
              :key="tab.value"
              @click="activeTab = tab.value"
              class="relative px-6 py-2.5 text-sm font-medium rounded-xl transition-all duration-300"
              :class="[
                activeTab === tab.value
                  ? 'text-white shadow-lg'
                  : 'text-gray-600 hover:text-gray-900 hover:bg-gray-200/50'
              ]"
            >
              <span v-if="activeTab === tab.value" class="absolute inset-0 bg-gradient-to-r from-[#3C3C9E] to-purple-600 rounded-xl"></span>
              <span class="relative flex items-center space-x-2">
                <component :is="tab.icon" class="h-4 w-4" />
                <span>{{ tab.label }}</span>
              </span>
            </button>
          </div>
        </div>

        <!-- Search Bar - Only on Services Tab -->
        <transition
          enter-active-class="transition duration-300 ease-out"
          enter-from-class="transform -translate-y-4 opacity-0"
          enter-to-class="transform translate-y-0 opacity-100"
          leave-active-class="transition duration-200 ease-in"
          leave-from-class="transform translate-y-0 opacity-100"
          leave-to-class="transform -translate-y-4 opacity-0"
        >
          <div v-if="activeTab === 'services'" class="mt-3 sm:mt-4">
            <div class="relative group">
              <div class="absolute -inset-0.5 bg-gradient-to-r from-[#3C3C9E]/20 to-purple-600/20 rounded-xl blur opacity-0 group-hover:opacity-100 transition duration-500"></div>
              <div class="relative">
                <input
                  v-model="searchQuery"
                  type="text"
                  placeholder="Search for parking, car wash, amusement..."
                  class="max-w-full pl-11 sm:pl-12 pr-4 py-3 sm:py-3.5 bg-white/90 backdrop-blur-sm border border-gray-200/80 rounded-xl sm:rounded-2xl focus:outline-none focus:ring-2 focus:ring-[#3C3C9E]/30 focus:border-[#3C3C9E] transition-all duration-300 text-sm sm:text-base text-gray-700 placeholder-gray-400"
                />
                <svg class="absolute left-3.5 sm:left-4 top-3 sm:top-3.5 h-4 w-4 sm:h-5 sm:w-5 text-gray-400 group-focus-within:text-[#3C3C9E] transition-colors" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
                </svg>
              </div>
            </div>
          </div>
        </transition>
      </div>
    </div>

    <!-- Main Content -->
    <div class="relative max-w-7xl mx-auto px-4 sm:px-6 py-4 sm:py-8 pb-24 sm:pb-8">
      <!-- Services Tab -->
      <div v-if="activeTab === 'services'">
        <!-- Loading State with Skeleton -->
        <div v-if="loading" class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4 sm:gap-6">
          <div v-for="n in 6" :key="n" class="bg-white rounded-xl sm:rounded-2xl p-4 sm:p-6 shadow-sm border border-gray-100 animate-pulse">
            <div class="flex justify-between items-start mb-4">
              <div>
                <div class="h-5 sm:h-6 w-28 sm:w-32 bg-gray-200 rounded-lg mb-2"></div>
                <div class="h-3 sm:h-4 w-16 sm:w-20 bg-gray-200 rounded-full"></div>
              </div>
              <div class="h-8 sm:h-10 w-16 sm:w-20 bg-gray-200 rounded-lg"></div>
            </div>
            <div class="space-y-2 mb-4">
              <div class="h-3 sm:h-4 w-full bg-gray-200 rounded"></div>
              <div class="h-3 sm:h-4 w-3/4 bg-gray-200 rounded"></div>
            </div>
            <div class="h-9 sm:h-10 w-full bg-gray-200 rounded-lg sm:rounded-xl"></div>
          </div>
        </div>

        <!-- Services Grid -->
        <transition-group 
          v-else-if="filteredServices.length > 0" 
          tag="div" 
          class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4 sm:gap-6"
          enter-active-class="transition duration-500 ease-out"
          enter-from-class="opacity-0 translate-y-4"
          enter-to-class="opacity-100 translate-y-0"
          leave-active-class="transition duration-300 ease-in"
          leave-from-class="opacity-100 translate-y-0"
          leave-to-class="opacity-0 translate-y-4"
        >
          <ServiceCard
            v-for="service in filteredServices"
            :key="service.serviceUuid"
            :service="service"
            @select="openOrderModal"
            class="transform hover:-translate-y-1 transition-all duration-300"
          />
        </transition-group>

        <!-- Empty State -->
        <div v-else class="relative flex flex-col items-center justify-center py-12 sm:py-20 px-4">
          <div class="absolute inset-0 bg-gradient-to-br from-[#3C3C9E]/5 to-purple-600/5 rounded-3xl blur-3xl"></div>
          <div class="relative text-center">
            <div class="inline-flex p-3 sm:p-4 bg-gradient-to-br from-gray-100 to-white rounded-2xl sm:rounded-3xl shadow-inner mb-4 sm:mb-6">
              <svg class="h-12 w-12 sm:h-16 sm:w-16 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
              </svg>
            </div>
            <h3 class="text-lg sm:text-2xl font-bold text-gray-700 mb-2">No services found</h3>
            <p class="text-sm sm:text-base text-gray-500 mb-4 sm:mb-6">Try adjusting your search or check back later</p>
            <button 
              @click="searchQuery = ''" 
              class="px-5 sm:px-6 py-2.5 sm:py-3 bg-gradient-to-r from-[#3C3C9E] to-purple-600 text-white text-sm sm:text-base rounded-xl font-medium shadow-lg shadow-[#3C3C9E]/30 hover:shadow-xl transform hover:-translate-y-0.5 transition-all duration-200"
            >
              Clear Search
            </button>
          </div>
        </div>
      </div>

      <!-- History Tab -->
      <div v-else-if="activeTab === 'history'">
        <transition
          enter-active-class="transition duration-500 ease-out"
          enter-from-class="opacity-0 translate-x-4"
          enter-to-class="opacity-100 translate-x-0"
        >
          <ServiceHistory
            :orders="serviceHistory"
            @view-details="viewOrderDetails"
            @cancel="cancelOrder"
            @pay="openPaymentModal"
            @rebook="rebookService"
          />
        </transition>
      </div>
    </div>

    <!-- Modals -->
    <ServiceOrderModal
      :show="showOrderModal"
      :service="selectedService"
      @close="closeOrderModal"
      @create="createServiceOrder"
    />

    <PaymentModal
      :show="showPaymentModal"
      :order="pendingPaymentOrder"
      @close="closePaymentModal"
      @payment-success="handlePaymentSuccess"
      @payment-error="handlePaymentError"
    />

    <ServiceOrderDetailModal
      :show="showOrderDetailModal"
      :order="selectedOrderDetail"
      @close="closeOrderDetailModal"
    />

    <!-- Floating Action Button for Quick Booking (Mobile Only) -->
    <transition
      enter-active-class="transition duration-300 ease-out"
      enter-from-class="opacity-0 translate-y-10"
      enter-to-class="opacity-100 translate-y-0"
      leave-active-class="transition duration-200 ease-in"
      leave-from-class="opacity-100 translate-y-0"
      leave-to-class="opacity-0 translate-y-10"
    >
      <button
        v-if="activeTab === 'services' && !loading && services.length > 0"
        @click="scrollToTop"
        class="fixed bottom-20 right-4 sm:bottom-6 sm:right-6 p-3.5 sm:p-4 bg-gradient-to-r from-[#3C3C9E] to-purple-600 text-white rounded-full shadow-xl hover:shadow-2xl transform hover:scale-110 transition-all duration-300 group z-10"
      >
        <svg class="h-5 w-5 sm:h-6 sm:w-6 group-hover:animate-bounce" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 10l7-7m0 0l7 7m-7-7v18" />
        </svg>
      </button>
    </transition>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { clientServiceApi } from '../api/clientServiceApi'
import ServiceCard from '../components/ServiceCard.vue'
import ServiceOrderModal from '../components/ServiceOrderModal.vue'
import ServiceHistory from '../components/ServiceHistory.vue'
import PaymentModal from '../components/PaymentModal.vue'
import ServiceOrderDetailModal from '../components/ServiceOrderDetailModal.vue'
import { useAuth } from '@/stores/auth'
import { useToast } from '@/features/client-service/components/ui/toast'
import {
  buildParkingGoogleMapsUrl,
  buildParkingInstructions,
  buildParkingLocationDisplay,
} from '../utils/parkingLayout'

// Icons
import { SparklesIcon, TicketIcon } from '@heroicons/vue/24/outline'

// ---------------- AUTH ----------------
const auth = useAuth()
const route = useRoute()
const router = useRouter()
const { showToast } = useToast()
const userUuid = computed(() => auth.auth.user?.userUuid || null)

// ---------------- STATE ----------------
const services = ref([])
const serviceHistory = ref([])
const loading = ref(false)
const searchQuery = ref('')
const activeTab = ref('services')

const showOrderModal = ref(false)
const selectedService = ref(null)

const showPaymentModal = ref(false)
const pendingPaymentOrder = ref(null)
const showOrderDetailModal = ref(false)
const selectedOrderDetail = ref(null)

// ---------------- TABS ----------------
const tabs = [
  { value: 'services', label: 'Services', icon: SparklesIcon },
  { value: 'history', label: 'My Orders', icon: TicketIcon }
]

const inferServiceType = (service) => {
  if (service?.type) return service.type

  const name = service?.name?.toLowerCase() || ''
  if (name.includes('car wash')) return 'CAR_WASH'
  if (name.includes('parking')) return 'PARKING'
  if (name.includes('meskel') || name.includes('amusement')) return 'AMUSEMENT'
  return ''
}

// ---------------- COMPUTED ----------------
const filteredServices = computed(() => {
  if (!searchQuery.value) return services.value

  const query = searchQuery.value.toLowerCase()
  return services.value.filter(service =>
    service.name?.toLowerCase().includes(query) ||
    service.description?.toLowerCase().includes(query) ||
    service.type?.toLowerCase().includes(query)
  )
})

// ---------------- METHODS ----------------
const fetchServices = async () => {
  loading.value = true
  try {
    const response = await clientServiceApi.getActiveServices()
    services.value = response.data
    
    showToast({
      title: 'Welcome to Meskel Square Parking!',
      description: 'Find the perfect parking service for you',
      type: 'success',
      duration: 3000
    })
  } catch (error) {
    console.error('Failed to fetch services:', error)
    showToast({
      title: 'Error',
      description: 'Failed to load services. Please refresh.',
      type: 'error'
    })
  } finally {
    loading.value = false
  }
}

const fetchServiceHistory = async () => {
  try {
    if (!userUuid.value) return
    const response = await clientServiceApi.getServiceHistory(userUuid.value)
    serviceHistory.value = response.data
  } catch (error) {
    console.error('Failed to fetch history:', error)
  }
}

const openOrderModal = (service) => {
  selectedService.value = service
  showOrderModal.value = true
}

const closeOrderModal = () => {
  showOrderModal.value = false
  setTimeout(() => {
    selectedService.value = null
  }, 300)
}

const createServiceOrder = async (orderData) => {
  try {
    if (!userUuid.value) {
      showToast({
        title: 'Authentication Required',
        description: 'Please login to continue',
        type: 'warning'
      })
      return
    }

    showToast({
      title: 'Processing...',
      description: 'Creating your booking',
      type: 'info',
      duration: 2000
    })

    const response = await clientServiceApi.createServiceOrder(
      userUuid.value,
      {
        ...orderData,
        userId: userUuid.value,
        createdAt: new Date().toISOString()
      }
    )

    closeOrderModal()

    showToast({
      title: 'Booking Confirmed! 🎉',
      description: 'Redirecting to secure payment...',
      type: 'success'
    })

    const resolvedTotal =
      response.data?.totalAmount ??
      response.data?.rateApplied ??
      orderData.totalAmount ??
      selectedService.value?.currentRate ??
      0

    // Store order data for payment
    const orderForPayment = {
      id: response.data?.id || response.data?.orderUuid,
      orderUuid: response.data?.orderUuid || response.data?.id,
      serviceName: selectedService.value?.name,
      serviceType: response.data?.serviceType || inferServiceType(selectedService.value),
      amount: resolvedTotal,
      rateApplied: response.data?.rateApplied || selectedService.value?.currentRate,
      bookingDetails: {
        date: orderData.appointmentDate || orderData.parkingDate || orderData.visitDate,
        time: orderData.appointmentTime || orderData.entryTime,
        location:
          response.data?.parkingLocationDisplay ||
          buildParkingLocationDisplay(orderData)
      },
      parkingLocationDisplay:
        response.data?.parkingLocationDisplay ||
        buildParkingLocationDisplay(orderData),
      googleMapsUrl:
        response.data?.googleMapsUrl ||
        buildParkingGoogleMapsUrl({ entranceName: orderData.entranceName }),
      navigationInstructions:
        response.data?.navigationInstructions ||
        buildParkingInstructions(orderData),
      userUuid: userUuid.value
    }

    // Open payment modal
    setTimeout(() => {
      pendingPaymentOrder.value = orderForPayment
      showPaymentModal.value = true
    }, 500)

    await fetchServiceHistory()

  } catch (error) {
    console.error('Order creation failed:', error)
    showToast({
      title: 'Booking Failed',
      description: error.response?.data?.message || 'Please try again',
      type: 'error'
    })
  }
}

const openPaymentModal = (order) => {
  pendingPaymentOrder.value = order
  showPaymentModal.value = true
}

const closePaymentModal = () => {
  showPaymentModal.value = false
  setTimeout(() => {
    pendingPaymentOrder.value = null
  }, 300)
}

const openOrderDetailModal = (orderDetail) => {
  selectedOrderDetail.value = orderDetail
  showOrderDetailModal.value = true
}

const closeOrderDetailModal = () => {
  showOrderDetailModal.value = false
  setTimeout(() => {
    selectedOrderDetail.value = null
  }, 300)
}

const handlePaymentSuccess = () => {
  closePaymentModal()
  
  showToast({
    title: 'Payment Session Ready',
    description: 'Complete payment to unlock the ride page and final order details.',
    type: 'success',
    duration: 5000
  })
}

const handlePaymentError = (error) => {
  showToast({
    title: 'Payment Failed',
    description: error || 'Please try again',
    type: 'error'
  })
}

const fetchOrderDetails = async (orderUuid) => {
  if (!userUuid.value || !orderUuid) return null
  const response = await clientServiceApi.getOrderDetails(orderUuid, userUuid.value)
  return response.data || null
}

const viewOrderDetails = async (order) => {
  try {
    const orderDetails = await fetchOrderDetails(order.orderUuid)
    if (orderDetails) {
      openOrderDetailModal(orderDetails)
    }
  } catch (error) {
    console.error('Failed to fetch order details:', error)
    showToast({
      title: 'Unable to load order details',
      description: error.response?.data?.message || 'Please try again.',
      type: 'error'
    })
  }
}

const cancelOrder = async (order) => {
  try {
    if (!userUuid.value) return
    
    const confirmed = window.confirm('Are you sure you want to cancel this order?')
    if (!confirmed) return

    await clientServiceApi.cancelOrder(order.orderUuid, userUuid.value)
    await fetchServiceHistory()
    
    showToast({
      title: 'Booking Cancelled',
      description: 'Your order has been cancelled successfully',
      type: 'warning'
    })
  } catch (error) {
    console.error('Cancel failed:', error)
    showToast({
      title: 'Cancellation Failed',
      description: 'Please try again',
      type: 'error'
    })
  }
}

const rebookService = async (order) => {
  const service = services.value.find(s => s.serviceUuid === order.serviceUuid)
  if (service) {
    openOrderModal(service)
    showToast({
      title: 'Quick Rebook',
      description: `Rebooking ${service.name}`,
      type: 'info'
    })
  }
}

const scrollToTop = () => {
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

const handleRouteState = async () => {
  const focusOrder = typeof route.query.focusOrder === 'string' ? route.query.focusOrder : null
  const paymentState = typeof route.query.payment === 'string' ? route.query.payment : null
  const targetTab = typeof route.query.tab === 'string' ? route.query.tab : null

  if (targetTab === 'history' || focusOrder || paymentState === 'success') {
    activeTab.value = 'history'
  }

  if (paymentState === 'success') {
    showToast({
      title: 'Payment Successful!',
      description: 'Your parking order is completed and the ride page is ready in order details.',
      type: 'success',
      duration: 6000
    })
  }

  if (focusOrder) {
    const orderDetails = await fetchOrderDetails(focusOrder)
    if (orderDetails) {
      openOrderDetailModal(orderDetails)
    }
  }

  if (focusOrder || paymentState || targetTab) {
    router.replace({ path: route.path, query: {} })
  }
}

// ---------------- LIFECYCLE ----------------
onMounted(async () => {
  await fetchServices()
  await fetchServiceHistory()
  await handleRouteState()
})
</script>

<style scoped>
/* Mobile-first animations */
@keyframes float {
  0%, 100% { transform: translateY(0px); }
  50% { transform: translateY(-10px); }
}

.animate-float {
  animation: float 3s ease-in-out infinite;
}

.animation-delay-2000 {
  animation-delay: 2s;
}

.animation-delay-4000 {
  animation-delay: 4s;
}

/* Mobile touch optimizations */
@media (max-width: 640px) {
  button, 
  [role="button"],
  input,
  select {
    min-height: 44px;
  }
  
  .min-h-screen {
    padding-bottom: env(safe-area-inset-bottom);
  }
}

/* Hide scrollbar for mobile tabs */
.overflow-x-auto {
  -webkit-overflow-scrolling: touch;
  scrollbar-width: none;
}

.overflow-x-auto::-webkit-scrollbar {
  display: none;
}
</style>
