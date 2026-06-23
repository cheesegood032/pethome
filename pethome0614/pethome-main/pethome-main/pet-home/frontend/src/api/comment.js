import request from '@/utils/request'

/* ========== 用户端 ========== */

// 提交评论
export function addComment(data) {
  return request({ url: '/comment/add', method: 'post', data })
}

// 查询商品评论列表
export function getCommentsByProduct(productId) {
  return request({ url: '/comment/list', method: 'get', params: { productId } })
}

// 查询某订单下已评论的订单项ID列表
export function getCommentStatus(orderId) {
  return request({ url: '/comment/status', method: 'get', params: { orderId } })
}

/* ========== 管理员端 ========== */

// 管理员查询所有评论
export function getAdminCommentList(params) {
  return request({ url: '/admin/comment/list', method: 'get', params })
}

// 管理员回复评论
export function replyComment(data) {
  return request({ url: '/admin/comment/reply', method: 'post', data })
}
