<template>
  <div v-if="show" class="fixed inset-0 z-50 overflow-y-auto">
    <!-- Backdrop with blur -->
    <div class="fixed inset-0 transition-opacity" @click="$emit('close')">
      <div class="absolute inset-0 bg-gray-900/60 backdrop-blur-sm"></div>
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
        <div class="relative w-full bg-white rounded-t-2xl sm:rounded-3xl shadow-2xl sm:my-8 sm:max-w-2xl sm:w-full">
          
          <!-- Header with Gradient - Mobile Optimized -->
          <div class="relative bg-gradient-to-r from-[#3C3C9E] to-purple-600 px-5 sm:px-6 py-4 sm:py-5">
            <div class="flex justify-between items-start">
              <div class="flex items-center space-x-3">
                <!-- Service Icon -->
                <div class="h-10 w-10 sm:h-12 sm:w-12 bg-white/20 backdrop-blur-sm rounded-xl flex items-center justify-center">
                  <svg v-if="service?.type === 'CAR_WASH'" class="h-5 w-5 sm:h-6 sm:w-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                  </svg>
                  <svg v-else-if="service?.type === 'PARKING'" class="h-5 w-5 sm:h-6 sm:w-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" />
                  </svg>
                  <svg v-else class="h-5 w-5 sm:h-6 sm:w-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M14.828 14.828a4 4 0 01-5.656 0M9 10h.01M15 10h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                  </svg>
                </div>
                <div>
                  <h3 class="text-lg sm:text-xl font-bold text-white">
                    {{ service?.name }}
                  </h3>
                  <p class="text-xs sm:text-sm text-white/80 mt-0.5 sm:mt-1 line-clamp-1">
                    {{ service?.description }}
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

          <!-- Body - Scrollable Content -->
          <div class="px-5 sm:px-6 py-5 sm:py-6 bg-gray-50 max-h-[75vh] sm:max-h-[70vh] overflow-y-auto">
            
            <!-- ========== CAR WASH ========== -->
            <div v-if="service.name?.toLowerCase().includes('car wash')" class="space-y-5 sm:space-y-6">
              
              <!-- Appointment Date & Time - VISIBLE NOW -->
              <div class="bg-white rounded-xl sm:rounded-2xl p-4 sm:p-6 border border-gray-100 shadow-sm">
                <div class="flex items-center space-x-2 mb-4">
                  <div class="h-7 w-7 sm:h-8 sm:w-8 bg-blue-100 rounded-full flex items-center justify-center">
                    <svg class="h-3.5 w-3.5 sm:h-4 sm:w-4 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                    </svg>
                  </div>
                  <h4 class="font-semibold text-gray-900 text-sm sm:text-base">Select Appointment</h4>
                  <span class="ml-auto text-xs bg-blue-100 text-blue-700 px-2 py-1 rounded-full font-medium">
                    Required
                  </span>
                </div>
                
                <div class="space-y-3 sm:space-y-4">
                  <!-- Date Picker -->
                  <div>
                    <label class="block text-xs font-medium text-gray-500 mb-1.5">
                      📅 Appointment Date
                    </label>
                    <input
                      v-model="formData.appointmentDate"
                      type="date"
                      :min="minDate"
                      class="w-full px-3 sm:px-4 py-2.5 sm:py-3 bg-white border border-gray-200 rounded-lg sm:rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                      required
                    />
                  </div>
                  
                  <!-- Time Picker -->
                  <div>
                    <label class="block text-xs font-medium text-gray-500 mb-1.5">
                      ⏰ Appointment Time
                    </label>
                    <select
                      v-model="formData.appointmentTime"
                      class="w-full px-3 sm:px-4 py-2.5 sm:py-3 bg-white border border-gray-200 rounded-lg sm:rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent appearance-none"
                      required
                    >
                      <option value="" disabled selected>Select time slot</option>
                      <option v-for="time in availableTimeSlots" :key="time" :value="time">
                        {{ time }}
                      </option>
                    </select>
                  </div>
                </div>

                <!-- Selected Appointment Summary -->
                <div v-if="formData.appointmentDate && formData.appointmentTime" class="mt-4 p-3 sm:p-4 bg-blue-50 rounded-lg sm:rounded-xl border border-blue-100">
                  <div class="flex items-center justify-between">
                    <div class="flex items-center space-x-2 sm:space-x-3">
                      <div class="h-8 w-8 sm:h-10 sm:w-10 bg-blue-100 rounded-full flex items-center justify-center">
                        <svg class="h-4 w-4 sm:h-5 sm:w-5 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
                        </svg>
                      </div>
                      <div>
                        <p class="text-xs sm:text-sm font-medium text-blue-800">Appointment Confirmed</p>
                        <p class="text-xs text-blue-600">{{ formatDate(formData.appointmentDate) }} at {{ formData.appointmentTime }}</p>
                      </div>
                    </div>
                    <button @click="formData.appointmentDate = ''; formData.appointmentTime = ''" class="text-xs text-blue-600 hover:text-blue-800">
                      Change
                    </button>
                  </div>
                </div>
              </div>

              <!-- Vehicle Details -->
              <div class="bg-white rounded-xl sm:rounded-2xl p-4 sm:p-6 border border-gray-100 shadow-sm">
                <div class="flex items-center space-x-2 mb-4">
                  <div class="h-7 w-7 sm:h-8 sm:w-8 bg-gray-100 rounded-full flex items-center justify-center">
                    <svg class="h-3.5 w-3.5 sm:h-4 sm:w-4 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 17a2 2 0 11-4 0 2 2 0 014 0zM19 17a2 2 0 11-4 0 2 2 0 014 0z" />
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16V6a1 1 0 00-1-1H4a1 1 0 00-1 1v10a1 1 0 001 1h1m8-1a1 1 0 01-1 1H9m4-1V8a1 1 0 011-1h2.586a1 1 0 01.707.293l3.414 3.414a1 1 0 01.293.707V16a1 1 0 01-1 1h-1m-6-1a1 1 0 001 1h1M5 17a2 2 0 104 0m-4 0a2 2 0 114 0m6 0a2 2 0 104 0m-4 0a2 2 0 114 0" />
                    </svg>
                  </div>
                  <h4 class="font-semibold text-gray-900 text-sm sm:text-base">Vehicle Information</h4>
                </div>
                
                <div class="grid grid-cols-1 sm:grid-cols-2 gap-3 sm:gap-4">
                  <div>
                    <label class="block text-xs font-medium text-gray-500 mb-1">License Plate</label>
                    <input
                      v-model="formData.vehiclePlate"
                      type="text"
                      placeholder="ABC-1234"
                      class="w-full px-2.5 sm:px-3 py-2 sm:py-2.5 text-sm border border-gray-200 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                    />
                  </div>
                  <div>
                    <label class="block text-xs font-medium text-gray-500 mb-1">Vehicle Type</label>
                    <select
                      v-model="formData.vehicleType"
                      class="w-full px-2.5 sm:px-3 py-2 sm:py-2.5 text-sm border border-gray-200 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                    >
                      <option value="">Select</option>
                      <option value="sedan">Sedan</option>
                      <option value="suv">SUV</option>
                      <option value="hatchback">Hatchback</option>
                      <option value="van">Van</option>
                    </select>
                  </div>
                </div>
              </div>

              <!-- Wash Package Selection -->
              <div class="bg-white rounded-xl sm:rounded-2xl p-4 sm:p-6 border border-gray-100 shadow-sm">
                <h4 class="font-semibold text-gray-900 text-sm sm:text-base mb-3 sm:mb-4">Choose Wash Package</h4>
                <div class="space-y-2 sm:space-y-3">
                  <label v-for="pkg in washPackages" :key="pkg.value" 
                         class="flex items-center justify-between p-3 sm:p-4 border-2 rounded-lg sm:rounded-xl cursor-pointer transition-all"
                         :class="formData.washPackage === pkg.value ? 'border-blue-500 bg-blue-50' : 'border-gray-200 hover:border-blue-200'">
                    <div class="flex items-center space-x-2 sm:space-x-3">
                      <input
                        type="radio"
                        :value="pkg.value"
                        v-model="formData.washPackage"
                        class="h-3.5 w-3.5 sm:h-4 sm:w-4 text-blue-600 focus:ring-blue-500"
                      />
                      <div>
                        <p class="text-xs sm:text-sm font-medium text-gray-900">{{ pkg.label }}</p>
                        <p class="text-xs text-gray-500">{{ pkg.description }}</p>
                      </div>
                    </div>
                    <span class="text-sm sm:text-base font-bold text-blue-600">+ETB {{ pkg.price }}</span>
                  </label>
                </div>
              </div>
            </div>

            <!-- ========== PARKING ========== -->
            <div v-else-if="service.name?.toLowerCase().includes('parking')" class="space-y-5 sm:space-y-6">
                <div class="bg-white rounded-xl sm:rounded-2xl p-4 sm:p-6 border border-gray-100 shadow-sm">
                <div class="flex items-center space-x-2 mb-4">
                  <div class="h-7 w-7 sm:h-8 sm:w-8 bg-green-100 rounded-full flex items-center justify-center">
                    <svg class="h-3.5 w-3.5 sm:h-4 sm:w-4 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                    </svg>
                  </div>
                  <h4 class="font-semibold text-gray-900 text-sm sm:text-base">Enter Vehicle Plate</h4>
                </div>
                <input
                  v-model="formData.vehiclePlate"
                  type="text"
                  class="w-full px-3 sm:px-4 py-2.5 sm:py-3 border border-gray-200 rounded-lg sm:rounded-xl text-sm focus:ring-2 focus:ring-green-500 focus:border-transparent"
                />
              </div>
              <!-- Date Selection -->

              <div class="bg-white rounded-xl sm:rounded-2xl p-4 sm:p-6 border border-gray-100 shadow-sm">
                <div class="flex items-center space-x-2 mb-4">
                  <div class="h-7 w-7 sm:h-8 sm:w-8 bg-green-100 rounded-full flex items-center justify-center">
                    <svg class="h-3.5 w-3.5 sm:h-4 sm:w-4 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                    </svg>
                  </div>
                  <h4 class="font-semibold text-gray-900 text-sm sm:text-base">Select Parking Date</h4>
                </div>
                <input
                  v-model="formData.parkingDate"
                  type="date"
                  :min="minDate"
                  class="w-full px-3 sm:px-4 py-2.5 sm:py-3 border border-gray-200 rounded-lg sm:rounded-xl text-sm focus:ring-2 focus:ring-green-500 focus:border-transparent"
                  @change="fetchAvailableSlots"
                />
              </div>
              <div class="bg-white rounded-xl sm:rounded-2xl p-4 sm:p-6 border border-gray-100 shadow-sm">
                <div class="flex flex-wrap justify-between items-center mb-4">
                  <h4 class="font-semibold text-gray-900 text-sm sm:text-base">Available Time Slots</h4>
                  <span class="text-xs bg-green-100 text-green-700 px-2 py-1 rounded-full">
                    Capacity: {{ props.service?.slot ?? 0 }} per time
                  </span>
                </div>

                <div v-if="loadingAvailability" class="flex justify-center py-6 sm:py-8">
                  <div class="animate-spin rounded-full h-8 w-8 sm:h-10 sm:w-10 border-b-2 border-green-600"></div>
                </div>

                <div v-else-if="timeAvailability.length === 0" class="text-center py-6 sm:py-8 bg-gray-50 rounded-lg sm:rounded-xl">
                  <svg class="h-10 w-10 sm:h-12 sm:w-12 text-gray-400 mx-auto mb-2 sm:mb-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                  </svg>
                  <p class="text-xs sm:text-sm text-gray-500">Select a date to load availability</p>
                </div>

                <div v-else class="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 gap-2 sm:gap-3">
                  <button
                    v-for="slot in timeAvailability"
                    :key="slot.entryTime"
                    @click="formData.entryTime = slot.entryTime"
                    :disabled="!slot.available"
                    class="relative p-2 sm:p-3 rounded-lg sm:rounded-xl border-2 transition-all"
                    :class="[
                      formData.entryTime === slot.entryTime
                        ? 'border-green-600 bg-green-50'
                        : slot.available
                          ? 'border-gray-200 hover:border-green-400 hover:bg-green-50/50'
                          : 'border-gray-200 bg-gray-100 cursor-not-allowed opacity-60'
                    ]"
                  >
                    <div class="text-center">
                      <span class="block font-bold text-sm sm:text-base" :class="formData.entryTime === slot.entryTime ? 'text-green-700' : 'text-gray-700'">
                        {{ slot.entryTime }}
                      </span>
                      <span class="block text-xs text-gray-500 mt-0.5">
                        {{ slot.remaining }} left
                      </span>
                    </div>
                  </button>
                </div>

                <p v-if="formData.entryTime && selectedTimeInfo" class="mt-3 text-xs text-gray-600">
                  Selected time: {{ formData.entryTime }} • Remaining: {{ selectedTimeInfo.remaining }}
                </p>
              </div>

              <div class="bg-white rounded-xl sm:rounded-2xl p-4 sm:p-6 border border-gray-100 shadow-sm">
                <div class="grid grid-cols-1 sm:grid-cols-2 gap-3 sm:gap-4">
                  <div>
                    <label class="block text-xs font-medium text-gray-500 mb-1.5">Duration</label>
                    <select
                      v-model="formData.duration"
                      class="w-full px-2.5 sm:px-3 py-2 sm:py-2.5 text-sm border border-gray-200 rounded-lg focus:ring-2 focus:ring-green-500"
                    >
                      <option value="1">1 hour</option>
                      <option value="2">2 hours</option>
                      <option value="4">4 hours</option>
                      <option value="8">8 hours</option>
                      <option value="24">24 hours</option>
                    </select>
                  </div>
                </div>
              </div>
            </div>
            <div v-else-if="service.name?.toLowerCase().includes('Meskel')" class="space-y-5 sm:space-y-6">
              <div class="bg-white rounded-xl sm:rounded-2xl p-4 sm:p-6 border border-gray-100 shadow-sm">
                <div class="flex items-center space-x-2 mb-4">
                  <div class="h-7 w-7 sm:h-8 sm:w-8 bg-purple-100 rounded-full flex items-center justify-center">
                    <svg class="h-3.5 w-3.5 sm:h-4 sm:w-4 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                    </svg>
                  </div>
                  <h4 class="font-semibold text-gray-900 text-sm sm:text-base">Select Visit Date</h4>
                </div>
                <input
                  v-model="formData.visitDate"
                  type="date"
                  :min="minDate"
                  class="w-full px-3 sm:px-4 py-2.5 sm:py-3 border border-gray-200 rounded-lg sm:rounded-xl text-sm focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                />
              </div>
              <div class="bg-white rounded-xl sm:rounded-2xl p-4 sm:p-6 border border-gray-100 shadow-sm">
                <h4 class="font-semibold text-gray-900 text-sm sm:text-base mb-3 sm:mb-4">Ticket Type</h4>
                <div class="space-y-2 sm:space-y-3">
                  <label v-for="ticket in ticketTypes" :key="ticket.value"
                         class="flex items-center justify-between p-3 sm:p-4 border-2 rounded-lg sm:rounded-xl cursor-pointer"
                         :class="formData.ticketType === ticket.value ? 'border-purple-500 bg-purple-50' : 'border-gray-200'">
                    <div class="flex items-center space-x-2 sm:space-x-3">
                      <input
                        type="radio"
                        :value="ticket.value"
                        v-model="formData.ticketType"
                        class="h-3.5 w-3.5 sm:h-4 sm:w-4 text-purple-600"
                      />
                      <div>
                        <p class="text-xs sm:text-sm font-medium text-gray-900">{{ ticket.label }}</p>
                        <p class="text-xs text-gray-500">{{ ticket.description }}</p>
                      </div>
                    </div>
                    <span class="text-sm sm:text-base font-bold text-purple-600">ETB {{ ticket.price }}</span>
                  </label>
                </div>
              </div>
              <div class="bg-white rounded-xl sm:rounded-2xl p-4 sm:p-6 border border-gray-100 shadow-sm">
                <h4 class="font-semibold text-gray-900 text-sm sm:text-base mb-3 sm:mb-4">Number of Tickets</h4>
                <div class="flex items-center justify-center space-x-3 sm:space-x-4">
                  <button
                    @click="formData.quantity = Math.max(1, formData.quantity - 1)"
                    class="h-10 w-10 sm:h-12 sm:w-12 bg-gray-100 rounded-full flex items-center justify-center hover:bg-gray-200 transition-colors"
                  >
                    <svg class="h-4 w-4 sm:h-5 sm:w-5 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 12H4" />
                    </svg>
                  </button>
                  <span class="text-2xl sm:text-3xl font-bold text-gray-900 w-12 sm:w-16 text-center">{{ formData.quantity }}</span>
                  <button
                    @click="formData.quantity = Math.min(10, formData.quantity + 1)"
                    class="h-10 w-10 sm:h-12 sm:w-12 bg-purple-100 rounded-full flex items-center justify-center hover:bg-purple-200 transition-colors"
                  >
                    <svg class="h-4 w-4 sm:h-5 sm:w-5 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
                    </svg>
                  </button>
                </div>
              </div>
              <div class="bg-white rounded-xl sm:rounded-2xl p-4 sm:p-6 border border-gray-100 shadow-sm">
                <h4 class="font-semibold text-gray-900 text-sm sm:text-base mb-3 sm:mb-4">Add-ons</h4>
                <div class="space-y-2">
                  <label v-for="addon in amusementAddons" :key="addon.value" 
                         class="flex items-center justify-between p-2.5 sm:p-3 border border-gray-200 rounded-lg">
                    <div class="flex items-center space-x-2 sm:space-x-3">
                      <input
                        type="checkbox"
                        :value="addon.value"
                        v-model="formData.addons"
                        class="h-3.5 w-3.5 sm:h-4 sm:w-4 text-purple-600 rounded"
                      />
                      <span class="text-xs sm:text-sm text-gray-700">{{ addon.label }}</span>
                    </div>
                    <span class="text-xs sm:text-sm text-purple-600 font-medium">+ETB {{ addon.price }}</span>
                  </label>
                </div>
              </div>
            </div>

            <div class="mt-4 sm:mt-6">
              <label class="block text-xs font-medium text-gray-700 mb-1.5 sm:mb-2">
                Special Instructions (Optional)
              </label>
              <textarea
                v-model="formData.notes"
                rows="2"
                class="w-full px-3 sm:px-4 py-2 sm:py-3 text-sm border border-gray-200 rounded-lg sm:rounded-xl focus:ring-2 focus:ring-[#3C3C9E] focus:border-transparent"
                placeholder="Any special requests? 🎯"
              ></textarea>
            </div>

            <!-- Price Summary Card -->
            <div class="mt-5 sm:mt-6 bg-gradient-to-br from-gray-50 to-white rounded-xl sm:rounded-2xl p-4 sm:p-6 border border-gray-200">
              <h4 class="font-semibold text-gray-900 text-sm sm:text-base mb-3 sm:mb-4">Price Summary</h4>
              <div class="space-y-2 sm:space-y-3">
                <div class="flex justify-between text-xs sm:text-sm">
                  <span class="text-gray-600">Base Price</span>
                  <span class="font-medium text-gray-900">ETB {{ service?.currentRate || 0 }}</span>
                </div>
                <div v-if="additionalPrice > 0" class="flex justify-between text-xs sm:text-sm">
                  <span class="text-gray-600">Add-ons</span>
                  <span class="font-medium text-green-600">+ETB {{ additionalPrice }}</span>
                </div>
                <div class="pt-2 sm:pt-3 border-t border-gray-200">
                  <div class="flex justify-between items-center">
                    <span class="text-sm sm:text-base font-semibold text-gray-900">Total Amount</span>
                    <span class="text-lg sm:text-2xl font-bold bg-gradient-to-r from-[#3C3C9E] to-purple-600 bg-clip-text text-transparent">
                      ETB {{ totalPrice }}
                    </span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Footer Actions - Mobile Optimized -->
          <div class="px-5 sm:px-6 py-4 bg-gray-50 border-t border-gray-200 flex flex-col-reverse sm:flex-row-reverse gap-2 sm:gap-3">
            <button
              @click="handleCreateOrder"
              :disabled="!isFormValid"
              class="max-w-full sm:w-auto inline-flex justify-center items-center px-5 sm:px-6 py-3 sm:py-3.5 bg-gradient-to-r from-[#3C3C9E] to-purple-600 text-white text-sm sm:text-base font-medium rounded-lg sm:rounded-xl shadow-lg shadow-[#3C3C9E]/30 hover:shadow-xl transform hover:-translate-y-0.5 transition-all duration-200 disabled:opacity-50 disabled:cursor-not-allowed disabled:transform-none"
            >
              <svg class="h-4 w-4 sm:h-5 sm:w-5 mr-1.5 sm:mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
              </svg>
              Confirm Booking • ETB {{ totalPrice }}
            </button>
            <button
              @click="$emit('close')"
              class="max-w-full sm:w-auto px-5 sm:px-6 py-3 sm:py-3.5 bg-white border border-gray-300 text-gray-700 text-sm sm:text-base font-medium rounded-lg sm:rounded-xl hover:bg-gray-50 transition-colors"
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
import { clientServiceApi } from '../api/clientServiceApi'

const props = defineProps({
  show: { type: Boolean, default: false },
  service: { type: Object, default: null }
})

const emit = defineEmits(['close', 'create'])

// Form Data
const formData = reactive({
  appointmentDate: '',
  appointmentTime: '',
  vehiclePlate: '',
  vehicleType: '',
  washPackage: 'standard',
  parkingDate: '',
  entryTime: '',
  duration: '4',
  visitDate: '',
  ticketType: 'adult',
  quantity: 1,
  addons: [],
  notes: ''
})

const timeAvailability = ref([])
const loadingAvailability = ref(false)

// Mock Data
const availableTimeSlots = [
  '09:00', '10:00', '11:00', '12:00', '13:00', '14:00', '15:00', '16:00', '17:00'
]

const washPackages = [
  { value: 'basic', label: 'Basic Wash', description: 'Exterior wash & dry', price: 0 },
  { value: 'standard', label: 'Standard Wash', description: 'Exterior + interior vacuum', price: 10 },
  { value: 'premium', label: 'Premium Wash', description: 'Full detail + wax', price: 25 }
]

const ticketTypes = [
  { value: 'adult', label: 'Adult', description: 'Ages 13-64', price: 45 },
  { value: 'child', label: 'Child', description: 'Ages 3-12', price: 25 },
  { value: 'senior', label: 'Senior', description: 'Ages 65+', price: 35 }
]

const amusementAddons = [
  { value: 'fastPass', label: 'Fast Pass - Skip the lines!', price: 30 },
  { value: 'mealDeal', label: 'Meal Deal - Lunch included', price: 20 },
  { value: 'parking', label: 'Preferred Parking', price: 15 }
]


// Computed
const minDate = computed(() => {
  const today = new Date()
  return today.toISOString().split('T')[0]
})

const totalPrice = computed(() => {
  let base = props.service?.currentRate || 0

  if (props.service?.pricingType === 'HOURLY') {
    const hours = Number(formData.duration) || 0
    base = base * (hours > 0 ? hours : 0)
  } else if (props.service.name?.toLowerCase().includes('Meskel')) {
    const ticket = ticketTypes.find(t => t.value === formData.ticketType)
    base = (ticket?.price || 45) * formData.quantity
  }

  return base + additionalPrice.value
})

const additionalPrice = computed(() => {
  let total = 0
  
  if (props.service.name?.toLowerCase().includes('car wash')) {
    const pkg = washPackages.find(p => p.value === formData.washPackage)
    total += pkg?.price || 0
  }
  
  if (props.service.name?.toLowerCase().includes('Meskel')) {
    formData.addons.forEach(addon => {
      const item = amusementAddons.find(a => a.value === addon)
      total += item?.price || 0
    })
  }
  
  return total
})

const selectedTimeInfo = computed(() => {
  return timeAvailability.value.find(t => t.entryTime === formData.entryTime) || null
})

const isSelectedTimeAvailable = computed(() => {
  if (!formData.entryTime) return false
  return selectedTimeInfo.value?.available === true
})

const isFormValid = computed(() => {
  if (!props.service) return false
  
  switch (props.service.type) {
    case 'CAR_WASH':
      return formData.appointmentDate && 
             formData.appointmentTime && 
             formData.vehiclePlate && 
             formData.vehicleType
    
    case 'PARKING':
      return formData.parkingDate && 
             formData.entryTime &&
            formData.vehiclePlate && 
             isSelectedTimeAvailable.value
    
    case 'AMUSEMENT':
      return formData.visitDate && 
             formData.ticketType && 
             formData.quantity > 0
    
    default:
      return true
  }
})

// Methods
const fetchAvailableSlots = async () => {
  if (!formData.parkingDate || !props.service?.serviceUuid) return
  formData.entryTime = ''
  loadingAvailability.value = true
  try {
    const response = await clientServiceApi.getParkingAvailability(
      props.service.serviceUuid,
      formData.parkingDate
    )
    timeAvailability.value = response.data || []
  } catch (error) {
    console.error('Failed to fetch availability:', error)
    timeAvailability.value = []
  } finally {
    loadingAvailability.value = false
  }
}

const formatDate = (date) => {
  if (!date) return ''
  const d = new Date(date)
  return d.toLocaleDateString('en-US', { 
    weekday: 'short', 
    month: 'short', 
    day: 'numeric' 
  })
}

const handleCreateOrder = () => {  
  if (!isFormValid.value) return
  const orderData = {
    serviceUuid: props.service?.serviceUuid || props.service?.id,
    serviceType: props.service?.type,
    serviceName: props.service?.name,
    ...formData,
    totalAmount: totalPrice.value,
    amount: totalPrice.value,
    orderDate: new Date().toISOString()
  }
  
  emit('create', orderData)
}

watch(() => props.show, (newVal) => {
  if (newVal) {
    Object.assign(formData, {
      appointmentDate: '',
      appointmentTime: '',
      vehiclePlate: '',
      vehicleType: '',
      washPackage: 'standard',
      parkingDate: '',
      entryTime: '',
      duration: '4',
      visitDate: '',
      ticketType: 'adult',
      quantity: 1,
      addons: [],
      notes: ''
    })
    timeAvailability.value = []
  }
})
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

/* Hide scrollbar for cleaner look */
.overflow-y-auto {
  -webkit-overflow-scrolling: touch;
  scrollbar-width: thin;
}

.overflow-y-auto::-webkit-scrollbar {
  width: 4px;
}

.overflow-y-auto::-webkit-scrollbar-track {
  background: #f1f1f1;
}

.overflow-y-auto::-webkit-scrollbar-thumb {
  background: #cbd5e0;
  border-radius: 4px;
}
</style>
