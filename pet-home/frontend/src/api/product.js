import request from '@/utils/request'

export function getProductList(params) {
  return request({
    url: '/product/list',
    method: 'get',
    params
  })
}

export function getAllProducts() {
  return request({
    url: '/product/all',
    method: 'get'
  })
}

export function searchProducts(params) {
  return request({
    url: '/product/search',
    method: 'get',
    params
  })
}

export function getProductDetail(id) {
  return request({
    url: '/product/detail',
    method: 'get',
    params: { id }
  })
}

export function addProduct(data) {
  return request({
    url: '/product/add',
    method: 'post',
    data
  })
}

export function updateProduct(data) {
  return request({
    url: '/product/update',
    method: 'post',
    data
  })
}

export function deleteProduct(id) {
  return request({
    url: '/product/delete',
    method: 'post',
    params: { id }
  })
}
