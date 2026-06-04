import request from '@/utils/request'

export function getPackageList() {
  return request({
    url: '/foster/package/list',
    method: 'get'
  })
}

export function getAllPackages() {
  return request({
    url: '/foster/package/all',
    method: 'get'
  })
}

export function getPackageDetail(id) {
  return request({
    url: '/foster/package/detail',
    method: 'get',
    params: { id }
  })
}

export function getFosterOrderList(params) {
  return request({
    url: '/foster/order/list',
    method: 'get',
    params
  })
}

export function getFosterOrderDetail(id) {
  return request({
    url: '/foster/order/detail',
    method: 'get',
    params: { id }
  })
}

export function getAllFosterOrders(params) {
  return request({
    url: '/foster/order/all',
    method: 'get',
    params
  })
}

export function createFosterOrder(data) {
  return request({
    url: '/foster/order/create',
    method: 'post',
    data
  })
}

export function cancelFosterOrder(orderId) {
  return request({
    url: '/foster/order/cancel',
    method: 'post',
    params: { orderId }
  })
}

export function auditFosterOrder(data) {
  return request({
    url: '/foster/order/audit',
    method: 'post',
    data
  })
}

export function completeFosterOrder(orderId) {
  return request({
    url: '/foster/order/complete',
    method: 'post',
    params: { orderId }
  })
}

export function searchFosterOrders(params) {
  return request({
    url: '/foster/order/search',
    method: 'get',
    params
  })
}
