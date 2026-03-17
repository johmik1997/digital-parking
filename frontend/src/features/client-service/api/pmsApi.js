import ApiService from "@/service/ApiService";

const api = new ApiService();
const path = "/pms";

export const pmsApi = {
  getPendingVehicles({ sinceMinutes = 2, limit = 20, refresh = false } = {}) {
    return api
      .addAuthenticationHeader()
      .get(`${path}/pending`, { params: { sinceMinutes, limit, refresh } });
  },
  createPayment(payload) {
    return api.addAuthenticationHeader().post(`${path}/payments`, payload);
  },
  getPaymentsHistory({ limit = 20 } = {}) {
    return api
      .addAuthenticationHeader()
      .get(`${path}/payments/history`, { params: { limit } });
  },
};
