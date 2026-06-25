import request from '@/utils/request'
// 【功能：获取购物车列表】
export function getCartList() {
  return request({
    url: '/cart/list',
    method: 'get'
  })
}
// 【功能：加入购物车】
export function addToCart(data) {
  return request({
    url: '/cart/add',
    method: 'post',
    data
  })
}
// 【功能：修改购物车数量】
export function updateCartQuantity(data) {
  return request({
    url: '/cart/update',
    method: 'post',
    data
  })
}
// 【功能：删除购物车商品】
export function removeFromCart(cartId) {
  return request({
    url: '/cart/remove',
    method: 'post',
    params: { cartId }
  })
}
// 【功能：清空购物车】
export function clearCart() {
  return request({
    url: '/cart/clear',
    method: 'post'
  })
}
