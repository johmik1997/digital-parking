export default [
  { 
    path: 'service/product',  // Remove leading slash - now it's a relative path
    name: 'Service', 
    component: () => import('@/features/product/pages/service.vue'), 
    meta: { 
      requiresAuth: true, 
      permissions: [] 
    } 
  },
  { 
    path: 'service/order',  // Remove leading slash - now it's a relative path
    name: 'Service-Order', 
    component: () => import('@/features/client-service/pages/ServiceOrder.vue'), 
    meta: { 
      requiresAuth: true, 
      permissions: [] 
    } 
  },
  {
    path: 'customer',
    name: 'Customer Dashboard',
    component: () => import('@/features/customer/pages/CustomerDashboard.vue'),
    meta: {
      requiresAuth: true,
      role: 'Customer',
    }
  },
   {
  path: 'cashier',
  name: 'Cashier',
  component: () => import('@/features/client-service/pages/CashierParkingDashboard.vue'),
  meta: { 
    requiresAuth: true,
        role: 'Cashier',   // Only users with role "Cashier" can access
  }
},
{
  path: 'cashier/report',
  name: 'Cashier Report',
  component: () => import('@/features/client-service/pages/CashierReportDashboard.vue'),
  meta: {
    requiresAuth: true,
    role: 'Cashier',
  }
}

]
