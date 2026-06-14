import request from '@/utils/request'

export function getProductList(params) {
  return request({ url: '/product/list', method: 'get', params })
}

export function getProductDetail(id) {
  return request({ url: '/product/detail', method: 'get', params: { id } })
}

export function getHotProducts() {
  return request({ url: '/product/hot', method: 'get' })
}

export function getNewProducts() {
  return request({ url: '/product/new', method: 'get' })
}

export function searchProducts(params) {
  return request({ url: '/product/search', method: 'get', params })
}
