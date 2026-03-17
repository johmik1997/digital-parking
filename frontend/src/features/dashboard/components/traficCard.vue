<template>  
  <div class="w-full h-full">  
    <div class="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-2 mb-4 sm:mb-6">
      <div>
        <h2 class="text-base sm:text-lg md:text-xl font-semibold text-gray-800">Daily Traffic</h2>  
        <p class="text-xs sm:text-sm text-gray-500 mt-0.5">Visitor analytics for today</p>
      </div>
      <div class="flex items-center gap-2 self-end sm:self-auto">
        <span class="text-xs bg-green-100 text-green-700 px-2 py-1 rounded-full font-medium flex items-center gap-1">
          <svg class="h-3 w-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 7h8m0 0v8m0-8l-8 8-4-4-6 6" />
          </svg>
          +2.45%
        </span>
      </div>
    </div>

    <!-- Main Stats -->
    <div class="flex flex-col sm:flex-row sm:items-baseline sm:justify-between gap-2 mb-4">
      <div class="flex items-baseline gap-2">
        <h1 class="text-2xl sm:text-3xl md:text-4xl font-bold text-gray-900">2,579</h1>  
        <p class="text-xs sm:text-sm text-gray-500">visitors</p>
      </div>
      <div class="flex items-center gap-3">
        <div class="flex items-center gap-1">
          <div class="w-2 h-2 bg-[#4318FF] rounded-full"></div>
          <span class="text-xs text-gray-600">Today</span>
        </div>
        <div class="flex items-center gap-1">
          <div class="w-2 h-2 bg-gray-300 rounded-full"></div>
          <span class="text-xs text-gray-600">Yesterday</span>
        </div>
      </div>
    </div>

    <!-- Bars Chart - Responsive Grid -->
    <div class="flex justify-between items-end gap-1 sm:gap-2 md:gap-3 mt-4 sm:mt-6">  
      <div 
        v-for="(data, index) in trafficData" 
        :key="index" 
        class="flex flex-col items-center flex-1 group"
      >  
        <!-- Bar Container -->
        <div class="relative w-full flex justify-center mb-2">
          <div 
            class="w-full max-w-[30px] sm:max-w-[40px] bg-gradient-to-t from-[#4318FF] to-[#6AD2FF] rounded-t-lg transition-all duration-500 group-hover:shadow-lg cursor-pointer"
            :style="{ 
              height: `${getBarHeight(data[0])}px`,
              opacity: isPeakHour(index) ? 1 : 0.8
            }"
          >
            <!-- Tooltip on hover -->
            <div class="absolute -top-8 left-1/2 transform -translate-x-1/2 bg-gray-900 text-white text-xs py-1 px-2 rounded opacity-0 group-hover:opacity-100 transition-opacity whitespace-nowrap">
              {{ data[0] }}k visitors
            </div>
          </div>
        </div>
        
        <!-- Time Label -->
        <span class="text-[10px] sm:text-xs text-gray-600 font-medium">{{ getTimeLabel(index) }}</span>
      </div>  
    </div>  

    <!-- Summary Stats -->
    <div class="grid grid-cols-3 gap-3 sm:gap-4 mt-6 sm:mt-8 pt-4 sm:pt-6 border-t border-gray-100">
      <div class="bg-gray-50 rounded-lg p-3 sm:p-4">
        <p class="text-xs text-gray-500">Peak Hour</p>
        <p class="text-sm sm:text-base font-bold text-gray-900 mt-1">14:00</p>
        <p class="text-xs text-green-600 mt-1">2.1k visitors</p>
      </div>
      <div class="bg-gray-50 rounded-lg p-3 sm:p-4">
        <p class="text-xs text-gray-500">Avg. Time</p>
        <p class="text-sm sm:text-base font-bold text-gray-900 mt-1">4m 32s</p>
        <p class="text-xs text-gray-600 mt-1">per visit</p>
      </div>
      <div class="bg-gray-50 rounded-lg p-3 sm:p-4">
        <p class="text-xs text-gray-500">Bounce Rate</p>
        <p class="text-sm sm:text-base font-bold text-gray-900 mt-1">32.4%</p>
        <p class="text-xs text-red-600 mt-1">-2.1%</p>
      </div>
    </div>

    <!-- Traffic Sources -->
    <div class="mt-4 sm:mt-6">
      <div class="flex justify-between items-center mb-3">
        <h3 class="text-sm font-medium text-gray-700">Traffic Sources</h3>
        <button class="text-xs text-[#4318FF] hover:underline">View Details</button>
      </div>
      <div class="space-y-2">
        <div class="flex items-center justify-between">
          <div class="flex items-center gap-2">
            <div class="w-2 h-2 bg-[#4318FF] rounded-full"></div>
            <span class="text-xs text-gray-600">Direct</span>
          </div>
          <span class="text-xs font-medium text-gray-900">45%</span>
        </div>
        <div class="flex items-center justify-between">
          <div class="flex items-center gap-2">
            <div class="w-2 h-2 bg-[#6AD2FF] rounded-full"></div>
            <span class="text-xs text-gray-600">Search</span>
          </div>
          <span class="text-xs font-medium text-gray-900">32%</span>
        </div>
        <div class="flex items-center justify-between">
          <div class="flex items-center gap-2">
            <div class="w-2 h-2 bg-purple-500 rounded-full"></div>
            <span class="text-xs text-gray-600">Social</span>
          </div>
          <span class="text-xs font-medium text-gray-900">23%</span>
        </div>
      </div>
    </div>
  </div>  
</template>  
  
<script setup>  
import { ref, computed } from 'vue';  

// Traffic data (in thousands)  
const trafficData = ref([  
  [3.2],  
  [4.1],  
  [5.8],  
  [6.5],  
  [7.2],  
  [8.1],  
  [9.5],  
  [8.8],  
  [7.4],  
  [6.2],  
  [5.1],  
  [4.2],
  [3.8],
  [3.2],
  [2.8],
  [2.5]
]);  

// Responsive bar height calculation
const getBarHeight = (value) => {
  // Scale based on screen size
  const baseHeight = Math.min(value * 12, 120);
  return baseHeight;
};

// Check if hour is peak (14:00 - 17:00)
const isPeakHour = (index) => {
  return index >= 6 && index <= 8;
};

// Get time labels (24-hour format)
const getTimeLabel = (index) => {
  const hour = (index + 8) % 24; // Start from 8 AM
  return `${hour.toString().padStart(2, '0')}:00`;
};
</script>  
  
<style scoped>  
/* Bar width responsiveness */
.flex-1 {
  min-width: 25px;
}

@media (min-width: 640px) {
  .flex-1 {
    min-width: 35px;
  }
}

@media (min-width: 768px) {
  .flex-1 {
    min-width: 45px;
  }
}

/* Smooth animations */
.transition-all {
  transition-property: all;
  transition-timing-function: cubic-bezier(0.4, 0, 0.2, 1);
  transition-duration: 500ms;
}

/* Tooltip animation */
.group-hover\:opacity-100 {
  transition: opacity 0.2s ease-in-out;
}

/* Bar hover effect */
.group:hover .bg-gradient-to-t {
  transform: scaleY(1.05);
  transform-origin: bottom;
}

/* Custom scrollbar for mobile */
@media (max-width: 640px) {
  .overflow-x-auto {
    -webkit-overflow-scrolling: touch;
  }
}
</style>