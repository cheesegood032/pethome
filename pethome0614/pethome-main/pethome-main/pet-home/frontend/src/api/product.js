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

export function getAllProducts(params) {
  return request({ url: '/admin/product/list', method: 'get', params })
}

export function addProduct(data) {
  return request({ url: '/admin/product/add', method: 'post', data })
}

export function updateProduct(data) {
  return request({ url: '/admin/product/update', method: 'post', data })
}

export function deleteProduct(id) {
  return request({ url: '/admin/product/delete', method: 'post', data: { id } })
}
