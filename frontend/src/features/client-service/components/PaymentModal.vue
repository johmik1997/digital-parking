<template>
  <div v-if="show" class="fixed inset-0 z-50 overflow-y-auto">
    <!-- Backdrop -->
    <div class="fixed inset-0 transition-opacity" @click="$emit('close')">
      <div class="absolute inset-0 bg-gray-900/70 backdrop-blur-sm"></div>
    </div>

    <!-- Modal Panel - Mobile First -->
    <div class="relative flex items-end sm:items-center justify-center min-h-screen px-0 sm:px-4">
      <transition
        enter-active-class="transition duration-300 ease-out"
        enter-from-class="opacity-0 translate-y-4 sm:translate-y-0 sm:scale-95"
        enter-to-class="opacity-100 translate-y-0 sm:scale-100"
        leave-active-class="transition duration-200 ease-in"
        leave-from-class="opacity-100 translate-y-0 sm:scale-100"
        leave-to-class="opacity-0 translate-y-4 sm:translate-y-0 sm:scale-95"
      >
        <div class="relative w-full bg-white rounded-t-2xl sm:rounded-3xl shadow-2xl sm:my-8 sm:max-w-md sm:w-full">
          
          <!-- Header -->
          <div class="relative bg-gradient-to-r from-[#3C3C9E] to-purple-600 px-5 sm:px-6 py-4 sm:py-5">
            <div class="flex justify-between items-center">
              <div class="flex items-center space-x-3">
                <div class="h-10 w-10 sm:h-12 sm:w-12 bg-white/20 backdrop-blur-sm rounded-xl flex items-center justify-center">
                  <svg class="h-5 w-5 sm:h-6 sm:w-6 text-white" viewBox="0 0 24 24" fill="currentColor">
                    <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm0 18c-4.41 0-8-3.59-8-8s3.59-8 8-8 8 3.59 8 8-3.59 8-8 8z"/>
                    <circle cx="12" cy="12" r="5"/>
                  </svg>
                </div>
                <div>
                  <h3 class="text-lg sm:text-xl font-bold text-white">
                    Pay with TeleBirr
                  </h3>
                  <p class="text-xs sm:text-sm text-white/80 mt-0.5">
                    Secure Ethiopian Payment Gateway
                  </p>
                </div>
              </div>
              <button 
                @click="$emit('close')" 
                class="p-1.5 sm:p-2 hover:bg-white/20 rounded-lg transition-colors"
              >
                <svg class="h-4 w-4 sm:h-5 sm:w-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                </svg>
              </button>
            </div>
          </div>

          <!-- Body -->
          <div class="px-5 sm:px-6 py-5 sm:py-6 max-h-[70vh] overflow-y-auto">
            
            <!-- Order Summary -->
            <div class="mb-5 sm:mb-6 p-4 sm:p-5 bg-gradient-to-br from-[#3C3C9E]/5 to-purple-50 rounded-xl sm:rounded-2xl border border-[#3C3C9E]/10">
              <div class="flex items-center mb-3">
                <img src="https://chapa.co/favicon.ico" alt="Chapa" class="h-5 w-5 sm:h-6 sm:w-6 mr-2 rounded" />
                <span class="text-xs sm:text-sm font-medium text-gray-700">Payment Summary</span>
              </div>
              <div class="space-y-2">
                <div class="flex justify-between items-center">
                  <span class="text-xs sm:text-sm text-gray-600">Service:</span>
                  <span class="text-xs sm:text-sm font-medium text-gray-900">{{ order?.serviceName || 'Parking Service' }}</span>
                </div>
                <div v-if="order?.bookingDetails" class="flex justify-between items-center">
                  <span class="text-xs sm:text-sm text-gray-600">{{ getBookingLabel(order.serviceType) }}:</span>
                  <span class="text-xs sm:text-sm text-gray-900">{{ getBookingValue(order) }}</span>
                </div>
                <div v-if="order?.parkingLocationDisplay" class="flex justify-between items-start gap-3">
                  <span class="text-xs sm:text-sm text-gray-600">Parking Slot:</span>
                  <span class="text-right text-xs sm:text-sm text-gray-900">{{ order.parkingLocationDisplay }}</span>
                </div>
                <div v-if="order?.navigationInstructions" class="rounded-lg bg-white/70 px-3 py-2 text-[11px] text-gray-600">
                  {{ order.navigationInstructions }}
                </div>
                <a
                  v-if="order?.googleMapsUrl"
                  :href="order.googleMapsUrl"
                  target="_blank"
                  rel="noopener noreferrer"
                  class="inline-flex items-center justify-center rounded-lg bg-white px-3 py-2 text-xs font-semibold text-[#3C3C9E] ring-1 ring-[#3C3C9E]/15 transition hover:bg-[#3C3C9E]/5"
                >
                  Open Google Maps
                </a>
                <div class="flex justify-between items-center pt-2 mt-2 border-t border-gray-200">
                  <span class="text-sm sm:text-base font-semibold text-gray-900">Total Amount:</span>
                  <span class="text-xl sm:text-2xl font-bold text-[#3C3C9E]">ETB {{ formatPrice(order?.amount || 0) }}</span>
                </div>
              </div>
            </div>

        
            <!-- Payment Methods Info -->
            <div class="mb-4 sm:mb-5 p-3 sm:p-4 bg-blue-50 rounded-lg sm:rounded-xl border border-blue-200">
              <div class="flex items-start">
                <svg class="h-4 w-4 sm:h-5 sm:w-5 text-blue-500 mt-0.5 mr-2 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
                <div class="flex-1">
                  <p class="text-xs sm:text-sm text-blue-700 font-medium mb-2">
                    Pay with:
                  </p>
                  <div class="flex flex-wrap gap-1.5 sm:gap-2">
                    <span class="inline-flex items-center px-2 py-1 bg-white rounded-md text-xs text-gray-700 border border-blue-200">
                                        <img src="../../../assets/telebirr-icon.svg" alt="Telebirr" class="h-5 w-5 sm:h-6 sm:w-6" />  
   Telebirr
                    </span>
                    <span class="inline-flex items-center px-2 py-1 bg-white rounded-md text-xs text-gray-700 border border-blue-200">
                      💳 CBE Birr
                    </span>
                    <span class="inline-flex items-center px-2 py-1 bg-white rounded-md text-xs text-gray-700 border border-blue-200">
                      🏦 Bank Cards
                    </span>
                    <span class="inline-flex items-center px-2 py-1 bg-white rounded-md text-xs text-gray-700 border border-blue-200">
                      📱 Amole
                    </span>
                  </div>
                </div>
              </div>
            </div>

            <!-- Loading State -->
            <div v-if="loading" class="flex flex-col items-center justify-center py-6 sm:py-8">
              <div class="animate-spin rounded-full h-10 w-10 sm:h-12 sm:w-12 border-b-2 border-[#3C3C9E]"></div>
              <p class="mt-3 text-xs sm:text-sm text-gray-600">Redirecting to Chapa secure payment...</p>
            </div>
          </div>

          <!-- Footer Actions -->
          <div class="px-5 sm:px-6 py-4 bg-gray-50 border-t border-gray-200 flex flex-col-reverse sm:flex-row-reverse gap-2 sm:gap-3">
            <button
              @click="initializeChapaPayment"
              :disabled="!isFormValid || loading"
              class="w-full sm:w-auto inline-flex justify-center items-center px-5 sm:px-6 py-3 sm:py-3.5 bg-gradient-to-r from-[#3C3C9E] to-purple-600 text-white text-sm sm:text-base font-medium rounded-lg sm:rounded-xl shadow-lg shadow-[#3C3C9E]/30 hover:shadow-xl transform hover:-translate-y-0.5 transition-all duration-200 disabled:opacity-50 disabled:cursor-not-allowed disabled:transform-none"
            >
              <svg v-if="!loading" class="h-4 w-4 sm:h-5 sm:w-5 mr-1.5 sm:mr-2" viewBox="0 0 24 24" fill="currentColor">
                <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm0 18c-4.41 0-8-3.59-8-8s3.59-8 8-8 8 3.59 8 8-3.59 8-8 8z"/>
                <circle cx="12" cy="12" r="5"/>
              </svg>
              <span v-if="!loading">Pay ETB {{ formatPrice(order?.amount || 0) }}</span>
              <span v-else>Processing...</span>
            </button>
            <button
              @click="$emit('close')"
              :disabled="loading"
              class="w-full sm:w-auto px-5 sm:px-6 py-3 sm:py-3.5 bg-white border border-gray-300 text-gray-700 text-sm sm:text-base font-medium rounded-lg sm:rounded-xl hover:bg-gray-50 transition-colors disabled:opacity-50"
            >
              Cancel
            </button>
          </div>
        </div>
      </transition>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch } from 'vue'
import { chapaApi } from '../api/ChapaApi'
import { useAuth } from "@/stores/auth";

const props = defineProps({
  show: {
    type: Boolean,
    default: false
  },
  order: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['close', 'payment-success', 'payment-error'])

const loading = ref(false)
const authStore = useAuth();
const apiBaseUrl =
  import.meta.env.VITE_API_URL ||
  'http://localhost:8280/api/medco-digital-parking/v1'
const appBaseUrl =
  import.meta.env.VITE_APP_URL ||
  window.location.origin

// Customer information for Chapa
const customerInfo = reactive({
  name: '',
  email: '',
  phone: ''
})

// Pre-fill with auth user data if available
watch(() => props.show, (newVal) => {
  if (newVal) {
    // Reset form
    customerInfo.name = ''
    customerInfo.email = ''
    customerInfo.phone = ''
    
    // Get user from auth store
    const user = authStore?.auth?.user;
    
    if (user) {
      // Fix: Properly assign user data
      customerInfo.name = user.firstName || user.fullName || user.name || '';
      customerInfo.email = user.email || '';
      
      // Handle phone number - remove +251 if present and get last 9 digits
      let phone = user.mobilePhone || user.phone || '';
      phone = phone.replace('+251', '').replace(/\D/g, '');
      // Take last 9 digits if longer
      if (phone.length > 9) {
        phone = phone.slice(-9);
      }
      customerInfo.phone = phone;
      
      console.log('Loaded user data:', {
        name: customerInfo.name,
        email: customerInfo.email,
        phone: customerInfo.phone
      });
    }
  }
})

// Form validation
const isFormValid = computed(() => {
  const isValid = 
    customerInfo.name?.trim() !== '' &&
    customerInfo.email?.trim() !== '' &&
    /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(customerInfo.email || '') &&
    customerInfo.phone?.trim() !== '' &&
    /^\d{9}$/.test(customerInfo.phone || '');
  
  console.log('Form validation:', isValid, customerInfo);
  return isValid;
})

// Format price
const formatPrice = (amount) => {
  return amount?.toFixed(2) || '0.00'
}

// Get booking label
const getBookingLabel = (serviceType) => {
  switch (serviceType) {
    case 'CAR_WASH': return 'Appointment'
    case 'PARKING': return 'Entry Time'
    case 'AMUSEMENT': return 'Visit Date'
    default: return 'Booking'
  }
}

// Get booking value
const getBookingValue = (order) => {
  if (!order?.bookingDetails) return 'N/A'
  
  switch (order.serviceType) {
    case 'CAR_WASH':
      return `${order.bookingDetails.date} at ${order.bookingDetails.time}`
    case 'PARKING':
      return `${order.bookingDetails.time} on ${order.bookingDetails.date}`
    case 'AMUSEMENT':
      return order.bookingDetails.date
    default:
      return 'Confirmed'
  }
}

// Generate unique transaction reference
const generateTxRef = () => {
  const timestamp = Date.now()
  const random = Math.floor(Math.random() * 1000000)
  const orderId = props.order?.orderUuid || props.order?.id || 'ORDER'
  return `PK-${orderId.slice(-8)}-${timestamp}-${random}`
}

// Initialize Chapa payment
const initializeChapaPayment = async () => {
  if (!isFormValid.value || loading.value) {
    console.log('Form invalid or already loading:', { 
      isValid: isFormValid.value, 
      loading: loading.value,
      customerInfo 
    });
    return;
  }
  
  loading.value = true
  
  try {
    // Generate unique transaction reference
    const txRef = generateTxRef()
    
    // Prepare payment data for Chapa
    const paymentData = {
      amount: props.order?.amount || 0,
      currency: 'ETB',
      email: customerInfo.email,
      first_name: customerInfo.name.split(' ')[0] || customerInfo.name,
      last_name: customerInfo.name.split(' ').slice(1).join(' ') || 'Customer',
      phone_number: `+251${customerInfo.phone}`,
      tx_ref: txRef,
      callback_url: `${apiBaseUrl}/api/chapa/webhook`,
      return_url: `${appBaseUrl}/payment/callback?tx_ref=${txRef}`,
      customization: {
        title: 'ParkEase Payment',
        description: `Payment for ${props.order?.serviceName || 'Parking Service'}`,
        logo: 'https://parkease.com/logo.png'
      },
      meta: {
        order_id: props.order?.id,
        order_uuid: props.order?.orderUuid,
        service_type: props.order?.serviceType,
        user_uuid: props.order?.userUuid
      }
    }
    
    console.log('Initializing Chapa payment with:', paymentData)
    
    // Call your backend to initialize Chapa payment
    const response = await chapaApi.initializePayment(paymentData)
    
    console.log('Chapa response:', response)
    
    // Check response and redirect
    if (response && response.status === 'success') {
      const checkoutUrl = response.data?.checkout_url || response.checkout_url
      
      if (checkoutUrl) {
        // Emit success event before redirect
        emit('payment-success', {
          tx_ref: txRef,
          checkout_url: checkoutUrl,
          amount: props.order?.amount,
          customer: { ...customerInfo }
        })
        
        // Store payment info in session storage for recovery
        sessionStorage.setItem('pending_payment', JSON.stringify({
          tx_ref: txRef,
          order: props.order,
          timestamp: Date.now()
        }))
        
        // Redirect to Chapa checkout page
        window.location.href = checkoutUrl
      } else {
        throw new Error('No checkout URL received')
      }
    } else {
      throw new Error(response?.message || 'Failed to initialize payment')
    }
    
  } catch (error) {
    console.error('Chapa payment initialization failed:', error)
    
    let errorMessage = 'Failed to initialize payment. '
    
    if (error.response) {
      console.error('Error response:', error.response.data)
      errorMessage += error.response.data?.message || 'Please try again.'
    } else if (error.request) {
      errorMessage += 'Network error. Please check your connection.'
    } else {
      errorMessage += error.message || 'Please try again.'
    }
    
    emit('payment-error', errorMessage)
    
    // Show user-friendly error
    alert(errorMessage)
  } finally {
    loading.value = false
  }
}
</script>
<style scoped>
/* Mobile optimizations */
@media (max-width: 640px) {
  .fixed.inset-0 {
    padding-bottom: env(safe-area-inset-bottom);
  }
  
  .rounded-t-2xl {
    border-top-left-radius: 1rem;
    border-top-right-radius: 1rem;
  }
}

/* Smooth transitions */
.translate-y-4 {
  transform: translateY(1rem);
}

.translate-y-0 {
  transform: translateY(0);
}

.scale-95 {
  transform: scale(0.95);
}

.scale-100 {
  transform: scale(1);
}
</style>
