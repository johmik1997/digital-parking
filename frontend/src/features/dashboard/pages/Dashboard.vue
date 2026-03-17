<script setup>
import { computed, onMounted, ref } from "vue";
import { useAuth } from "@/stores/auth";
import icons from "@/utils/icons";

const authStore = useAuth();

const apiBase =
  import.meta.env.VITE_API_URL ||
  "http://localhost:8280/api/medco-digital-parking/v1";

const loading = ref(true);
const error = ref(null);
const summary = ref({
  overview: {
    totalUsers: 0,
    activeUsers: 0,
    activeUserStatus: 0,
    totalServices: 0,
    activeServices: 0,
    totalOrders: 0,
    pendingOrders: 0,
    completedOrders: 0,
    totalRevenue: 0,
    pendingRevenue: 0,
  },
  last7DaysRevenue: [],
  serviceTypeCounts: [],
  revenueByServiceType: [],
  occupancy: { capacity: 0, occupied: 0, available: 0, utilizationPercent: 0 },
  paymentSuccess: { completed: 0, failed: 0, cancelled: 0, total: 0, successRate: 0 },
  peakHours: [],
  alerts: [],
  recentOrders: [],
});

const revenueMax = computed(() => {
  const values = summary.value.last7DaysRevenue.map((d) => Number(d.total || 0));
  return Math.max(1, ...values);
});

const totalServiceTypeCount = computed(() =>
  summary.value.serviceTypeCounts.reduce((acc, item) => acc + Number(item.count || 0), 0)
);

const totalRevenueByService = computed(() =>
  summary.value.revenueByServiceType.reduce((acc, item) => acc + Number(item.total || 0), 0)
);

const formatCurrency = (value) => {
  const amount = Number(value || 0);
  return new Intl.NumberFormat("en-ET", {
    style: "currency",
    currency: "ETB",
    maximumFractionDigits: 2,
  }).format(amount);
};

const formatDate = (value) => {
  if (!value) return "—";
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return value;
  return date.toLocaleString("en-ET", {
    year: "numeric",
    month: "short",
    day: "2-digit",
    hour: "2-digit",
    minute: "2-digit",
  });
};

const shortUuid = (value) => {
  if (!value) return "—";
  return value.slice(0, 8);
};

const exportRecentOrders = () => {
  const rows = summary.value.recentOrders.map((order) => ({
    orderUuid: order.orderUuid,
    serviceName: order.serviceName,
    serviceType: order.serviceType,
    vehiclePlate: order.vehiclePlate,
    totalAmount: order.totalAmount,
    status: order.status,
    createdAt: order.createdAt,
  }));

  if (!rows.length) return;

  const headers = Object.keys(rows[0]);
  const csv = [
    headers.join(","),
    ...rows.map((row) =>
      headers
        .map((key) => `"${String(row[key] ?? "").replace(/"/g, '""')}"`)
        .join(",")
    ),
  ].join("\n");

  const blob = new Blob([csv], { type: "text/csv;charset=utf-8;" });
  const url = URL.createObjectURL(blob);
  const link = document.createElement("a");
  link.href = url;
  link.download = "recent-orders.csv";
  link.click();
  URL.revokeObjectURL(url);
};

const statusClasses = (status) => {
  switch (status) {
    case "COMPLETED":
      return "bg-green-100 text-green-700";
    case "PENDING":
      return "bg-amber-100 text-amber-700";
    case "CANCELLED":
    case "FAILED":
      return "bg-rose-100 text-rose-700";
    default:
      return "bg-gray-100 text-gray-700";
  }
};

onMounted(async () => {
  try {
    const token = authStore.auth?.accessToken;
    const response = await fetch(`${apiBase}/dashboard/summary`, {
      headers: token ? { Authorization: `Bearer ${token}` } : {},
    });
    if (!response.ok) {
      throw new Error("Failed to load dashboard data");
    }
    summary.value = await response.json();
  } catch (err) {
    error.value = err?.message || "Dashboard data unavailable";
  } finally {
    loading.value = false;
  }
});
</script>

<template>
  <div class="min-h-screen w-full bg-gray-50/50">
    <!-- Main Container - Mobile First -->
    <div class="max-w-7xl mx-auto px-3 sm:px-4 md:px-6 py-4 sm:py-6 md:py-8">
      
      <!-- Header Section -->
      <div class="flex flex-col sm:flex-row justify-between items-start sm:items-center mb-4 sm:mb-6 gap-3">
        <div>
          <h1 class="text-xl sm:text-2xl md:text-3xl font-bold text-gray-800">Dashboard</h1>
          <p class="text-xs sm:text-sm text-gray-500 mt-1">
            Live operational snapshot from the parking system.
          </p>
        </div>
        <!-- <div class="flex items-center gap-2 w-full sm:w-auto">
          <button class="flex-1 sm:flex-none px-4 py-2 bg-white border border-gray-200 rounded-xl text-sm text-gray-700 hover:bg-gray-50 transition-colors flex items-center justify-center gap-2">
            <svg class="h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 4h13M3 8h9m-9 4h6m4 0l4-4m0 0l4 4m-4-4v12" />
            </svg>
            <span class="hidden sm:inline">Filter</span>
          </button>
          <button class="flex-1 sm:flex-none px-4 py-2 bg-[#4318FF] text-white rounded-xl text-sm hover:bg-[#3311DD] transition-colors flex items-center justify-center gap-2">
            <svg class="h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
            </svg>
            <span class="hidden sm:inline">New Report</span>
          </button>
        </div> -->
      </div>

      <!-- Stats Cards - Horizontal Scroll on Mobile -->
      <div class="overflow-x-auto pb-2 -mx-3 px-3 sm:overflow-visible sm:pb-0 sm:mx-0 sm:px-0">
        <div class="flex sm:grid sm:grid-cols-3 gap-3 sm:gap-4 min-w-min sm:min-w-0">
          
          <!-- Total Revenue Card -->
          <div class="bg-white rounded-xl sm:rounded-2xl p-4 sm:p-5 shadow-sm border border-gray-100 flex-shrink-0 w-64 sm:w-auto hover:shadow-md transition-shadow">
            <div class="flex items-start justify-between">
              <div class="flex items-center gap-3">
                <div class="h-10 w-10 sm:h-12 sm:w-12 bg-[#4318FF]/10 rounded-xl flex items-center justify-center">
                  <i v-html="icons.bar" class="w-5 h-5 sm:w-6 sm:h-6 text-[#4318FF]" />
                </div>
                <div>
                  <p class="text-xs sm:text-sm text-gray-500">Total Revenue</p>
                  <p class="text-lg sm:text-xl md:text-2xl font-bold text-gray-800 mt-1">
                    {{ formatCurrency(summary.overview.totalRevenue) }}
                  </p>
                </div>
              </div>
              <span class="text-xs bg-green-100 text-green-600 px-2 py-1 rounded-full font-medium">
                Completed
              </span>
            </div>
          </div>

          <!-- Orders Card -->
          <div class="bg-white rounded-xl sm:rounded-2xl p-4 sm:p-5 shadow-sm border border-gray-100 flex-shrink-0 w-64 sm:w-auto hover:shadow-md transition-shadow">
            <div class="flex items-start justify-between">
              <div class="flex items-center gap-3">
                <div class="h-10 w-10 sm:h-12 sm:w-12 bg-[#6AD2FF]/10 rounded-xl flex items-center justify-center">
                  <i v-html="icons.logo" class="w-5 h-5 sm:w-6 sm:h-6 text-[#6AD2FF]" />
                </div>
                <div>
                  <p class="text-xs sm:text-sm text-gray-500">Orders</p>
                  <p class="text-lg sm:text-xl md:text-2xl font-bold text-gray-800 mt-1">
                    {{ summary.overview.totalOrders }}
                  </p>
                  <p class="text-xs text-gray-400 mt-0.5">
                    Pending: {{ summary.overview.pendingOrders }}
                  </p>
                </div>
              </div>
              <span class="text-xs bg-blue-100 text-blue-600 px-2 py-1 rounded-full font-medium">
                {{ summary.overview.completedOrders }} done
              </span>
            </div>
          </div>

          <!-- Users Card -->
          <div class="bg-white rounded-xl sm:rounded-2xl p-4 sm:p-5 shadow-sm border border-gray-100 flex-shrink-0 w-64 sm:w-auto hover:shadow-md transition-shadow">
            <div class="flex items-start justify-between">
              <div class="flex items-center gap-3">
                <div class="h-10 w-10 sm:h-12 sm:w-12 bg-purple-100 rounded-xl flex items-center justify-center">
                  <i v-html="icons.task" class="w-5 h-5 sm:w-6 sm:h-6 text-purple-600" />
                </div>
                <div>
                  <p class="text-xs sm:text-sm text-gray-500">Users</p>
                  <p class="text-lg sm:text-xl md:text-2xl font-bold text-gray-800 mt-1">
                    {{ summary.overview.totalUsers }}
                  </p>
                  <p class="text-xs text-gray-400 mt-0.5">
                    Active: {{ summary.overview.activeUserStatus }}
                  </p>
                </div>
              </div>
              <span class="text-xs bg-purple-100 text-purple-600 px-2 py-1 rounded-full font-medium">
                {{ summary.overview.activeServices }} services
              </span>
            </div>
          </div>
        </div>
      </div>

      <!-- Charts Section - Stack on Mobile, Grid on Desktop -->
        <div class="mt-4 sm:mt-6 grid grid-cols-1 lg:grid-cols-2 gap-4 sm:gap-6">
        <!-- Weekly Revenue -->
        <div class="bg-white rounded-xl sm:rounded-2xl p-4 sm:p-6 shadow-sm border border-gray-100">
          <div class="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-2 mb-4 sm:mb-6">
            <div>
              <h2 class="text-base sm:text-lg md:text-xl font-semibold text-gray-800">Revenue (Last 7 Days)</h2>
              <p class="text-xs sm:text-sm text-gray-500 mt-0.5">Completed orders only</p>
            </div>
            <span class="text-xs bg-gray-100 text-gray-700 px-3 py-1.5 rounded-lg">
              {{ formatCurrency(summary.overview.totalRevenue) }}
            </span>
          </div>

          <div class="flex items-end gap-3 sm:gap-4 h-40">
            <div
              v-for="day in summary.last7DaysRevenue"
              :key="day.day"
              class="flex-1 flex flex-col items-center gap-2"
            >
              <div class="w-full bg-[#EFF4FB] rounded-lg h-full flex items-end">
                <div
                  class="w-full bg-[#4318FF] rounded-lg transition-all"
                  :style="{ height: `${(Number(day.total || 0) / revenueMax) * 100}%` }"
                ></div>
              </div>
              <span class="text-xs text-gray-500">{{ day.day.slice(5) }}</span>
            </div>
          </div>
        </div>
        
        <!-- Service Mix -->
        <div class="bg-white rounded-xl sm:rounded-2xl p-4 sm:p-6 shadow-sm border border-gray-100">
          <div class="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-2 mb-4 sm:mb-6">
            <div>
              <h2 class="text-base sm:text-lg md:text-xl font-semibold text-gray-800">Service Mix</h2>
              <p class="text-xs sm:text-sm text-gray-500 mt-0.5">Orders by service type</p>
            </div>
          </div>

          <div class="space-y-4">
            <div
              v-for="item in summary.serviceTypeCounts"
              :key="item.serviceType"
              class="space-y-1"
            >
              <div class="flex justify-between text-sm text-gray-700">
                <span>{{ item.serviceType }}</span>
                <span>{{ item.count }}</span>
              </div>
              <div class="h-2 w-full bg-gray-100 rounded-full">
                <div
                  class="h-2 bg-[#6AD2FF] rounded-full"
                  :style="{
                    width: `${totalServiceTypeCount ? (item.count / totalServiceTypeCount) * 100 : 0}%`,
                  }"
                ></div>
              </div>
            </div>
            <p v-if="!summary.serviceTypeCounts.length" class="text-xs text-gray-400">No orders yet.</p>
          </div>
        </div>
      </div>

      <!-- Ops Insights -->
      <div class="mt-4 sm:mt-6 grid grid-cols-1 lg:grid-cols-3 gap-4 sm:gap-6">
        <div class="bg-white rounded-xl sm:rounded-2xl p-4 sm:p-6 shadow-sm border border-gray-100">
          <div class="flex items-start justify-between">
            <div>
              <h2 class="text-base sm:text-lg md:text-xl font-semibold text-gray-800">Occupancy</h2>
              <p class="text-xs sm:text-sm text-gray-500 mt-0.5">Active parking capacity</p>
            </div>
            <span class="text-xs bg-emerald-100 text-emerald-700 px-2 py-1 rounded-full font-medium">
              {{ summary.occupancy.utilizationPercent }}%
            </span>
          </div>
          <div class="mt-4 space-y-2 text-sm text-gray-600">
            <div class="flex justify-between">
              <span>Capacity</span>
              <span class="font-semibold text-gray-800">{{ summary.occupancy.capacity }}</span>
            </div>
            <div class="flex justify-between">
              <span>Occupied</span>
              <span class="font-semibold text-gray-800">{{ summary.occupancy.occupied }}</span>
            </div>
            <div class="flex justify-between">
              <span>Available</span>
              <span class="font-semibold text-gray-800">{{ summary.occupancy.available }}</span>
            </div>
          </div>
          <div class="mt-4 h-2 w-full bg-gray-100 rounded-full">
            <div
              class="h-2 bg-emerald-400 rounded-full"
              :style="{ width: `${summary.occupancy.utilizationPercent}%` }"
            ></div>
          </div>
        </div>

        <div class="bg-white rounded-xl sm:rounded-2xl p-4 sm:p-6 shadow-sm border border-gray-100">
          <div class="flex items-start justify-between">
            <div>
              <h2 class="text-base sm:text-lg md:text-xl font-semibold text-gray-800">Payment Success</h2>
              <p class="text-xs sm:text-sm text-gray-500 mt-0.5">Completed vs failed</p>
            </div>
            <span class="text-xs bg-indigo-100 text-indigo-700 px-2 py-1 rounded-full font-medium">
              {{ summary.paymentSuccess.successRate }}%
            </span>
          </div>
          <div class="mt-4 grid grid-cols-3 gap-2 text-xs text-gray-500">
            <div class="rounded-lg bg-green-50 p-3 text-center">
              <p class="text-lg font-semibold text-green-700">{{ summary.paymentSuccess.completed }}</p>
              <p>Completed</p>
            </div>
            <div class="rounded-lg bg-rose-50 p-3 text-center">
              <p class="text-lg font-semibold text-rose-700">{{ summary.paymentSuccess.failed }}</p>
              <p>Failed</p>
            </div>
            <div class="rounded-lg bg-amber-50 p-3 text-center">
              <p class="text-lg font-semibold text-amber-700">{{ summary.paymentSuccess.cancelled }}</p>
              <p>Cancelled</p>
            </div>
          </div>
        </div>

        <div class="bg-white rounded-xl sm:rounded-2xl p-4 sm:p-6 shadow-sm border border-gray-100">
          <div class="flex items-start justify-between">
            <div>
              <h2 class="text-base sm:text-lg md:text-xl font-semibold text-gray-800">Peak Hours</h2>
              <p class="text-xs sm:text-sm text-gray-500 mt-0.5">Top request windows</p>
            </div>
          </div>
          <div class="mt-4 space-y-3">
            <div
              v-for="slot in summary.peakHours"
              :key="slot.hour"
              class="flex items-center justify-between text-sm text-gray-600"
            >
              <span>{{ slot.hour }}</span>
              <span class="font-semibold text-gray-800">{{ slot.count }}</span>
            </div>
            <p v-if="!summary.peakHours.length" class="text-xs text-gray-400">No traffic yet.</p>
          </div>
        </div>
      </div>

      <div class="mt-4 sm:mt-6 grid grid-cols-1 lg:grid-cols-2 gap-4 sm:gap-6">
        <div class="bg-white rounded-xl sm:rounded-2xl p-4 sm:p-6 shadow-sm border border-gray-100">
          <div class="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-2 mb-4 sm:mb-6">
            <div>
              <h2 class="text-base sm:text-lg md:text-xl font-semibold text-gray-800">Revenue by Service</h2>
              <p class="text-xs sm:text-sm text-gray-500 mt-0.5">Completed orders only</p>
            </div>
            <span class="text-xs bg-gray-100 text-gray-700 px-3 py-1.5 rounded-lg">
              {{ formatCurrency(totalRevenueByService) }}
            </span>
          </div>
          <div class="space-y-4">
            <div v-for="item in summary.revenueByServiceType" :key="item.serviceType">
              <div class="flex justify-between text-sm text-gray-700">
                <span>{{ item.serviceType }}</span>
                <span>{{ formatCurrency(item.total) }}</span>
              </div>
              <div class="h-2 w-full bg-gray-100 rounded-full">
                <div
                  class="h-2 bg-[#4318FF] rounded-full"
                  :style="{
                    width: `${totalRevenueByService ? (item.total / totalRevenueByService) * 100 : 0}%`,
                  }"
                ></div>
              </div>
            </div>
            <p v-if="!summary.revenueByServiceType.length" class="text-xs text-gray-400">No revenue yet.</p>
          </div>
        </div>

        <div class="bg-white rounded-xl sm:rounded-2xl p-4 sm:p-6 shadow-sm border border-gray-100">
          <div class="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-2 mb-4 sm:mb-6">
            <div>
              <h2 class="text-base sm:text-lg md:text-xl font-semibold text-gray-800">Alerts</h2>
              <p class="text-xs sm:text-sm text-gray-500 mt-0.5">Failed or cancelled orders</p>
            </div>
          </div>
          <div class="space-y-3">
            <div
              v-for="alert in summary.alerts"
              :key="alert.orderUuid"
              class="rounded-xl border border-gray-100 p-3"
            >
              <div class="flex items-center justify-between">
                <p class="text-sm font-semibold text-gray-700">#{{ shortUuid(alert.orderUuid) }}</p>
                <span class="text-[10px] font-semibold px-2 py-1 rounded-full" :class="statusClasses(alert.status)">
                  {{ alert.status }}
                </span>
              </div>
              <p class="text-xs text-gray-500 mt-1">{{ alert.serviceType || "Service" }}</p>
              <div class="flex items-center justify-between mt-2 text-xs text-gray-600">
                <span>{{ alert.vehiclePlate || "—" }}</span>
                <span>{{ formatCurrency(alert.totalAmount) }}</span>
              </div>
              <p class="text-[11px] text-gray-400 mt-2">{{ formatDate(alert.createdAt) }}</p>
            </div>
            <p v-if="!summary.alerts.length" class="text-xs text-gray-400">No alerts.</p>
          </div>
        </div>
      </div>

      <!-- Recent Orders -->
      <div class="mt-4 sm:mt-6">
        <div class="bg-white rounded-xl sm:rounded-2xl p-4 sm:p-6 shadow-sm border border-gray-100">
          <div class="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-2 mb-4 sm:mb-6">
            <div>
              <h2 class="text-base sm:text-lg md:text-xl font-semibold text-gray-800">Recent Orders</h2>
              <p class="text-xs sm:text-sm text-gray-500 mt-0.5">Latest 5 orders logged</p>
            </div>
            <button
              class="text-xs bg-gray-100 text-gray-700 px-3 py-2 rounded-lg hover:bg-gray-200"
              @click="exportRecentOrders"
            >
              Export CSV
            </button>
          </div>

          <div class="overflow-x-auto hidden sm:block">
            <table class="min-w-full text-left text-sm">
              <thead class="text-xs uppercase text-gray-400">
                <tr>
                  <th class="py-2 pr-3">Order</th>
                  <th class="py-2 pr-3">Service</th>
                  <th class="py-2 pr-3">Plate</th>
                  <th class="py-2 pr-3">Amount</th>
                  <th class="py-2 pr-3">Status</th>
                  <th class="py-2 pr-3">Created</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-gray-100">
                <tr v-for="order in summary.recentOrders" :key="order.orderUuid">
                  <td class="py-3 pr-3 font-semibold text-gray-700">{{ shortUuid(order.orderUuid) }}</td>
                  <td class="py-3 pr-3 text-gray-600">
                    {{ order.serviceName }} <span class="text-xs text-gray-400">({{ order.serviceType }})</span>
                  </td>
                  <td class="py-3 pr-3 text-gray-600">{{ order.vehiclePlate || "—" }}</td>
                  <td class="py-3 pr-3 text-gray-700">{{ formatCurrency(order.totalAmount) }}</td>
                  <td class="py-3 pr-3">
                    <span class="text-xs font-semibold px-2 py-1 rounded-full" :class="statusClasses(order.status)">
                      {{ order.status }}
                    </span>
                  </td>
                  <td class="py-3 pr-3 text-gray-500">{{ formatDate(order.createdAt) }}</td>
                </tr>
              </tbody>
            </table>
            <p v-if="!summary.recentOrders.length" class="text-xs text-gray-400 mt-3">No orders found.</p>
          </div>
          <div class="sm:hidden space-y-3">
            <div
              v-for="order in summary.recentOrders"
              :key="order.orderUuid"
              class="border border-gray-100 rounded-xl p-3"
            >
              <div class="flex items-center justify-between">
                <p class="text-sm font-semibold text-gray-700">#{{ shortUuid(order.orderUuid) }}</p>
                <span class="text-[10px] font-semibold px-2 py-1 rounded-full" :class="statusClasses(order.status)">
                  {{ order.status }}
                </span>
              </div>
              <p class="text-xs text-gray-500 mt-1">{{ order.serviceName }} ({{ order.serviceType }})</p>
              <div class="flex items-center justify-between mt-2 text-xs text-gray-600">
                <span>{{ order.vehiclePlate || "—" }}</span>
                <span>{{ formatCurrency(order.totalAmount) }}</span>
              </div>
              <p class="text-[11px] text-gray-400 mt-2">{{ formatDate(order.createdAt) }}</p>
            </div>
            <p v-if="!summary.recentOrders.length" class="text-xs text-gray-400 mt-3">No orders found.</p>
          </div>
        </div>
      </div>

      <p v-if="error" class="text-xs text-rose-500 mt-4">{{ error }}</p>
      <p v-if="loading" class="text-xs text-gray-400 mt-2">Loading live dashboard data…</p>

    </div>
  </div>
</template>

<style scoped>
/* Mobile optimizations */
@media (max-width: 640px) {
  .overflow-x-auto {
    -webkit-overflow-scrolling: touch;
    scrollbar-width: none;
  }
  
  .overflow-x-auto::-webkit-scrollbar {
    display: none;
  }
}

/* Smooth transitions */
.transition-shadow {
  transition: box-shadow 0.2s ease-in-out;
}

/* Custom scrollbar for desktop */
@media (min-width: 640px) {
  ::-webkit-scrollbar {
    width: 8px;
    height: 8px;
  }
  
  ::-webkit-scrollbar-track {
    background: #f1f1f1;
    border-radius: 10px;
  }
  
  ::-webkit-scrollbar-thumb {
    background: #c1c1c1;
    border-radius: 10px;
  }
  
  ::-webkit-scrollbar-thumb:hover {
    background: #a8a8a8;
  }
}
</style>
