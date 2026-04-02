<template>
  <div class="min-h-screen bg-gradient-to-br from-slate-50 via-white to-emerald-50/40">
    <header class="sticky top-0 z-10 border-b border-slate-200 bg-white/90 backdrop-blur">
      <div class="mx-auto flex max-w-7xl items-center justify-between px-4 py-4 sm:px-6">
        <div class="flex items-center gap-3">
          <div class="grid h-10 w-10 place-items-center rounded-xl bg-gradient-to-br from-emerald-600 to-teal-600 text-white shadow">
            <svg class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
            </svg>
          </div>
          <div>
            <h1 class="text-xl font-bold text-slate-900">Customer Dashboard</h1>
            <p class="text-xs text-slate-500">
              {{ loading && !hasLoaded ? "Loading your latest orders..." : `Welcome back, ${displayName}.` }}
            </p>
          </div>
        </div>
        <div class="hidden items-center gap-2 text-xs text-slate-500 sm:flex">
          <span class="h-2 w-2 rounded-full bg-emerald-500"></span>
          <span>{{ overview.totalOrders || 0 }} orders tracked</span>
        </div>
      </div>
    </header>

    <main class="mx-auto flex max-w-7xl flex-col gap-6 px-4 py-6 sm:px-6">
      <div
        v-if="error"
        class="rounded-2xl border border-rose-200 bg-rose-50 px-4 py-3 text-sm text-rose-700"
      >
        {{ error }}
      </div>

      <section class="grid grid-cols-1 gap-4 sm:grid-cols-2 xl:grid-cols-4">
        <div class="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
          <p class="text-xs text-slate-500">Total Orders</p>
          <p class="mt-2 text-2xl font-bold text-slate-900">{{ overview.totalOrders ?? 0 }}</p>
          <p class="mt-1 text-[11px] text-slate-400">All service bookings on your account</p>
        </div>
        <div class="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
          <p class="text-xs text-slate-500">Completed Orders</p>
          <p class="mt-2 text-2xl font-bold text-emerald-700">{{ overview.completedOrders ?? 0 }}</p>
          <p class="mt-1 text-[11px] text-slate-400">Successful finishes so far</p>
        </div>
        <div class="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
          <p class="text-xs text-slate-500">Total Spent</p>
          <p class="mt-2 text-2xl font-bold text-slate-900">ETB {{ formatAmount(overview.totalSpent) }}</p>
          <p class="mt-1 text-[11px] text-slate-400">Completed orders only</p>
        </div>
        <div class="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
          <p class="text-xs text-slate-500">Pending Orders</p>
          <p class="mt-2 text-2xl font-bold text-amber-600">{{ overview.pendingOrders ?? 0 }}</p>
          <p class="mt-1 text-[11px] text-slate-400">
            Pending amount: ETB {{ formatAmount(overview.pendingAmount) }}
          </p>
        </div>
      </section>

      <section class="grid grid-cols-1 gap-4 lg:grid-cols-3">
        <div class="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm lg:col-span-2">
          <div class="flex flex-col gap-3 sm:flex-row sm:items-start sm:justify-between">
            <div>
              <h2 class="text-sm font-semibold text-slate-800">Latest Parking Reservation</h2>
              <p class="mt-1 text-xs text-slate-500">{{ latestParkingMessage }}</p>
            </div>
            <span
              v-if="latestParkingReservation"
              :class="statusClass(latestParkingReservation.status)"
              class="inline-flex rounded-full px-3 py-1 text-[11px] font-semibold"
            >
              {{ latestParkingReservation.status }}
            </span>
          </div>

          <div
            v-if="latestParkingReservation"
            class="mt-5 grid grid-cols-1 gap-3 sm:grid-cols-2 xl:grid-cols-4"
          >
            <div class="rounded-2xl border border-slate-200 bg-slate-50 px-4 py-3">
              <p class="text-[11px] uppercase tracking-wide text-slate-400">Slot</p>
              <p class="mt-2 text-sm font-semibold text-slate-900">{{ latestParkingLocation }}</p>
              <p class="mt-1 text-[11px] text-slate-500">{{ latestParkingReservation.serviceName }}</p>
            </div>
            <div class="rounded-2xl border border-slate-200 bg-slate-50 px-4 py-3">
              <p class="text-[11px] uppercase tracking-wide text-slate-400">Schedule</p>
              <p class="mt-2 text-sm font-semibold text-slate-900">
                {{ formatParkingSchedule(latestParkingReservation.parkingDate, latestParkingReservation.scheduledEntryTime) }}
              </p>
              <p class="mt-1 text-[11px] text-slate-500">Duration {{ latestParkingReservation.duration || "-" }}</p>
            </div>
            <div class="rounded-2xl border border-slate-200 bg-slate-50 px-4 py-3">
              <p class="text-[11px] uppercase tracking-wide text-slate-400">Entrance</p>
              <p class="mt-2 text-sm font-semibold text-slate-900">{{ latestParkingEntrance }}</p>
              <p class="mt-1 text-[11px] text-slate-500">
                Plate {{ latestParkingReservation.vehiclePlate || "-" }}
              </p>
            </div>
            <div class="rounded-2xl border border-slate-200 bg-slate-50 px-4 py-3">
              <p class="text-[11px] uppercase tracking-wide text-slate-400">Amount</p>
              <p class="mt-2 text-sm font-semibold text-slate-900">
                ETB {{ formatAmount(latestParkingReservation.totalAmount) }}
              </p>
              <p class="mt-1 text-[11px] text-slate-500">
                Ordered {{ formatDateTime(latestParkingReservation.createdAt) }}
              </p>
            </div>
          </div>

          <div
            v-else-if="!loading || hasLoaded"
            class="mt-5 rounded-2xl border border-dashed border-slate-200 bg-slate-50 px-4 py-5 text-sm text-slate-500"
          >
            Your dashboard will show the latest reserved slot here once you place a parking order.
          </div>

          <div class="mt-5 flex flex-wrap gap-3">
            <RouterLink
              to="/service/order"
              class="inline-flex rounded-xl bg-emerald-600 px-4 py-2 text-xs font-bold text-white transition hover:bg-emerald-700"
            >
              {{ latestParkingReservation ? "Book Another Slot" : "Book Parking" }}
            </RouterLink>
            <RouterLink
              to="/service/order?tab=history"
              class="inline-flex rounded-xl border border-slate-300 px-4 py-2 text-xs font-bold text-slate-700 transition hover:bg-slate-50"
            >
              View Order History
            </RouterLink>
          </div>
        </div>

        <div class="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
          <h2 class="text-sm font-semibold text-slate-800">Recent Momentum</h2>
          <p class="mt-1 text-xs text-slate-500">Your completed spending trend from the last 7 days.</p>

          <div class="mt-5 flex items-end gap-2">
            <div
              v-for="point in last7DaysRevenue"
              :key="point.day"
              class="flex flex-1 flex-col items-center gap-2"
            >
              <div class="flex h-24 w-full items-end">
                <div
                  class="w-full rounded-t-xl bg-gradient-to-t from-emerald-500 to-teal-400"
                  :style="{ height: `${Math.max(8, (Number(point.total || 0) / revenueMax) * 100)}%` }"
                ></div>
              </div>
              <p class="text-[10px] text-slate-400">{{ formatDayShort(point.day) }}</p>
            </div>
          </div>

          <div class="mt-5 rounded-2xl bg-slate-50 px-4 py-4">
            <p class="text-[11px] uppercase tracking-wide text-slate-400">Last 7 days total</p>
            <p class="mt-2 text-lg font-bold text-slate-900">ETB {{ formatAmount(last7DaysTotal) }}</p>
            <p class="mt-1 text-[11px] text-slate-500">
              Payment success rate {{ formatPercent(paymentSuccess.successRate) }}
            </p>
          </div>

          <div class="mt-4 rounded-2xl border border-slate-200 px-4 py-4">
            <p class="text-[11px] uppercase tracking-wide text-slate-400">Latest order</p>
            <div v-if="latestOrder" class="mt-2">
              <p class="text-sm font-semibold text-slate-900">{{ latestOrder.serviceName }}</p>
              <p class="mt-1 text-xs text-slate-500">
                {{ formatOrderMoment(latestOrder) }}
              </p>
              <p class="mt-2 text-xs text-slate-500">
                {{ shortOrderId(latestOrder.orderUuid) }} • ETB {{ formatAmount(latestOrder.totalAmount) }}
              </p>
            </div>
            <p v-else class="mt-2 text-xs text-slate-500">No recent orders yet.</p>
          </div>
        </div>
      </section>

      <section class="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
        <div class="flex flex-col gap-1 sm:flex-row sm:items-center sm:justify-between">
          <div>
            <h2 class="text-sm font-semibold text-slate-800">Service Breakdown</h2>
            <p class="text-xs text-slate-500">How your bookings are distributed by service type.</p>
          </div>
          <p class="text-xs text-slate-400">Total categories: {{ serviceTypeCounts.length }}</p>
        </div>

        <div v-if="serviceTypeCounts.length === 0" class="mt-4 text-xs text-slate-500">
          No service history yet.
        </div>
        <div v-else class="mt-4 grid grid-cols-1 gap-3 sm:grid-cols-2 xl:grid-cols-4">
          <div
            v-for="item in serviceTypeCounts"
            :key="item.serviceType"
            class="rounded-2xl border border-slate-200 p-4"
          >
            <p class="text-xs text-slate-500">{{ item.serviceType }}</p>
            <p class="mt-1 text-lg font-bold text-slate-900">{{ item.count }}</p>
            <p class="mt-1 text-[11px] text-slate-400">
              {{ formatServiceShare(item.count) }} of your orders
            </p>
          </div>
        </div>
      </section>

      <section class="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
        <h2 class="text-sm font-semibold text-slate-800">Recent Activity</h2>
        <div v-if="loading && !hasLoaded" class="mt-4 text-xs text-slate-500">Loading recent orders...</div>
        <div v-else-if="recentOrders.length === 0" class="mt-4 text-xs text-slate-500">
          No recent activity yet.
        </div>
        <div v-else class="mt-4 overflow-x-auto">
          <table class="min-w-full text-xs">
            <thead>
              <tr class="text-left text-slate-500">
                <th class="py-2 pr-4">Order</th>
                <th class="py-2 pr-4">Status</th>
                <th class="py-2 pr-4">Amount</th>
                <th class="py-2">When</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="order in recentOrders" :key="order.orderUuid" class="border-t border-slate-100">
                <td class="py-3 pr-4">
                  <p class="font-semibold text-slate-800">{{ order.serviceName }}</p>
                  <p class="mt-1 text-[11px] text-slate-500">
                    {{ order.serviceType || "SERVICE" }} • {{ shortOrderId(order.orderUuid) }}
                  </p>
                  <p
                    v-if="order.serviceType === 'PARKING'"
                    class="mt-1 text-[11px] text-slate-400"
                  >
                    {{ buildOrderLocation(order) }}
                  </p>
                  <p
                    v-else-if="order.vehiclePlate"
                    class="mt-1 text-[11px] text-slate-400"
                  >
                    Plate {{ order.vehiclePlate }}
                  </p>
                </td>
                <td class="py-3 pr-4">
                  <span :class="statusClass(order.status)" class="rounded-full px-2 py-0.5 text-[10px] font-semibold">
                    {{ order.status }}
                  </span>
                </td>
                <td class="py-3 pr-4 text-slate-700">ETB {{ formatAmount(order.totalAmount) }}</td>
                <td class="py-3 text-slate-500">{{ formatOrderMoment(order) }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </section>
    </main>
  </div>
</template>

<script setup>
import { computed, onMounted, onUnmounted, ref } from "vue";
import { RouterLink } from "vue-router";
import { useAuth } from "@/stores/auth";
import {
  buildParkingLocationDisplay,
  resolveEntranceName,
} from "@/features/client-service/utils/parkingLayout";

const authStore = useAuth();

const createDefaultSummary = () => ({
  overview: {
    totalOrders: 0,
    completedOrders: 0,
    pendingOrders: 0,
    failedOrders: 0,
    cancelledOrders: 0,
    totalSpent: 0,
    pendingAmount: 0,
  },
  last7DaysRevenue: [],
  serviceTypeCounts: [],
  revenueByServiceType: [],
  paymentSuccess: {
    completed: 0,
    failed: 0,
    cancelled: 0,
    total: 0,
    successRate: 0,
  },
  recentOrders: [],
  latestParkingReservation: null,
});

const summary = ref(createDefaultSummary());
const loading = ref(true);
const hasLoaded = ref(false);
const error = ref("");

const apiBase =
  import.meta.env.VITE_API_URL ||
  import.meta.env.v_API_URL ||
  "http://localhost:8280/api/medco-digital-parking/v1";

const overview = computed(() => summary.value?.overview || createDefaultSummary().overview);
const paymentSuccess = computed(() => summary.value?.paymentSuccess || createDefaultSummary().paymentSuccess);
const recentOrders = computed(() => summary.value?.recentOrders || []);
const serviceTypeCounts = computed(() => summary.value?.serviceTypeCounts || []);
const last7DaysRevenue = computed(() => summary.value?.last7DaysRevenue || []);
const latestParkingReservation = computed(() => summary.value?.latestParkingReservation || null);
const latestOrder = computed(() => recentOrders.value[0] || null);

const displayName = computed(() => {
  const user = authStore.auth?.user;
  return user?.firstName || user?.fullName || user?.email?.split("@")[0] || "Customer";
});

const revenueMax = computed(() => {
  const totals = last7DaysRevenue.value.map((item) => Number(item.total || 0));
  return Math.max(1, ...totals);
});

const last7DaysTotal = computed(() =>
  last7DaysRevenue.value.reduce((total, item) => total + Number(item.total || 0), 0)
);

const latestParkingLocation = computed(() => {
  if (!latestParkingReservation.value) return "-";
  return buildOrderLocation(latestParkingReservation.value);
});

const latestParkingEntrance = computed(() => {
  if (!latestParkingReservation.value) return "-";
  return resolveEntranceName(latestParkingReservation.value);
});

const latestParkingMessage = computed(() => {
  if (loading.value && !hasLoaded.value) {
    return "Loading your latest reserved slot.";
  }

  if (!latestParkingReservation.value) {
    return "Your next reserved parking slot will appear here after booking.";
  }

  switch (latestParkingReservation.value.status) {
    case "PENDING":
      return "This reservation is waiting for completion or payment confirmation.";
    case "PROCESSING":
      return "Your parking reservation is currently being processed.";
    case "COMPLETED":
      return "This is your latest completed parking reservation.";
    case "CANCELLED":
      return "Your latest parking reservation was cancelled.";
    case "FAILED":
      return "Your latest parking reservation did not complete successfully.";
    default:
      return "Latest parking reservation loaded.";
  }
});

const formatAmount = (value) => {
  const numberValue = Number(value || 0);
  return numberValue.toLocaleString("en-ET");
};

const formatPercent = (value) => {
  const numberValue = Number(value || 0);
  return `${numberValue.toFixed(1)}%`;
};

const formatDateTime = (value) => {
  if (!value) return "-";
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

const formatCalendarDate = (value) => {
  if (!value) return "-";
  const date = new Date(`${value}T00:00:00`);
  if (Number.isNaN(date.getTime())) return value;
  return date.toLocaleDateString("en-ET", {
    year: "numeric",
    month: "short",
    day: "2-digit",
  });
};

const formatParkingSchedule = (parkingDate, scheduledEntryTime) => {
  if (!parkingDate && !scheduledEntryTime) return "-";

  const dateLabel = parkingDate ? formatCalendarDate(parkingDate) : null;
  return [dateLabel, scheduledEntryTime].filter(Boolean).join(" at ");
};

const formatDayShort = (value) => {
  if (!value) return "-";
  const date = new Date(`${value}T00:00:00`);
  if (Number.isNaN(date.getTime())) return value;
  return date.toLocaleDateString("en-ET", { weekday: "short" });
};

const shortOrderId = (value) => {
  if (!value) return "Order";
  return `#${value.slice(0, 8)}`;
};

const formatServiceShare = (count) => {
  const totalOrders = Number(overview.value.totalOrders || 0);
  if (!totalOrders) return "0.0%";
  return formatPercent((Number(count || 0) / totalOrders) * 100);
};

const buildOrderLocation = (order) => {
  const location = buildParkingLocationDisplay(order || {});
  return location || "Parking slot not assigned yet";
};

const formatOrderMoment = (order) => {
  if (!order) return "-";
  if (order.serviceType === "PARKING") {
    return formatParkingSchedule(order.parkingDate, order.scheduledEntryTime);
  }
  return formatDateTime(order.createdAt);
};

const statusClass = (status) => {
  switch (status) {
    case "COMPLETED":
      return "bg-emerald-100 text-emerald-700";
    case "PENDING":
      return "bg-amber-100 text-amber-700";
    case "PROCESSING":
      return "bg-sky-100 text-sky-700";
    case "FAILED":
    case "CANCELLED":
      return "bg-rose-100 text-rose-700";
    default:
      return "bg-slate-100 text-slate-600";
  }
};

const loadSummary = async ({ silent = false } = {}) => {
  if (!silent || !hasLoaded.value) {
    loading.value = true;
  }

  error.value = "";

  try {
    const token = authStore.auth?.accessToken;
    const response = await fetch(`${apiBase}/dashboard/customer/summary`, {
      headers: token ? { Authorization: `Bearer ${token}` } : {},
    });

    if (!response.ok) {
      throw new Error("Failed to load customer dashboard");
    }

    summary.value = {
      ...createDefaultSummary(),
      ...(await response.json()),
    };
  } catch (err) {
    error.value = err?.message || "Customer dashboard unavailable";
  } finally {
    hasLoaded.value = true;
    loading.value = false;
  }
};

let refreshTimer = null;

const refreshSummaryOnFocus = () => {
  loadSummary({ silent: true });
};

onMounted(async () => {
  await loadSummary();
  refreshTimer = window.setInterval(() => {
    loadSummary({ silent: true });
  }, 30000);
  window.addEventListener("focus", refreshSummaryOnFocus);
});

onUnmounted(() => {
  if (refreshTimer) {
    window.clearInterval(refreshTimer);
  }
  window.removeEventListener("focus", refreshSummaryOnFocus);
});
</script>
