import { createRouter, createWebHistory } from "vue-router";
import MainLayout from '@/layouts/MainLayout.vue';
import { useAuth } from '@/stores/auth';

// Lazy-loaded page components
const Dashboard = () => import('@/features/dashboard/pages/Dashboard.vue');
const Login = () => import('@/pages/login/Login.vue');
const SignUp = () => import('@/pages/signUp.vue');
const PaymentCallback = () => import('@/features/payment/pages/PaymentCallback.vue');
const SpsDemo = () => import('@/pages/SpsDemo.vue');

// Route modules (keep static, they contain route definitions)
import rolesRoutes from './roles.routes';
import privilagesRoutes from './privilages.routes';
import usersRoutes from './users.routes';
import profileRoutes from './profile.routes';
import serviceRoutes from "./service.routes";

const routes = [
  { path: "/", name: "SpsDemo", component: SpsDemo, meta: { requiresAuth: false } },
  { path: "/sps-demo", redirect: "/" },
  {
    path: "",
    name: "Root",
    component: MainLayout,
    meta: { requiresAuth: true },
    children: [
      { path: "", redirect: "/dashboard" },
      { path: "/dashboard", name: "dashboard", component: Dashboard, meta: { requiresAuth: true } },
      ...rolesRoutes,
      ...privilagesRoutes,
      ...serviceRoutes,  // Now this will work with relative path
      ...usersRoutes,
      ...profileRoutes,
    ],
  },
  { path: "/login", name: "Login", component: Login },
  { path: "/signUp", name: "SignUp", component: SignUp },
  { path: "/payment/callback", name: "PaymentCallback", component: PaymentCallback, meta: { requiresAuth: true } },
  // Remove the service route from here
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

router.beforeEach(async (to, from) => {
  const auth = useAuth();

  // Try to restore auth from localStorage if not in store
  if (!auth.auth?.accessToken) {
    let detail = localStorage.getItem('userDetail');
    if (detail) {
      detail = JSON.parse(detail);
      auth.setAuth({
        user: detail,
        accessToken: detail?.token,
      });
    }
  }

  // If going to login and already authenticated, redirect back
  if (to.path == '/login' && auth.auth?.accessToken) {
    return { path: from.path };
  }

  // If no authentication and trying to access protected route
  if (!auth.auth?.accessToken && to.meta?.requiresAuth) {
    return {
      path: `/login`,
      query: { redirect: to.path },
    };
  }

  // Role-based default dashboards
  if (auth.auth?.accessToken && to.path === '/dashboard') {
    const roleName = auth.auth?.user?.roleName;
    if (roleName === 'Cashier') return { path: '/cashier' };
    if (roleName === 'Customer') return { path: '/customer' };
  }

  // If route doesn't require auth, allow access
  if (!to.meta?.requiresAuth) {
    return true;
  }

  // Check privileges for authenticated users
  if (
    auth.auth?.user?.privileges?.includes('All Privileges') ||
    auth.auth?.user?.roleName === 'Super Admin'
  ) {
    return true;
  }

  // Role-based access
  if (
    (auth.auth?.user?.roleName && to.meta?.role && 
     auth.auth?.user?.roleName == to.meta?.role) ||
    (!to.meta?.role && !to.meta?.privileges)
  ) {
    return true;
  }

  // Privilege-based access
  const privileges = auth.auth.user?.privileges;
  const found = (to.meta?.privileges || []).find((privilege) => {
    return privileges?.includes(`ROLE_${privilege}`);
  });

  if (found) return true;

  return { path: '/forbidden' };
});

export default router;



