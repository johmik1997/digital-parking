<script setup lang="ts">
import { Chart as ChartJS, ArcElement, Tooltip, Legend } from 'chart.js'
import { Doughnut } from 'vue-chartjs'

ChartJS.register(ArcElement, Tooltip, Legend)

const chartdata = {
    datasets: [{
        label: 'My First Dataset',
        data: [63, 21, 16],
        backgroundColor: [
            '#4318FF',
            '#6AD2FF',
            '#EFF4FB'
        ],
        hoverOffset: 4,
        borderWidth: 0
    }]
};

const options = {
    cutout: '70%',
    plugins: {
        legend: {
            display: false
        },
        tooltip: {
            enabled: true
        }
    },
    maintainAspectRatio: false,
    responsive: true
};
</script>

<template>
  <div class="w-full h-full flex flex-col">  
    <div class="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-2 mb-4 sm:mb-6">
      <div>
        <h2 class="text-base sm:text-lg md:text-xl font-semibold text-gray-800">Traffic Sources</h2>
        <p class="text-xs sm:text-sm text-gray-500 mt-0.5">Last 30 days analytics</p>
      </div>
      <div class="flex items-center gap-2">
        <span class="text-xs bg-gray-100 text-gray-700 px-3 py-1.5 rounded-lg">Monthly</span>
      </div>
    </div>
    
    <!-- Chart Container - Responsive -->
    <div class="relative flex-1 min-h-[150px] sm:min-h-[180px] md:min-h-[200px] w-full flex items-center justify-center">
      <Doughnut :data="chartdata" :options="options" class="max-h-full max-w-full" />
    </div>
    
    <!-- Legend - Stack on Mobile, Row on Desktop -->
    <div class="mt-4 sm:mt-6 space-y-2 sm:space-y-3">  
      <div class="flex flex-col sm:flex-row sm:justify-between gap-2 sm:gap-4">  
        <div class="flex items-center justify-between sm:justify-start gap-2 p-2 hover:bg-gray-50 rounded-lg transition-colors">  
          <div class="flex items-center gap-2">
            <span class="w-3 h-3 rounded-full bg-[#4318FF]"></span>
            <span class="text-xs sm:text-sm text-gray-700">Your files</span>
          </div>
          <span class="text-xs sm:text-sm font-medium text-gray-900">63%</span>
        </div>  
        <div class="flex items-center justify-between sm:justify-start gap-2 p-2 hover:bg-gray-50 rounded-lg transition-colors">  
          <div class="flex items-center gap-2">
            <span class="w-3 h-3 rounded-full bg-[#6AD2FF]"></span>
            <span class="text-xs sm:text-sm text-gray-700">System</span>
          </div>
          <span class="text-xs sm:text-sm font-medium text-gray-900">25%</span>
        </div>
        <div class="flex items-center justify-between sm:justify-start gap-2 p-2 hover:bg-gray-50 rounded-lg transition-colors">  
          <div class="flex items-center gap-2">
            <span class="w-3 h-3 rounded-full bg-[#EFF4FB]"></span>
            <span class="text-xs sm:text-sm text-gray-700">Others</span>
          </div>
          <span class="text-xs sm:text-sm font-medium text-gray-900">16%</span>
        </div>
      </div>  
    </div>

    <!-- Summary Stats -->
    <div class="mt-4 pt-4 border-t border-gray-100 grid grid-cols-3 gap-2 sm:gap-4">
      <div class="text-center">
        <p class="text-xs text-gray-500">Total Visits</p>
        <p class="text-sm sm:text-base font-bold text-gray-800">12.5K</p>
      </div>
      <div class="text-center">
        <p class="text-xs text-gray-500">Bounce Rate</p>
        <p class="text-sm sm:text-base font-bold text-gray-800">32%</p>
      </div>
      <div class="text-center">
        <p class="text-xs text-gray-500">Avg. Time</p>
        <p class="text-sm sm:text-base font-bold text-gray-800">2.4m</p>
      </div>
    </div>
  </div>  
</template>

<style scoped>
/* Ensure chart is responsive */
canvas {
  max-width: 100%;
  height: auto !important;
}

/* Smooth hover transitions */
.hover\:bg-gray-50:hover {
  background-color: rgba(249, 250, 251, 1);
  transition: background-color 0.2s ease;
}
</style>