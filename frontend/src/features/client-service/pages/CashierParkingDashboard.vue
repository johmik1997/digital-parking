<template>
  <div class="min-h-screen bg-gradient-to-br from-blue-50 via-white to-indigo-50 overflow-x-hidden">
    <!-- Header -->
    <header class="bg-white/80 backdrop-blur-sm border-b border-gray-200 px-4 sm:px-6 py-3 sm:py-4 sticky top-0 z-10">
      <div class="max-w-7xl mx-auto flex justify-between items-center">
        <div class="flex items-center space-x-2 sm:space-x-3">
          <div class="h-8 w-8 sm:h-10 sm:w-10 bg-gradient-to-br from-blue-600 to-indigo-600 rounded-lg flex items-center justify-center text-white shadow-md">
            <svg class="h-4 w-4 sm:h-5 sm:w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" />
            </svg>
          </div>
          <div>
            <h1 class="text-base sm:text-xl font-bold text-gray-800">Meskel Square Parking</h1>
            <p class="text-xs text-gray-500">Premium Parking Management System</p>
          </div>
        </div>
        <div class="bg-gray-100 px-3 py-1.5 sm:px-4 sm:py-2 rounded-xl">
          <p class="text-[10px] sm:text-xs text-gray-500 font-medium">Device ID</p>
          <p class="text-xs sm:text-sm font-mono font-bold text-blue-600">{{ displayCashierId }}</p>
        </div>
      </div>
    </header>

    <!-- Main Content -->
    <div class="max-w-7xl mx-auto px-4 sm:px-6 py-4 sm:py-8">
      <div class="grid grid-cols-1 lg:grid-cols-3 gap-4 sm:gap-6 lg:gap-8">
        
        <!-- Left Panel - PMS Sync -->
        <div class="lg:col-span-1 space-y-4 sm:space-y-6">
          <!-- Arrival Search -->
          <div class="bg-white rounded-xl sm:rounded-2xl border border-gray-200 p-4 sm:p-5 shadow-lg">
            <h2 class="text-base sm:text-lg font-bold text-gray-800">Find Appointment</h2>
            <p class="text-[10px] sm:text-xs text-gray-500 mt-1">Enter vehicle plate to locate today’s appointment.</p>
            <div class="mt-3 flex gap-2">
              <input
                v-model="arrivalPlate"
                type="text"
                placeholder="Plate number (e.g., 3-98765)"
                class="flex-1 px-3 py-2 text-sm border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
              <button
                @click="searchAppointment"
                :disabled="isSearching || !arrivalPlate"
                class="px-4 py-2 bg-gradient-to-r from-blue-600 to-indigo-600 text-white text-xs font-bold rounded-lg disabled:opacity-60"
              >
                {{ isSearching ? 'Searching...' : 'Search' }}
              </button>
            </div>
            <p v-if="arrivalError" class="mt-2 text-[10px] text-red-600">{{ arrivalError }}</p>

            <!-- Search Result Card -->
            <div v-if="arrivalResult" class="mt-4 bg-slate-50 border border-slate-200 rounded-xl p-3">
              <div class="flex justify-between items-start">
                <div>
                  <p class="text-xs text-slate-500">Appointment Found</p>
                  <p class="text-base font-mono font-bold text-slate-900">{{ arrivalResult.vehiclePlate }}</p>
                  <p class="text-[10px] text-slate-500 mt-1">
                    Order: {{ arrivalResult.orderUuid }}
                  </p>
                </div>
                <span class="text-[10px] px-2 py-0.5 rounded-full bg-blue-100 text-blue-700 font-bold">PARKING</span>
              </div>
              <div class="mt-3 flex justify-between items-center">
                <div class="text-[10px] text-slate-500">
                  Rate: ETB {{ arrivalResult.rateApplied || 0 }} / hr
                </div>
                <button
                  @click="acceptArrivalFromResult"
                  :disabled="isAccepting"
                  class="px-3 py-1.5 bg-gradient-to-r from-green-600 to-emerald-600 text-white text-[10px] font-bold rounded-lg disabled:opacity-60"
                >
                  {{ isAccepting ? 'Accepting...' : 'Accept Arrival' }}
                </button>
              </div>
            </div>
          </div>
          <!-- Connection Status Card -->
          <div class="bg-white rounded-xl sm:rounded-2xl border border-gray-200 p-4 sm:p-5 shadow-lg relative overflow-hidden">
            <div class="absolute top-0 right-0">
              <div class="bg-gradient-to-r from-blue-600 to-indigo-600 text-white text-[8px] sm:text-[10px] font-bold px-2 sm:px-3 py-1 rounded-bl-lg uppercase tracking-wider shadow-sm">
                Live PMS Sync
              </div>
            </div>
            
            <div class="mb-4 sm:mb-6">
              <h2 class="text-base sm:text-lg font-bold text-gray-800 flex items-center">
                <span class="flex h-2 w-2 sm:h-3 sm:w-3 rounded-full bg-green-500 mr-2 animate-pulse"></span>
                Database Status
              </h2>
              <p class="text-[10px] sm:text-xs text-gray-500 mt-1 sm:mt-2 flex items-center">
                <span class="inline-block w-2 h-2 bg-green-500 rounded-full mr-1"></span>
                Connected to PMS
              </p>
            </div>

            <!-- Sync Button -->
           <button 
  @click="fetchPendingFromPMS" 
  :disabled="isFetching" 
  class="w-full mr-12 p-3 sm:p-4 bg-gradient-to-br from-blue-600 to-indigo-700 rounded-xl sm:rounded-2xl text-white transition-all hover:shadow-xl active:scale-[0.98] disabled:opacity-70 disabled:cursor-not-allowed group"
>
  <div v-if="!isFetching" class="bg-white/20 p-1.5 sm:p-2 rounded-full mb-1.5 sm:mb-2 inline-block group-hover:bg-white/30 transition-colors">
    <svg class="h-4 w-4 sm:h-5 sm:w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
    </svg>
  </div>
  <svg v-else class="animate-spin h-5 w-5 sm:h-6 sm:w-6 mx-auto mb-1.5 sm:mb-2" fill="none" viewBox="0 0 24 24">
    <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
    <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
  </svg>
  <p class="font-bold text-xs uppercase tracking-wide">
    {{ isFetching ? 'Syncing...' : 'Sync Pending Vehicles' }}
  </p>
  <p class="text-blue-100 text-[10px] mt-0.5 opacity-80">
    Last sync: {{ lastRefreshTime }}
  </p>
</button>

            <!-- Quick Stats -->
            <div class="mt-4 grid grid-cols-2 gap-2">
              <div class="bg-gray-50 p-2 sm:p-3 rounded-lg">
                <p class="text-[10px] sm:text-xs text-gray-500">Active</p>
                <p class="text-lg sm:text-xl font-bold text-gray-800">{{ activeCars.length }}</p>
              </div>
              <div class="bg-gray-50 p-2 sm:p-3 rounded-lg">
                <p class="text-[10px] sm:text-xs text-gray-500">Today's Revenue</p>
                <p class="text-lg sm:text-xl font-bold text-green-600">ETB {{ calculateTodayRevenue() }}</p>
              </div>
            </div>
          </div>
        </div>

        <!-- Right Panel - Active Cars / History -->
        <div class="lg:col-span-2 space-y-4 sm:space-y-6">
          <!-- Tab Navigation -->
          <div class="bg-white rounded-xl sm:rounded-2xl border border-gray-200 p-1 flex shadow-lg">
            <button 
              @click="activeTab = 'active'" 
              :class="[
                activeTab === 'active' 
                  ? 'bg-gradient-to-r from-blue-600 to-indigo-600 text-white shadow-md' 
                  : 'text-gray-600 hover:text-gray-900',
                'flex-1 py-2 sm:py-3 rounded-lg sm:rounded-xl text-xs sm:text-sm font-bold transition-all duration-200'
              ]"
            >
              Active Vehicles ({{ activeCars.length }})
            </button>
            <button 
              @click="activeTab = 'completed'" 
              :class="[
                activeTab === 'completed' 
                  ? 'bg-gradient-to-r from-blue-600 to-indigo-600 text-white shadow-md' 
                  : 'text-gray-600 hover:text-gray-900',
                'flex-1 py-2 sm:py-3 rounded-lg sm:rounded-xl text-xs sm:text-sm font-bold transition-all duration-200'
              ]"
            >
              History ({{ recentScans.length }})
            </button>
          </div>

          <!-- Active Cars Tab -->
          <div v-show="activeTab === 'active'" class="space-y-3 sm:space-y-4">
            <div v-if="activeCars.length === 0" class="bg-white rounded-xl sm:rounded-2xl border border-gray-200 p-6 sm:p-8 text-center">
              <div class="inline-block p-3 bg-gray-100 rounded-full mb-3">
                <svg class="h-6 w-6 sm:h-8 sm:w-8 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
              </div>
              <p class="text-gray-500 text-sm sm:text-base">No active vehicles</p>
              <p class="text-xs text-gray-400 mt-1">Vehicles will appear here when they enter</p>
            </div>
            
            <div v-for="car in activeCars" :key="car.id" 
                 class="bg-white rounded-xl sm:rounded-2xl border border-gray-200 p-4 sm:p-5 shadow-sm hover:border-blue-300 hover:shadow-md transition-all duration-200">
              <div class="flex flex-col sm:flex-row sm:justify-between sm:items-start gap-3 sm:gap-4">
                <div class="flex space-x-3 sm:space-x-4">
                  <div class="h-10 w-10 sm:h-12 sm:w-12 bg-gradient-to-br from-blue-50 to-indigo-50 rounded-xl flex items-center justify-center font-bold text-blue-600 border border-blue-100">
                    {{ car.plate.split('-')[0] }}
                  </div>
                  <div>
                    <h3 class="text-lg sm:text-xl font-mono font-bold text-gray-900">{{ car.plate }}</h3>
                    <div class="flex flex-wrap items-center gap-2 text-[10px] sm:text-xs text-gray-500 mt-1">
                      <span class="flex items-center">
                        <svg class="h-3 w-3 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                        </svg>
                        In: {{ formatTime(car.entryTime) }}
                      </span>
                      <span class="bg-gray-100 px-2 py-0.5 rounded-full">ID: {{ car.cashierId }}</span>
                    </div>
                  </div>
                </div>
                <div class="sm:text-right flex sm:block justify-between items-center">
                  <p class="text-xl sm:text-2xl font-mono font-bold text-gray-800">{{ calculateDuration(car.entryTime) }}</p>
                  <p class="text-base sm:text-lg font-bold text-green-600">ETB {{ calculatePMSPrice(car) }}</p>
                </div>
              </div>
              <div class="mt-3 sm:mt-4">
                <button 
                  @click="openPaymentDemo(car)" 
                  class="w-full bg-gradient-to-r from-blue-600 to-indigo-600 text-white py-2 sm:py-3 rounded-lg sm:rounded-xl font-bold text-xs sm:text-sm hover:from-blue-700 hover:to-indigo-700 transition-all shadow-md hover:shadow-lg"
                >
                  Process Payment & Exit
                </button>
              </div>
            </div>
          </div>

          <!-- History Tab -->
          <div v-show="activeTab === 'completed'" class="space-y-2 sm:space-y-3">
            <div v-if="recentScans.length === 0" class="bg-white rounded-xl sm:rounded-2xl border border-gray-200 p-6 sm:p-8 text-center">
              <div class="inline-block p-3 bg-gray-100 rounded-full mb-3">
                <svg class="h-6 w-6 sm:h-8 sm:w-8 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
              </div>
              <p class="text-gray-500 text-sm sm:text-base">No history yet</p>
              <p class="text-xs text-gray-400 mt-1">Completed transactions will appear here</p>
            </div>
            
            <div v-for="(scan, index) in recentScans" :key="index" 
                 class="bg-white rounded-lg sm:rounded-xl border border-gray-200 p-3 sm:p-4 hover:border-gray-300 transition-all">
              <div class="flex justify-between items-center">
                <div class="flex items-center space-x-3">
                  <div class="h-8 w-8 bg-gray-100 rounded-lg flex items-center justify-center font-bold text-gray-600">
                    {{ scan.plate.split('-')[0] }}
                  </div>
                  <div>
                    <p class="font-mono font-bold text-sm sm:text-base text-gray-900">{{ scan.plate }}</p>
                    <div class="flex items-center gap-2 text-[10px] sm:text-xs text-gray-500">
                      <span>{{ scan.time }} • {{ scan.type }}</span>
                      <span class="px-1.5 py-0.5 rounded-full" :class="scan.paymentMethod === 'Telebirr' ? 'bg-green-100 text-green-700' : 'bg-blue-100 text-blue-700'">
                        {{ scan.paymentMethod }}
                      </span>
                    </div>
                  </div>
                </div>
                <div class="text-right">
                  <span class="text-xs font-bold text-gray-700">ETB {{ scan.amount }}</span>
                  <p class="text-[8px] text-gray-400">{{ scan.cashierId }}</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Payment Modal -->
    <Transition name="modal">
      <div v-if="selectedCar" class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-gray-900/60 backdrop-blur-sm" @click.self="selectedCar = null">
        <div class="bg-white rounded-2xl sm:rounded-3xl w-full max-w-md overflow-hidden shadow-2xl">
          <!-- Modal Header -->
          <div class="bg-gradient-to-r from-blue-600 to-indigo-600 p-4 sm:p-6 text-white text-center">
            <p class="text-[10px] sm:text-xs uppercase font-bold tracking-widest opacity-80 mb-1">Payment Summary</p>
            <h2 class="text-2xl sm:text-3xl font-mono font-black">{{ selectedCar.plate }}</h2>
          </div>
          
          <!-- Modal Body -->
          <div class="p-4 sm:p-6 space-y-3 sm:space-y-4">
            <div class="flex justify-between text-xs sm:text-sm border-b border-dashed border-gray-200 pb-2 sm:pb-3">
              <span class="text-gray-500">Duration</span>
              <span class="font-bold text-gray-800">{{ calculateDuration(selectedCar.entryTime) }}</span>
            </div>
            <div class="flex justify-between text-xs sm:text-sm border-b border-dashed border-gray-200 pb-2 sm:pb-3">
              <span class="text-gray-500">Rate Plan</span>
              <span class="font-bold text-gray-800">Standard Rate</span>
            </div>
            <div class="flex justify-between items-center pt-2">
              <span class="text-base sm:text-lg font-bold text-gray-800">Total Amount</span>
              <span class="text-2xl sm:text-3xl font-black text-blue-600">ETB {{ calculatePMSPrice(selectedCar) }}</span>
            </div>

            <!-- Payment Methods -->
            <div class="grid grid-cols-2 gap-2 sm:gap-3 mt-4 sm:mt-6">
              <!-- Cash Option -->
              <div 
                @click="selectedPaymentMethod = 'cash'"
                :class="[
                  selectedPaymentMethod === 'cash' 
                    ? 'border-2 border-blue-600 bg-blue-50' 
                    : 'border border-gray-200 hover:border-blue-300',
                  'p-2 sm:p-3 rounded-xl sm:rounded-2xl flex flex-col items-center cursor-pointer transition-all'
                ]"
              >
                <span class="text-[8px] sm:text-[10px] font-bold uppercase" :class="selectedPaymentMethod === 'cash' ? 'text-blue-600' : 'text-gray-500'">
                  Cash
                </span>
                <svg class="h-5 w-5 sm:h-6 sm:w-6 mt-1" :class="selectedPaymentMethod === 'cash' ? 'text-blue-600' : 'text-gray-400'" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 9V7a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2m2 4h10a2 2 0 002-2v-6a2 2 0 00-2-2H9a2 2 0 00-2 2v6a2 2 0 002 2zm7-5a2 2 0 11-4 0 2 2 0 014 0z" />
                </svg>
              </div>

              <!-- Telebirr Option - Now Working -->
              <div 
                @click="selectedPaymentMethod = 'telebirr'"
                :class="[
                  selectedPaymentMethod === 'telebirr' 
                    ? 'border-2 border-green-600 bg-green-50' 
                    : 'border border-gray-200 hover:border-green-300',
                  'p-2 sm:p-3 rounded-xl sm:rounded-2xl flex flex-col items-center cursor-pointer transition-all'
                ]"
              >
                <span class="text-[8px] sm:text-[10px] font-bold uppercase" :class="selectedPaymentMethod === 'telebirr' ? 'text-green-600' : 'text-gray-500'">
                  Telebirr
                </span>
                <!-- Telebirr Logo/SVG -->
                <div class="h-5 w-10 sm:h-6 sm:w-12 mt-1 flex items-center justify-center">
                  <svg class="h-4 w-8 sm:h-5 sm:w-10" :class="selectedPaymentMethod === 'telebirr' ? 'text-green-600' : 'text-gray-400'" viewBox="0 0 40 24" fill="currentColor">
                    <rect x="4" y="4" width="32" height="16" rx="4" :fill="selectedPaymentMethod === 'telebirr' ? '#059669' : '#9CA3AF'" />
                    <text x="10" y="17" font-family="Arial" font-size="8" fill="white" font-weight="bold">Telebirr</text>
                  </svg>
                </div>
              </div>
            </div>

            <!-- Telebirr Payment Modal (Shows when Telebirr is selected) -->
            <Transition name="slide-fade">
              <div v-if="selectedPaymentMethod === 'telebirr'" class="mt-4 p-4 bg-green-50 rounded-xl border border-green-200">
                <h4 class="text-sm font-bold text-green-800 mb-3">Pay with Telebirr</h4>
                
                <!-- QR Code Simulation -->
                <div class="flex justify-center mb-4">
                  <div class="bg-white p-3 rounded-xl shadow-sm">
                    <div class="grid grid-cols-4 gap-1">
                      <div v-for="i in 16" :key="i" 
                           class="w-2 h-2 sm:w-3 sm:h-3"
                           :class="Math.random() > 0.5 ? 'bg-black' : 'bg-gray-200'">
                      </div>
                    </div>
                  </div>
                </div>

                <!-- Payment Details -->
                <div class="space-y-2 text-sm">
                  <div class="flex justify-between">
                    <span class="text-green-700">Amount:</span>
                    <span class="font-bold text-green-800">ETB {{ selectedCar ? calculatePMSPrice(selectedCar) : 0 }}</span>
                  </div>
                  <div class="flex justify-between">
                    <span class="text-green-700">Merchant:</span>
                    <span class="font-bold text-green-800">Meskel Parking</span>
                  </div>
                  <div class="flex justify-between">
                    <span class="text-green-700">Reference:</span>
                    <span class="font-mono text-xs text-green-800">{{ generateReference() }}</span>
                  </div>
                </div>

                <!-- Instructions -->
                <div class="mt-4 text-xs text-green-700 bg-white p-3 rounded-lg border border-green-100">
                  <p class="font-bold mb-1">📱 How to pay:</p>
                  <ol class="list-decimal list-inside space-y-1">
                    <li>Open Telebirr app</li>
                    <li>Scan the QR code</li>
                    <li>Confirm payment</li>
                  </ol>
                </div>
              </div>
            </Transition>

            <!-- Confirm Button - Updated to handle both payment methods -->
            <button 
              @click="processPayment" 
              :disabled="isProcessing || !selectedPaymentMethod" 
              :class="[
                selectedPaymentMethod === 'telebirr' 
                  ? 'bg-gradient-to-r from-green-600 to-emerald-600 hover:from-green-700 hover:to-emerald-700' 
                  : 'bg-gradient-to-r from-blue-600 to-indigo-600 hover:from-blue-700 hover:to-indigo-700',
                'w-full text-white py-3 sm:py-4 rounded-xl sm:rounded-2xl font-black text-base sm:text-lg mt-4 transition-all shadow-lg disabled:opacity-70 disabled:cursor-not-allowed flex items-center justify-center'
              ]"
            >
              <span v-if="!isProcessing">
                {{ selectedPaymentMethod === 'telebirr' ? 'CONFIRM TELEBIRR PAYMENT' : 'CONFIRM CASH PAYMENT' }}
              </span>
              <svg v-else class="animate-spin h-5 w-5 sm:h-6 sm:w-6 text-white" fill="none" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
            </button>
            
            <!-- Cancel Button -->
            <button 
              @click="selectedCar = null; selectedPaymentMethod = null" 
              class="w-full text-gray-400 text-xs sm:text-sm font-bold py-2 uppercase tracking-widest hover:text-red-500 transition-colors"
            >
              Cancel
            </button>
          </div>
        </div>
      </div>
    </Transition>
  </div>
</template>

<script setup>
import { pmsApi } from '../api/pmsApi'
import { clientServiceApi } from '../api/clientServiceApi'
import { ref, onMounted } from 'vue'
import { useCashierPayments } from '../composables/useCashierPayments'

// State
const isFetching = ref(false)
const isProcessing = ref(false)
const lastRefreshTime = ref(new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }))
const activeTab = ref('active')
const selectedCar = ref(null)
const selectedPaymentMethod = ref(null)
const arrivalPlate = ref('')
const arrivalError = ref('')
const isAccepting = ref(false)
const isSearching = ref(false)
const arrivalResult = ref(null)

// Mock Data
const activeCars = ref([])

const { payments: recentScans, cashierId: displayCashierId, fetchPayments } = useCashierPayments()

// Methods
const calculatePMSPrice = (car) => {
  if (!car?.entryTime) return 0
  const diffInMinutes = Math.max(0, Math.floor((new Date() - new Date(car.entryTime)) / 60000))

  if (car.rateApplied) {
    const hours = diffInMinutes / 60
    return Math.max(0, Number((hours * car.rateApplied).toFixed(2)))
  }

  if (diffInMinutes >= 220) return 200
  if (diffInMinutes >= 205) return 175
  if (diffInMinutes >= 120) return 100
  if (diffInMinutes >= 60) return 50
  return 30
}

const calculateTodayRevenue = () => {
  const activeRevenue = activeCars.value.reduce((sum, car) => sum + calculatePMSPrice(car), 0)
  const historyRevenue = recentScans.value.reduce((sum, scan) => sum + (scan.amount || 0), 0)
  return activeRevenue + historyRevenue
}

const fetchPendingFromPMS = async () => {
  isFetching.value = true
  try {
    const response = await pmsApi.getPendingVehicles({ sinceMinutes: 2, limit: 20, refresh: true })
    const pmsCars = (response.data || []).map((v, idx) => ({
      id: idx + 1,
      plate: v.plateNo,
      entryTime: v.entryTime,
      cashierId: v.cashierId || displayCashierId.value
    }))
    const acceptedCars = await loadActiveOrders()
    const merged = [...acceptedCars, ...pmsCars].reduce((acc, car) => {
      const key = car.orderUuid || car.plate
      if (!acc.map.has(key)) {
        acc.map.add(key)
        acc.list.push(car)
      }
      return acc
    }, { map: new Set(), list: [] }).list
    activeCars.value = merged
    lastRefreshTime.value = new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
  } catch (error) {
    console.error('Failed to fetch PMS pending vehicles:', error)
  } finally {
    isFetching.value = false
  }
}

const openPaymentDemo = (car) => {
  selectedCar.value = car
  selectedPaymentMethod.value = null // Reset payment method when opening modal
}

const searchAppointment = async () => {
  arrivalError.value = ''
  arrivalResult.value = null
  if (!arrivalPlate.value) return
  isSearching.value = true
  try {
    const response = await clientServiceApi.findParkingAppointment(arrivalPlate.value)
    const data = response.data
    arrivalResult.value = {
      vehiclePlate: data.vehiclePlate || arrivalPlate.value.trim().toUpperCase(),
      orderUuid: data.orderUuid,
      rateApplied: data.rateApplied
    }
  } catch (error) {
    arrivalError.value = error?.response?.data?.message || error?.message || 'Failed to accept appointment'
  } finally {
    isSearching.value = false
  }
}

const acceptArrivalFromResult = () => {
  if (!arrivalResult.value) return
  isAccepting.value = true
  clientServiceApi.acceptParkingArrival(arrivalResult.value.vehiclePlate)
    .then((response) => {
      const data = response.data
      activeCars.value.unshift({
        id: Date.now(),
        plate: data.vehiclePlate || arrivalResult.value.vehiclePlate,
        entryTime: data.entryTime || new Date().toISOString(),
        cashierId: displayCashierId.value,
        orderUuid: data.orderUuid,
        rateApplied: data.rateApplied
      })
      arrivalPlate.value = ''
      arrivalResult.value = null
    })
    .catch((error) => {
      arrivalError.value = error?.response?.data?.message || error?.message || 'Failed to accept appointment'
    })
    .finally(() => {
      isAccepting.value = false
    })
}

const loadActiveOrders = async () => {
  try {
    const response = await clientServiceApi.getActiveOrders('PARKING')
    return (response.data || []).map((o, idx) => ({
      id: idx + 1,
      plate: o.vehiclePlate,
      entryTime: o.entryTime,
      cashierId: displayCashierId.value,
      orderUuid: o.orderUuid,
      rateApplied: o.rateApplied
    }))
  } catch (error) {
    console.error('Failed to load active orders:', error)
    return []
  }
}

const generateReference = () => {
  return 'TEB' + Math.random().toString(36).substring(2, 10).toUpperCase()
}

const processPayment = async () => {
  if (!selectedPaymentMethod.value) {
    alert('Please select a payment method')
    return
  }
  
  isProcessing.value = true
  
  try {
    const car = selectedCar.value
    const amount = calculatePMSPrice(car)
    const payload = {
      plateNo: car.plate,
      entryTime: car.entryTime,
      cashierId: displayCashierId.value,
      paymentMethod: selectedPaymentMethod.value === 'telebirr' ? 'TELEBIRR' : 'CASH',
      amount
    }

    await pmsApi.createPayment(payload)

    // Remove from active cars
    activeCars.value = activeCars.value.filter(c => c.id !== car.id)

    await fetchPayments()

    selectedCar.value = null
    selectedPaymentMethod.value = null

    alert(`Payment successful via ${payload.paymentMethod === 'TELEBIRR' ? 'Telebirr' : 'Cash'}`)
  } catch (error) {
    console.error('Payment failed:', error)
    alert('Payment failed. Please try again.')
  } finally {
    isProcessing.value = false
  }
}

const formatTime = (date) => {
  return new Date(date).toLocaleTimeString([], { 
    hour: '2-digit', 
    minute: '2-digit',
    hour12: true 
  })
}

const calculateDuration = (entryTime) => {
  const diff = Math.floor((new Date() - new Date(entryTime)) / 60000)
  const hours = Math.floor(diff / 60)
  const minutes = diff % 60
  return `${hours}h ${minutes.toString().padStart(2, '0')}m`
}


// Auto-refresh durations every minute
onMounted(() => {
  fetchPendingFromPMS()
  fetchPayments()
  setInterval(() => {
    // Force re-render to update durations
    activeCars.value = [...activeCars.value]
  }, 60000)
})
</script>

<style scoped>
.modal-enter-active,
.modal-leave-active {
  transition: opacity 0.3s ease;
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}

.modal-enter-active .bg-white,
.modal-leave-active .bg-white {
  transition: transform 0.3s ease;
}

.modal-enter-from .bg-white,
.modal-leave-to .bg-white {
  transform: scale(0.95);
}

.slide-fade-enter-active {
  transition: all 0.3s ease-out;
}

.slide-fade-leave-active {
  transition: all 0.2s cubic-bezier(1, 0.5, 0.8, 1);
}

.slide-fade-enter-from,
.slide-fade-leave-to {
  transform: translateY(-10px);
  opacity: 0;
}

/* Custom scrollbar */
::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 10px;
}

::-webkit-scrollbar-thumb {
  background: #cbd5e0;
  border-radius: 10px;
}

::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}
</style>
