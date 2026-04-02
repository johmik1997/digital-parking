<template>
  <div class="min-h-screen bg-[radial-gradient(circle_at_top_left,_rgba(14,165,233,0.10),_transparent_28%),radial-gradient(circle_at_top_right,_rgba(245,158,11,0.10),_transparent_26%),linear-gradient(180deg,_#f8fafc_0%,_#ffffff_42%,_#f8fafc_100%)]">
    <header class="sticky top-0 z-20 border-b border-slate-200/80 bg-white/90 backdrop-blur-xl">
      <div class="mx-auto flex max-w-7xl flex-col gap-4 px-4 py-4 sm:px-6 lg:flex-row lg:items-center lg:justify-between">
        <div class="flex items-center gap-3">
          <div class="grid h-11 w-11 place-items-center rounded-2xl bg-gradient-to-br from-sky-600 to-cyan-500 text-white shadow-lg shadow-sky-200">
            <svg class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 13l2-5h14l2 5M5 13h14m-13 4h1m10 0h1M6 17h12a1 1 0 001-1v-3H5v3a1 1 0 001 1z" />
            </svg>
          </div>
          <div>
            <p class="text-[11px] font-semibold uppercase tracking-[0.25em] text-sky-600">Cashier Console</p>
            <h1 class="text-xl font-bold text-slate-900">Meskel Square Parking Operations</h1>
            <p class="text-xs text-slate-500">
              Handle reserved arrivals, late customers, overtime, and exit checkout from one screen.
            </p>
          </div>
        </div>

        <div class="grid grid-cols-2 gap-3 sm:flex sm:items-center">
          <div class="rounded-2xl border border-slate-200 bg-slate-50 px-4 py-3">
            <p class="text-[10px] uppercase tracking-[0.2em] text-slate-400">Cashier</p>
            <p class="mt-1 text-sm font-semibold text-slate-900">{{ cashierLabel }}</p>
          </div>
          <div class="rounded-2xl border border-slate-200 bg-slate-50 px-4 py-3">
            <p class="text-[10px] uppercase tracking-[0.2em] text-slate-400">Last refresh</p>
            <p class="mt-1 text-sm font-semibold text-slate-900">{{ lastRefreshLabel }}</p>
          </div>
        </div>
      </div>
    </header>

    <main class="mx-auto flex max-w-7xl flex-col gap-6 px-4 py-6 sm:px-6">
      <div
        v-if="errorMessage"
        class="rounded-2xl border border-rose-200 bg-rose-50 px-4 py-3 text-sm text-rose-700"
      >
        {{ errorMessage }}
      </div>

      <section class="grid grid-cols-1 gap-4 sm:grid-cols-2 xl:grid-cols-5">
        <article class="rounded-3xl border border-slate-200 bg-white p-5 shadow-sm">
          <p class="text-[11px] uppercase tracking-[0.2em] text-slate-400">Expected Today</p>
          <p class="mt-3 text-3xl font-black text-slate-900">{{ reservations.length }}</p>
          <p class="mt-2 text-xs text-slate-500">Reserved vehicles waiting for arrival or payment confirmation.</p>
        </article>

        <article class="rounded-3xl border border-slate-200 bg-white p-5 shadow-sm">
          <p class="text-[11px] uppercase tracking-[0.2em] text-slate-400">Active Inside</p>
          <p class="mt-3 text-3xl font-black text-slate-900">{{ activeOrders.length }}</p>
          <p class="mt-2 text-xs text-slate-500">Cars currently in the parking area and not checked out yet.</p>
        </article>

        <article class="rounded-3xl border border-slate-200 bg-white p-5 shadow-sm">
          <p class="text-[11px] uppercase tracking-[0.2em] text-slate-400">Late Arrivals</p>
          <p class="mt-3 text-3xl font-black text-amber-600">{{ lateReservations.length }}</p>
          <p class="mt-2 text-xs text-slate-500">Reservations that arrived after the 15-minute grace window.</p>
        </article>

        <article class="rounded-3xl border border-slate-200 bg-white p-5 shadow-sm">
          <p class="text-[11px] uppercase tracking-[0.2em] text-slate-400">Overtime Due</p>
          <p class="mt-3 text-3xl font-black text-rose-600">ETB {{ formatMoney(totalOvertimeDue) }}</p>
          <p class="mt-2 text-xs text-slate-500">Extra amount currently owed by active vehicles after grace time.</p>
        </article>

        <article class="rounded-3xl border border-slate-200 bg-white p-5 shadow-sm">
          <p class="text-[11px] uppercase tracking-[0.2em] text-slate-400">Checked Out</p>
          <p class="mt-3 text-3xl font-black text-emerald-600">{{ completedOrders.length }}</p>
          <p class="mt-2 text-xs text-slate-500">Completed parking exits with arrival and checkout timestamps.</p>
        </article>
      </section>

      <section class="grid grid-cols-1 gap-6 xl:grid-cols-[1.05fr_1fr_1fr]">
        <div class="space-y-6">
          <article class="rounded-[28px] border border-slate-200 bg-white p-5 shadow-sm">
            <div class="flex items-start justify-between gap-4">
              <div>
                <p class="text-[11px] font-semibold uppercase tracking-[0.25em] text-sky-600">Arrival Desk</p>
                <h2 class="mt-1 text-lg font-bold text-slate-900">Find a reservation by plate</h2>
                <p class="mt-1 text-xs text-slate-500">
                  Use this when a customer arrives. The system will show whether the booking is prepaid,
                  late, or still has money due at exit.
                </p>
              </div>
              <button
                @click="refreshAll({ silent: false })"
                :disabled="isLoading"
                class="inline-flex items-center rounded-xl border border-slate-200 px-3 py-2 text-xs font-semibold text-slate-700 transition hover:bg-slate-50 disabled:opacity-60"
              >
                Refresh
              </button>
            </div>

            <div class="mt-4 flex gap-2">
              <input
                v-model="arrivalPlate"
                type="text"
                placeholder="Plate number"
                class="flex-1 rounded-2xl border border-slate-200 px-4 py-3 text-sm uppercase text-slate-900 outline-none transition focus:border-sky-500 focus:ring-4 focus:ring-sky-100"
                @keyup.enter="searchAppointment"
              />
              <button
                @click="searchAppointment"
                :disabled="isSearching || !arrivalPlate.trim()"
                class="rounded-2xl bg-gradient-to-r from-sky-600 to-cyan-500 px-5 py-3 text-sm font-bold text-white shadow-lg shadow-sky-200 transition hover:opacity-95 disabled:opacity-60"
              >
                {{ isSearching ? "Searching..." : "Search" }}
              </button>
            </div>

            <p v-if="arrivalError" class="mt-3 text-xs text-rose-600">{{ arrivalError }}</p>

            <div v-if="arrivalResult" class="mt-5 rounded-[24px] border border-slate-200 bg-slate-50 p-4">
              <div class="flex flex-wrap items-start justify-between gap-3">
                <div>
                  <p class="text-xs font-semibold uppercase tracking-[0.2em] text-slate-400">Matched Reservation</p>
                  <h3 class="mt-2 text-lg font-black text-slate-900">{{ arrivalResult.vehiclePlate }}</h3>
                  <p class="mt-1 text-xs text-slate-500">{{ shortOrderId(arrivalResult.orderUuid) }} • {{ arrivalResult.clientName || "Customer" }}</p>
                </div>
                <span
                  :class="arrivalWindowClass(arrivalResult.arrivalWindowStatus)"
                  class="rounded-full px-3 py-1 text-[11px] font-bold"
                >
                  {{ arrivalWindowLabel(arrivalResult) }}
                </span>
              </div>

              <div class="mt-4 grid grid-cols-1 gap-3 sm:grid-cols-2">
                <div class="rounded-2xl border border-slate-200 bg-white px-4 py-3">
                  <p class="text-[11px] uppercase tracking-[0.2em] text-slate-400">Reserved Slot</p>
                  <p class="mt-2 text-sm font-semibold text-slate-900">{{ parkingLocation(arrivalResult) }}</p>
                  <p class="mt-1 text-[11px] text-slate-500">{{ entranceName(arrivalResult) }}</p>
                </div>
                <div class="rounded-2xl border border-slate-200 bg-white px-4 py-3">
                  <p class="text-[11px] uppercase tracking-[0.2em] text-slate-400">Booked Window</p>
                  <p class="mt-2 text-sm font-semibold text-slate-900">{{ bookingWindow(arrivalResult) }}</p>
                  <p class="mt-1 text-[11px] text-slate-500">{{ paymentSummary(arrivalResult) }}</p>
                </div>
              </div>

              <div class="mt-4 rounded-2xl border border-dashed border-amber-200 bg-amber-50 px-4 py-3 text-xs text-amber-800">
                {{ reservationPolicyText(arrivalResult) }}
              </div>

              <div class="mt-4 flex flex-wrap gap-3">
                <button
                  @click="acceptArrivalFromResult"
                  :disabled="isAccepting"
                  class="rounded-2xl bg-slate-900 px-4 py-3 text-sm font-bold text-white transition hover:bg-slate-800 disabled:opacity-60"
                >
                  {{ isAccepting ? "Checking In..." : "Accept Arrival" }}
                </button>
                <button
                  @click="clearSearch"
                  class="rounded-2xl border border-slate-200 px-4 py-3 text-sm font-semibold text-slate-700 transition hover:bg-white"
                >
                  Clear
                </button>
              </div>
            </div>
          </article>

          <article class="rounded-[28px] border border-slate-200 bg-slate-950 p-5 text-white shadow-sm">
            <p class="text-[11px] font-semibold uppercase tracking-[0.25em] text-sky-300">Handling Rules</p>
            <h2 class="mt-2 text-lg font-bold">Cashier Policy for Late Arrivals and Overtime</h2>
            <div class="mt-4 space-y-3 text-sm text-slate-200">
              <div class="rounded-2xl border border-white/10 bg-white/5 px-4 py-3">
                <p class="font-semibold text-white">Arrival grace</p>
                <p class="mt-1 text-xs text-slate-300">Customers are treated as on time within 15 minutes of the booked entry time.</p>
              </div>
              <div class="rounded-2xl border border-white/10 bg-white/5 px-4 py-3">
                <p class="font-semibold text-white">Late customers</p>
                <p class="mt-1 text-xs text-slate-300">Cashier can still check them in. The reservation time is used to flag lateness, but the paid parking duration is tracked from actual arrival.</p>
              </div>
              <div class="rounded-2xl border border-white/10 bg-white/5 px-4 py-3">
                <p class="font-semibold text-white">Extra stay</p>
                <p class="mt-1 text-xs text-slate-300">Overtime starts only after the vehicle has used more than the paid duration plus the 15-minute grace window.</p>
              </div>
            </div>
          </article>
        </div>

        <article
          ref="liveFloorSection"
          class="rounded-[28px] border border-slate-200 bg-white p-5 shadow-sm"
        >
          <div class="flex items-center justify-between gap-3">
            <div>
              <p class="text-[11px] font-semibold uppercase tracking-[0.25em] text-amber-500">Today Queue</p>
              <h2 class="mt-1 text-lg font-bold text-slate-900">Reserved arrivals</h2>
            </div>
            <span class="rounded-full bg-amber-50 px-3 py-1 text-[11px] font-bold text-amber-700">
              {{ reservations.length }} pending
            </span>
          </div>

          <div v-if="isLoading && !hasLoaded" class="mt-6 text-sm text-slate-500">Loading reservations...</div>
          <div v-else-if="reservations.length === 0" class="mt-6 rounded-2xl border border-dashed border-slate-200 bg-slate-50 px-4 py-5 text-sm text-slate-500">
            No reserved arrivals are waiting right now.
          </div>

          <div v-else class="mt-5 space-y-3">
            <button
              v-for="reservation in reservations"
              :key="reservation.orderUuid"
              @click="setArrivalResult(reservation)"
              class="w-full rounded-[22px] border border-slate-200 bg-slate-50 px-4 py-4 text-left transition hover:border-sky-300 hover:bg-sky-50/50"
            >
              <div class="flex items-start justify-between gap-4">
                <div>
                  <p class="text-base font-black text-slate-900">{{ reservation.vehiclePlate }}</p>
                  <p class="mt-1 text-xs text-slate-500">{{ reservation.clientName || "Customer" }}</p>
                  <p class="mt-2 text-xs text-slate-500">{{ parkingLocation(reservation) }}</p>
                </div>
                <span
                  :class="arrivalWindowClass(reservation.arrivalWindowStatus)"
                  class="rounded-full px-3 py-1 text-[11px] font-bold"
                >
                  {{ arrivalWindowLabel(reservation) }}
                </span>
              </div>

              <div class="mt-3 flex flex-wrap gap-2 text-[11px] text-slate-500">
                <span class="rounded-full bg-white px-2.5 py-1">{{ formatParkingSchedule(reservation) }}</span>
                <span class="rounded-full bg-white px-2.5 py-1">{{ paymentSummary(reservation) }}</span>
              </div>
            </button>
          </div>
        </article>

        <article class="rounded-[28px] border border-slate-200 bg-white p-5 shadow-sm">
          <div class="flex items-center justify-between gap-3">
            <div>
              <p class="text-[11px] font-semibold uppercase tracking-[0.25em] text-rose-500">Live Floor</p>
              <h2 class="mt-1 text-lg font-bold text-slate-900">Vehicles currently inside</h2>
            </div>
            <span class="rounded-full bg-rose-50 px-3 py-1 text-[11px] font-bold text-rose-700">
              {{ activeOrders.length }} active
            </span>
          </div>

          <div v-if="isLoading && !hasLoaded" class="mt-6 text-sm text-slate-500">Loading active vehicles...</div>
          <div v-else-if="activeOrders.length === 0" class="mt-6 rounded-2xl border border-dashed border-slate-200 bg-slate-50 px-4 py-5 text-sm text-slate-500">
            No active vehicles at the moment.
          </div>

          <div v-else class="mt-5 space-y-3">
            <div
              v-for="order in activeOrders"
              :key="order.orderUuid"
              :class="[
                lastAcceptedOrderUuid === order.orderUuid
                  ? 'border-sky-300 bg-sky-50 ring-4 ring-sky-100'
                  : 'border-slate-200 bg-slate-50',
                'rounded-[22px] border px-4 py-4 transition-all duration-500'
              ]"
            >
              <div class="flex items-start justify-between gap-4">
                <div>
                  <p class="text-base font-black text-slate-900">{{ order.vehiclePlate }}</p>
                  <p class="mt-1 text-xs text-slate-500">{{ parkingLocation(order) }}</p>
                </div>
                <span
                  :class="paymentStatusClass(order)"
                  class="rounded-full px-3 py-1 text-[11px] font-bold"
                >
                  {{ order.paymentStatusLabel || "ACTIVE" }}
                </span>
              </div>

              <div class="mt-4 grid grid-cols-2 gap-3">
                <div class="rounded-2xl border border-white bg-white px-3 py-3">
                  <p class="text-[11px] uppercase tracking-[0.2em] text-slate-400">Entered</p>
                  <p class="mt-2 text-sm font-semibold text-slate-900">{{ formatDateTime(order.entryTime) }}</p>
                </div>
                <div class="rounded-2xl border border-white bg-white px-3 py-3">
                  <p class="text-[11px] uppercase tracking-[0.2em] text-slate-400">Planned Exit</p>
                  <p class="mt-2 text-sm font-semibold text-slate-900">{{ formatDateTime(order.plannedExitTime) }}</p>
                </div>
                <div class="rounded-2xl border border-white bg-white px-3 py-3">
                  <p class="text-[11px] uppercase tracking-[0.2em] text-slate-400">Elapsed</p>
                  <p class="mt-2 text-sm font-semibold text-slate-900">{{ formatMinutes(order.elapsedMinutes) }}</p>
                </div>
                <div class="rounded-2xl border border-white bg-white px-3 py-3">
                  <p class="text-[11px] uppercase tracking-[0.2em] text-slate-400">Due Now</p>
                  <p class="mt-2 text-sm font-semibold text-slate-900">ETB {{ formatMoney(order.amountDueNow) }}</p>
                </div>
              </div>

              <div
                v-if="Number(order.overtimeMinutes || 0) > 0 || Number(order.lateMinutes || 0) > 0"
                class="mt-4 rounded-2xl border border-amber-200 bg-amber-50 px-4 py-3 text-xs text-amber-800"
              >
                <span v-if="Number(order.lateMinutes || 0) > 0">Late arrival {{ order.lateMinutes }} min.</span>
                <span v-if="Number(order.lateMinutes || 0) > 0 && Number(order.overtimeMinutes || 0) > 0"> </span>
                <span v-if="Number(order.overtimeMinutes || 0) > 0">
                  Overtime {{ order.overtimeMinutes }} min after grace, extra ETB {{ formatMoney(order.overtimeAmount) }}.
                </span>
              </div>

              <div class="mt-4 flex gap-3">
                <button
                  @click="openCheckout(order)"
                  class="flex-1 rounded-2xl bg-slate-900 px-4 py-3 text-sm font-bold text-white transition hover:bg-slate-800"
                >
                  Check Out Vehicle
                </button>
              </div>
            </div>
          </div>
        </article>
      </section>

      <section class="rounded-[28px] border border-slate-200 bg-white p-5 shadow-sm">
        <div class="flex flex-col gap-2 sm:flex-row sm:items-center sm:justify-between">
          <div>
            <p class="text-[11px] font-semibold uppercase tracking-[0.25em] text-emerald-500">Exit Log</p>
            <h2 class="mt-1 text-lg font-bold text-slate-900">Recent completed exits</h2>
          </div>
          <p class="text-xs text-slate-500">
            Total completed amount ETB {{ formatMoney(completedAmountTotal) }}
          </p>
        </div>

        <div v-if="completedOrders.length === 0" class="mt-5 rounded-2xl border border-dashed border-slate-200 bg-slate-50 px-4 py-5 text-sm text-slate-500">
          Completed exit transactions will appear here after checkout.
        </div>

        <div v-else class="mt-5 overflow-x-auto">
          <table class="min-w-full text-sm">
            <thead>
              <tr class="text-left text-slate-500">
                <th class="py-3 pr-4">Plate</th>
                <th class="py-3 pr-4">Reservation</th>
                <th class="py-3 pr-4">Exit</th>
                <th class="py-3 pr-4">Late / Overtime</th>
                <th class="py-3">Final Amount</th>
              </tr>
            </thead>
            <tbody>
              <tr
                v-for="order in completedOrders"
                :key="order.orderUuid"
                class="border-t border-slate-100"
              >
                <td class="py-4 pr-4">
                  <p class="font-black text-slate-900">{{ order.vehiclePlate }}</p>
                  <p class="mt-1 text-xs text-slate-500">{{ shortOrderId(order.orderUuid) }}</p>
                </td>
                <td class="py-4 pr-4">
                  <p class="font-semibold text-slate-800">{{ parkingLocation(order) }}</p>
                  <p class="mt-1 text-xs text-slate-500">{{ formatParkingSchedule(order) }}</p>
                </td>
                <td class="py-4 pr-4 text-slate-600">
                  {{ formatDateTime(order.completedAt) }}
                </td>
                <td class="py-4 pr-4 text-slate-600">
                  <span v-if="Number(order.lateMinutes || 0) > 0">Late {{ order.lateMinutes }} min</span>
                  <span v-else>On time</span>
                  <span v-if="Number(order.overtimeMinutes || 0) > 0">
                    • Overtime {{ order.overtimeMinutes }} min
                  </span>
                </td>
                <td class="py-4 font-semibold text-slate-900">
                  ETB {{ formatMoney(order.totalAmount) }}
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </section>
    </main>

    <Transition
      enter-active-class="transition duration-300 ease-out"
      enter-from-class="opacity-0"
      enter-to-class="opacity-100"
      leave-active-class="transition duration-200 ease-in"
      leave-from-class="opacity-100"
      leave-to-class="opacity-0"
    >
      <div
        v-if="checkoutOrder"
        class="fixed inset-0 z-50 flex items-center justify-center bg-slate-950/55 px-4 py-6 backdrop-blur-sm"
        @click.self="closeCheckout"
      >
        <div class="w-full max-w-xl overflow-hidden rounded-[32px] border border-slate-200 bg-white shadow-2xl">
          <div class="bg-gradient-to-r from-slate-900 via-slate-800 to-sky-700 px-6 py-5 text-white">
            <p class="text-[11px] uppercase tracking-[0.25em] text-sky-200">Vehicle Checkout</p>
            <h3 class="mt-2 text-2xl font-black">{{ checkoutOrder.vehiclePlate }}</h3>
            <p class="mt-1 text-sm text-sky-100">{{ parkingLocation(checkoutOrder) }}</p>
          </div>

          <div class="space-y-5 px-6 py-6">
            <div class="grid grid-cols-1 gap-3 sm:grid-cols-2">
              <div class="rounded-2xl border border-slate-200 bg-slate-50 px-4 py-3">
                <p class="text-[11px] uppercase tracking-[0.2em] text-slate-400">Scheduled Window</p>
                <p class="mt-2 text-sm font-semibold text-slate-900">{{ bookingWindow(checkoutOrder) }}</p>
              </div>
              <div class="rounded-2xl border border-slate-200 bg-slate-50 px-4 py-3">
                <p class="text-[11px] uppercase tracking-[0.2em] text-slate-400">Actual Entry</p>
                <p class="mt-2 text-sm font-semibold text-slate-900">{{ formatDateTime(checkoutOrder.entryTime) }}</p>
              </div>
              <div class="rounded-2xl border border-slate-200 bg-slate-50 px-4 py-3">
                <p class="text-[11px] uppercase tracking-[0.2em] text-slate-400">Booked Amount</p>
                <p class="mt-2 text-sm font-semibold text-slate-900">ETB {{ formatMoney(checkoutOrder.bookedAmount) }}</p>
              </div>
              <div class="rounded-2xl border border-slate-200 bg-slate-50 px-4 py-3">
                <p class="text-[11px] uppercase tracking-[0.2em] text-slate-400">Prepaid</p>
                <p class="mt-2 text-sm font-semibold text-slate-900">ETB {{ formatMoney(checkoutOrder.prepaidAmount) }}</p>
              </div>
            </div>

            <div class="rounded-[24px] border border-slate-200 bg-slate-50 p-4">
              <div class="flex items-center justify-between">
                <div>
                  <p class="text-[11px] uppercase tracking-[0.2em] text-slate-400">Amount Due Now</p>
                  <p class="mt-2 text-3xl font-black text-slate-900">ETB {{ formatMoney(checkoutOrder.amountDueNow) }}</p>
                </div>
                <div class="text-right text-xs text-slate-500">
                  <p>Overtime {{ formatMinutes(checkoutOrder.overtimeMinutes) }}</p>
                  <p class="mt-1">Extra ETB {{ formatMoney(checkoutOrder.overtimeAmount) }}</p>
                </div>
              </div>
            </div>

            <div
              v-if="checkoutOrder.prepaid"
              class="rounded-2xl border border-sky-200 bg-sky-50 px-4 py-3 text-sm text-sky-800"
            >
              The reserved booking is already paid. Collect only the additional used time if the stay went beyond the booked duration.
            </div>

            <div v-if="Number(checkoutOrder.amountDueNow || 0) > 0">
              <p class="text-xs font-semibold uppercase tracking-[0.2em] text-slate-400">Payment Method</p>
              <div class="mt-3 grid grid-cols-2 gap-3">
                <button
                  @click="selectedPaymentMethod = 'CASH'"
                  :class="paymentMethodButtonClass('CASH')"
                  class="rounded-2xl px-4 py-4 text-left transition"
                >
                  <p class="text-sm font-bold">Cash</p>
                  <p class="mt-1 text-xs opacity-80">Collect directly at the desk</p>
                </button>
                <button
                  @click="selectedPaymentMethod = 'TELEBIRR'"
                  :class="paymentMethodButtonClass('TELEBIRR')"
                  class="rounded-2xl px-4 py-4 text-left transition"
                >
                <img src="../../../assets/telebirr-icon.svg" alt="Telebirr" class="h-6 w-6" />
                  <p class="text-sm font-bold">Telebirr</p>
                  <p class="mt-1 text-xs opacity-80">Take digital payment before release</p>
                </button>
              </div>
            </div>

            <div
              v-else
              class="rounded-2xl border border-emerald-200 bg-emerald-50 px-4 py-3 text-sm text-emerald-800"
            >
              No extra balance is due. This vehicle can be checked out as prepaid.
            </div>
          </div>

          <div class="flex flex-col gap-3 border-t border-slate-200 bg-slate-50 px-6 py-5 sm:flex-row sm:justify-end">
            <button
              @click="closeCheckout"
              class="rounded-2xl border border-slate-200 px-4 py-3 text-sm font-semibold text-slate-700 transition hover:bg-white"
            >
              Cancel
            </button>
            <button
              @click="confirmCheckout"
              :disabled="isCheckingOut || checkoutRequiresPaymentMethod"
              class="rounded-2xl bg-slate-900 px-5 py-3 text-sm font-bold text-white transition hover:bg-slate-800 disabled:opacity-60"
            >
              {{ isCheckingOut ? "Processing..." : checkoutButtonLabel }}
            </button>
          </div>
        </div>
      </div>
    </Transition>
  </div>
</template>

<script setup>
import { computed, nextTick, onMounted, onUnmounted, ref } from "vue";
import { useAuth } from "@/stores/auth";
import { useToast } from "@/features/client-service/components/ui/toast";
import { clientServiceApi } from "../api/clientServiceApi";
import {
  buildParkingLocationDisplay,
  resolveEntranceName,
} from "../utils/parkingLayout";

const authStore = useAuth();
const { showToast } = useToast();

const arrivalPlate = ref("");
const arrivalError = ref("");
const arrivalResult = ref(null);
const reservations = ref([]);
const activeOrders = ref([]);
const completedOrders = ref([]);
const checkoutOrder = ref(null);
const selectedPaymentMethod = ref("");
const isLoading = ref(false);
const hasLoaded = ref(false);
const isSearching = ref(false);
const isAccepting = ref(false);
const isCheckingOut = ref(false);
const errorMessage = ref("");
const lastRefreshAt = ref(null);
const liveFloorSection = ref(null);
const lastAcceptedOrderUuid = ref(null);

const cashierLabel = computed(() => {
  const user = authStore.auth?.user;
  return user?.fullName || user?.firstName || user?.email || "Cashier";
});

const lastRefreshLabel = computed(() => {
  if (!lastRefreshAt.value) return "Not synced yet";
  return lastRefreshAt.value.toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" });
});

const lateReservations = computed(() =>
  reservations.value.filter((order) => Number(order.lateMinutes || 0) > 0)
);

const totalOvertimeDue = computed(() =>
  activeOrders.value.reduce((sum, order) => sum + Number(order.overtimeAmount || 0), 0)
);

const completedAmountTotal = computed(() =>
  completedOrders.value.reduce((sum, order) => sum + Number(order.totalAmount || 0), 0)
);

const checkoutRequiresPaymentMethod = computed(
  () =>
    Number(checkoutOrder.value?.amountDueNow || 0) > 0 &&
    !selectedPaymentMethod.value
);

const checkoutButtonLabel = computed(() => {
  if (!checkoutOrder.value) return "Complete Checkout";
  if (Number(checkoutOrder.value.amountDueNow || 0) <= 0) {
    return "Complete Prepaid Exit";
  }
  return `Collect ETB ${formatMoney(checkoutOrder.value.amountDueNow)} and Exit`;
});

const shortOrderId = (value) => {
  if (!value) return "Order";
  return `#${value.slice(0, 8)}`;
};

const parkingLocation = (order) => {
  return buildParkingLocationDisplay(order || {}) || "Parking location not assigned";
};

const entranceName = (order) => {
  return resolveEntranceName(order || {}) || "Entrance not assigned";
};

const formatMoney = (value) => {
  return Number(value || 0).toLocaleString("en-ET", {
    minimumFractionDigits: 0,
    maximumFractionDigits: 2,
  });
};

const formatDateTime = (value) => {
  if (!value) return "-";
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return value;
  return date.toLocaleString("en-ET", {
    month: "short",
    day: "2-digit",
    hour: "2-digit",
    minute: "2-digit",
  });
};

const formatMinutes = (minutes) => {
  const total = Number(minutes || 0);
  if (!total) return "0m";
  const hours = Math.floor(total / 60);
  const remainder = total % 60;
  if (!hours) return `${remainder}m`;
  return `${hours}h ${String(remainder).padStart(2, "0")}m`;
};

const formatParkingSchedule = (order) => {
  const date = order?.parkingDate ? new Date(`${order.parkingDate}T00:00:00`) : null;
  const dateLabel =
    date && !Number.isNaN(date.getTime())
      ? date.toLocaleDateString("en-ET", { month: "short", day: "2-digit" })
      : order?.parkingDate || "-";
  const timeLabel = order?.scheduledEntryTime || "-";
  return `${dateLabel} at ${timeLabel}`;
};

const bookingWindow = (order) => {
  const start = formatParkingSchedule(order);
  const plannedExit = order?.plannedExitTime ? formatDateTime(order.plannedExitTime) : null;
  return plannedExit ? `${start} • Exit ${plannedExit}` : start;
};

const arrivalWindowClass = (status) => {
  switch ((status || "").toUpperCase()) {
    case "EARLY":
      return "bg-sky-100 text-sky-700";
    case "ON_TIME":
      return "bg-emerald-100 text-emerald-700";
    case "LATE":
      return "bg-amber-100 text-amber-700";
    case "VERY_LATE":
      return "bg-rose-100 text-rose-700";
    default:
      return "bg-slate-100 text-slate-600";
  }
};

const paymentStatusClass = (order) => {
  const dueNow = Number(order?.amountDueNow || 0);
  const overtime = Number(order?.overtimeAmount || 0);

  if (!dueNow) return "bg-emerald-100 text-emerald-700";
  if (overtime > 0) return "bg-rose-100 text-rose-700";
  return "bg-amber-100 text-amber-700";
};

const arrivalWindowLabel = (order) => {
  const status = `${order?.arrivalWindowStatus || "PENDING"}`.replaceAll("_", " ");
  if (Number(order?.lateMinutes || 0) > 0) {
    return `${status} • ${order.lateMinutes}m`;
  }
  return status;
};

const paymentSummary = (order) => {
  const label = order?.paymentStatusLabel || "Payment status unavailable";
  const due = Number(order?.amountDueNow || 0);
  const prepaid = Boolean(order?.prepaid);

  if (prepaid && due > 0) {
    return `BOOKED TIME PAID • ETB ${formatMoney(due)} extra due`;
  }

  if (prepaid && due <= 0) {
    return "BOOKED TIME PAID";
  }

  if (due > 0) {
    return `${label} • ETB ${formatMoney(due)} due`;
  }
  return label;
};

const reservationPolicyText = (order) => {
  const lateMinutes = Number(order?.lateMinutes || 0);
  const plannedExit = order?.plannedExitTime ? formatDateTime(order.plannedExitTime) : "the reserved end time";
  const prepaid = Boolean(order?.prepaid);

  if (lateMinutes > 0) {
    return `Customer is ${lateMinutes} minutes late. Cashier can still accept the vehicle. After arrival, the customer keeps the paid parking duration, and only extra used time after that duration plus the grace window should be charged.${prepaid ? " The booked time is already paid." : ""}`;
  }

  return `Customer is within the allowed arrival window. After arrival, track checkout from the paid parking duration and collect only additional used time beyond that duration if needed.${prepaid ? " The booking is already paid online." : ""}`.trim();
};

const clearSearch = () => {
  arrivalPlate.value = "";
  arrivalError.value = "";
  arrivalResult.value = null;
};

const setArrivalResult = (reservation) => {
  arrivalPlate.value = reservation?.vehiclePlate || "";
  arrivalError.value = "";
  arrivalResult.value = reservation;
};

const upsertActiveOrder = (order) => {
  if (!order?.orderUuid) return;
  activeOrders.value = [
    order,
    ...activeOrders.value.filter((item) => item.orderUuid !== order.orderUuid),
  ];
};

const removeReservation = (order) => {
  const orderUuid = order?.orderUuid;
  const vehiclePlate = `${order?.vehiclePlate || ""}`.toUpperCase();

  reservations.value = reservations.value.filter((item) => {
    if (orderUuid && item.orderUuid === orderUuid) {
      return false;
    }
    if (vehiclePlate && `${item.vehiclePlate || ""}`.toUpperCase() === vehiclePlate) {
      return false;
    }
    return true;
  });
};

const loadReservations = async () => {
  const today = new Date().toISOString().slice(0, 10);
  const response = await clientServiceApi.getCashierReservations(today);
  reservations.value = response.data || [];
};

const loadActiveOrders = async () => {
  const response = await clientServiceApi.getActiveOrders("PARKING");
  activeOrders.value = response.data || [];
};

const loadCompletedOrders = async () => {
  const response = await clientServiceApi.getCompletedOrders("PARKING");
  completedOrders.value = response.data || [];
};

const refreshAll = async ({ silent = true } = {}) => {
  if (!silent || !hasLoaded.value) {
    isLoading.value = true;
  }

  errorMessage.value = "";

  try {
    await Promise.all([loadReservations(), loadActiveOrders(), loadCompletedOrders()]);
    lastRefreshAt.value = new Date();
  } catch (error) {
    const message =
      error?.response?.data?.message ||
      error?.message ||
      "Failed to load cashier dashboard";
    errorMessage.value = message;
    if (!silent) {
      showToast({
        title: "Dashboard unavailable",
        description: message,
        type: "error",
      });
    }
  } finally {
    hasLoaded.value = true;
    isLoading.value = false;
  }
};

const searchAppointment = async () => {
  arrivalError.value = "";
  arrivalResult.value = null;

  if (!arrivalPlate.value.trim()) return;

  isSearching.value = true;
  try {
    const response = await clientServiceApi.findParkingAppointment(arrivalPlate.value.trim().toUpperCase());
    arrivalResult.value = response.data;
  } catch (error) {
    arrivalError.value =
      error?.response?.data?.message ||
      error?.message ||
      "No reservation found for this plate";
  } finally {
    isSearching.value = false;
  }
};

const acceptArrivalFromResult = async () => {
  if (!arrivalResult.value?.vehiclePlate) return;

  isAccepting.value = true;
  arrivalError.value = "";

  try {
    const response = await clientServiceApi.acceptParkingArrival(arrivalResult.value.vehiclePlate);
    const acceptedOrder = response?.data || null;
    const acceptedOrderUuid = acceptedOrder?.orderUuid || arrivalResult.value.orderUuid || null;

    if (acceptedOrder) {
      upsertActiveOrder(acceptedOrder);
      removeReservation(acceptedOrder);
    }

    showToast({
      title: "Arrival accepted",
      description: `${arrivalResult.value.vehiclePlate} is now checked in.`,
      type: "success",
    });
    clearSearch();
    lastAcceptedOrderUuid.value = acceptedOrderUuid;
    await nextTick();
    liveFloorSection.value?.scrollIntoView({
      behavior: "smooth",
      block: "start",
    });

    await refreshAll({ silent: true });

    window.setTimeout(() => {
      if (lastAcceptedOrderUuid.value === acceptedOrderUuid) {
        lastAcceptedOrderUuid.value = null;
      }
    }, 5000);
  } catch (error) {
    arrivalError.value =
      error?.response?.data?.message ||
      error?.message ||
      "Failed to accept vehicle arrival";
  } finally {
    isAccepting.value = false;
  }
};

const openCheckout = (order) => {
  checkoutOrder.value = order;
  selectedPaymentMethod.value = Number(order?.amountDueNow || 0) > 0 ? "" : "PREPAID";
};

const closeCheckout = () => {
  checkoutOrder.value = null;
  selectedPaymentMethod.value = "";
};

const paymentMethodButtonClass = (method) => {
  return selectedPaymentMethod.value === method
    ? "border-2 border-sky-500 bg-sky-50 text-sky-800"
    : "border border-slate-200 bg-white text-slate-700 hover:border-sky-300";
};

const confirmCheckout = async () => {
  if (!checkoutOrder.value) return;
  if (checkoutRequiresPaymentMethod.value) return;

  isCheckingOut.value = true;

  try {
    await clientServiceApi.checkoutParkingOrder(
      checkoutOrder.value.orderUuid,
      Number(checkoutOrder.value.amountDueNow || 0) > 0 ? selectedPaymentMethod.value : null
    );

    showToast({
      title: "Vehicle checked out",
      description: `${checkoutOrder.value.vehiclePlate} has been completed successfully.`,
      type: "success",
    });

    closeCheckout();
    await refreshAll({ silent: true });
  } catch (error) {
    showToast({
      title: "Checkout failed",
      description:
        error?.response?.data?.message ||
        error?.message ||
        "Unable to complete checkout right now.",
      type: "error",
    });
  } finally {
    isCheckingOut.value = false;
  }
};

let refreshIntervalId = null;

onMounted(async () => {
  await refreshAll({ silent: false });
  refreshIntervalId = window.setInterval(() => {
    refreshAll({ silent: true });
  }, 30000);
});

onUnmounted(() => {
  if (refreshIntervalId) {
    window.clearInterval(refreshIntervalId);
  }
});
</script>
