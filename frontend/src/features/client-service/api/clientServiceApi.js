import ApiService from "@/service/ApiService";
import { useAuth } from "@/stores/auth";

const api = new ApiService();
const path = "/client/services";

export const clientServiceApi = {

  // ✅ Get all active services
  getActiveServices() {
    return api.addAuthenticationHeader().get(`${path}/active`);
  },

  // ✅ Get service by ID
  getServiceById(id) {
    return api.addAuthenticationHeader().get(`${path}/${id}`);
  },

  // ✅ Get parking availability by date
  getParkingAvailability(serviceUuid, parkingDate) {
    return api
      .addAuthenticationHeader()
      .get(`${path}/${serviceUuid}/availability`, { params: { parkingDate } });
  },

  // ✅ Create service order
  createServiceOrder(userUuid, data) {
    return api
      .addAuthenticationHeader()
      .post(`${path}/orders/${userUuid}`, data);
  },

  // ✅ Get client's order history
  getServiceHistory(userUuid) {
    return api
      .addAuthenticationHeader()
      .get(`${path}/orders/history/${userUuid}`);
  },

  // ✅ Get order details
  getOrderDetails(orderUuid, userUuid) {
    return api
      .addAuthenticationHeader()
      .get(`${path}/orders/${orderUuid}/${userUuid}`);
  },

  // ✅ Cancel order
  cancelOrder(orderUuid, userUuid) {
    return api
      .addAuthenticationHeader()
      .put(`${path}/orders/${orderUuid}/cancel/${userUuid}`);
  },

  // ✅ Cashier: accept parking appointment by plate
  acceptParkingArrival(plate) {
    return api
      .addAuthenticationHeader()
      .post(`${path}/orders/arrive`, null, { params: { plate } });
  },

  // ✅ Cashier: search pending parking appointment by plate
  findParkingAppointment(plate) {
    return api
      .addAuthenticationHeader()
      .get(`${path}/orders/appointment`, { params: { plate } });
  },

  // ✅ Cashier: list active (processing) orders
  getActiveOrders(serviceType = 'PARKING') {
    return api
      .addAuthenticationHeader()
      .get(`${path}/orders/active`, { params: { serviceType } });
  }
};
