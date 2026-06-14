import request from '@/utils/request'

export function getOrderList(params) {
  return request({ url: '/order/list', method: 'get', params })
}

export function getOrderDetail(orderId) {
  return request({ url: '/order/detail', method: 'get', params: { orderId } })
}

export function createOrder(data) {
  return request({ url: '/order/create', method: 'post', data })
}

export function payOrder(orderId) {
  return request({ url: '/order/pay', method: 'post', data: { orderId } })
}

export function completeOrder(orderId) {
  return request({ url: '/order/complete', method: 'post', data: { orderId } })
}

export function cancelOrder(orderId) {
  return request({ url: '/order/cancel', method: 'post', data: { orderId } })
}
