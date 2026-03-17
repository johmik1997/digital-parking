<template>
  <div class="h-full flex items-center justify-center px-4 sm:pr-20">
    <div class="bg-white px-6 sm:px-10 py-8 w-full max-w-sm rounded-xl shadow-2xl flex flex-col gap-6 relative z-10">
      <!-- Logo -->
      <div class="flex justify-center">
        <img src="@/assets/logo.svg" alt="Logo" class="h-[60px] sm:h-[80px]" />
      </div>

      <!-- Form -->
      <NewFormLayout v-slot="{ submit }" id="login-form">
        <div class="flex flex-col gap-5">
          <Input
            label="Email/Phone Number"
            name="email"
            validation="required"
            :attributes="{ placeholder: 'Email' }"
          />
          <InputPassword
            label="Password"
            name="password"
            validation="required"
            :attributes="{ placeholder: 'Password' }"
          />

          <div class="flex flex-col sm:flex-row justify-between text-sm text-gray-500 gap-2 sm:gap-0">
            <label class="flex gap-2 items-center cursor-pointer">
              <i v-html="icons.checkBox" />
              Keep me logged in
            </label>
            <span
              class="text-primary font-medium cursor-pointer"
              @click="openForgotPasswordModal"
            >
              Forgot Password?
            </span>
          </div>

          <FormSubmitButton
            class="w-full"
            @click.prevent="submit(handleLogin)"
            btn-text="Login"
            :pending="loginReq.pending.value"
          />
        </div>
      </NewFormLayout>

      <!-- Signup Link -->
      <div class="text-center text-sm text-gray-600">
        Don't have an account?
        <span @click="goToSignup" class="text-primary font-medium cursor-pointer hover:underline">
          Sign up
        </span>
      </div>
    </div>

    <!-- 🔹 Account Verification Modal -->
   <!-- 🔹 Account Verification Modal -->
<div
  v-if="showVerificationModal"
  class="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4"
>
  <div class="bg-white rounded-2xl w-full max-w-md shadow-xl p-6 relative animate-fadeIn">
    <!-- Close Button -->
    <button
      @click="closeVerificationModal"
      class="absolute top-4 right-4 text-gray-600 hover:text-gray-900"
    >
      <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
      </svg>
    </button>

    <h3 class="text-xl font-semibold text-gray-800 mb-4 text-center">
      Account Verification
    </h3>
    <p class="text-gray-600 text-sm text-center mb-6">
      Enter the code we sent to your phone number.
    </p>

    <!-- Phone Input -->
    <div class="mb-4">
      <label class="block text-sm font-medium text-gray-700 mb-1">Phone Number</label>
      <div class="flex gap-2">
        <input
          type="text"
          v-model="phoneNumber"
          placeholder="Phone number"
          class="flex-1 py-2 px-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
        />
        <button
          @click="sendVerification"
          :disabled="!phoneNumber.trim() || verifyReq.pending.value"
          class="whitespace-nowrap text-sm font-medium px-4 py-2 rounded-lg transition-colors
                 bg-[#2E3365] text-white hover:bg-[#1E224D]
                 disabled:bg-gray-300 disabled:cursor-not-allowed"
        >
          <span v-if="verifyReq.pending.value">Sending...</span>
          <span v-else>Send Code</span>
        </button>
      </div>
    </div>

    <!-- Code Input (4 boxes) -->
    <div class="mb-6">
      <label class="block text-sm font-medium text-gray-700 mb-2 text-center">Verification Code</label>
      <div class="flex justify-center gap-3">
        <input
          v-for="(digit, index) in 4"
          :key="index"
          type="text"
          maxlength="1"
          v-model="otpInputs[index]"
          @input="focusNext(index)"
          class="w-12 h-12 text-center text-lg font-bold border border-gray-300 rounded-lg
                 focus:ring-2 focus:ring-blue-500 outline-none"
        />
      </div>
    </div>

    <div class="flex justify-between gap-3">
      <button
        @click="closeVerificationModal"
        class="flex-1 bg-gray-100 text-gray-700 px-6 py-2 text-sm font-medium hover:bg-gray-200 rounded-lg"
      >
        Cancel
      </button>
      <button
        @click="submitVerification"
        :disabled="otpInputs.join('').length < 4 || verifyReq.pending.value"
        class="flex-1 text-white px-6 py-2 text-sm font-medium rounded-lg transition-colors
               bg-[#2E3365] hover:bg-[#1E224D]
               disabled:bg-gray-300 disabled:cursor-not-allowed"
      >
        <span v-if="verifyReq.pending.value">Verifying...</span>
        <span v-else>Verify</span>
      </button>
    </div>
  </div>
</div>

    <!-- 🔹 Forgot Password Modal -->
    <div
  v-if="showForgotPasswordModal"
  class="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4"
>
  <div class="bg-white rounded-2xl w-full max-w-md shadow-xl p-6 relative animate-fadeIn">
    <!-- Close -->
    <button
      @click="closeForgotPasswordModal"
      class="absolute top-4 right-4 text-gray-600 hover:text-gray-900"
    >
      <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
              d="M6 18L18 6M6 6l12 12" />
      </svg>
    </button>

    <h3 class="text-xl font-semibold text-gray-800 mb-4 text-center">
      Reset Your Password
    </h3>

    <!-- Step 1: Phone -->
    <div v-if="forgotStep === 1" class="space-y-4">
      <p class="text-sm text-gray-600 text-center">
        Enter your phone number to receive an OTP for password reset.
      </p>
      <input
        type="text"
        v-model="forgotPhone"
        placeholder="Phone number"
        class="w-4/5 py-2 px-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
       

      />
      <button
        @click="sendForgotPassword"
        :disabled="!forgotPhone.trim() || forgotReq.pending.value"
        class="w-full bg-[#2E3365] text-white py-2 rounded-lg hover:bg-[#1E224D] transition-colors disabled:bg-gray-300 disabled:cursor-not-allowed"
      >
        <span v-if="forgotReq.pending.value">Sending...</span>
        <span v-else>Send OTP</span>
      </button>
    </div>

    <!-- Step 2: OTP -->
    <div v-if="forgotStep === 2" class="space-y-4">
      <p class="text-sm text-gray-600 text-center">Enter the 4-digit OTP sent to your phone.</p>
      <div class="flex justify-center gap-3">
        <input
          v-for="(digit, index) in 4"
          :key="index"
          type="text"
          maxlength="1"
          v-model="forgotOtpInputs[index]"
          @input="focusNextForgotOtp(index)"
          class="w-12 h-12 text-center text-lg font-bold border border-gray-300 rounded-lg
                 focus:ring-2 focus:ring-blue-500 outline-none"
        />
      </div>
      <button
        @click="verifyForgotOtp"
        :disabled="forgotOtpInputs.join('').length < 4 || forgotReq.pending.value"
        class="w-full bg-[#2E3365] text-white py-2 rounded-lg hover:bg-[#1E224D] transition-colors disabled:bg-gray-300 disabled:cursor-not-allowed"
      >
        <span v-if="forgotReq.pending.value">Verifying...</span>
        <span v-else>Verify OTP</span>
      </button>
    </div>

    <!-- Step 3: Reset Password -->
    <div v-if="forgotStep === 3" class="space-y-4">
      <p class="text-sm text-gray-600 text-center">Enter your new password below.</p>
      <InputPassword
        label="New Password"
        v-model="newPassword"
        :attributes="{ placeholder: 'New password' }"
      />
      <InputPassword
        label="Confirm Password"
        v-model="confirmPassword"
        :attributes="{ placeholder: 'Confirm password' }"
      />
      <button
        @click="submitResetPassword"
        :disabled="!newPassword.trim() || !confirmPassword.trim() || forgotReq.pending.value"
        class="w-full bg-[#2E3365] text-white py-2 rounded-lg hover:bg-[#1E224D] transition-colors disabled:bg-gray-300 disabled:cursor-not-allowed"
      >
        <span v-if="forgotReq.pending.value">Resetting...</span>
        <span v-else>Reset Password</span>
      </button>
    </div>
  </div>
</div>

  </div>
</template>

<script setup>
import FormSubmitButton from "@/components/FormSubmitButton.vue";
import Input from "@/components/new_form_elements/Input.vue";
import InputPassword from "@/components/new_form_elements/InputPassword.vue";
import NewFormLayout from "@/components/NewFormLayout.vue";
import icons from "@/utils/icons";
import { useApiRequest } from "@/composables/useApiRequest";
import { useAuth } from "@/stores/auth";
import { useRoute, useRouter } from "vue-router";
import { toasted } from "@/utils/utils";
import { login } from "../api/LoginApi";
import {
  sendVerificationCode,
  verifyUser,
  forgotPassword,
  enterVerificationCode,
  resetPassword,
} from "@/features/users/Api/UserApi";
import { ref } from "vue";

const router = useRouter();
const route = useRoute();
const auth = useAuth();
let detiail = localStorage.getItem("userDetail");

// ------------------ LOGIN ------------------
const loginReq = useApiRequest();
const verifyReq = useApiRequest();

const showVerificationModal = ref(false);
const phoneNumber = ref("");
const verificationCode = ref("");
const pendingLoginData = ref(null);

function reRoute() {
  if (route.query.redirect && route.query?.from == "other")
    location.href = route.query.redirect;
  else if (route.query.redirect) router.replace(route.query.redirect);
  else {
    const roleName = auth.auth?.user?.roleName;
    if (roleName === "Cashier") router.replace("/cashier");
    else if (roleName === "Customer") router.replace("/customer");
    else router.replace("/dashboard");
  }
}

if (detiail) {
  detiail = JSON.parse(detiail);
  auth.setAuth({ user: detiail, accessToken: detiail.token });
  reRoute();
}

function handleLogin({ values }) {
  if (loginReq.pending.value) return;
  loginReq.send(() => login(values), (res) => {
    if (res.success) {
      auth.setAuth({ user: res.data, accessToken: res.data?.token });
      localStorage.setItem("userDetail", JSON.stringify(res.data));
      reRoute();
    } else {
      const errorMsg = res?.error?.toLowerCase?.() || "";
      if (res.status === 404 && errorMsg.includes("not  active")) {
        pendingLoginData.value = values;
        showVerificationModal.value = true;
        toasted(false, "", "Account verification required");
        return;
      }
      toasted(false, "Login failed", res.error || res.message || "Something went wrong");
    }
  });
}

function sendVerification() {
  verifyReq.send(() => sendVerificationCode(phoneNumber.value), (res) => {
    let parsed = null;
    if (typeof res.data === "string" && res.data.startsWith("Success:")) {
      try {
        parsed = JSON.parse(res.data.replace("Success: ", ""));
      } catch (e) {
        console.error("Failed to parse verification response:", e);
      }
    }
    if (parsed && parsed.acknowledge === "success") {
      toasted(true, "Verification code sent successfully");
    } else {
      const errorMsg =
        parsed?.response?.errors?.[0] || parsed?.acknowledge || res.error || "Verification failed";
      toasted(false, "Send Code Failed", errorMsg);
    }
  });
}

function submitVerification() {
  if (!verificationCode.value.trim() || !phoneNumber.value.trim()) {
    toasted(false, "", "Please enter both phone number and verification code");
    return;
  }
  verifyReq.send(() => verifyUser(phoneNumber.value, verificationCode.value), (res) => {
    if (res.success) {
      toasted(true, "Account verified successfully");
      closeVerificationModal();
      if (pendingLoginData.value) handleLogin({ values: pendingLoginData.value });
    } else {
      toasted(false, "", res.error || "Verification failed");
    }
  });
}

function closeVerificationModal() {
  showVerificationModal.value = false;
  phoneNumber.value = "";
  verificationCode.value = "";
  pendingLoginData.value = null;
}

// ------------------ FORGOT PASSWORD ------------------
const showForgotPasswordModal = ref(false);
const forgotStep = ref(1);
const forgotPhone = ref("");
const forgotOtp = ref("");
const newPassword = ref("");
const confirmPassword = ref("");
const forgotReq = useApiRequest();

function openForgotPasswordModal() {
  showForgotPasswordModal.value = true;
  forgotStep.value = 1;
}

function closeForgotPasswordModal() {
  showForgotPasswordModal.value = false;
  forgotPhone.value = "";
  forgotOtp.value = "";
  newPassword.value = "";
  confirmPassword.value = "";
  forgotStep.value = 1;
}

function sendForgotPassword() {
  forgotReq.send(() => forgotPassword(forgotPhone.value), (res) => {
    console.log("Forgot Password Response:", res); // log full response for debugging

    if (res.success) {
      toasted(true, "OTP sent successfully");
      forgotStep.value = 2; // move to OTP step
    } else {
      const msg = res.message || res.error || "";
      console.log("Error message:", msg);

      // Check for OTP already sent scenario
      if (
        msg.toLowerCase().includes("otp has already been sent")
      ) {
        toasted(true, "OTP already sent. Please enter the last received OTP");
        forgotStep.value = 2; // force step 2
      } else {
        toasted(false, "", msg || "Failed to send OTP");
      }
    }
  });
}


function verifyForgotOtp() {
  forgotReq.send(() => enterVerificationCode(forgotPhone.value, forgotOtp.value), (res) => {
    if (res.success) {
      toasted(true, "OTP verified");
      forgotStep.value = 3;
    } else {
      toasted(false, "", res.error || "Invalid OTP");
    }
  });
}

function submitResetPassword() {
  if (newPassword.value !== confirmPassword.value) {
    toasted(false, "", "Passwords do not match");
    return;
  }
  const data = {
    passwordResetOtp: forgotOtp.value,
    userName: forgotPhone.value,
    confirmPassword: confirmPassword.value,
    newPassword: newPassword.value,
  };
  forgotReq.send(() => resetPassword(data), (res) => {
    if (res.success) {
      toasted(true, "Password reset successfully");
      closeForgotPasswordModal();
    } else {
      toasted(false, "", res.error || "Reset failed");
    }
  });
}

function goToSignup() {
  router.push("/signUp");
}
const otpInputs = ref(["", "", "", ""]);

function focusNext(index) {
  if (otpInputs.value[index].length === 1 && index < 3) {
    const next = document.querySelectorAll('input[type="text"]')[index + 1];
    next?.focus();
  }
  verificationCode.value = otpInputs.value.join("");
}
const forgotOtpInputs = ref(["", "", "", ""]);

function focusNextForgotOtp(index) {
  if (forgotOtpInputs.value[index].length === 1 && index < 3) {
    const next = document.querySelectorAll('div[ v-if="forgotStep === 2"] input')[index + 1];
    next?.focus();
  }
  forgotOtp.value = forgotOtpInputs.value.join("");
}

</script>
