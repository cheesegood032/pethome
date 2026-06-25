import request from '@/utils/request'
// 【功能：用户登录】
export function login(data) {
  return request({
    url: '/user/login',
    method: 'post',
    data
  })
}
// 【功能：用户注册】
export function register(data) {
  return request({
    url: '/user/register',
    method: 'post',
    data
  })
}
// 【功能：退出登录】
export function logout() {
  return request({
    url: '/user/logout',
    method: 'post'
  })
}
// 【功能：获取个人信息】
export function getUserInfo() {
  return request({
    url: '/user/info',
    method: 'get'
  })
}
// 【功能：更新个人信息】
export function updateUser(data) {
  return request({
    url: '/user/update',
    method: 'post',
    data
  })
}
// 【功能：修改密码】
export function changePassword(data) {
  return request({
    url: '/user/changePassword',
    method: 'post',
    data
  })
}
// 【功能：获取所有用户（管理员）】
export function getAllUsers(params) {
  return request({
    url: '/admin/user/list',
    method: 'get',
    params
  })
}
// 【功能：删除用户（管理员）】
export function deleteUser(id) {
  return request({
    url: '/admin/user/delete',
    method: 'post',
    data: { id }
  })
}
