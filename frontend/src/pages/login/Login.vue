<script setup>
import icons from "@/utils/icons";
import LoginForm from "./components/LoginForm.vue";
import { useRouter } from "vue-router";

const router = useRouter();

function goToSignup() {
  router.push("/signUp");
}
</script>

<template>
  <div class="min-h-screen w-full relative overflow-auto bg-gradient-to-br from-slate-900 via-blue-900 to-indigo-900">
    <!-- Digital Parking Grid Background -->
    <div class="absolute inset-0 opacity-20">
      <svg class="w-full h-full" xmlns="http://www.w3.org/2000/svg">
        <defs>
          <pattern id="parking-grid" width="60" height="60" patternUnits="userSpaceOnUse">
            <path d="M 30 0 L 30 60 M 0 30 L 60 30" stroke="white" stroke-width="0.5" stroke-dasharray="4 4"/>
            <circle cx="30" cy="30" r="2" fill="white" opacity="0.5"/>
          </pattern>
          <pattern id="parking-spots" width="120" height="120" patternUnits="userSpaceOnUse">
            <rect x="10" y="10" width="40" height="60" stroke="cyan" stroke-width="1" fill="none" opacity="0.3"/>
            <rect x="70" y="10" width="40" height="60" stroke="cyan" stroke-width="1" fill="none" opacity="0.3"/>
            <rect x="10" y="80" width="40" height="30" stroke="lime" stroke-width="1" fill="none" opacity="0.3"/>
          </pattern>
        </defs>
        <rect width="100%" height="100%" fill="url(#parking-grid)"/>
        <rect width="100%" height="100%" fill="url(#parking-spots)"/>
      </svg>
    </div>

    <!-- Animated Parking Sensor Lines -->
    <div class="absolute inset-0 overflow-hidden">
      <div class="absolute top-20 left-10 w-32 h-32 border-2 border-blue-400/20 rounded-full animate-ping"></div>
      <div class="absolute bottom-20 right-10 w-48 h-48 border-2 border-cyan-400/20 rounded-full animate-ping delay-1000"></div>
      <div class="absolute top-1/2 left-1/4 w-2 h-2 bg-green-400 rounded-full animate-pulse"></div>
      <div class="absolute top-1/3 right-1/4 w-2 h-2 bg-blue-400 rounded-full animate-pulse delay-700"></div>
    </div>

    <!-- Background Mask (Hidden on Mobile) - Replaced with Digital Pattern -->
    <div class="absolute top-0 right-0 w-1/2 h-full hidden md:block">
      <div class="relative w-full h-full">
        <!-- Digital Circuit Pattern -->
        <svg class="absolute inset-0 w-full h-full" viewBox="0 0 800 800" preserveAspectRatio="none">
          <path d="M0 400 L200 400 L200 200 L400 200 L400 600 L600 600 L600 400 L800 400" 
                stroke="rgba(100, 200, 255, 0.2)" stroke-width="2" fill="none" 
                stroke-dasharray="10 10">
            <animate attributeName="stroke-dashoffset" values="0;1000" dur="30s" repeatCount="indefinite"/>
          </path>
          <circle cx="200" cy="400" r="4" fill="rgba(100, 200, 255, 0.5)">
            <animate attributeName="r" values="4;6;4" dur="2s" repeatCount="indefinite"/>
          </circle>
          <circle cx="400" cy="200" r="4" fill="rgba(100, 200, 255, 0.5)">
            <animate attributeName="r" values="4;6;4" dur="2s" repeatCount="indefinite" begin="0.5s"/>
          </circle>
          <circle cx="600" cy="600" r="4" fill="rgba(100, 200, 255, 0.5)">
            <animate attributeName="r" values="4;6;4" dur="2s" repeatCount="indefinite" begin="1s"/>
          </circle>
        </svg>
        
        <!-- Floating Parking Icons -->
        <div class="absolute top-20 left-20 animate-float">
          <svg class="w-16 h-16 text-white/20" fill="currentColor" viewBox="0 0 24 24">
            <path d="M19 6H5v12h14V6zm-2 10H7V8h10v8zM9 10h2v2H9v-2zm4 0h2v2h-2v-2z"/>
          </svg>
        </div>
        <div class="absolute bottom-32 right-20 animate-float-delayed">
          <svg class="w-20 h-20 text-white/20" fill="currentColor" viewBox="0 0 24 24">
            <path d="M4 6h16v12H4V6zm2 2v8h12V8H6zm2 2h2v4H8v-4zm6 0h2v4h-2v-4z"/>
          </svg>
        </div>
      </div>
    </div>

    <!-- Main Layout -->
    <div class="grid grid-cols-1 md:grid-cols-2 min-h-screen relative z-10">
      <!-- Left Content - Digital Parking System Info (Hidden on Mobile) -->
      <div class="hidden md:flex p-10 md:p-20 flex-col justify-center gap-6 backdrop-blur-sm">
        <!-- Smart Parking Badge -->
        <div class="flex items-center gap-2 mb-4">
      
          <span class="w-2 h-2 bg-green-400 rounded-full animate-pulse"></span>
          <span class="text-green-300 text-xs">ONLINE</span>
        </div>

        <p class="font-degular text-3xl md:text-[48px] text-white font-bold leading-tight">
        MESKEL SQUARE PARKING
        </p>
        
        <p class="text-blue-100 text-base md:text-[18px] leading-relaxed max-w-[500px]">
          Experience seamless parking with our intelligent digital system. 
          Real-time slot availability, contactless entry, automated billing, 
          and 24/7 security monitoring. Transform your parking experience 
          with smart technology.
        </p>

        <!-- Feature Pills -->
        <div class="flex flex-wrap gap-3 mt-4">
          <span class="px-3 py-1.5 bg-white/10 text-white rounded-full text-sm border border-white/20">
            ⚡ Real-time availability
          </span>
          <span class="px-3 py-1.5 bg-white/10 text-white rounded-full text-sm border border-white/20">
            🔐 Contactless entry
          </span>
          <span class="px-3 py-1.5 bg-white/10 text-white rounded-full text-sm border border-white/20">
            💳 Automated billing
          </span>
          <span class="px-3 py-1.5 bg-white/10 text-white rounded-full text-sm border border-white/20">
            📱 Mobile access
          </span>
          <span class="px-3 py-1.5 bg-white/10 text-white rounded-full text-sm border border-white/20">
            🎫 Digital permits
          </span>
        </div>

        <!-- Stats -->
        <div class="grid grid-cols-3 gap-4 mt-8 max-w-[500px]">
          <div class="bg-white/5 rounded-lg p-4 backdrop-blur border border-white/10">
            <p class="text-2xl font-bold text-white">500+</p>
            <p class="text-xs text-blue-200">Parking Locations</p>
          </div>
          <div class="bg-white/5 rounded-lg p-4 backdrop-blur border border-white/10">
            <p class="text-2xl font-bold text-white">50k+</p>
            <p class="text-xs text-blue-200">Active Users</p>
          </div>
          <div class="bg-white/5 rounded-lg p-4 backdrop-blur border border-white/10">
            <p class="text-2xl font-bold text-white">99.9%</p>
            <p class="text-xs text-blue-200">Uptime Rate</p>
          </div>
        </div>

        <button 
          class="mt-6 w-fit px-8 py-3 bg-gradient-to-r from-blue-500 to-cyan-400 text-white font-semibold rounded-lg 
                 hover:from-blue-600 hover:to-cyan-500 transition-all shadow-lg hover:shadow-blue-500/25 
                 flex items-center gap-2 group"
          @click="goToSignup"
        >
          <span>Register Your Vehicle</span>
          <svg class="w-5 h-5 group-hover:translate-x-1 transition-transform" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 7l5 5m0 0l-5 5m5-5H6"/>
          </svg>
        </button>
      </div>

      <!-- Right Content - Login Form -->
      <div class="flex items-center justify-center p-4 sm:p-10">
        <div class="relative w-full max-w-md">
          <!-- Mobile Header (Visible only on mobile) -->
          <div class="md:hidden mb-8 text-center">
            <div class="inline-flex items-center gap-2 px-3 py-1 bg-blue-500/20 text-blue-300 rounded-full text-xs border border-blue-400/30 mb-4">
              <span>🚗 MESKEL SQUARE PARKING</span>
              <span class="w-1.5 h-1.5 bg-green-400 rounded-full"></span>
            </div>
            <h1 class="text-2xl font-bold text-white mb-2">
              Welcome Back
            </h1>
            <p class="text-blue-100 text-sm">
              Access your parking dashboard
            </p>
          </div>

          <LoginForm />
          
          <!-- Mobile Sign Up Link (Visible only on mobile) -->
          <div class="md:hidden mt-6 text-center">
            <p class="text-blue-100 text-sm">
              New to our parking network?
              <button 
                @click="goToSignup"
                class="ml-1 text-white font-semibold hover:underline underline-offset-2"
              >
                Register vehicle
              </button>
            </p>
          </div>
        </div>
      </div>
    </div>

    <!-- Footer Status Bar -->
    <div class="absolute bottom-0 left-0 right-0 h-8 bg-black/20 backdrop-blur border-t border-white/10 hidden md:flex items-center px-6 text-xs text-blue-200">
      <div class="flex items-center gap-4">
        <span class="flex items-center gap-1">
          <span class="w-1.5 h-1.5 bg-green-400 rounded-full"></span>
          SYSTEM ACTIVE
        </span>
        <span class="flex items-center gap-1">
          <svg class="w-3 h-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"/>
          </svg>
          REAL-TIME SYNC
        </span>
        <span class="flex items-center gap-1">
          <svg class="w-3 h-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-5m6 2a9 9 0 11-18 0 9 9 0 0118 0z"/>
          </svg>
          SECURE ACCESS
        </span>
      </div>
    </div>
  </div>
</template>

<style scoped>
@keyframes float {
  0%, 100% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-20px);
  }
}

@keyframes float-delayed {
  0%, 100% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-15px);
  }
}

.animate-float {
  animation: float 6s ease-in-out infinite;
}

.animate-float-delayed {
  animation: float-delayed 8s ease-in-out infinite;
}

.delay-1000 {
  animation-delay: 1000ms;
}

.delay-700 {
  animation-delay: 700ms;
}

/* Custom scrollbar for the whole page */
.min-h-screen {
  scrollbar-width: thin;
  scrollbar-color: rgba(96, 165, 250, 0.5) rgba(0, 0, 0, 0.1);
}

.min-h-screen::-webkit-scrollbar {
  width: 8px;
}

.min-h-screen::-webkit-scrollbar-track {
  background: rgba(0, 0, 0, 0.1);
}

.min-h-screen::-webkit-scrollbar-thumb {
  background: rgba(96, 165, 250, 0.5);
  border-radius: 4px;
}

.min-h-screen::-webkit-scrollbar-thumb:hover {
  background: rgba(96, 165, 250, 0.7);
}
</style>