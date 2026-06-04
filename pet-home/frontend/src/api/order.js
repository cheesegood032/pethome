import request from '@/utils/request'

export function getOrderList(params) {
  return request({
    url: '/order/list',
    method: 'get',
    params
  })
}

export function getOrderDetail(orderId) {
  return request({
    url: '/order/detail',
    method: 'get',
    params: { orderId }
  })
}

export function createOrder(data) {
  return request({
    url: '/order/create',
    method: 'post',
    data
  })
}

export function payOrder(orderId) {
  return request({
    url: '/order/pay',
    method: 'post',
    params: { orderId }
  })
}

export function receiveOrder(orderId) {
  return request({
    url: '/order/receive',
    method: 'post',
    params: { orderId }
  })
}

export function getAdminOrderList(params) {
  return request({
    url: '/order/admin/list',
    method: 'get',
    params
  })
}

export function shipOrder(orderId) {
  return request({
    url: '/order/ship',
    method: 'post',
    params: { orderId }
  })
}
