import ApiService from "@/service/ApiService";
import { getQueryFormObject } from "@/utils/utils.js";

const api = new ApiService();
const path = "/services";

export function createService(data) {
  return api.addAuthenticationHeader().post(`${path}`, data);
}

export const updateService = (id, data) => {
  return api.addAuthenticationHeader().put(`${path}/${id}`, data);
};

export function getServices(query = {}) {
  // Fix: Don't append query object directly to URL string
  // Use the params option instead
  return api.addAuthenticationHeader().get(`${path}`, { 
    params: query 
  });
}

export const toggleService = (id) => {
  return api.post(`${path}/${id}/toggle`);
}
