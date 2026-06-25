import request from '@/utils/request'
// ═══════════════════════════════════════════════════
// 用户端 API
// ═══════════════════════════════════════════════════
// 获取寄养套餐列表
export function getPackageList() {
  return request({ url: '/foster/package/list', method: 'get' })
}
// 【功能：获取我的寄养订单】
export function getFosterOrderList(params) {
  return request({ url: '/foster/order/list', method: 'get', params })
}
// 【功能：获取寄养订单详情】
export function getFosterOrderDetail(id) {
  return request({ url: '/foster/order/detail', method: 'get', params: { id } })
}
// 创建寄养订单
export function createFosterOrder(data) {
  return request({ url: '/foster/order/create', method: 'post', data })
}
// 【功能：取消寄养订单】
export function cancelFosterOrder(orderId) {
  return request({ url: '/foster/order/cancel', method: 'post', data: { orderId } })
}
// 【功能：支付寄养订单】
export function payFosterOrder(orderId) {
  return request({ url: '/foster/order/pay', method: 'post', data: { orderId } })
}
// ═══════════════════════════════════════════════════
// 管理端 API（需要管理员权限）
// ═══════════════════════════════════════════════════
// 【功能：获取所有寄养订单（管理员）】
export function getAllFosterOrders(params) {
  return request({ url: '/admin/foster/list', method: 'get', params })
}
// 【功能：审批寄养订单（管理员）】
export function auditFosterOrder(data) {
  if (data.approved) {
    return request({ url: '/admin/foster/approve', method: 'post', data })
  } else {
    return request({ url: '/admin/foster/reject', method: 'post', data })
  }
}
// 【功能：办理入住（管理员）】
export function checkinFosterOrder(orderId) {
  return request({ url: '/admin/foster/checkin', method: 'post', data: { orderId } })
}
// 【功能：完成寄养（管理员）】
export function completeFosterOrder(orderId) {
  return request({ url: '/admin/foster/complete', method: 'post', data: { orderId } })
}
