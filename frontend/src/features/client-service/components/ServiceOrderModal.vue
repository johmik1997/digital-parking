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
        <div class="relative w-full bg-white rounded-t-2xl sm:rounded-3xl shadow-2xl sm:my-8 sm:max-w-4xl lg:max-w-5xl xl:max-w-6xl sm:w-full">
          
          <!-- Header with Gradient - Mobile Optimized -->
          <div class="relative bg-gradient-to-r from-[#3C3C9E] to-purple-600 px-4 sm:px-6 md:px-8 py-3 sm:py-4 md:py-5">
            <div class="flex justify-between items-start">
              <div class="flex items-center space-x-2 sm:space-x-3 md:space-x-4">
                <!-- Service Icon -->
                <div class="h-8 w-8 sm:h-10 sm:w-10 md:h-12 md:w-12 bg-white/20 backdrop-blur-sm rounded-lg sm:rounded-xl flex items-center justify-center">
                  <svg v-if="service?.type === 'CAR_WASH'" class="h-4 w-4 sm:h-5 sm:w-5 md:h-6 md:w-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                  </svg>
                  <svg v-else-if="service?.type === 'PARKING'" class="h-4 w-4 sm:h-5 sm:w-5 md:h-6 md:w-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" />
                  </svg>
                  <svg v-else class="h-4 w-4 sm:h-5 sm:w-5 md:h-6 md:w-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M14.828 14.828a4 4 0 01-5.656 0M9 10h.01M15 10h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                  </svg>
                </div>
                <div>
                  <h3 class="text-base sm:text-lg md:text-xl font-bold text-white">
                    {{ service?.name }}
                  </h3>
                  <p class="text-[11px] sm:text-xs md:text-sm text-white/80 mt-0.5 sm:mt-1 line-clamp-1">
                    {{ service?.description }}
                  </p>
                </div>
              </div>
              <button 
                @click="$emit('close')" 
                class="p-1 sm:p-1.5 md:p-2 hover:bg-white/20 rounded-lg transition-colors"
              >
                <svg class="h-3 w-3 sm:h-4 sm:w-4 md:h-5 md:w-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                </svg>
              </button>
            </div>
          </div>

          <!-- Body - Scrollable Content -->
          <div class="px-4 sm:px-6 md:px-8 py-4 sm:py-5 md:py-6 bg-gray-50 max-h-[75vh] sm:max-h-[70vh] overflow-y-auto">
            
            <!-- ========== CAR WASH ========== -->
            <div v-if="service.name?.toLowerCase().includes('car wash')" class="space-y-4 sm:space-y-5 md:space-y-6">
              <!-- Car wash content remains the same as before -->
              <div class="bg-white rounded-xl sm:rounded-2xl p-4 sm:p-5 md:p-6 border border-gray-100 shadow-sm">
                <div class="flex items-center space-x-2 mb-3 sm:mb-4">
                  <div class="h-6 w-6 sm:h-7 sm:w-7 md:h-8 md:w-8 bg-blue-100 rounded-full flex items-center justify-center">
                    <svg class="h-3 w-3 sm:h-3.5 sm:w-3.5 md:h-4 md:w-4 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                    </svg>
                  </div>
                  <h4 class="font-semibold text-gray-900 text-sm sm:text-base">Select Appointment</h4>
                  <span class="ml-auto text-[10px] sm:text-xs bg-blue-100 text-blue-700 px-2 py-1 rounded-full font-medium">
                    Required
                  </span>
                </div>
                
                <div class="space-y-3 sm:space-y-4">
                  <div>
                    <label class="block text-[10px] sm:text-xs font-medium text-gray-500 mb-1.5">
                      📅 Appointment Date
                    </label>
                    <input
                      v-model="formData.appointmentDate"
                      type="date"
                      :min="minDate"
                      class="w-full px-3 sm:px-4 py-2 sm:py-2.5 md:py-3 bg-white border border-gray-200 rounded-lg sm:rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                      required
                    />
                  </div>
                  
                  <div>
                    <label class="block text-[10px] sm:text-xs font-medium text-gray-500 mb-1.5">
                      ⏰ Appointment Time
                    </label>
                    <select
                      v-model="formData.appointmentTime"
                      class="w-full px-3 sm:px-4 py-2 sm:py-2.5 md:py-3 bg-white border border-gray-200 rounded-lg sm:rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent appearance-none"
                      required
                    >
                      <option value="" disabled selected>Select time slot</option>
                      <option v-for="time in availableTimeSlots" :key="time" :value="time">
                        {{ time }}
                      </option>
                    </select>
                  </div>
                </div>
              </div>

              <div class="bg-white rounded-xl sm:rounded-2xl p-4 sm:p-5 md:p-6 border border-gray-100 shadow-sm">
                <div class="flex items-center space-x-2 mb-3 sm:mb-4">
                  <div class="h-6 w-6 sm:h-7 sm:w-7 md:h-8 md:w-8 bg-gray-100 rounded-full flex items-center justify-center">
                    <svg class="h-3 w-3 sm:h-3.5 sm:w-3.5 md:h-4 md:w-4 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 17a2 2 0 11-4 0 2 2 0 014 0zM19 17a2 2 0 11-4 0 2 2 0 014 0z" />
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16V6a1 1 0 00-1-1H4a1 1 0 00-1 1v10a1 1 0 001 1h1m8-1a1 1 0 01-1 1H9m4-1V8a1 1 0 011-1h2.586a1 1 0 01.707.293l3.414 3.414a1 1 0 01.293.707V16a1 1 0 01-1 1h-1m-6-1a1 1 0 001 1h1M5 17a2 2 0 104 0m-4 0a2 2 0 114 0m6 0a2 2 0 104 0m-4 0a2 2 0 114 0" />
                    </svg>
                  </div>
                  <h4 class="font-semibold text-gray-900 text-sm sm:text-base">Vehicle Information</h4>
                </div>
                
                <div class="grid grid-cols-1 sm:grid-cols-2 gap-3 sm:gap-4">
                  <div>
                    <label class="block text-[10px] sm:text-xs font-medium text-gray-500 mb-1">License Plate</label>
                    <input
                      v-model="formData.vehiclePlate"
                      type="text"
                      placeholder="ABC-1234"
                      class="w-full px-2.5 sm:px-3 py-2 sm:py-2.5 text-sm border border-gray-200 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                    />
                  </div>
                  <div>
                    <label class="block text-[10px] sm:text-xs font-medium text-gray-500 mb-1">Vehicle Type</label>
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

              <div class="bg-white rounded-xl sm:rounded-2xl p-4 sm:p-5 md:p-6 border border-gray-100 shadow-sm">
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
                        class="h-3 w-3 sm:h-3.5 sm:w-3.5 md:h-4 md:w-4 text-blue-600 focus:ring-blue-500"
                      />
                      <div>
                        <p class="text-xs sm:text-sm font-medium text-gray-900">{{ pkg.label }}</p>
                        <p class="text-[10px] sm:text-xs text-gray-500">{{ pkg.description }}</p>
                      </div>
                    </div>
                    <span class="text-sm sm:text-base font-bold text-blue-600">+ETB {{ pkg.price }}</span>
                  </label>
                </div>
              </div>
            </div>

            <!-- ========== PARKING ========== -->
            <div v-else-if="serviceType === 'PARKING'" class="space-y-4 sm:space-y-5 md:space-y-6">
              <div
                ref="parkingSetupRef"
                class="overflow-hidden rounded-2xl sm:rounded-[28px] border border-slate-200 bg-white shadow-[0_22px_60px_rgba(15,23,42,0.08)]"
              >
                <div class="bg-gradient-to-r from-[#121b2f] via-[#1d2842] to-[#285279] px-4 sm:px-5 md:px-6 py-4 sm:py-5 text-white">
                  <div class="flex flex-col gap-3 xl:flex-row xl:items-end xl:justify-between">
                    <div>
                      <p class="text-[10px] sm:text-xs font-semibold uppercase tracking-[0.34em] text-sky-200/80">Parking Setup</p>
                      <h4 class="mt-1 sm:mt-2 text-lg sm:text-xl font-semibold">Reserve your slot before you arrive</h4>
                      <p class="mt-1 max-w-2xl text-xs sm:text-sm text-slate-200/80">
                        Set the vehicle, date, and arrival time first. Then use the visual slot picker below to choose the exact parking space you want.
                      </p>
                    </div>
                    <div class="inline-flex items-center gap-2 self-start rounded-full border border-white/10 bg-white/10 px-2 sm:px-3 py-1 text-[10px] sm:text-xs font-medium text-sky-100">
                      <span class="h-1.5 w-1.5 sm:h-2 sm:w-2 rounded-full bg-emerald-400"></span>
                      Guided parking enabled
                    </div>
                  </div>
                </div>

                <div class="grid gap-4 p-4 sm:p-5 md:p-6 xl:grid-cols-[minmax(0,1.1fr)_minmax(0,0.9fr)]">
                  <div class="grid gap-3 sm:gap-4 sm:grid-cols-2">
                    <div class="rounded-xl sm:rounded-[22px] border border-slate-200 bg-slate-50 p-3 sm:p-4">
                      <label class="block text-[10px] sm:text-xs font-semibold uppercase tracking-[0.22em] text-slate-500">Vehicle Plate</label>
                      <input
                        v-model="formData.vehiclePlate"
                        type="text"
                        placeholder="ABC-1234"
                        class="mt-2 sm:mt-3 w-full rounded-xl sm:rounded-2xl border border-slate-200 bg-white px-3 sm:px-4 py-2 sm:py-3 text-sm text-slate-800 focus:border-sky-500 focus:outline-none focus:ring-2 focus:ring-sky-500/20"
                      />
                    </div>

                    <div class="rounded-xl sm:rounded-[22px] border border-slate-200 bg-slate-50 p-3 sm:p-4">
                      <label class="block text-[10px] sm:text-xs font-semibold uppercase tracking-[0.22em] text-slate-500">Parking Date</label>
                      <input
                        v-model="formData.parkingDate"
                        type="date"
                        :min="minDate"
                        class="mt-2 sm:mt-3 w-full rounded-xl sm:rounded-2xl border border-slate-200 bg-white px-3 sm:px-4 py-2 sm:py-3 text-sm text-slate-800 focus:border-sky-500 focus:outline-none focus:ring-2 focus:ring-sky-500/20"
                        @change="fetchAvailableSlots"
                      />
                    </div>

                    <div class="rounded-xl sm:rounded-[22px] border border-slate-200 bg-slate-50 p-3 sm:p-4">
                      <label class="block text-[10px] sm:text-xs font-semibold uppercase tracking-[0.22em] text-slate-500">Duration</label>
                      <select
                        v-model="formData.duration"
                        class="mt-2 sm:mt-3 w-full rounded-xl sm:rounded-2xl border border-slate-200 bg-white px-3 sm:px-4 py-2 sm:py-3 text-sm text-slate-800 focus:border-sky-500 focus:outline-none focus:ring-2 focus:ring-sky-500/20"
                      >
                        <option value="1">1 hour</option>
                        <option value="2">2 hours</option>
                        <option value="4">4 hours</option>
                        <option value="8">8 hours</option>
                        <option value="24">24 hours</option>
                      </select>
                    </div>

                    <div class="rounded-xl sm:rounded-[22px] border border-slate-200 bg-slate-50 p-3 sm:p-4">
                      <p class="text-[10px] sm:text-xs font-semibold uppercase tracking-[0.22em] text-slate-500">Current Selection</p>
                      <p class="mt-2 sm:mt-3 text-sm sm:text-base font-semibold text-slate-900">
                        {{ parkingLocationDisplay || 'No parking slot selected yet' }}
                      </p>
                      <p class="mt-1 text-xs sm:text-sm text-slate-500">
                        {{ parkingInstructions || 'Pick a floor, zone, and slot to generate the guidance route.' }}
                      </p>
                    </div>
                  </div>

                  <div class="rounded-xl sm:rounded-[24px] border border-slate-200 bg-slate-50 p-3 sm:p-4 md:p-5">
                    <div class="flex flex-wrap items-center justify-between gap-2 sm:gap-3">
                      <div>
                        <p class="text-[10px] sm:text-xs font-semibold uppercase tracking-[0.22em] text-slate-500">Arrival Window</p>
                        <h4 class="mt-1 text-sm sm:text-base font-semibold text-slate-900">Available Time Slots</h4>
                      </div>
                      <span class="rounded-full bg-emerald-100 px-2 sm:px-3 py-0.5 sm:py-1 text-[10px] sm:text-xs font-medium text-emerald-700">
                        Capacity: {{ props.service?.slot ?? 0 }} per time
                      </span>
                    </div>

                    <div v-if="loadingAvailability" class="flex justify-center py-6 sm:py-8 md:py-10">
                      <div class="h-8 w-8 sm:h-10 sm:w-10 animate-spin rounded-full border-b-2 border-sky-600"></div>
                    </div>

                    <div v-else-if="timeAvailability.length === 0" class="rounded-xl sm:rounded-[20px] border border-dashed border-slate-300 bg-white px-3 sm:px-4 py-6 sm:py-8 text-center">
                      <svg class="mx-auto h-8 w-8 sm:h-10 sm:w-10 md:h-12 md:w-12 text-slate-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.8" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                      </svg>
                      <p class="mt-2 sm:mt-3 text-xs sm:text-sm text-slate-500">Select a parking date to load the available time slots.</p>
                    </div>

                    <div v-else class="mt-3 sm:mt-4 grid grid-cols-2 sm:grid-cols-3 gap-1.5 sm:gap-2">
                      <button
                        v-for="slot in timeAvailability"
                        :key="slot.entryTime"
                        type="button"
                        @click="formData.entryTime = slot.entryTime"
                        :disabled="!slot.available"
                        class="rounded-xl sm:rounded-2xl border px-2 sm:px-3 py-2 sm:py-3 text-left transition-all duration-200"
                        :class="[
                          formData.entryTime === slot.entryTime
                            ? 'border-sky-500 bg-sky-50 shadow-[0_10px_24px_rgba(14,116,244,0.12)]'
                            : slot.available
                              ? 'border-slate-200 bg-white hover:border-sky-300 hover:bg-sky-50/40'
                              : 'cursor-not-allowed border-slate-200 bg-slate-100 opacity-60'
                        ]"
                      >
                        <span class="block text-xs sm:text-sm font-semibold" :class="formData.entryTime === slot.entryTime ? 'text-sky-700' : 'text-slate-800'">
                          {{ slot.entryTime }}
                        </span>
                        <span class="mt-0.5 sm:mt-1 block text-[10px] sm:text-xs text-slate-500">{{ slot.remaining }} left</span>
                      </button>
                    </div>

                    <div class="mt-3 sm:mt-4 rounded-xl sm:rounded-[20px] bg-white px-3 sm:px-4 py-2.5 sm:py-3 ring-1 ring-slate-200">
                      <p class="text-[10px] sm:text-xs font-semibold uppercase tracking-[0.2em] text-slate-500">Schedule</p>
                      <p class="mt-1 text-xs sm:text-sm font-medium text-slate-900">{{ parkingScheduleSummary }}</p>
                    </div>
                  </div>
                </div>
              </div>

              <div class="mx-auto w-full max-w-4xl">
                <div
                  class="parking-phone-surface relative overflow-hidden rounded-2xl sm:rounded-[30px] bg-[#1b2235] p-3 sm:p-4 md:p-5 text-white shadow-[0_28px_80px_rgba(15,23,42,0.35)]"
                >
                  <div class="relative z-10">
                    <div class="flex items-center justify-between">
                      <button
                        type="button"
                        @click="scrollToParkingSetup"
                        class="inline-flex h-8 w-8 sm:h-9 sm:w-9 md:h-10 md:w-10 items-center justify-center rounded-xl sm:rounded-2xl border border-white/10 bg-white/5 text-slate-200 transition hover:bg-white/10"
                      >
                        <svg class="h-3 w-3 sm:h-4 sm:w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 18l-6-6 6-6" />
                        </svg>
                      </button>
                      <div class="text-center">
                        <p class="text-xs sm:text-sm font-medium text-slate-100">Choose Slot</p>
                      </div>
                      <span class="rounded-full border border-sky-400/30 bg-sky-400/10 px-2 sm:px-3 py-0.5 sm:py-1 text-[9px] sm:text-[11px] font-semibold uppercase tracking-[0.24em] text-sky-100">
                        Live
                      </span>
                    </div>

                    <div class="mt-4 sm:mt-5 md:mt-6 flex items-center justify-between gap-2 sm:gap-3">
                      <button
                        type="button"
                        @click="shiftParkingLevel(-1)"
                        :disabled="!canMoveParkingLevelBackward"
                        class="inline-flex h-8 w-8 sm:h-9 sm:w-9 md:h-10 md:w-10 items-center justify-center rounded-xl sm:rounded-2xl border border-white/10 bg-white/5 text-slate-200 transition hover:bg-white/10 disabled:cursor-not-allowed disabled:opacity-40"
                      >
                        <svg class="h-3 w-3 sm:h-4 sm:w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 18l-6-6 6-6" />
                        </svg>
                      </button>

                      <div class="text-center">
                        <h4 class="text-xl sm:text-2xl md:text-3xl font-semibold tracking-tight text-white">
                          {{ selectedParkingLevel?.label || 'Select Level' }}
                        </h4>
                        <p class="mt-0.5 sm:mt-1 text-xs sm:text-sm text-slate-400">{{ parkingAvailabilityCount }} Available</p>
                      </div>

                      <button
                        type="button"
                        @click="shiftParkingLevel(1)"
                        :disabled="!canMoveParkingLevelForward"
                        class="inline-flex h-8 w-8 sm:h-9 sm:w-9 md:h-10 md:w-10 items-center justify-center rounded-xl sm:rounded-2xl border border-white/10 bg-white/5 text-slate-200 transition hover:bg-white/10 disabled:cursor-not-allowed disabled:opacity-40"
                      >
                        <svg class="h-3 w-3 sm:h-4 sm:w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 6l6 6-6 6" />
                        </svg>
                      </button>
                    </div>

                    <div class="mt-4 sm:mt-5 flex flex-wrap justify-center gap-1.5 sm:gap-2">
                      <button
                        v-for="zone in availableParkingZones"
                        :key="zone"
                        type="button"
                        @click="formData.parkingZone = zone"
                        class="rounded-full border px-2 sm:px-3 md:px-4 py-1 sm:py-1.5 md:py-2 text-[10px] sm:text-xs font-semibold uppercase tracking-[0.22em] transition-all"
                        :class="formData.parkingZone === zone
                          ? 'border-sky-300 bg-sky-400/15 text-sky-100 shadow-[0_12px_28px_rgba(14,116,244,0.18)]'
                          : 'border-white/10 bg-white/5 text-slate-300 hover:border-sky-300/40 hover:text-white'"
                      >
                        Zone {{ zone }}
                      </button>
                    </div>

                    <div class="relative mt-4 sm:mt-5 md:mt-6">
                      <div v-if="parkingSlotCards.length" class="grid gap-2 sm:gap-3 grid-cols-1 sm:grid-cols-2">
                        <button
                          v-for="slot in parkingSlotCards"
                          :key="slot.code"
                          type="button"
                          @click="!slot.isReserved && (formData.selectedSlot = slot.code)"
                          :disabled="slot.isReserved"
                          class="relative overflow-hidden rounded-xl sm:rounded-[24px] border p-2.5 sm:p-3 md:p-4 text-left transition-all duration-200"
                          :class="[
                            isParkingSlotSelected(slot.code)
                              ? 'border-sky-400 bg-gradient-to-br from-[#153260] via-[#173a70] to-[#102546] shadow-[0_18px_40px_rgba(14,116,244,0.22)]'
                              : slot.isReserved
                                ? 'cursor-not-allowed border-white/10 bg-white/[0.02] opacity-55'
                                : 'border-white/10 bg-white/[0.04] hover:border-sky-300/40 hover:bg-white/[0.08]'
                          ]"
                        >
                          <div class="flex items-start justify-between gap-2 sm:gap-3">
                            <span
                              class="inline-flex items-center gap-1 sm:gap-2 text-[9px] sm:text-[11px] font-semibold uppercase tracking-[0.22em]"
                              :class="isParkingSlotSelected(slot.code) ? 'text-sky-100' : slot.isReserved ? 'text-rose-200' : 'text-slate-400'"
                            >
                              <span
                                class="h-1.5 w-1.5 sm:h-2 sm:w-2 rounded-full"
                                :class="isParkingSlotSelected(slot.code)
                                  ? 'bg-sky-400 shadow-[0_0_0_4px_rgba(59,130,246,0.18)]'
                                  : slot.isReserved
                                    ? 'bg-rose-400/90'
                                    : 'bg-emerald-400/90'"
                              ></span>
                              {{ isParkingSlotSelected(slot.code) ? 'Selected' : slot.isReserved ? 'Reserved' : 'Available' }}
                            </span>
                            <span class="text-[10px] sm:text-xs text-slate-500">{{ slot.sequenceLabel }}</span>
                          </div>

                          <div class="mt-3 sm:mt-4 md:mt-5 flex items-end justify-between gap-2 sm:gap-3">
                            <div>
                              <p class="text-lg sm:text-xl md:text-2xl font-semibold tracking-tight text-white">{{ slot.prettyCode }}</p>
                              <p class="mt-0.5 sm:mt-1 text-[10px] sm:text-xs" :class="isParkingSlotSelected(slot.code) ? 'text-sky-100/70' : slot.isReserved ? 'text-rose-100/80' : 'text-slate-400'">
                        {{ slot.isReserved ? 'Reserved during this booking window' : selectedParkingLevel?.entranceName || 'Entrance assigned' }}
                              </p>
                            </div>

                            <div
                              class="flex h-12 w-20 sm:h-14 sm:w-24 md:h-16 md:w-28 items-center justify-center rounded-xl sm:rounded-[20px] border"
                              :class="isParkingSlotSelected(slot.code)
                                ? 'border-white/15 bg-white/10'
                                : slot.isReserved
                                  ? 'border-white/5 bg-black/20'
                                  : 'border-white/10 bg-black/10'"
                            >
                              <svg
                                class="h-8 w-14 sm:h-9 sm:w-16 md:h-10 md:w-20 transition-transform duration-200"
                                :class="slot.sequenceLabel % 2 === 0 ? 'rotate-180' : ''"
                                :style="getParkingSlotCarStyle(slot)"
                                viewBox="0 0 104 52"
                                fill="none"
                                xmlns="http://www.w3.org/2000/svg"
                              >
                                <rect x="10" y="10" width="84" height="32" rx="12" fill="currentColor" opacity="0.18" />
                                <rect x="20" y="12" width="64" height="28" rx="10" fill="currentColor" />
                                <rect x="34" y="16" width="36" height="20" rx="7" fill="#E5EEF8" opacity="0.6" />
                                <circle cx="26" cy="17" r="5" fill="#111827" opacity="0.55" />
                                <circle cx="78" cy="17" r="5" fill="#111827" opacity="0.55" />
                                <circle cx="26" cy="35" r="5" fill="#111827" opacity="0.55" />
                                <circle cx="78" cy="35" r="5" fill="#111827" opacity="0.55" />
                              </svg>
                            </div>
                          </div>
                        </button>
                      </div>

                      <div
                        v-else
                        class="rounded-xl sm:rounded-[24px] border border-dashed border-white/10 bg-white/[0.03] px-4 sm:px-5 py-8 sm:py-10 md:py-12 text-center text-xs sm:text-sm text-slate-400"
                      >
                        Select a level and zone to load parking slots.
                      </div>
                    </div>

                    <div class="mt-4 sm:mt-5 rounded-xl sm:rounded-[24px] border border-white/10 bg-black/10 p-3 sm:p-4">
                      <div class="flex flex-col gap-2 sm:gap-3 sm:flex-row sm:items-center sm:justify-between">
                        <div>
                          <p class="text-[10px] sm:text-xs font-semibold uppercase tracking-[0.22em] text-slate-400">Selected Parking</p>
                          <p class="mt-0.5 sm:mt-1 text-sm sm:text-base font-semibold text-white">
                            {{ parkingLocationDisplay || 'Pick a slot to continue' }}
                          </p>
                          <p class="mt-0.5 sm:mt-1 text-xs sm:text-sm text-slate-400">{{ parkingEntranceName || 'Entrance will appear here' }}</p>
                        </div>
                        <div class="rounded-xl sm:rounded-[18px] border border-white/10 bg-white/5 px-3 sm:px-4 py-2 sm:py-3 text-left sm:text-right">
                          <p class="text-[10px] sm:text-xs font-semibold uppercase tracking-[0.22em] text-slate-400">Entry Time</p>
                          <p class="mt-0.5 sm:mt-1 text-sm sm:text-base font-semibold text-white">{{ formData.entryTime || '--:--' }}</p>
                        </div>
                      </div>
                    </div>

                  
                  </div>
                </div>
              </div>
            </div>
            
            <!-- ========== AMUSEMENT ========== -->
            <div v-else-if="service.name?.toLowerCase().includes('Meskel')" class="space-y-4 sm:space-y-5 md:space-y-6">
              <div class="bg-white rounded-xl sm:rounded-2xl p-4 sm:p-5 md:p-6 border border-gray-100 shadow-sm">
                <div class="flex items-center space-x-2 mb-3 sm:mb-4">
                  <div class="h-6 w-6 sm:h-7 sm:w-7 md:h-8 md:w-8 bg-purple-100 rounded-full flex items-center justify-center">
                    <svg class="h-3 w-3 sm:h-3.5 sm:w-3.5 md:h-4 md:w-4 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                    </svg>
                  </div>
                  <h4 class="font-semibold text-gray-900 text-sm sm:text-base">Select Visit Date</h4>
                </div>
                <input
                  v-model="formData.visitDate"
                  type="date"
                  :min="minDate"
                  class="w-full px-3 sm:px-4 py-2 sm:py-2.5 md:py-3 border border-gray-200 rounded-lg sm:rounded-xl text-sm focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                />
              </div>
              <div class="bg-white rounded-xl sm:rounded-2xl p-4 sm:p-5 md:p-6 border border-gray-100 shadow-sm">
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
                        class="h-3 w-3 sm:h-3.5 sm:w-3.5 md:h-4 md:w-4 text-purple-600"
                      />
                      <div>
                        <p class="text-xs sm:text-sm font-medium text-gray-900">{{ ticket.label }}</p>
                        <p class="text-[10px] sm:text-xs text-gray-500">{{ ticket.description }}</p>
                      </div>
                    </div>
                    <span class="text-sm sm:text-base font-bold text-purple-600">ETB {{ ticket.price }}</span>
                  </label>
                </div>
              </div>
              <div class="bg-white rounded-xl sm:rounded-2xl p-4 sm:p-5 md:p-6 border border-gray-100 shadow-sm">
                <h4 class="font-semibold text-gray-900 text-sm sm:text-base mb-3 sm:mb-4">Number of Tickets</h4>
                <div class="flex items-center justify-center space-x-2 sm:space-x-3 md:space-x-4">
                  <button
                    @click="formData.quantity = Math.max(1, formData.quantity - 1)"
                    class="h-8 w-8 sm:h-10 sm:w-10 md:h-12 md:w-12 bg-gray-100 rounded-full flex items-center justify-center hover:bg-gray-200 transition-colors"
                  >
                    <svg class="h-3 w-3 sm:h-4 sm:w-4 md:h-5 md:w-5 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 12H4" />
                    </svg>
                  </button>
                  <span class="text-xl sm:text-2xl md:text-3xl font-bold text-gray-900 w-10 sm:w-12 md:w-16 text-center">{{ formData.quantity }}</span>
                  <button
                    @click="formData.quantity = Math.min(10, formData.quantity + 1)"
                    class="h-8 w-8 sm:h-10 sm:w-10 md:h-12 md:w-12 bg-purple-100 rounded-full flex items-center justify-center hover:bg-purple-200 transition-colors"
                  >
                    <svg class="h-3 w-3 sm:h-4 sm:w-4 md:h-5 md:w-5 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
                    </svg>
                  </button>
                </div>
              </div>
              <div class="bg-white rounded-xl sm:rounded-2xl p-4 sm:p-5 md:p-6 border border-gray-100 shadow-sm">
                <h4 class="font-semibold text-gray-900 text-sm sm:text-base mb-3 sm:mb-4">Add-ons</h4>
                <div class="space-y-2">
                  <label v-for="addon in amusementAddons" :key="addon.value" 
                         class="flex items-center justify-between p-2 sm:p-2.5 md:p-3 border border-gray-200 rounded-lg">
                    <div class="flex items-center space-x-2 sm:space-x-3">
                      <input
                        type="checkbox"
                        :value="addon.value"
                        v-model="formData.addons"
                        class="h-3 w-3 sm:h-3.5 sm:w-3.5 md:h-4 md:w-4 text-purple-600 rounded"
                      />
                      <span class="text-xs sm:text-sm text-gray-700">{{ addon.label }}</span>
                    </div>
                    <span class="text-[10px] sm:text-xs md:text-sm text-purple-600 font-medium">+ETB {{ addon.price }}</span>
                  </label>
                </div>
              </div>
            </div>

            <div class="mt-3 sm:mt-4 md:mt-6">
              <label class="block text-[10px] sm:text-xs font-medium text-gray-700 mb-1 sm:mb-1.5 md:mb-2">
                Special Instructions (Optional)
              </label>
              <textarea
                v-model="formData.notes"
                rows="2"
                class="w-full px-3 sm:px-4 py-2 sm:py-2.5 md:py-3 text-sm border border-gray-200 rounded-lg sm:rounded-xl focus:ring-2 focus:ring-[#3C3C9E] focus:border-transparent"
                placeholder="Any special requests? 🎯"
              ></textarea>
            </div>

            <!-- Price Summary Card -->
            <div class="mt-4 sm:mt-5 md:mt-6 bg-gradient-to-br from-gray-50 to-white rounded-xl sm:rounded-2xl p-4 sm:p-5 md:p-6 border border-gray-200">
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
                    <span class="text-base sm:text-lg md:text-2xl font-bold bg-gradient-to-r from-[#3C3C9E] to-purple-600 bg-clip-text text-transparent">
                      ETB {{ totalPrice }}
                    </span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Footer Actions - Mobile Optimized -->
          <div ref="bookingFooterRef" class="px-4 sm:px-6 md:px-8 py-3 sm:py-4 bg-gray-50 border-t border-gray-200 flex flex-col-reverse sm:flex-row-reverse gap-2 sm:gap-3">
            <button
              @click="handleCreateOrder"
              :disabled="!isFormValid"
              class="max-w-full sm:w-auto inline-flex justify-center items-center px-4 sm:px-5 md:px-6 py-2.5 sm:py-3 md:py-3.5 bg-gradient-to-r from-[#3C3C9E] to-purple-600 text-white text-xs sm:text-sm md:text-base font-medium rounded-lg sm:rounded-xl shadow-lg shadow-[#3C3C9E]/30 hover:shadow-xl transform hover:-translate-y-0.5 transition-all duration-200 disabled:opacity-50 disabled:cursor-not-allowed disabled:transform-none"
            >
              <svg class="h-3 w-3 sm:h-4 sm:w-4 md:h-5 md:w-5 mr-1 sm:mr-1.5 md:mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
              </svg>
              Confirm Booking • ETB {{ totalPrice }}
            </button>
            <button
              @click="$emit('close')"
              class="max-w-full sm:w-auto px-4 sm:px-5 md:px-6 py-2.5 sm:py-3 md:py-3.5 bg-white border border-gray-300 text-gray-700 text-xs sm:text-sm md:text-base font-medium rounded-lg sm:rounded-xl hover:bg-gray-50 transition-colors"
            >
              Cancel
            </button>
          </div>
        </div>
      </transition>
    </div>
  </div>
</template>


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

/* Tablet and desktop optimizations */
@media (min-width: 1024px) {
  .overflow-y-auto {
    scrollbar-width: thin;
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

.parking-phone-surface::before {
  content: '';
  position: absolute;
  inset: 0;
  background:
    radial-gradient(circle at top left, rgba(77, 163, 255, 0.18), transparent 36%),
    radial-gradient(circle at bottom right, rgba(255, 122, 33, 0.12), transparent 28%);
  pointer-events: none;
}

/* Responsive padding and margin adjustments */
@media (max-width: 640px) {
  .parking-phone-surface {
    padding: 0.75rem;
  }
}

@media (min-width: 641px) and (max-width: 1024px) {
  .parking-phone-surface {
    padding: 1rem;
  }
}

/* Ensure smooth transitions */
button, input, select, textarea {
  transition: all 0.2s ease-in-out;
}
</style>

<script setup>
import { ref, reactive, computed, watch, nextTick } from 'vue'
import { clientServiceApi } from '../api/clientServiceApi'
import {
  PARKING_LEVELS,
  buildParkingInstructions,
  buildParkingLocationDisplay,
  getParkingSlots,
  getParkingZones,
  resolveEntranceName,
} from '../utils/parkingLayout'

const props = defineProps({
  show: { type: Boolean, default: false },
  service: { type: Object, default: null }
})

const emit = defineEmits(['close', 'create'])
const parkingSetupRef = ref(null)
const bookingFooterRef = ref(null)
const parkingLevels = PARKING_LEVELS
const parkingSlotPalette = ['#ff7a21', '#f1eadb', '#f1b43c', '#7b8698']

// Form Data
const formData = reactive({
  appointmentDate: '',
  appointmentTime: '',
  vehiclePlate: '',
  vehicleType: '',
  washPackage: 'standard',
  parkingDate: '',
  entranceName: '',
  parkingLevelType: '',
  parkingLevelCode: '',
  parkingZone: '',
  selectedSlot: '',
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

const inferServiceType = (service) => {
  if (service?.type) return service.type

  const name = service?.name?.toLowerCase() || ''
  if (name.includes('car wash')) return 'CAR_WASH'
  if (name.includes('parking')) return 'PARKING'
  if (name.includes('meskel') || name.includes('amusement')) return 'AMUSEMENT'
  return ''
}

// Computed
const serviceType = computed(() => inferServiceType(props.service))

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

const selectedParkingLevelIndex = computed(() => {
  const index = parkingLevels.findIndex(level => level.code === formData.parkingLevelCode)
  return index >= 0 ? index : 0
})

const selectedParkingLevel = computed(() => parkingLevels[selectedParkingLevelIndex.value] || null)

const availableParkingZones = computed(() => getParkingZones(formData.parkingLevelCode))

const availableParkingSlots = computed(() =>
  getParkingSlots(formData.parkingLevelCode, formData.parkingZone, props.service?.slot)
)

const reservedParkingSlotKeys = computed(() =>
  new Set((selectedTimeInfo.value?.reservedSlotKeys || []).map(key => `${key}`.toUpperCase()))
)

const parkingSlotCards = computed(() =>
  availableParkingSlots.value.map((slot, index) => ({
    ...slot,
    prettyCode: formatParkingSlotCode(slot.code),
    sequenceLabel: String(index + 1).padStart(2, '0'),
    isReserved: reservedParkingSlotKeys.value.has(
      buildParkingSlotKey(formData.parkingLevelCode, formData.parkingZone, slot.code)
    ),
    accentColor: parkingSlotPalette[index % parkingSlotPalette.length],
  }))
)

const parkingAvailabilityCount = computed(() =>
  parkingSlotCards.value.filter(slot => !slot.isReserved).length
)

const parkingEntranceName = computed(() =>
  resolveEntranceName({
    parkingLevelCode: formData.parkingLevelCode,
    parkingLevelType: formData.parkingLevelType,
    entranceName: formData.entranceName,
  })
)

const parkingLocationDisplay = computed(() =>
  buildParkingLocationDisplay({
    parkingLevelCode: formData.parkingLevelCode,
    parkingLevelType: formData.parkingLevelType,
    parkingZone: formData.parkingZone,
    selectedSlot: formData.selectedSlot,
  })
)

const parkingInstructions = computed(() =>
  buildParkingInstructions({
    entranceName: parkingEntranceName.value,
    parkingLevelCode: formData.parkingLevelCode,
    parkingLevelType: formData.parkingLevelType,
    parkingZone: formData.parkingZone,
    selectedSlot: formData.selectedSlot,
  })
)

const parkingScheduleSummary = computed(() => {
  if (!formData.parkingDate) return 'Choose a parking date to lock your arrival window.'
  const formattedDate = formatDate(formData.parkingDate)
  if (!formData.entryTime) return `${formattedDate} • Choose an entry time`
  return `${formattedDate} at ${formData.entryTime}`
})

const canMoveParkingLevelBackward = computed(() => selectedParkingLevelIndex.value > 0)

const canMoveParkingLevelForward = computed(() => selectedParkingLevelIndex.value < parkingLevels.length - 1)

const isSelectedTimeAvailable = computed(() => {
  if (!formData.entryTime) return false
  return selectedTimeInfo.value?.available === true
})

const isFormValid = computed(() => {
  if (!props.service) return false
  
  switch (serviceType.value) {
    case 'CAR_WASH':
      return formData.appointmentDate && 
             formData.appointmentTime && 
             formData.vehiclePlate && 
             formData.vehicleType
    
    case 'PARKING':
      return formData.parkingDate && 
             formData.entryTime &&
             formData.vehiclePlate && 
             formData.parkingLevelType &&
             formData.parkingLevelCode &&
             formData.parkingZone &&
             formData.selectedSlot &&
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
const selectParkingLevel = (level) => {
  if (!level) return
  formData.parkingLevelType = level.type
  formData.parkingLevelCode = level.code
  formData.entranceName = level.entranceName
}

const shiftParkingLevel = (direction) => {
  const nextIndex = selectedParkingLevelIndex.value + direction
  if (nextIndex < 0 || nextIndex >= parkingLevels.length) return
  selectParkingLevel(parkingLevels[nextIndex])
}

const fetchAvailableSlots = async () => {
  if (!formData.parkingDate || !props.service?.serviceUuid) return
  formData.entryTime = ''
  loadingAvailability.value = true
  try {
    const response = await clientServiceApi.getParkingAvailability(
      props.service.serviceUuid,
      formData.parkingDate,
      formData.duration
    )
    timeAvailability.value = response.data || []
  } catch (error) {
    console.error('Failed to fetch availability:', error)
    timeAvailability.value = []
  } finally {
    loadingAvailability.value = false
  }
}

const formatParkingSlotCode = (code) => {
  if (!code) return ''
  return code.replace(/^([A-Z]+)0+(\d+)$/, '$1$2')
}

const buildParkingSlotKey = (levelCode, zone, slotCode) => {
  if (!levelCode || !zone || !slotCode) return ''
  return `${levelCode}|${zone}|${slotCode}`.toUpperCase()
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

const isParkingSlotSelected = (slotCode) => formData.selectedSlot === slotCode

const getParkingSlotCarStyle = (slot) => ({
  color: slot.isReserved ? '#4b5563' : isParkingSlotSelected(slot.code) ? '#4DA3FF' : slot.accentColor,
  opacity: slot.isReserved ? 0.55 : 1,
})

const scrollToRef = (targetRef) => {
  nextTick(() => {
    targetRef.value?.scrollIntoView({ behavior: 'smooth', block: 'nearest' })
  })
}

const scrollToParkingSetup = () => scrollToRef(parkingSetupRef)
const scrollToBookingFooter = () => scrollToRef(bookingFooterRef)

const handleCreateOrder = () => {  
  if (!isFormValid.value) return
  const orderData = {
    serviceUuid: props.service?.serviceUuid || props.service?.id,
    serviceType: serviceType.value,
    serviceName: props.service?.name,
    ...formData,
    entranceName: parkingEntranceName.value,
    totalAmount: totalPrice.value,
    amount: totalPrice.value,
    orderDate: new Date().toISOString()
  }
  
  emit('create', orderData)
}

watch(() => formData.parkingLevelCode, (levelCode, previousLevelCode) => {
  if (!levelCode) {
    formData.parkingZone = ''
    formData.selectedSlot = ''
    return
  }

  const zones = getParkingZones(levelCode)
  if (!zones.includes(formData.parkingZone)) {
    formData.parkingZone = zones[0] || ''
  }

  if (levelCode !== previousLevelCode) {
    formData.selectedSlot = ''
  }
})

watch(() => formData.parkingZone, (zone, previousZone) => {
  if (zone !== previousZone) {
    formData.selectedSlot = ''
  }
})

watch(() => formData.entryTime, (entryTime, previousEntryTime) => {
  if (entryTime !== previousEntryTime) {
    formData.selectedSlot = ''
  }
})

watch(() => formData.duration, (duration, previousDuration) => {
  if (duration !== previousDuration && formData.parkingDate) {
    fetchAvailableSlots()
  }
})

watch(parkingSlotCards, (slots) => {
  const selectedSlot = slots.find(slot => slot.code === formData.selectedSlot)
  if (selectedSlot?.isReserved) {
    formData.selectedSlot = ''
  }
})

watch(() => props.show, (newVal) => {
  if (newVal) {
    Object.assign(formData, {
      appointmentDate: '',
      appointmentTime: '',
      vehiclePlate: '',
      vehicleType: '',
      washPackage: 'standard',
      parkingDate: '',
      entranceName: '',
      parkingLevelType: '',
      parkingLevelCode: '',
      parkingZone: '',
      selectedSlot: '',
      entryTime: '',
      duration: '4',
      visitDate: '',
      ticketType: 'adult',
      quantity: 1,
      addons: [],
      notes: ''
    })
    timeAvailability.value = []

    if (serviceType.value === 'PARKING' && parkingLevels.length) {
      selectParkingLevel(parkingLevels[0])
    }
  }
})
</script>
<!-- 
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

.parking-phone-surface::before {
  content: '';
  position: absolute;
  inset: 0;
  background:
    radial-gradient(circle at top left, rgba(77, 163, 255, 0.18), transparent 36%),
    radial-gradient(circle at bottom right, rgba(255, 122, 33, 0.12), transparent 28%);
  pointer-events: none;
}

.parking-slot-lane {
  background-image: linear-gradient(
    to bottom,
    rgba(255, 255, 255, 0.18) 0,
    rgba(255, 255, 255, 0.18) 14px,
    transparent 14px,
    transparent 30px
  );
  background-repeat: repeat-y;
  background-size: 1px 30px;
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
</style> -->
