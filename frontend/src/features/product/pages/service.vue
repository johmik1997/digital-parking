<script setup>
import { ref, onMounted, computed } from "vue";
import {
  getServices,
  createService,
  updateService,
  toggleService,
} from "@/features/product/api/productApi.js";

const services = ref([]);
const loading = ref(false);
const showForm = ref(false);
const searchQuery = ref('');
const selectedPricingType = ref('ALL');

// Form states
const newService = ref({
  name: "",
  description: "",
  pricingType: "HOURLY",
  slot: null,
  rate: null,
});

const editService = ref({
  id: null,
  name: "",
  description: "",
  pricingType: "HOURLY",
  slot: null,
  rate: null,
});
const showEditModal = ref(false);

// Toast notification
const toast = ref({
  show: false,
  message: '',
  type: 'success'
});

const showToast = (message, type = 'success') => {
  toast.value = { show: true, message, type };
  setTimeout(() => {
    toast.value.show = false;
  }, 3000);
};

const fetchServices = async () => {
  loading.value = true;
  try {
    console.log("Fetching services...");
    const res = await getServices();
    console.log("API Response:", res);
    services.value = res.data;
  } catch (error) {
    console.error("Failed to fetch services:", error);
    showToast('Failed to fetch services', 'error');
    if (error.response) {
      console.error("Error response data:", error.response.data);
      console.error("Error response status:", error.response.status);
    }
  } finally {
    loading.value = false;
  }
};

const handleCreateService = async () => {
  if (!newService.value.name?.trim()) {
    showToast('Service name is required', 'error');
    return;
  }
  if (!newService.value.pricingType) {
    showToast('Pricing type is required', 'error');
    return;
  }
  if (newService.value.rate === null || newService.value.rate === '' || Number.isNaN(Number(newService.value.rate))) {
    showToast('Rate is required', 'error');
    return;
  }
  
  try {
    const payload = {
      name: newService.value.name,
      description: newService.value.description,
      pricingType: newService.value.pricingType,
      slot: newService.value.slot !== null && newService.value.slot !== '' ? Number(newService.value.slot) : null,
      rate: Number(newService.value.rate),
    };
    await createService(payload);
    newService.value = { name: "", description: "", pricingType: "HOURLY", slot: null, rate: null };
    await fetchServices();
    showToast('Service created successfully');
    showForm.value = false;
  } catch (error) {
    console.error("Failed to create service:", error);
    showToast('Failed to create service', 'error');
  }
};

const openEditModal = (service) => {
  editService.value = {
    id: service.serviceUuid,
    name: service.name || "",
    description: service.description || "",
    pricingType: service.pricingType || "HOURLY",
    slot: service.slot ?? null,
    rate: service.currentRate ?? null,
  };
  showEditModal.value = true;
};

const handleUpdateService = async () => {
  if (!editService.value.name?.trim()) {
    showToast('Service name is required', 'error');
    return;
  }
  if (!editService.value.pricingType) {
    showToast('Pricing type is required', 'error');
    return;
  }
  if (editService.value.rate === null || editService.value.rate === '' || Number.isNaN(Number(editService.value.rate))) {
    showToast('Rate is required', 'error');
    return;
  }

  try {
    const payload = {
      name: editService.value.name,
      description: editService.value.description,
      pricingType: editService.value.pricingType,
      slot: editService.value.slot !== null && editService.value.slot !== '' ? Number(editService.value.slot) : null,
      rate: Number(editService.value.rate),
    };
    await updateService(editService.value.id, payload);
    showEditModal.value = false;
    await fetchServices();
    showToast('Service updated successfully');
  } catch (error) {
    console.error("Failed to update service:", error);
    showToast('Failed to update service', 'error');
  }
};

const handleToggle = async (id, currentStatus) => {
  try {
    await toggleService(id);
    await fetchServices();
    showToast(`Service ${currentStatus ? 'disabled' : 'enabled'} successfully`);
  } catch (error) {
    console.error("Failed to toggle service:", error);
    showToast('Failed to update service status', 'error');
  }
};

// Filtered services
const filteredServices = computed(() => {
  let filtered = services.value;
  
  // Filter by search
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase();
    filtered = filtered.filter(s => 
      s.name?.toLowerCase().includes(query) || 
      s.description?.toLowerCase().includes(query)
    );
  }
  
  // Filter by pricing type
  if (selectedPricingType.value !== 'ALL') {
    filtered = filtered.filter(s => s.pricingType === selectedPricingType.value);
  }
  
  return filtered;
});

// Stats
const stats = computed(() => ({
  total: services.value.length,
  active: services.value.filter(s => s.active).length,
  hourly: services.value.filter(s => s.pricingType === 'HOURLY').length,
  fixed: services.value.filter(s => s.pricingType === 'FIXED').length
}));

onMounted(fetchServices);
</script>

<template>
  <div class="min-h-screen bg-gray-50 overflow-x-hidden">
    <!-- Toast Notification -->
    <Transition
      enter-active-class="transform ease-out duration-300 transition"
      enter-from-class="translate-y-2 opacity-0 sm:translate-y-0 sm:translate-x-2"
      enter-to-class="translate-y-0 opacity-100 sm:translate-x-0"
      leave-active-class="transition ease-in duration-100"
      leave-from-class="opacity-100"
      leave-to-class="opacity-0"
    >
      <div
        v-if="toast.show"
        :class="[
          'fixed top-4 right-4 z-50 px-4 py-3 rounded-lg shadow-lg flex items-center space-x-2 max-w-full sm:max-w-sm w-full sm:w-auto',
          toast.type === 'success' ? 'bg-green-50 text-green-800 border border-green-200' : 'bg-red-50 text-red-800 border border-red-200'
        ]"
      >
        <svg
          v-if="toast.type === 'success'"
          class="h-5 w-5 text-green-400 flex-shrink-0"
          fill="none"
          stroke="currentColor"
          viewBox="0 0 24 24"
        >
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
        </svg>
        <svg
          v-else
          class="h-5 w-5 text-red-400 flex-shrink-0"
          fill="none"
          stroke="currentColor"
          viewBox="0 0 24 24"
        >
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
        </svg>
        <span class="text-sm font-medium break-words">{{ toast.message }}</span>
      </div>
    </Transition>

    <!-- Header Section -->
    <div class="bg-white border-b border-gray-200 sticky top-0 z-30">
      <div class="max-w-full px-4 sm:px-6 py-4">
        <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
          <div>
            <h1 class="text-xl sm:text-2xl font-bold text-gray-900">Service Management</h1>
            <p class="text-sm text-gray-500 mt-1">Manage your parking services and rates</p>
          </div>
          
          <!-- Mobile Add Button -->
          <button
            @click="showForm = true"
            class="sm:hidden fixed bottom-6 right-4 z-40 bg-blue-600 text-white p-4 rounded-full shadow-lg hover:bg-blue-700 transition-all hover:scale-105"
          >
            <svg class="h-6 w-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
            </svg>
          </button>
          
          <!-- Desktop Add Button -->
          <button
            @click="showForm = true"
            class="hidden sm:flex items-center space-x-2 bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 transition-colors"
          >
            <svg class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
            </svg>
            <span>Add New Service</span>
          </button>
        </div>
      </div>
    </div>

    <!-- Stats Cards - Mobile First -->
    <div class="max-w-full px-4 sm:px-6 py-4">
      <div class="grid grid-cols-2 sm:grid-cols-4 gap-3 sm:gap-4">
        <div class="bg-white p-4 rounded-lg shadow-sm border border-gray-100">
          <p class="text-xs sm:text-sm text-gray-500">Total Services</p>
          <p class="text-xl sm:text-2xl font-bold text-gray-900">{{ stats.total }}</p>
        </div>
        <div class="bg-white p-4 rounded-lg shadow-sm border border-gray-100">
          <p class="text-xs sm:text-sm text-gray-500">Active</p>
          <p class="text-xl sm:text-2xl font-bold text-green-600">{{ stats.active }}</p>
        </div>
        <div class="bg-white p-4 rounded-lg shadow-sm border border-gray-100">
          <p class="text-xs sm:text-sm text-gray-500">Hourly</p>
          <p class="text-xl sm:text-2xl font-bold text-blue-600">{{ stats.hourly }}</p>
        </div>
        <div class="bg-white p-4 rounded-lg shadow-sm border border-gray-100">
          <p class="text-xs sm:text-sm text-gray-500">Fixed</p>
          <p class="text-xl sm:text-2xl font-bold text-purple-600">{{ stats.fixed }}</p>
        </div>
      </div>
    </div>

    <!-- Add Service Form - Mobile Modal -->
    <Transition
      enter-active-class="transform ease-out duration-300 transition"
      enter-from-class="translate-y-full"
      enter-to-class="translate-y-0"
      leave-active-class="transform ease-in duration-200 transition"
      leave-from-class="translate-y-0"
      leave-to-class="translate-y-full"
    >
      <div
        v-if="showForm"
        class="fixed inset-0 z-50"
      >
        <div class="fixed inset-0 bg-black/50" @click="showForm = false"></div>
        <div class="fixed bottom-0 left-0 right-0 sm:inset-0 sm:flex sm:items-center sm:justify-center">
          <div class="bg-white rounded-t-2xl sm:rounded-2xl w-full sm:max-w-2xl sm:mx-4 max-h-[90vh] overflow-y-auto shadow-2xl">
          <div class="sticky top-0 z-10 bg-white/95 backdrop-blur border-b border-gray-100 px-6 py-4 flex justify-between items-center">
            <div>
              <h2 class="text-lg font-semibold text-gray-900">Add New Service</h2>
              <p class="text-xs text-gray-500 mt-1">Fill the details below to create a service.</p>
            </div>
            <button @click="showForm = false" class="text-gray-400 hover:text-gray-600 p-2 -m-2">
              <svg class="h-6 w-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          </div>
          
          <div class="space-y-4 px-6 py-5">
            <div>
              <label class="block text-xs font-medium text-gray-700 mb-1">Service Name *</label>
              <input
                v-model="newService.name"
                placeholder="e.g., Car Wash, Parking"
                class="max-w-full border border-gray-200 bg-gray-50 px-4 py-3 rounded-xl focus:ring-2 focus:ring-blue-500/30 focus:border-blue-500 text-base"
              />
            </div>
            
            <div>
              <label class="block text-xs font-medium text-gray-700 mb-1">Description</label>
              <textarea
                v-model="newService.description"
                placeholder="Brief description of the service"
                rows="3"
                class="max-w-full border border-gray-200 bg-gray-50 px-4 py-3 rounded-xl focus:ring-2 focus:ring-blue-500/30 focus:border-blue-500 text-base resize-none"
              ></textarea>
            </div>
            
            <div>
              <label class="block text-xs font-medium text-gray-700 mb-1">Pricing Type</label>
              <select
                v-model="newService.pricingType"
                class="max-w-full border border-gray-200 bg-gray-50 px-4 py-3 rounded-xl focus:ring-2 focus:ring-blue-500/30 focus:border-blue-500 text-base"
              >
                <option value="HOURLY">Hourly</option>
                <option value="FIXED">Fixed</option>
              </select>
            </div>

            <div class="grid grid-cols-1 sm:grid-cols-2 gap-3">
              <div>
                <label class="block text-xs font-medium text-gray-700 mb-1">Slot</label>
                <input
                  v-model.number="newService.slot"
                  type="number"
                  min="0"
                  placeholder="e.g., 50"
                  class="max-w-full border border-gray-200 bg-gray-50 px-4 py-3 rounded-xl focus:ring-2 focus:ring-blue-500/30 focus:border-blue-500 text-base"
                />
              </div>
              <div>
                <label class="block text-xs font-medium text-gray-700 mb-1">Rate *</label>
                <input
                  v-model.number="newService.rate"
                  type="number"
                  min="0"
                  step="0.01"
                  placeholder="0.00"
                  class="max-w-full border border-gray-200 bg-gray-50 px-4 py-3 rounded-xl focus:ring-2 focus:ring-blue-500/30 focus:border-blue-500 text-base"
                />
              </div>
            </div>
            
            <div class="flex flex-col sm:flex-row gap-3 pt-2">
              <button
                @click="showForm = false"
                class="max-w-full sm:flex-1 px-4 py-3 bg-gray-100 text-gray-700 rounded-xl hover:bg-gray-200 transition-colors text-sm font-medium"
              >
                Cancel
              </button>
              <button
                @click="handleCreateService"
                :disabled="!newService.name?.trim()"
                class="max-w-full sm:flex-1 px-4 py-3 bg-blue-600 text-white rounded-xl hover:bg-blue-700 transition-colors disabled:bg-blue-300 disabled:cursor-not-allowed text-sm font-medium"
              >
                Add Service
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
    </Transition>

    <!-- Filters and Search -->
    <div class="max-w-full px-4 sm:px-6 py-3">
      <div class="bg-white border border-gray-200 rounded-xl p-3 sm:p-4 shadow-sm">
        <div class="flex flex-col gap-3 sm:flex-row">
          <div class="relative w-full sm:flex-1 min-w-0">
            <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
              <svg class="h-5 w-5 text-gray-400 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
              </svg>
            </div>
            <input
              v-model="searchQuery"
              type="text"
              placeholder="Search services..."
              class="max-w-full w-full pl-10 pr-4 py-2.5 border border-gray-200 rounded-lg focus:ring-2 focus:ring-blue-500/30 focus:border-blue-500 text-sm bg-white"
            />
          </div>
          
          <select
            v-model="selectedPricingType"
            class="max-w-full sm:w-56 w-full py-2.5 px-4 border border-gray-200 rounded-lg focus:ring-2 focus:ring-blue-500/30 focus:border-blue-500 text-sm bg-white"
          >
            <option value="ALL">All Types</option>
            <option value="HOURLY">Hourly</option>
            <option value="FIXED">Fixed</option>
          </select>
        </div>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="max-w-full px-4 sm:px-6 py-8">
      <div class="flex flex-col items-center justify-center py-12">
        <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
        <p class="mt-4 text-sm text-gray-500">Loading services...</p>
      </div>
    </div>

    <!-- Services List - Mobile Card View -->
    <div v-else-if="filteredServices.length > 0" class="max-w-full px-4 sm:px-6 pb-6">
      <!-- Mobile View (Cards) -->
      <div class="sm:hidden space-y-4">
        <div
          v-for="service in filteredServices"
          :key="service.id"
          class="bg-white rounded-lg border border-gray-200 p-4 shadow-sm hover:shadow-md transition-shadow"
        >
          <div class="flex justify-between items-start mb-3">
            <div class="flex-1 min-w-0 pr-2">
              <h3 class="font-medium text-gray-900 break-words">{{ service.name }}</h3>
              <p class="text-xs text-gray-500 mt-1 break-words">{{ service.description || 'No description' }}</p>
            </div>
            <span
              :class="[
                'px-2 py-1 rounded-full text-xs font-medium flex-shrink-0',
                service.active 
                  ? 'bg-green-100 text-green-800' 
                  : 'bg-red-100 text-red-800'
              ]"
            >
              {{ service.active ? "Active" : "Disabled" }}
            </span>
          </div>

          <div class="grid grid-cols-2 sm:grid-cols-3 gap-3 mb-3 text-sm">
            <div>
              <span class="text-xs text-gray-500">Type</span>
              <p :class="service.pricingType === 'HOURLY' ? 'text-blue-600' : 'text-purple-600'" class="font-medium">
                {{ service.pricingType }}
              </p>
            </div>
            <div>
              <span class="text-xs text-gray-500">Slot</span>
              <p class="font-medium text-gray-900">
                {{ service.slot ?? '-' }}
              </p>
            </div>
            <div>
              <span class="text-xs text-gray-500">Rate</span>
              <p class="font-medium text-gray-900">
                <span v-if="service.currentRate">
                  ${{ service.currentRate }}
                  <span v-if="service.pricingType === 'HOURLY'" class="text-xs text-gray-500">/hr</span>
                </span>
                <span v-else class="text-gray-400">Not set</span>
              </p>
            </div>
          </div>

          <div class="flex flex-row gap-2 pt-3 border-t border-gray-100">
            <button
              @click="openEditModal(service)"
              class="flex-1 px-2 py-0.5 rounded-lg text-xs font-semibold border border-blue-200 bg-blue-600 text-white hover:bg-blue-700 transition-colors"
            >
              Edit Service
            </button>
            <button
              @click="handleToggle(service.serviceUuid, service.active)"
              :class="[
                'flex-1 px-2 py-0.2 rounded-lg transition-colors text-xs font-semibold border',
                service.active 
                  ? 'bg-rose-600 text-white border-rose-600 hover:bg-rose-700' 
                  : 'bg-emerald-600 text-white border-emerald-600 hover:bg-emerald-700'
              ]"
            >
              {{ service.active ? "Disable" : "Enable" }}
            </button>
          </div>
        </div>
      </div>

      <!-- Desktop View (Table) -->
      <div class="hidden sm:block bg-white rounded-lg shadow-sm border border-gray-200 overflow-hidden">
        <div class="overflow-x-auto">
          <table class="w-full">
            <thead class="bg-gray-50">
              <tr>
                <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Name</th>
                <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Description</th>
                <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Type</th>
                <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Slot</th>
                <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Rate</th>
                <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Status</th>
                <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-200">
              <tr
                v-for="service in filteredServices"
                :key="service.id"
                class="hover:bg-gray-50 transition-colors"
              >
                <td class="px-4 py-3 text-sm font-medium text-gray-900">{{ service.name }}</td>
                <td class="px-4 py-3 text-sm text-gray-500 max-w-xs truncate">{{ service.description || '—' }}</td>
                <td class="px-4 py-3">
                  <span :class="[
                    'px-2 py-1 rounded-full text-xs font-medium',
                    service.pricingType === 'HOURLY' 
                      ? 'bg-blue-100 text-blue-800' 
                      : 'bg-purple-100 text-purple-800'
                  ]">
                    {{ service.pricingType }}
                  </span>
                </td>
                <td class="px-4 py-3 text-sm text-gray-900">
                  {{ service.slot ?? '-' }}
                </td>
                <td class="px-4 py-3 text-sm font-medium text-gray-900">
                  <span v-if="service.currentRate">
                    ${{ service.currentRate }}
                    <span v-if="service.pricingType === 'HOURLY'" class="text-xs text-gray-500">/hr</span>
                  </span>
                  <span v-else class="text-gray-400">Not set</span>
                </td>
                <td class="px-4 py-3">
                  <span
                    :class="[
                      'px-2 py-1 rounded-full text-xs font-medium',
                      service.active 
                        ? 'bg-green-100 text-green-800' 
                        : 'bg-red-100 text-red-800'
                    ]"
                  >
                    {{ service.active ? "Active" : "Disabled" }}
                  </span>
                </td>
                <td class="px-4 py-3">
                  <div class="flex items-center gap-2">
                    <button
                      @click="openEditModal(service)"
                      class="px-3 py-1.5 rounded-lg text-xs font-semibold border border-blue-200 bg-blue-600 text-white hover:bg-blue-700 transition-colors whitespace-nowrap"
                    >
                      Edit Service
                    </button>
                    <button
                      @click="handleToggle(service.serviceUuid, service.active)"
                      :class="[
                        'px-3 py-1.5 rounded-lg transition-colors text-xs font-semibold border whitespace-nowrap',
                        service.active 
                          ? 'bg-rose-600 text-white border-rose-600 hover:bg-rose-700' 
                          : 'bg-emerald-600 text-white border-emerald-600 hover:bg-emerald-700'
                      ]"
                    >
                      {{ service.active ? "Disable" : "Enable" }}
                    </button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <!-- Empty State -->
    <div v-else class="max-w-full px-4 sm:px-6 py-12">
      <div class="text-center">
        <svg class="mx-auto h-12 w-12 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" />
        </svg>
        <h3 class="mt-2 text-sm font-medium text-gray-900">No services found</h3>
        <p class="mt-1 text-sm text-gray-500">
          {{ searchQuery || selectedPricingType !== 'ALL' ? 'Try adjusting your filters' : 'Get started by creating a new service' }}
        </p>
        <div class="mt-6">
          <button
            v-if="searchQuery || selectedPricingType !== 'ALL'"
            @click="searchQuery = ''; selectedPricingType = 'ALL'"
            class="inline-flex items-center px-4 py-2 border border-gray-300 shadow-sm text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
          >
            Clear filters
          </button>
        </div>
      </div>
    </div>

    <!-- Edit Service Modal -->
    <div
      v-if="showEditModal"
      class="fixed inset-0 z-50 flex items-center justify-center p-3 sm:p-4"
    >
      <div class="fixed inset-0 bg-black/50" @click="showEditModal = false"></div>
      
      <div class="relative bg-white rounded-2xl w-full max-w-full sm:max-w-2xl p-4 sm:p-6 shadow-xl mx-2 sm:mx-4 max-h-[85vh] overflow-y-auto">
        <div class="flex justify-between items-center mb-4">
          <div>
            <h2 class="text-base sm:text-lg font-semibold text-gray-900">Edit Service</h2>
            <p class="text-xs text-gray-500 mt-1">Update service details, slot, pricing type and rate.</p>
          </div>
          <button @click="showEditModal = false" class="text-gray-400 hover:text-gray-600 p-2 -m-2">
            <svg class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>

        <div class="space-y-4">
          <div>
            <label class="block text-xs font-medium text-gray-700 mb-1">Service Name *</label>
            <input
              v-model="editService.name"
              placeholder="Service name"
              class="w-full border border-gray-200 bg-gray-50 px-4 py-3 rounded-xl focus:ring-2 focus:ring-blue-500/30 focus:border-blue-500 text-base"
            />
          </div>

          <div>
            <label class="block text-xs font-medium text-gray-700 mb-1">Description</label>
            <textarea
              v-model="editService.description"
              rows="3"
              placeholder="Brief description of the service"
              class="w-full border border-gray-200 bg-gray-50 px-4 py-3 rounded-xl focus:ring-2 focus:ring-blue-500/30 focus:border-blue-500 text-base resize-none"
            ></textarea>
          </div>

          <div>
            <label class="block text-xs font-medium text-gray-700 mb-1">Pricing Type</label>
            <select
              v-model="editService.pricingType"
              class="w-full border border-gray-200 bg-gray-50 px-4 py-3 rounded-xl focus:ring-2 focus:ring-blue-500/30 focus:border-blue-500 text-base"
            >
              <option value="HOURLY">Hourly</option>
              <option value="FIXED">Fixed</option>
            </select>
          </div>

          <div class="grid grid-cols-1 sm:grid-cols-2 gap-3">
            <div>
              <label class="block text-xs font-medium text-gray-700 mb-1">Slot</label>
              <input
                v-model.number="editService.slot"
                type="number"
                min="0"
                placeholder="e.g., 50"
                class="w-full border border-gray-200 bg-gray-50 px-4 py-3 rounded-xl focus:ring-2 focus:ring-blue-500/30 focus:border-blue-500 text-base"
              />
            </div>
            <div>
              <label class="block text-xs font-medium text-gray-700 mb-1">Rate *</label>
              <input
                v-model.number="editService.rate"
                type="number"
                min="0"
                step="0.01"
                placeholder="0.00"
                class="w-full border border-gray-200 bg-gray-50 px-4 py-3 rounded-xl focus:ring-2 focus:ring-blue-500/30 focus:border-blue-500 text-base"
                @keyup.enter="handleUpdateService"
              />
            </div>
          </div>
        </div>

        <div class="flex flex-col-reverse sm:flex-row sm:justify-end gap-2 sm:gap-3 mt-6">
          <button
            @click="showEditModal = false"
            class="w-full sm:w-auto px-4 py-3 bg-gray-100 text-gray-700 rounded-xl hover:bg-gray-200 transition-colors text-sm font-medium"
          >
            Cancel
          </button>
          <button
            @click="handleUpdateService"
            class="w-full sm:w-auto px-4 py-3 bg-blue-600 text-white rounded-xl hover:bg-blue-700 transition-colors text-sm font-medium"
          >
            Save Changes
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* Smooth scrolling */
html {
  scroll-behavior: smooth;
}

/* Better touch targets for mobile */
@media (max-width: 640px) {
  button, 
  [role="button"],
  input,
  select,
  textarea {
    min-height: 44px;
  }
  
  .table-container {
    -webkit-overflow-scrolling: touch;
  }
}

/* Loading animation */
@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
}

.animate-pulse {
  animation: pulse 2s cubic-bezier(0.4, 0, 0.6, 1) infinite;
}

/* Transition animations */
.translate-y-full {
  transform: translateY(100%);
}

.translate-y-0 {
  transform: translateY(0);
}

/* Custom scrollbar */
::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
}

::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 4px;
}

::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

/* Ensure all inputs and buttons use full width on mobile */
@media (max-width: 640px) {
  .max-w-full {
    width: 100%;
  }
}
</style>
