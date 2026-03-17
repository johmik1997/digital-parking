// Matching your Spring Boot entity structure
export const OrderStatus = {
  PENDING: 'PENDING',
  PROCESSING: 'PROCESSING',
  COMPLETED: 'COMPLETED',
  CANCELLED: 'CANCELLED',
  FAILED: 'FAILED'
};

export class ServiceOrderRequest {
  constructor(serviceId, duration = null, totalAmount = null) {
    this.serviceId = serviceId;
    this.duration = duration;
    this.totalAmount = totalAmount;
  }
}