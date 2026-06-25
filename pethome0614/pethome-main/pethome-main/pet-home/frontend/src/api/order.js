import request from '@/utils/request'
// 【功能：获取我的订单列表】
export function getOrderList(params) {
  return request({ url: '/order/list', method: 'get', params })
}
// 【功能：获取订单详情】
export function getOrderDetail(orderId) {
  return request({ url: '/order/detail', method: 'get', params: { orderId } })
}
// 【功能：创建订单】
export function createOrder(data) {
  return request({ url: '/order/create', method: 'post', data })
}
// 【功能：支付订单】
export function payOrder(orderId) {
  return request({ url: '/order/pay', method: 'post', data: { orderId } })
}
// 【功能：确认完成订单】
export function completeOrder(orderId) {
  return request({ url: '/order/complete', method: 'post', data: { orderId } })
}
// 【功能：取消订单】
export function cancelOrder(orderId) {
  return request({ url: '/order/cancel', method: 'post', data: { orderId } })
}
// 【功能：获取所有订单（管理员）】
export function getAdminOrderList(params) {
  return request({ url: '/admin/order/list', method: 'get', params })
}
// 【功能：发货（管理员）】
export function shipOrder(orderId) {
  return request({ url: '/admin/order/ship', method: 'post', data: { orderId } })
}
