<template>
  <div class="bg-white rounded-lg shadow-sm hover:shadow-md transition-shadow duration-200 border border-gray-100 overflow-hidden w-full">
    <div class="p-4 sm:p-5">
      <!-- Service Header -->
      <div class="flex flex-col sm:flex-row sm:justify-between sm:items-start gap-2 mb-3">
        <div>
          <h3 class="text-base sm:text-lg font-semibold text-gray-900">{{ service.name }}</h3>
          <span 
            class="inline-block mt-1 px-2 py-1 text-xs font-medium rounded-full"
            :class="serviceTypeClass"
          >
            {{ getServiceTypeLabel(serviceType) }}
          </span>
        </div>
        <div class="sm:text-right">
          <p class="text-xl sm:text-2xl font-bold text-[#3C3C9E]">ETB {{ service.currentRate }}</p>
          <p class="text-xs text-gray-500">per {{ rateUnitLabel }}</p>
        </div>
      </div>
      
      <!-- Service Description -->
      <p class="text-sm text-gray-600 mb-4 line-clamp-2">{{ service.description }}</p>
      
      <!-- Service Features -->
      <div class="space-y-2 mb-4">
        <div v-for="(feature, index) in serviceFeatures" :key="index" class="flex items-center text-sm text-gray-600">
          <svg class="h-4 w-4 text-green-500 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
          </svg>
          {{ feature }}
        </div>
      </div>
      
      <!-- Service Specific Badge -->
      <div v-if="service.name?.toLowerCase().includes('car wash')" class="mb-4 p-3 bg-blue-50 rounded-lg">
        <div class="flex items-center text-sm text-blue-700">
          <svg class="h-5 w-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
          </svg>
          <span class="font-medium">Appointment Required</span>
        </div>
      </div>
      
      <div v-else-if="service.name?.toLowerCase().includes('parking')" class="mb-4 p-3 bg-green-50 rounded-lg">
        <div class="flex items-center text-sm text-green-700">
          <svg class="h-5 w-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
          </svg>
          <span class="font-medium">Real-time Slot Availability</span>
        </div>
      </div>
      
      <div v-else-if="service.name?.toLowerCase().includes('Meskel')" class="mb-4 p-3 bg-purple-50 rounded-lg">
        <div class="flex items-center text-sm text-purple-700">
          <svg class="h-5 w-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M14.828 14.828a4 4 0 01-5.656 0M9 10h.01M15 10h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
          <span class="font-medium">Family-friendly Fun</span>
        </div>
      </div>
      
      <!-- Book Button -->
      <button
        @click="$emit('select', service)"
        class="w-full py-3 sm:py-2.5 px-4 bg-[#3C3C9E] hover:bg-[#2c2c7a] text-white font-medium rounded-lg transition-colors duration-200 flex items-center justify-center space-x-2"
      >
        <span>Book Now</span>
        <svg class="h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
        </svg>
      </button>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue';

const props = defineProps({
  service: {
    type: Object,
    required: true
  }
});

defineEmits(['select']);

const serviceTypeClass = computed(() => {
  switch (serviceType.value) {
    case 'CAR_WASH':
      return 'bg-blue-100 text-blue-800';
    case 'PARKING':
      return 'bg-green-100 text-green-800';
    case 'AMUSEMENT':
      return 'bg-purple-100 text-purple-800';
    default:
      return 'bg-gray-100 text-gray-800';
  }
});

const getServiceTypeLabel = (type) => {
  switch (type) {
    case 'CAR_WASH': return 'Car Wash';
    case 'PARKING': return 'Parking';
    case 'AMUSEMENT': return 'Amusement Park';
    default: return type;
  }
};

const serviceType = computed(() => {
  if (props.service.type) return props.service.type

  const name = props.service.name?.toLowerCase() || ''
  if (name.includes('car wash')) return 'CAR_WASH'
  if (name.includes('parking')) return 'PARKING'
  if (name.includes('meskel') || name.includes('amusement')) return 'AMUSEMENT'
  return 'SERVICE'
})

const rateUnitLabel = computed(() => {
  return props.service.pricingType === 'HOURLY' ? 'hour' : 'booking'
})

const serviceFeatures = computed(() => {
  const name = props.service.name?.toLowerCase() || '';

  if (name.includes('car wash')) {
    return [
      'Professional hand wash',
      'Interior vacuuming',
      'Tire shine',
      'Window cleaning'
    ];
  }

  if (name.includes('parking')) {
    return [
      '24/7 security surveillance',
      'Covered parking',
      'EV charging available',
      'Valet option'
    ];
  }

  if (name.includes('Meskel')) {
    return [
      'All-day access',
      'Ride passes included',
      'Free parking',
      'Food court access'
    ];
  }

  return ['Premium service', 'Professional staff'];
});

</script>
