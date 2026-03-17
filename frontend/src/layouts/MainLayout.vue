<template>
  <div class="flex h-screen w-full flex-col overflow-hidden bg-gray-50 lg:flex-row lg:rounded-lg">
    <!-- Mobile Menu Button - Only shown on mobile -->
    <button 
      v-if="!isMobileMenuOpen"
      @click="toggleMobileMenu"
      class="lg:hidden fixed top-4 left-4 z-50 p-3 rounded-lg bg-white shadow-lg border border-gray-200 hover:bg-gray-50 transition-colors"
    >
      <svg 
        xmlns="http://www.w3.org/2000/svg" 
        class="h-5 w-5 text-[#3C3C9E]"
        fill="none" 
        viewBox="0 0 24 24" 
        stroke="currentColor"
      >
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16" />
      </svg>
    </button>

    <!-- Close Button when menu is open -->
    <button 
      v-else
      @click="toggleMobileMenu"
      class="lg:hidden fixed top-4 left-[calc(50%+1rem)] z-50 p-3 rounded-lg bg-white shadow-lg border border-gray-200 hover:bg-gray-50 transition-colors"
    >
      <svg 
        xmlns="http://www.w3.org/2000/svg" 
        class="h-5 w-5 text-[#3C3C9E]"
        fill="none" 
        viewBox="0 0 24 24" 
        stroke="currentColor"
      >
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
      </svg>
    </button>

    <!-- Sidebar - Now only takes half screen on mobile -->
    <div 
      class="__scrollable-hidden fixed lg:relative w-3/4 sm:w-80 lg:w-72 border-r text-white bg-[#3C3C9E] overflow-y-auto min-h-screen h-full lg:h-screen lg:sticky lg:top-0 transition-transform duration-300 ease-in-out z-40 shadow-xl"
      :class="{ 
        '-translate-x-full lg:translate-x-0': !isMobileMenuOpen,
        'translate-x-0': isMobileMenuOpen 
      }"
    >
      <!-- Sidebar Header -->
      <div class="p-4 h-16 flex items-center sticky top-0 bg-[#3C3C9E] z-10 border-b border-white/10">
        <div v-html="icons.logo" class="w-16 h-8 ml-12 flex items-center"></div>
      </div>

      <!-- Navigation Items -->
      <div class="flex flex-col p-4 space-y-6 pb-8">
        <div v-for="(navItems, category) in grouped" :key="category" class="flex flex-col space-y-1">
          <h3 v-if="category !== 'undefined'" class="text-xs font-semibold text-white/70 uppercase tracking-wider px-4 py-2">
            {{ category }}
          </h3>
          
          <div v-for="nav in navItems" :key="nav.name" class="flex flex-col">
            <!-- Parent Nav Item -->
            <div v-if="shouldShowNavItem(nav)">
              <!-- Link without children -->
              <RouterLink
                v-if="!nav.children"
                :to="nav.path"
                class="flex items-center px-4 py-2.5 mx-2 rounded-lg transition-all duration-200 hover:bg-white/10"
                :class="{ 'bg-white text-[#3C3C9E] shadow-md': isRouteActive(nav.path) }"
                @click="isMobileMenuOpen = false"
              >
                <div class="w-5 h-5 mr-3 flex-shrink-0" v-html="nav.icon" />
                <span class="text-sm font-medium truncate">{{ nav.name }}</span>
              </RouterLink>
              
              <!-- Parent with children (dropdown) -->
              <div
                v-else
                class="flex items-center justify-between px-4 py-2.5 mx-2 rounded-lg cursor-pointer transition-all duration-200 hover:bg-white/10"
                :class="{ 'bg-white/10': isDropdownOpen(nav.path) }"
                @click.stop="toggleDropdown(nav.path)"
              >
                <div class="flex items-center flex-1 min-w-0">
                  <div class="w-5 h-5 mr-3 flex-shrink-0" v-html="nav.icon" />
                  <span class="text-sm font-medium truncate">{{ nav.name }}</span>
                </div>
                <i class="transition-transform duration-200 w-4 h-4 flex-shrink-0 ml-2"
                   :class="{ 'rotate-180': isDropdownOpen(nav.path) }"
                   v-html="icons.down" />
              </div>
            </div>

            <!-- Children Items -->
            <div
              v-if="nav.children"
              class="overflow-hidden transition-all duration-300 ease-in-out"
              :class="{ 'mt-1': isDropdownOpen(nav.path) }"
              :style="{ height: isDropdownOpen(nav.path) ? autoHeight : '0' }"
            >
              <div class="pl-9 pr-2 space-y-0.5">
                <RouterLink
                  v-for="child in nav.children"
                  :key="child.name"
                  :to="child.path"
                  class="block py-2 px-3 text-sm rounded-lg transition-all duration-200 hover:bg-white/10"
                  :class="{ 'bg-white text-[#3C3C9E] font-medium shadow-sm': isRouteActive(child.path) }"
                  @click="isMobileMenuOpen = false"
                >
                  {{ child.name }}
                </RouterLink>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Mobile Overlay - Only shown when menu is open -->
    <div 
      v-if="isMobileMenuOpen" 
      class="fixed inset-0 bg-black/50 z-30 lg:hidden transition-opacity duration-300"
      @click="toggleMobileMenu"
    ></div>

    <!-- Main Content Area -->
    <div class="flex h-full w-full flex-1 flex-col bg-gray-50">
      <!-- Navbar - Adjust padding when menu is closed on mobile -->
      <div 
        class="h-16 flex items-center pl-16 pr-4 sm:px-6 bg-white border-b border-gray-200 shadow-sm transition-all duration-300"
        :class="{ 'lg:pl-6': true }"
      >
        <NavBar v-model="search" class="w-full" :title="route.name" />
      </div>
      
      <!-- Content Area -->
      <div class="__scrollable-hidden flex-1 overflow-y-auto">
        <div class="p-4 sm:p-6">
          <RouterView :search="search" :inputData="inputData" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, computed, provide, nextTick } from "vue";
import { useRoute, RouterLink, RouterView } from "vue-router";
import { useAuth } from "@/stores/auth";
import NavBar from "@/components/NavBar.vue";
import navs from "@/config/navs";
import icons from "@/utils/icons";

const authStore = useAuth();
const route = useRoute();
const search = ref("");
const inputData = ref("");
const openDropdowns = ref(new Set());
const isMobileMenuOpen = ref(false);
const autoHeight = ref('auto');

provide("search", search);

// --- NAVIGATION LOGIC ---

const toggleMobileMenu = () => {
  isMobileMenuOpen.value = !isMobileMenuOpen.value;
};

const toggleDropdown = async (navPath) => {
  await nextTick();
  if (openDropdowns.value.has(navPath)) {
    openDropdowns.value.delete(navPath);
  } else {
    openDropdowns.value.add(navPath);
  }
  // Force recalculation of auto height
  autoHeight.value = 'auto';
};

const isDropdownOpen = (navPath) => openDropdowns.value.has(navPath);

const isRouteActive = (navPath) => {
  if (navPath === '/') return route.path === '/';
  return route.path.startsWith(navPath);
};

// --- PRIVILEGE FILTERING ---

const filteredNavs = computed(() => {
  return navs.filter(nav => shouldShowNavItem(nav));
});

const grouped = computed(() => {
  return Object.groupBy(filteredNavs.value, (el) => el.type || 'Main');
});

const shouldShowNavItem = (nav) => {
  const user = authStore.auth?.user;
  const userPrivs = user?.privileges || [];
  const requiredPrivs = nav.privilage || [];
  const userRole = user?.roleName;
  const allowedRoles = nav.roles || (nav.role ? [nav.role] : []);
  const normalizedUserRole = (userRole || '').toLowerCase();
  const normalizedAllowedRoles = allowedRoles.map((r) => String(r).toLowerCase());
  
  // Also show parent if any child is accessible
  const childrenPrivs = nav.children?.flatMap(c => c.privilage || []) || [];
  const allRelatedPrivs = [...requiredPrivs, ...childrenPrivs];

  if (userRole === 'Super Admin' || userPrivs.includes("All Privileges")) {
    return normalizedAllowedRoles.includes('super admin');
  }
  // Check role-based access
  if (allowedRoles.length > 0 && !normalizedAllowedRoles.includes(normalizedUserRole)) {
    return false;
  }

  // Check privilege-based access
  if (allRelatedPrivs.length === 0) return true;
  
  return allRelatedPrivs.some(p => userPrivs.includes(`ROLE_${p}`));
};

// --- WATCHERS ---

watch(() => route.path, (newPath) => {
  isMobileMenuOpen.value = false;
  
  // Automatically open parent dropdowns for active route
  navs.forEach(nav => {
    if (nav.children && newPath.startsWith(nav.path)) {
      openDropdowns.value.add(nav.path);
    }
  });
}, { immediate: true });
</script>

<style scoped>
/* Hide scrollbar but keep functionality */
.__scrollable-hidden {
  -ms-overflow-style: none;
  scrollbar-width: none;
}

.__scrollable-hidden::-webkit-scrollbar {
  display: none;
}

/* Fixed height for navbar */
.h-16 {
  height: 64px;
}

/* Mobile adjustments */
@media (max-width: 1023px) {
  /* Adjust main content padding when menu is closed */
  .pl-4 {
    padding-left: 1rem;
  }
}

/* Smooth transitions for dropdown */
.transition-all {
  transition-property: all;
  transition-timing-function: cubic-bezier(0.4, 0, 0.2, 1);
  transition-duration: 300ms;
}

/* Better touch targets for mobile */
@media (max-width: 639px) {
  .p-4 {
    padding: 1rem;
  }
  
  .py-2\.5 {
    padding-top: 0.75rem;
    padding-bottom: 0.75rem;
  }
}

/* Active state styling */
.router-link-active.router-link-exact-active {
  background-color: white;
  color: #3C3C9E;
  font-weight: 500;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
}
</style>
