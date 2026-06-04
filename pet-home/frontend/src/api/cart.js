import request from '@/utils/request'

export function getCartList() {
  return request({
    url: '/cart/list',
    method: 'get'
  })
}

export function addToCart(data) {
  return request({
    url: '/cart/add',
    method: 'post',
    data
  })
}

export function updateCartQuantity(data) {
  return request({
    url: '/cart/update',
    method: 'post',
    data
  })
}

export function removeFromCart(cartId) {
  return request({
    url: '/cart/remove',
    method: 'post',
    params: { cartId }
  })
}

export function clearCart() {
  return request({
    url: '/cart/clear',
    method: 'post'
  })
}
