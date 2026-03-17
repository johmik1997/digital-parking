import ApiService from "@/service/ApiService";
import { getQueryFormObject } from "@/utils/utils.js";

const api = new ApiService();
const path = "/auth/users";

// ----------------- EXISTING -----------------
export function CreateUser(data) {
  return api.addAuthenticationHeader().post(`${path}/signUp`, data);
}
export const verifyUser = (phoneNumber, code) => {
  return api.post(`${path}/EnterVerificationCode?phoneNumber=${phoneNumber}&code=${code}`);
};
export const sendVerificationCode = (phoneNumber) => {
  return api.post(`${path}/send?phoneNumber=${phoneNumber}`);
};
export function getAllUser(query = {}) {
  const qr = getQueryFormObject(query);
  return api.addAuthenticationHeader().get(`${path}/all${qr}`);
}
export function getUserById(id) {
  return api.addAuthenticationHeader().get(`${path}/${id}`);
}
export function updateUserById(id, data) {
  return api.addAuthenticationHeader().put(`${path}/update/${id}`, data);
}
export function removeUserById(id) {
  return api.addAuthenticationHeader().delete(`${path}/${id}`);
}
export function fetchUserFilesView() {
  return api.addAuthenticationHeader().get(`${path}`);
}
export function fetchUserFiles() {
  return api.addAuthenticationHeader().get(`${path}`);
}

// 1️⃣ Forgot Password (send OTP to phone number)
export function forgotPassword(phoneNumber) {
  return api.put(
    `${path}/password/forgotPassword/${phoneNumber}`
  );
}

// 2️⃣ Enter Verification Code (verify OTP)
export function enterVerificationCode(phoneNumber, code) {
  // Example: http://192.168.100.85:8989/api/premium/users/EnterVerificationCode?phoneNumber=099&code=22
  return api.post(
    `${path}/password/checkResetCode?userName=${phoneNumber}&verificationCode=${code}`
  );
}

// 3️⃣ Reset Password (submit new password with OTP)
export function resetPassword(data) {
  // data = { passwordResetOtp, userName, confirmPassword, newPassword }
  // Example endpoint: ${path}/users/resetPassword
  return api.put(
    `${path}/resetPassword`,
    data
  );
}
