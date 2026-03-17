<template>  
  <div class="w-full">  
    <div class="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-3 mb-4 sm:mb-6">
      <div>
        <h2 class="text-base sm:text-lg md:text-xl font-semibold text-gray-800">Complex Table</h2>  
        <p class="text-xs sm:text-sm text-gray-500 mt-0.5">Advanced project management</p>
      </div>
      <div class="flex items-center gap-2">
        <button class="px-3 py-1.5 bg-gray-100 hover:bg-gray-200 rounded-lg text-xs sm:text-sm text-gray-700 transition-colors flex items-center gap-1">
          <svg class="h-3.5 w-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
          </svg>
          <span>Filter</span>
        </button>
      </div>
    </div>

    <!-- Mobile Card View -->
    <div class="block sm:hidden space-y-3">
      <div v-for="(item, index) in items" :key="index" 
           class="bg-white border border-gray-100 rounded-xl p-4 hover:shadow-md transition-all">
        <div class="flex items-start justify-between mb-3">
          <div>
            <h3 class="font-medium text-gray-900">{{ item.name }}</h3>
            <div class="flex items-center gap-2 mt-1">
              <div v-html="icons[item.statusIcon]" class="w-4 h-4" />
              <span :class="statusClasses[item.status]" class="text-xs font-medium">{{ item.status }}</span>
            </div>
          </div>
          <span :class="getProgressBadgeClass(item.progress)" class="text-xs px-2 py-1 rounded-full">
            {{ item.progress }}
          </span>
        </div>
        
        <div class="flex items-center justify-between text-sm">
          <span class="text-gray-500">Date:</span>
          <span class="font-medium text-gray-900">{{ item.date }}</span>
        </div>
        
        <div class="mt-3">
          <div class="flex justify-between items-center mb-1">
            <span class="text-xs text-gray-500">Progress</span>
            <span class="text-xs font-medium text-gray-900">{{ item.progress }}</span>
          </div>
          <div class="w-full bg-gray-200 rounded-full h-1.5">
            <div 
              class="bg-[#4318FF] h-1.5 rounded-full transition-all duration-300"
              :style="{ width: item.progress }"
            ></div>
          </div>
        </div>
      </div>
    </div>

    <!-- Desktop Table View -->
    <div class="hidden sm:block overflow-x-auto">
      <table class="min-w-full">  
        <thead>  
          <tr class="border-b border-gray-200">  
            <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">NAME</th>  
            <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">STATUS</th>  
            <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">DATE</th>  
            <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">PROGRESS</th>  
            <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">ACTIONS</th>
          </tr>  
        </thead>  
        <tbody class="divide-y divide-gray-200">  
          <tr v-for="(item, index) in items" :key="index" class="hover:bg-gray-50 transition-colors">  
            <td class="px-4 py-3">
              <div class="flex items-center gap-2">
                <div class="h-8 w-8 bg-gradient-to-br from-[#4318FF]/10 to-purple-600/10 rounded-lg flex items-center justify-center">
                  <span class="text-xs font-bold text-[#4318FF]">{{ item.name.charAt(0) }}</span>
                </div>
                <span class="font-medium text-gray-900">{{ item.name }}</span>
              </div>
            </td>  
            <td class="px-4 py-3">
              <div class="flex items-center gap-2">  
                <i v-html="icons[item.statusIcon]" class="w-5 h-5"></i>  
                <span :class="statusClasses[item.status]" class="text-sm font-medium">{{ item.status }}</span>
              </div>  
            </td>  
            <td class="px-4 py-3 text-gray-600">{{ item.date }}</td>  
            <td class="px-4 py-3">
              <div class="flex items-center gap-3">
                <div class="w-24 bg-gray-200 rounded-full h-1.5">
                  <div 
                    class="bg-[#4318FF] h-1.5 rounded-full transition-all duration-300"
                    :style="{ width: item.progress }"
                  ></div>
                </div>
                <span class="text-xs font-medium text-gray-700">{{ item.progress }}</span>
              </div>
            </td>  
            <td class="px-4 py-3">
              <div class="flex items-center gap-2">
                <button class="p-1 hover:bg-gray-100 rounded-lg transition-colors">
                  <svg class="h-4 w-4 text-gray-500 hover:text-[#4318FF]" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                  </svg>
                </button>
                <button class="p-1 hover:bg-gray-100 rounded-lg transition-colors">
                  <svg class="h-4 w-4 text-gray-500 hover:text-[#4318FF]" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                  </svg>
                </button>
              </div>
            </td>
          </tr>  
        </tbody>  
      </table>
    </div>
  </div>  
</template>  
  
<script setup>  
import { ref } from 'vue';  
import icons from '@/utils/icons';  

const items = ref([  
  {  
    name: 'Horizon UI PRO',  
    status: 'Approved',  
    statusIcon: 'Approved',  
    date: '18 Apr 2021',  
    progress: '75%',  
  },  
  {  
    name: 'Horizon UI Free',  
    status: 'Disable',  
    statusIcon: 'Disable',  
    date: '18 Apr 2021',  
    progress: '40%',  
  },  
  {  
    name: 'Marketplace',  
    status: 'Error',  
    statusIcon: 'Error',  
    date: '20 May 2021',  
    progress: '50%',  
  },  
  {  
    name: 'Weekly Updates',  
    status: 'Approved',  
    statusIcon: 'Approved',  
    date: '12 Jul 2021',  
    progress: '80%',  
  },  
]);  

const statusClasses = {  
  Approved: 'text-green-600 bg-green-50 px-2 py-1 rounded-lg text-xs font-medium',  
  Disable: 'text-red-600 bg-red-50 px-2 py-1 rounded-lg text-xs font-medium',  
  Error: 'text-yellow-600 bg-yellow-50 px-2 py-1 rounded-lg text-xs font-medium',  
};

const getProgressBadgeClass = (progress) => {
  const value = parseInt(progress);
  if (value >= 75) return 'bg-green-100 text-green-700';
  if (value >= 50) return 'bg-blue-100 text-blue-700';
  if (value >= 25) return 'bg-yellow-100 text-yellow-700';
  return 'bg-red-100 text-red-700';
};
</script>  

<style scoped>  
/* Mobile optimizations */
@media (max-width: 640px) {
  .border-gray-100 {
    border-width: 1px;
  }
}

/* Smooth progress bar animation */
.bg-\[\#4318FF\] {
  transition: width 0.3s ease-in-out;
}

/* Hover effects */
.hover\:bg-gray-100:hover {
  background-color: rgba(243, 244, 246, 1);
  transition: background-color 0.2s ease;
}
</style>