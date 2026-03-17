<template>
  <div class="min-h-screen bg-gradient-to-br from-slate-50 via-white to-emerald-50/40">
    <!-- Header -->
    <header class="sticky top-0 z-10 border-b border-slate-200 bg-white/90 backdrop-blur">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 py-4 flex items-center justify-between">
        <div class="flex items-center gap-3">
          <div class="h-10 w-10 rounded-xl bg-gradient-to-br from-emerald-600 to-teal-600 text-white grid place-items-center shadow">
            <svg class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
            </svg>
          </div>
          <div>
            <h1 class="text-xl font-bold text-slate-900">Customer Dashboard</h1>
            <p class="text-xs text-slate-500">Welcome back! Manage your parking in one place.</p>
          </div>
        </div>
        <div class="hidden sm:flex items-center gap-2 text-xs text-slate-500">
          <span class="h-2 w-2 rounded-full bg-emerald-500"></span>
          <span>Account Active</span>
        </div>
      </div>
    </header>

    <main class="max-w-7xl mx-auto px-4 sm:px-6 py-6 space-y-6">
      <!-- Quick Actions -->
      <section class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-2 gap-4">
        <div class="bg-white border border-slate-200 rounded-2xl p-5 shadow-sm">
          <h2 class="text-sm font-semibold text-slate-700">Start Service Order</h2>
          <p class="text-xs text-slate-500 mt-1">Book parking, car wash, or other services.</p>
          <RouterLink
            to="/service/order"
            class="inline-block mt-4 px-4 py-2 rounded-lg bg-emerald-600 text-white text-xs font-bold hover:bg-emerald-700"
          >
            New Order
          </RouterLink>
        </div>
        <div class="bg-white border border-slate-200 rounded-2xl p-5 shadow-sm">
          <h2 class="text-sm font-semibold text-slate-700">Service Catalog</h2>
          <p class="text-xs text-slate-500 mt-1">Browse available services and rates.</p>
          <RouterLink
            to="/service/product"
            class="inline-block mt-4 px-4 py-2 rounded-lg bg-blue-600 text-white text-xs font-bold hover:bg-blue-800"
          >
            View Services
          </RouterLink>
        </div>
      </section>

      <!-- Summary Cards -->
      <section class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <div class="bg-white border border-slate-200 rounded-2xl p-5 shadow-sm">
          <p class="text-xs text-slate-500">Total Payments</p>
          <p class="text-2xl font-bold text-slate-900 mt-2">{{ overview.completedOrders ?? 0 }}</p>
          <p class="text-[11px] text-slate-400 mt-1">Completed by your account</p>
        </div>
        <div class="bg-white border border-slate-200 rounded-2xl p-5 shadow-sm">
          <p class="text-xs text-slate-500">Total Spent</p>
          <p class="text-2xl font-bold text-emerald-700 mt-2">ETB {{ formatAmount(overview.totalSpent) }}</p>
          <p class="text-[11px] text-slate-400 mt-1">All completed orders</p>
        </div>
        <div class="bg-white border border-slate-200 rounded-2xl p-5 shadow-sm">
          <p class="text-xs text-slate-500">Pending Orders</p>
          <p class="text-2xl font-bold text-amber-600 mt-2">{{ overview.pendingOrders ?? 0 }}</p>
          <p class="text-[11px] text-slate-400 mt-1">ETB {{ formatAmount(overview.pendingAmount) }}</p>
        </div>
        <div class="bg-white border border-slate-200 rounded-2xl p-5 shadow-sm">
          <p class="text-xs text-slate-500">Success Rate</p>
          <p class="text-2xl font-bold text-slate-900 mt-2">{{ formatPercent(paymentSuccess.successRate) }}</p>
          <p class="text-[11px] text-slate-400 mt-1">Payments completed</p>
        </div>
      </section>

      <!-- Status Cards -->
      <section class="grid grid-cols-1 lg:grid-cols-3 gap-4">
        <div class="lg:col-span-2 bg-white border border-slate-200 rounded-2xl p-5 shadow-sm">
          <h2 class="text-sm font-semibold text-slate-700">Active Parking</h2>
          <div class="mt-4 flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3">
            <div class="text-sm text-slate-600">
              {{ loading ? "Loading your status..." : "No active parking session right now." }}
            </div>
            <RouterLink
              to="/service/order"
              class="px-4 py-2 rounded-lg border border-emerald-600 text-emerald-700 text-xs font-bold hover:bg-emerald-50"
            >
              Start New Session
            </RouterLink>
          </div>
        </div>
      </section>

      <!-- Service Breakdown -->
      <section class="bg-white border border-slate-200 rounded-2xl p-5 shadow-sm">
        <h2 class="text-sm font-semibold text-slate-700">Service Breakdown</h2>
        <div v-if="serviceTypeCounts.length === 0" class="mt-4 text-xs text-slate-500">
          No service history yet.
        </div>
        <div v-else class="mt-4 grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-3">
          <div
            v-for="item in serviceTypeCounts"
            :key="item.serviceType"
            class="border border-slate-200 rounded-xl p-4"
          >
            <p class="text-xs text-slate-500">{{ item.serviceType }}</p>
            <p class="text-lg font-bold text-slate-900 mt-1">{{ item.count }}</p>
          </div>
        </div>
      </section>

      <!-- Recent Activity -->
      <section class="bg-white border border-slate-200 rounded-2xl p-5 shadow-sm">
        <h2 class="text-sm font-semibold text-slate-700">Recent Activity</h2>
        <div v-if="loading" class="mt-4 text-xs text-slate-500">Loading recent orders...</div>
        <div v-else-if="recentOrders.length === 0" class="mt-4 text-xs text-slate-500">
          No recent activity yet.
        </div>
        <div v-else class="mt-4 overflow-x-auto">
          <table class="min-w-full text-xs">
            <thead>
              <tr class="text-left text-slate-500">
                <th class="py-2 pr-4">Service</th>
                <th class="py-2 pr-4">Status</th>
                <th class="py-2 pr-4">Amount</th>
                <th class="py-2">Created</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="order in recentOrders" :key="order.orderUuid" class="border-t border-slate-100">
                <td class="py-2 pr-4 text-slate-700">{{ order.serviceType || order.serviceName }}</td>
                <td class="py-2 pr-4">
                  <span :class="statusClass(order.status)" class="px-2 py-0.5 rounded-full text-[10px] font-semibold">
                    {{ order.status }}
                  </span>
                </td>
                <td class="py-2 pr-4 text-slate-700">ETB {{ formatAmount(order.totalAmount) }}</td>
                <td class="py-2 text-slate-500">{{ formatDate(order.createdAt) }}</td>
              </tr>
            </tbody>
          </table>
        </div>
        <div v-if="error" class="mt-3 text-xs text-rose-600">{{ error }}</div>
      </section>
    </main>
  </div>
</template>

<script setup>
import { RouterLink } from "vue-router";
import { computed, onMounted, ref } from "vue";
import { useAuth } from "@/stores/auth";

const authStore = useAuth();
const summary = ref(null);
const loading = ref(true);
const error = ref("");

const apiBase = import.meta.env.v_API_URL;

const overview = computed(() => summary.value?.overview || {});
const paymentSuccess = computed(() => summary.value?.paymentSuccess || {});
const recentOrders = computed(() => summary.value?.recentOrders || []);
const serviceTypeCounts = computed(() => summary.value?.serviceTypeCounts || []);

const displayName = computed(() => {
  const user = authStore.auth?.user;
  return user?.firstName || user?.fullName || user?.email || "Customer";
});

const formatAmount = (value) => {
  const numberValue = Number(value || 0);
  return numberValue.toLocaleString();
};

const formatPercent = (value) => {
  const numberValue = Number(value || 0);
  return `${numberValue.toFixed(1)}%`;
};

const formatDate = (value) => {
  if (!value) return "-";
  return value.replace("T", " ").slice(0, 16);
};

const statusClass = (status) => {
  switch (status) {
    case "COMPLETED":
      return "bg-emerald-100 text-emerald-700";
    case "PENDING":
      return "bg-amber-100 text-amber-700";
    case "FAILED":
    case "CANCELLED":
      return "bg-rose-100 text-rose-700";
    default:
      return "bg-slate-100 text-slate-600";
  }
};

onMounted(async () => {
  try {
    const token = authStore.auth?.accessToken;
    const response = await fetch(`${apiBase}/dashboard/customer/summary`, {
      headers: token ? { Authorization: `Bearer ${token}` } : {},
    });
    if (!response.ok) {
      throw new Error("Failed to load customer dashboard");
    }
    summary.value = await response.json();
  } catch (err) {
    error.value = err?.message || "Customer dashboard unavailable";
  } finally {
    loading.value = false;
  }
});
</script>
