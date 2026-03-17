<template>  
  <div class="w-full">  
    <div class="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-3 mb-4 sm:mb-6">
      <div>
        <h2 class="text-base sm:text-lg md:text-xl font-semibold text-gray-800">Check Table</h2>  
        <p class="text-xs sm:text-sm text-gray-500 mt-0.5">Recent transactions overview</p>
      </div>
      <div class="flex items-center gap-2 w-full sm:w-auto">
        <button class="flex-1 sm:flex-none px-3 sm:px-4 py-2 bg-gray-100 hover:bg-gray-200 rounded-lg text-xs sm:text-sm text-gray-700 transition-colors flex items-center justify-center gap-1.5">
          <svg class="h-3.5 w-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4" />
          </svg>
          <span>Export</span>
        </button>
        <button class="flex-1 sm:flex-none px-3 sm:px-4 py-2 bg-[#4318FF] hover:bg-[#3311DD] text-white rounded-lg text-xs sm:text-sm transition-colors flex items-center justify-center gap-1.5">
          <svg class="h-3.5 w-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
          </svg>
          <span>Add</span>
        </button>
      </div>
    </div>

    <!-- Mobile Card View (Visible on small screens) -->
    <div class="block sm:hidden space-y-3">
      <div v-for="(item, index) in tableData" :key="index" 
           class="bg-white border border-gray-100 rounded-xl p-4 hover:shadow-md transition-shadow">
        <div class="flex items-start justify-between mb-3">
          <div class="flex items-center gap-3">
            <input type="checkbox" v-model="item.checked" class="w-4 h-4 rounded border-gray-300 text-[#4318FF] focus:ring-[#4318FF]" />
            <div>
              <h3 class="font-medium text-gray-900">{{ item.name }}</h3>
              <p class="text-xs text-gray-500 mt-0.5">ID: #{{ index + 1000 }}</p>
            </div>
          </div>
          <span :class="getProgressColor(item.progress)" class="text-xs font-medium px-2 py-1 rounded-full">
            {{ item.progress }}
          </span>
        </div>
        
        <div class="grid grid-cols-2 gap-3 text-sm">
          <div>
            <p class="text-xs text-gray-500">Quantity</p>
            <p class="font-medium text-gray-900">{{ item.quantity }}</p>
          </div>
          <div>
            <p class="text-xs text-gray-500">Date</p>
            <p class="font-medium text-gray-900">{{ item.date }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- Desktop Table View (Hidden on small screens) -->
    <div class="hidden sm:block overflow-x-auto">
      <table class="min-w-full">  
        <thead>  
          <tr class="border-b border-gray-200">  
            <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">  
              <input type="checkbox" @change="toggleAll" :checked="isAllSelected" class="rounded border-gray-300 text-[#4318FF] focus:ring-[#4318FF]" />
            </th>  
            <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">NAME</th>  
            <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">PROGRESS</th>  
            <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">QUANTITY</th>  
            <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">DATE</th>  
            <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">ACTIONS</th>
          </tr>  
        </thead>  
        <tbody class="divide-y divide-gray-200">  
          <tr v-for="(item, index) in tableData" :key="index" class="hover:bg-gray-50 transition-colors">  
            <td class="px-4 py-3">  
              <input type="checkbox" v-model="item.checked" class="rounded border-gray-300 text-[#4318FF] focus:ring-[#4318FF]" />  
            </td>  
            <td class="px-4 py-3">
              <div class="flex items-center gap-2">
                <div class="h-8 w-8 bg-gradient-to-br from-[#4318FF]/10 to-[#6AD2FF]/10 rounded-lg flex items-center justify-center">
                  <span class="text-xs font-bold text-[#4318FF]">{{ item.name.charAt(0) }}</span>
                </div>
                <span class="font-medium text-gray-900">{{ item.name }}</span>
              </div>
            </td>  
            <td class="px-4 py-3">
              <div class="flex items-center gap-2">
                <div class="w-20 bg-gray-200 rounded-full h-1.5">
                  <div class="bg-[#4318FF] h-1.5 rounded-full" :style="{ width: item.progress }"></div>
                </div>
                <span class="text-xs text-gray-600">{{ item.progress }}</span>
              </div>
            </td>  
            <td class="px-4 py-3 font-medium text-gray-900">{{ item.quantity }}</td>  
            <td class="px-4 py-3 text-gray-600">{{ item.date }}</td>  
            <td class="px-4 py-3">
              <button class="text-gray-400 hover:text-[#4318FF] transition-colors">
                <svg class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 5v.01M12 12v.01M12 19v.01M12 6a1 1 0 110-2 1 1 0 010 2zm0 7a1 1 0 110-2 1 1 0 010 2zm0 7a1 1 0 110-2 1 1 0 010 2z" />
                </svg>
              </button>
            </td>
          </tr>  
        </tbody>  
      </table>
    </div>

    <!-- Pagination -->
    <div class="flex flex-col sm:flex-row justify-between items-center gap-3 mt-4 sm:mt-6 pt-4 border-t border-gray-200">
      <p class="text-xs sm:text-sm text-gray-500">Showing 1 to 5 of 25 entries</p>
      <div class="flex items-center gap-2">
        <button class="px-3 py-1.5 border border-gray-300 rounded-lg text-sm text-gray-600 hover:bg-gray-50 disabled:opacity-50" disabled>Previous</button>
        <button class="px-3 py-1.5 bg-[#4318FF] text-white rounded-lg text-sm hover:bg-[#3311DD]">1</button>
        <button class="px-3 py-1.5 border border-gray-300 rounded-lg text-sm text-gray-600 hover:bg-gray-50">2</button>
        <button class="px-3 py-1.5 border border-gray-300 rounded-lg text-sm text-gray-600 hover:bg-gray-50">3</button>
        <button class="px-3 py-1.5 border border-gray-300 rounded-lg text-sm text-gray-600 hover:bg-gray-50">Next</button>
      </div>
    </div>
  </div>  
</template>  

<script setup>  
import { ref, computed } from 'vue';  

const tableData = ref([  
  { name: 'Horizon UI PRO', progress: '17.5%', quantity: '2,458', date: '24 Jan 2021', checked: false },  
  { name: 'Horizon UI Free', progress: '10.8%', quantity: '1,485', date: '12 Jun 2021', checked: true },  
  { name: 'Weekly Update', progress: '21.3%', quantity: '1,024', date: '5 Jan 2021', checked: true },  
  { name: 'Venus 3D Asset', progress: '31.5%', quantity: '858', date: '7 Mar 2021', checked: false },  
  { name: 'Marketplace', progress: '12.2%', quantity: '258', date: '17 Dec 2021', checked: false }  
]);  

const isAllSelected = computed(() => {
  return tableData.value.length > 0 && tableData.value.every(item => item.checked);
});

const toggleAll = (event) => {
  const checked = event.target.checked;
  tableData.value.forEach(item => item.checked = checked);
};

const getProgressColor = (progress) => {
  const value = parseFloat(progress);
  if (value >= 20) return 'bg-green-100 text-green-700';
  if (value >= 15) return 'bg-blue-100 text-blue-700';
  return 'bg-yellow-100 text-yellow-700';
};
</script>  

<style scoped>  
/* Mobile optimizations */
@media (max-width: 640px) {
  .border-gray-100 {
    border-width: 1px;
  }
}

/* Smooth transitions */
.hover\:shadow-md:hover {
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
  transition: box-shadow 0.2s ease;
}
</style>