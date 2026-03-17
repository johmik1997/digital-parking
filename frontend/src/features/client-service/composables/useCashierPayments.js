import { computed, ref } from 'vue'
import { useAuth } from '@/stores/auth'
import { pmsApi } from '../api/pmsApi'

const DEFAULT_CASHIER_ID = 'YH989'

export const useCashierPayments = () => {
  const authStore = useAuth()
  const isLoading = ref(false)
  const updatedAt = ref(new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }))
  const payments = ref([])

  const cashierId = computed(() => authStore.auth?.user?.cashierId || DEFAULT_CASHIER_ID)

  const cashierPayments = computed(() =>
    payments.value.filter((scan) => (scan.cashierId || cashierId.value) === cashierId.value)
  )

  const fetchPayments = async ({ limit = 30 } = {}) => {
    isLoading.value = true
    try {
      const response = await pmsApi.getPaymentsHistory({ limit })
      payments.value = (response.data || []).map((p, idx) => ({
        id: p.id || idx,
        plate: p.plateNo,
        createdAt: p.createdAt,
        time: p.createdAt
          ? new Date(p.createdAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
          : '',
        type: p.status ? `Exit - ${p.status}` : 'Exit - Paid',
        paymentMethod: p.paymentMethod && p.paymentMethod.toUpperCase().includes('TELEBIRR') ? 'Telebirr' : 'Cash',
        cashierId: p.cashierId || cashierId.value,
        amount: Number(p.amount || 0)
      }))
      updatedAt.value = new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
    } catch (error) {
      console.error('Failed to load payment history:', error)
    } finally {
      isLoading.value = false
    }
  }

  return {
    payments,
    cashierPayments,
    cashierId,
    isLoading,
    updatedAt,
    fetchPayments
  }
}
