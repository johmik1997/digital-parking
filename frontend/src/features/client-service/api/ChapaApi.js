import ApiService from '@/service/ApiService'

const api = new ApiService()

// ✅ FIXED ENV VARIABLE
const BASE_URL =
  import.meta.env.VITE_API_URL ||
  'http://localhost:8280/api/medco-digital-parking/v1'

export const chapaApi = {

  initializePayment: async (paymentData) => {
    try {
      const response = await api
        .addAuthenticationHeader()
        .post(`${BASE_URL}/api/v1/chapa/initialize`, paymentData)

      if (!response || response.success === false) {
        throw {
          status: 'error',
          message: response?.error || 'Payment initialization failed'
        }
      }

      return response.data // backend JSON body
    } catch (error) {
      console.error('Chapa payment initialization error:', error)

      // Normalize error structure
      if (error.response?.data) {
        throw error.response.data
      }

      throw {
        status: 'error',
        message: error?.message || 'Network error. Please try again.'
      }
    }
  },

  verifyPayment: async (txRef) => {
    try {
      const response = await api
        .addAuthenticationHeader()
        .get(`${BASE_URL}/api/v1/chapa/verify/${txRef}`)

      return response.data
    } catch (error) {
      console.error('Chapa payment verification error:', error)
      throw error.response?.data || error
    }
  }
}
