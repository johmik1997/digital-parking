import { ref } from 'vue'

const toasts = ref([])

export const useToast = () => {
  const showToast = ({ title, description, type = 'info', duration = 3000 }) => {
    const id = Date.now()
    toasts.value.push({ id, title, description, type })
    
    setTimeout(() => {
      toasts.value = toasts.value.filter(t => t.id !== id)
    }, duration)
  }
  
  return { showToast, toasts }
}