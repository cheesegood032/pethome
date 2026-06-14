import request from '@/utils/request'

export function getPackageList() {
  return request({ url: '/foster/package/list', method: 'get' })
}

export function getFosterOrderList(params) {
  return request({ url: '/foster/order/list', method: 'get', params })
}

export function getFosterOrderDetail(id) {
  return request({ url: '/foster/order/detail', method: 'get', params: { id } })
}

export function createFosterOrder(data) {
  return request({ url: '/foster/order/create', method: 'post', data })
}

export function cancelFosterOrder(orderId) {
  return request({ url: '/foster/order/cancel', method: 'post', data: { orderId } })
}

export function payFosterOrder(orderId) {
  return request({ url: '/foster/order/pay', method: 'post', data: { orderId } })
}
