<script setup>
import { ref, onMounted, watch } from "vue";
import { useAuth } from "@/stores/auth";
import { useRouter } from "vue-router";
import icons from "@/utils/icons";
import imageSrc from "@/assets/img/profile.png";

// Define props
const props = defineProps({
  modelValue: {
    type: String,
  },
  title: {
    type: String,
  },
});

// Initialize store and router
const authStore = useAuth();
const router = useRouter();
const isScrolled = ref(false);

// Default profile picture
const profilePicture = ref(imageSrc);

// Check if user data is available
const user = authStore?.auth?.user || { name: "Birhane Araya", role: "Marketing Administrator" };

// Process the profile image
async function processProfilePicture() {
  // Log the entire authStore object to inspect
  console.log("authStore:", authStore);
  console.log("authStore.auth:", authStore.auth);
  console.log("authStore.auth.user:", authStore.auth?.user);
  
  const profilePic = authStore.auth?.user?.profilePicture; // Use profilePicture from user

  // Log the profilePicture to inspect it
  console.log("Profile Picture:", profilePic);

  if (profilePic) {
    // If profilePic is a valid base64 string, use it
    if (!profilePic.startsWith("data:image/")) {
      profilePicture.value = `data:image/png;base64,${profilePic}`;
    } else {
      profilePicture.value = profilePic;
    }
  } else {
    // If no profilePic is available, fallback to the default image
    profilePicture.value = imageSrc;
  }
}

// Handle image load error and fallback to default image
function handleImageError() {
  profilePicture.value = imageSrc;
}

// Handle logout functionality
function logout() {
  localStorage.removeItem("userDetail");
  window.location.href = "/login";
}

onMounted(() => {
  // Process the profile picture when component is mounted
  processProfilePicture();
  // Handle scroll event to toggle navbar style
  window.addEventListener('scroll', () => {
    isScrolled.value = window.scrollY > 10;
  });
});

// Watch for changes in input data (if any)
const inputData = ref("");
const emit = defineEmits(["update:modelValue"]);
watch(inputData, () => {
  emit("update:modelValue", inputData.value);
});

// Toggle user menu dropdown visibility
const showUserMenu = ref(false);
const toggleUserMenu = () => {
  showUserMenu.value = !showUserMenu.value;
};

// Navigate to previous page (back button)
const goBack = () => {
  router.go(-1);
};

// Navigate to Profile or Settings page
const navigateTo = (page) => {
  if (page === "profile") {
    router.push('/profile'); 
  } else if (page === "settings") {
    router.push('/SettingsPage'); 
  }
};
</script>

<template>
  <div class="flex justify-between items-center bg-gray-50 relative">
    <!-- Left Side - Back Button and Title -->
    <div class="flex items-center gap-2 sm:gap-4">
      <button 
        @click="goBack" 
        class="p-2 hover:bg-gray-100 rounded-lg flex items-center gap-2 transition-colors"
      >
        <span class="item-center">
          <svg width="7" height="13" viewBox="0 0 7 13" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path opacity="0.6" fill-rule="evenodd" clip-rule="evenodd"
              d="M5.82539 1.0134C6.03505 1.20471 6.05933 1.54072 5.87962 1.76391L2.15854 6.38525L5.87962 11.0066C6.05933 11.2298 6.03505 11.5658 5.82539 11.7571C5.61572 11.9484 5.30007 11.9226 5.12036 11.6994L1.12037 6.73164C0.959876 6.53232 0.959876 6.23819 1.12037 6.03887L5.12036 1.07113C5.30008 0.847943 5.61572 0.822096 5.82539 1.0134Z"
              fill="#263558" stroke="#263558" stroke-linecap="round" 
            />
          </svg>
        </span>
        
      </button>
      <span class="capitalize text-base sm:text-lg font-bold truncate">{{ props.title }}</span>
    </div>

    <!-- Right Side - User Info and Icons -->
    <div class="flex gap-2 sm:gap-4 items-center">
      <!-- Icons - Hidden on mobile, visible on tablet and up -->
      <div class="hidden sm:flex gap-4 items-center">
        <!-- <button class="p-2 hover:bg-gray-100 rounded-full transition-colors">
          <i v-html="icons.notification" />
        </button>
        <button class="p-2 hover:bg-gray-100 rounded-full transition-colors">
          <i v-html="icons.message" />
        </button> -->
        <button class="p-2 hover:bg-gray-100 rounded-full transition-colors">
          <i v-html="icons.bire" />
        </button>
      </div>

      <!-- User Profile Section -->
      <div class="relative">
        <button 
          @click="toggleUserMenu"
          class="flex items-center gap-2 p-2 hover:bg-gray-100 rounded-lg transition-colors"
        >
           <div class="relative">
            <div class="w-9 h-9 rounded-full overflow-hidden border-2 border-white shadow">
              <img
                :src="profilePicture || imageSrc"
                alt="User avatar"
                class="w-full h-full object-cover group-hover:scale-110 transition-transform duration-300"
                @error="handleImageError"
              />
            </div>
            <span class="absolute -bottom-1 -right-1 w-3 h-3 bg-green-500 rounded-full border-2 border-white animate-ping"></span>
          </div>
          <!-- User Info - Hidden on mobile -->
          <div class="hidden sm:block text-right">
            <p class="font-Poppin text-sm">{{ user.name }}</p>
            <p class="text-xs text-gray-500">{{ user.role }}</p>
          </div>
          
          <i v-html="icons.down" class="transition-transform duration-200" 
             :class="{ 'rotate-180': showUserMenu }" />
        </button>

        <!-- Dropdown Menu -->
        <div v-if="showUserMenu" 
             class="absolute right-0 top-full mt-2 w-48 bg-white rounded-lg shadow-lg py-2 z-50">
          <!-- Mobile-only icons -->
          <!-- <div class="sm:hidden border-b border-gray-100">
            <button class="w-36 px-4 py-2 text-left hover:bg-gray-50 flex items-center gap-2">
              <i v-html="icons.notification" />
              <span>Notifications</span>
            </button>
            <button class="w-36 px-4 py-2 text-left hover:bg-gray-50 flex items-center gap-2">
              <i v-html="icons.message" />
              <span>Messages</span>
            </button>
          </div>
           -->
          <!-- Common menu items -->
          <button @click="navigateTo('profile')" class="w-36 px-4 py-2 text-left hover:bg-gray-50">Profile</button>
          <!-- <button @click="navigateTo('settings')" class="w-36 px-4 py-2 text-left hover:bg-gray-50">Settings</button> -->
          <button  @click="logout()" class=" px-4 w-36 py-2 text-left hover:bg-red-50 text-red-600">Logout</button>
        </div>
      </div>
    </div>

    <!-- Overlay for closing dropdown -->
    <div v-if="showUserMenu" 
         @click="showUserMenu = false"
         class="fixed inset-0 z-40 bg-black bg-opacity-20">
    </div>
  </div>
</template>

<style scoped>
.truncate {
  max-width: 200px;
  @apply overflow-hidden text-ellipsis whitespace-nowrap;
}

@media (max-width: 640px) {
  .truncate {
    max-width: 150px;
  }
}
</style>
