import request from '@/utils/request'
// 【功能：管理员登录】
export function login(data) {
  return request({
    url: '/admin/login',
    method: 'post',
    data
  })
}
// 【功能：管理员退出】
export function logout() {
  return request({
    url: '/admin/logout',
    method: 'post'
  })
}
// 【功能：获取管理员信息】
export function getAdminInfo() {
  return request({
    url: '/admin/info',
    method: 'get'
  })
}
// 【功能：获取仪表盘统计数据】
export function getDashboardStats() {
  return request({
    url: '/admin/stats',
    method: 'get'
  })
}