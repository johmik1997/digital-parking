import axios from "axios";
import { responseHandler } from "./ApiResponseHandler";
import { useAuth } from "@/stores/auth";

const backendApiUrl = import.meta.env.v_API_URL|| 'http://localhost:8280/api/medco-digital-parking/v1';

export default class ApiService {
  api;

  _initApi(baseURL) {
    this.api = axios.create({
      baseURL,
      validateStatus: (status) => status >= 200 && status < 300,
    });
  }

  constructor(baseURL) {
    this._initApi(baseURL || backendApiUrl);
  }

  /**
   * Adds bearer token from auth store to headers
   */
  addAuthenticationHeader() {
    const authStore = useAuth();
    this.api.defaults.headers.common.Authorization = `Bearer ${authStore.auth?.accessToken}`;
    return this;
  }

  /**
   * Helper to conditionally set Content-Type
   */
  _getHeaders(data, configHeaders = {}) {
    const isFormData = data instanceof FormData;

    return {
      ...configHeaders,
      ...(isFormData ? {} : { "Content-Type": "application/json" }),
    };
  }

  async get(url, config = {}) {
    return await responseHandler(
      this.api({
        ...config,
        url,
        method: "get",
      })
    );
  }

  async post(url, data, config = {}) {
    return await responseHandler(
      this.api({
        ...config,
        url,
        data,
        method: "post",
        headers: this._getHeaders(data, config.headers),
      })
    );
  }

async put(url, data, config = {}) {
  const headers = this._getHeaders(data, config.headers);
  console.log("[ApiService.put] URL:", url);
  console.log("[ApiService.put] Headers:", headers);
  console.log("[ApiService.put] Data instance of FormData?", data instanceof FormData);
  
  return await responseHandler(
    this.api({
      ...config,
      url,
      data,
      method: "put",
      headers,
    })
  );
}


  async patch(url, data, config = {}) {
    return await responseHandler(
      this.api({
        ...config,
        url,
        data,
        method: "patch",
        headers: this._getHeaders(data, config.headers),
      })
    );
  }

  async delete(url, config = {}) {
    return await responseHandler(
      this.api({
        ...config,
        url,
        method: "delete",
      })
    );
  }
}
