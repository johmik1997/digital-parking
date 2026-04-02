<template>
  <div class="space-y-3 sm:space-y-4">
    <!-- Empty State -->
    <div v-if="orders.length === 0" class="text-center py-8 sm:py-12 bg-white rounded-xl">
      <svg class="mx-auto h-10 w-10 sm:h-12 sm:w-12 text-gray-300" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
      </svg>
      <h3 class="mt-2 text-sm font-medium text-gray-700">No service history</h3>
      <p class="mt-1 text-xs sm:text-sm text-gray-500">You haven't ordered any services yet.</p>
    </div>

    <!-- Order Cards -->
    <div
      v-for="order in orders"
      :key="order.id"
      class="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden hover:shadow-md transition-shadow"
    >
      <!-- Status Bar -->
      <div class="flex items-center justify-between px-3 sm:px-4 py-2 bg-gray-50 border-b border-gray-100">
        <p class="text-xs font-medium text-gray-500">Order #{{ order.id }}</p>
        <span
          :class="[
            'px-2 py-0.5 text-[10px] sm:text-xs font-medium rounded-full',
            order.status === 'COMPLETED' ? 'bg-green-100 text-green-700' : 
            order.status === 'PENDING' ? 'bg-yellow-100 text-yellow-700' :
            order.status === 'PROCESSING' ? 'bg-blue-100 text-blue-700' :
            order.status === 'CANCELLED' ? 'bg-gray-100 text-gray-700' :
            'bg-red-100 text-red-700'
          ]"
        >
          {{ order.status }}
        </span>
      </div>

      <!-- Main Content -->
      <div class="p-3 sm:p-4">
        <!-- Service Name -->
        <h4 class="text-sm sm:text-base font-semibold text-gray-900 mb-3">{{ order.serviceName }}</h4>

        <!-- Details Grid -->
        <div class="grid grid-cols-2 gap-3 mb-3">
          <div class="bg-gray-50 rounded-lg p-2">
            <p class="text-[10px] sm:text-xs text-gray-500 mb-0.5">{{ getDateLabel(order) }}</p>
            <p class="text-xs sm:text-sm font-medium text-gray-900">{{ getDateValue(order) }}</p>
            <p v-if="getDateMeta(order)" class="mt-1 text-[10px] sm:text-xs text-gray-500">{{ getDateMeta(order) }}</p>
          </div>
          <div class="bg-gray-50 rounded-lg p-2">
            <p class="text-[10px] sm:text-xs text-gray-500 mb-0.5">Duration</p>
            <p class="text-xs sm:text-sm font-medium text-gray-900">{{ order.duration ? `${order.duration}h` : 'Fixed' }}</p>
          </div>
          <div class="bg-gray-50 rounded-lg p-2">
            <p class="text-[10px] sm:text-xs text-gray-500 mb-0.5">Rate</p>
            <p class="text-xs sm:text-sm font-medium text-gray-900">${{ order.rateApplied }}/{{ order.pricingType === 'HOURLY' ? 'hr' : 'job' }}</p>
          </div>
          <div class="bg-[#3C3C9E]/5 rounded-lg p-2 border border-[#3C3C9E]/10">
            <p class="text-[10px] sm:text-xs text-[#3C3C9E]/70 mb-0.5">Total</p>
            <p class="text-sm sm:text-base font-bold text-[#3C3C9E]">${{ order.totalAmount }}</p>
          </div>
        </div>

        <div v-if="order.parkingLocationDisplay || order.navigationInstructions" class="mb-3 rounded-xl border border-emerald-100 bg-emerald-50/70 p-3">
          <p v-if="order.parkingLocationDisplay" class="text-xs font-semibold text-emerald-700">
            {{ order.parkingLocationDisplay }}
          </p>
          <p v-if="order.entranceName" class="mt-1 text-[11px] text-gray-600">
            Entrance: {{ order.entranceName }}
          </p>
          <p v-if="order.navigationInstructions" class="mt-1 text-[11px] text-gray-600">
            {{ order.navigationInstructions }}
          </p>
        </div>

        <!-- Rate Reference (if available) -->
        <div v-if="order.rateUuid" class="mb-3">
          <div class="bg-gray-50 rounded-lg px-2 py-1.5 inline-block">
            <p class="text-[8px] sm:text-[10px] text-gray-400 font-mono">
              Rate ID: {{ formatUuid(order.rateUuid) }}
            </p>
          </div>
        </div>

        <!-- Action Buttons -->
        <div class="flex flex-col sm:flex-row sm:justify-end gap-2 mt-2 pt-2 border-t border-gray-100">
          <a
            v-if="isRideReady(order)"
            :href="order.googleMapsUrl"
            target="_blank"
            rel="noopener noreferrer"
            class="max-w-full sm:w-auto px-3 py-2.5 sm:py-2 text-center text-xs sm:text-sm font-medium text-emerald-700 bg-emerald-50 hover:bg-emerald-100 rounded-lg transition-colors"
          >
            Navigate
          </a>
          <button
            @click="$emit('view-details', order)"
            class="max-w-full sm:w-auto px-3 py-2.5 sm:py-2 text-xs sm:text-sm font-medium text-[#3C3C9E] bg-[#3C3C9E]/5 hover:bg-[#3C3C9E]/10 rounded-lg transition-colors"
          >
            View Details
          </button>
          <button
            v-if="order.status === 'PENDING' || order.status === 'PROCESSING'"
            @click="$emit('cancel', order)"
            class="max-w-full sm:w-auto px-3 py-2.5 sm:py-2 text-xs sm:text-sm font-medium text-red-600 bg-red-50 hover:bg-red-100 rounded-lg transition-colors"
          >
            Cancel Order
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  orders: {
    type: Array,
    required: true
  }
});

defineEmits(['view-details', 'cancel']);

const formatDate = (dateString) => {
  const date = new Date(dateString);
  const now = new Date();
  const diffTime = Math.abs(now - date);
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
  
  if (diffDays === 1) {
    return `Yesterday, ${date.toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit' })}`;
  } else if (diffDays < 7) {
    return date.toLocaleDateString('en-US', { 
      weekday: 'short',
      hour: '2-digit', 
      minute: '2-digit' 
    });
  } else {
    return date.toLocaleDateString('en-US', { 
      month: 'short', 
      day: 'numeric',
      hour: '2-digit', 
      minute: '2-digit' 
    });
  }
};

const formatPlainDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  if (Number.isNaN(date.getTime())) return dateString;

  return date.toLocaleDateString('en-US', {
    month: 'short',
    day: 'numeric',
    year: 'numeric',
  });
};

const formatUuid = (uuid) => {
  if (!uuid) return '';
  return uuid.substring(0, 8) + '...' + uuid.substring(uuid.length - 4);
};

const isRideReady = (order) => {
  return `${order?.serviceType || ''}`.toUpperCase() === 'PARKING' &&
    `${order?.status || ''}`.toUpperCase() === 'COMPLETED' &&
    !!order?.googleMapsUrl;
};

const getDateLabel = (order) => {
  return `${order?.serviceType || ''}`.toUpperCase() === 'PARKING' ? 'Scheduled' : 'Date';
};

const getDateValue = (order) => {
  if (`${order?.serviceType || ''}`.toUpperCase() === 'PARKING') {
    const parkingDate = formatPlainDate(order?.parkingDate);
    const parkingTime = order?.scheduledEntryTime || order?.entryTime;
    if (parkingDate && parkingTime) return `${parkingDate}, ${parkingTime}`;
    return parkingDate || parkingTime || formatDate(order?.createdAt);
  }

  return formatDate(order?.createdAt);
};

const getDateMeta = (order) => {
  if (`${order?.serviceType || ''}`.toUpperCase() !== 'PARKING') return '';
  return order?.createdAt ? `Ordered ${formatDate(order.createdAt)}` : '';
};
</script>
