import request from '@/utils/request'
// 【功能：获取商品列表】
export function getProductList(params) {
  return request({ url: '/product/list', method: 'get', params })
}
// 【功能：获取商品详情】—
export function getProductDetail(id) {
  return request({ url: '/product/detail', method: 'get', params: { id } })
}
// 【功能：获取热销商品】
export function getHotProducts() {
  return request({ url: '/product/hot', method: 'get' })
}
// 【功能：获取新品商品】
export function getNewProducts() {
  return request({ url: '/product/new', method: 'get' })
}
// 【功能：搜索商品】
export function searchProducts(params) {
  return request({ url: '/product/search', method: 'get', params })
}
// ═══════════════════════════════════════════════════
// 以下为管理端 API（需要管理员权限）
// ═══════════════════════════════════════════════════
// 获取商品列表
export function getAllProducts(params) {
  return request({ url: '/admin/product/list', method: 'get', params })
}
// 新增商品
export function addProduct(data) {
  return request({ url: '/admin/product/add', method: 'post', data })
}
// 更新商品（编辑、上下架）
export function updateProduct(data) {
  return request({ url: '/admin/product/update', method: 'post', data })
}
// 删除商品（软删除）
export function deleteProduct(id) {
  return request({ url: '/admin/product/delete', method: 'post', data: { id } })
}
