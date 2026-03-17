<template>  
  <div class="w-full">  
    <div class="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-2 mb-4 sm:mb-6">  
      <div>
        <h2 class="text-base sm:text-lg md:text-xl font-semibold text-gray-800">Weekly Revenue</h2>  
        <p class="text-xs sm:text-sm text-gray-500 mt-0.5">Last 9 days performance</p>
      </div>
      <button class="p-2 bg-gray-100 hover:bg-gray-200 rounded-lg transition-colors">  
        <span class="text-gray-600 text-sm">📈 Export</span>  
      </button>  
    </div>  
    
    <!-- Bars Container - Responsive Grid -->
    <div class="flex flex-wrap justify-start sm:justify-between items-end gap-3 sm:gap-2 md:gap-4">  
      <div 
        v-for="(data, index) in revenueData" 
        :key="index" 
        class="flex flex-col items-center flex-1 min-w-[35px] sm:min-w-[40px] max-w-[50px] sm:max-w-[60px]"
      >  
        <!-- Stacked Bars -->
        <div class="relative w-full flex flex-col items-center mb-2">  
          <!-- First Bar (Light Blue) -->
          <div 
            class="w-full bg-[#EFF4FB] rounded-t-lg transition-all duration-300 hover:opacity-80"  
            :style="{ height: `${getHeight(data[0])}px` }"
          ></div>  
          <!-- Second Bar (Blue) -->
          <div 
            class="w-full bg-[#6AD2FF] transition-all duration-300 hover:opacity-80"  
            :style="{ height: `${getHeight(data[1])}px` }"
          ></div>  
          <!-- Third Bar (Dark Blue) -->
          <div 
            class="w-full bg-[#4318FF] rounded-b-lg transition-all duration-300 hover:opacity-80"  
            :style="{ height: `${getHeight(data[2])}px` }"
          ></div>  
        </div>  
        
        <!-- Day Label -->
        <span class="text-xs sm:text-sm text-gray-600 font-medium">{{ getDayLabel(index) }}</span>
        
        <!-- Value Tooltip (on hover) -->
        <div class="text-[10px] sm:text-xs text-gray-400 mt-1 opacity-0 group-hover:opacity-100 transition-opacity">
          {{ getTotal(data) }}
        </div>
      </div>  
    </div>  
    
    <!-- Legend -->
    <div class="flex flex-wrap items-center gap-4 sm:gap-6 mt-6 pt-4 border-t border-gray-100">
      <div class="flex items-center gap-2">
        <div class="w-3 h-3 bg-[#4318FF] rounded-sm"></div>
        <span class="text-xs text-gray-600">Revenue</span>
      </div>
      <div class="flex items-center gap-2">
        <div class="w-3 h-3 bg-[#6AD2FF] rounded-sm"></div>
        <span class="text-xs text-gray-600">Expenses</span>
      </div>
      <div class="flex items-center gap-2">
        <div class="w-3 h-3 bg-[#EFF4FB] rounded-sm"></div>
        <span class="text-xs text-gray-600">Profit</span>
      </div>
    </div>
  </div>  
</template>  
  
<script setup>  
import { ref, computed } from 'vue';  

// Sample revenue data with three values for each bar  
const revenueData = ref([  
  [5, 3, 8],  
  [3, 5, 8],  
  [2, 4, 6],  
  [8, 7, 15],  
  [6, 4, 10],  
  [8, 10, 18],  
  [5, 5, 10],  
  [5, 7, 12],  
  [13, 6, 7]  
]);  

// Responsive height calculation
const getHeight = (value) => {
  // Scale based on screen size
  const baseHeight = Math.min(value * 4, 80);
  return baseHeight;
};

// Get day labels (17-25 or relative days)
const getDayLabel = (index) => {
  const days = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun', 'Mon', 'Tue'];
  return days[index] || `${17 + index}`;
};

// Get total for tooltip
const getTotal = (data) => {
  return `$${data.reduce((a, b) => a + b, 0)}`;
};
</script>  
  
<style scoped>  
/* Responsive bar container */
.flex-col {  
  min-width: 35px;
}  

@media (min-width: 640px) {
  .flex-col {
    min-width: 40px;
  }
}

@media (min-width: 768px) {
  .flex-col {
    min-width: 50px;
  }
}

/* Smooth transitions */
.transition-all {
  transition-property: all;
  transition-timing-function: cubic-bezier(0.4, 0, 0.2, 1);
  transition-duration: 300ms;
}

/* Group hover for tooltip */
.group:hover .group-hover\:opacity-100 {
  opacity: 1;
}
</style>