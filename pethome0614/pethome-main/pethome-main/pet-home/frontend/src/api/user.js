import request from '@/utils/request'

export function login(data) {
  return request({
    url: '/user/login',
    method: 'post',
    data
  })
}

export function register(data) {
  return request({
    url: '/user/register',
    method: 'post',
    data
  })
}

export function logout() {
  return request({
    url: '/user/logout',
    method: 'post'
  })
}

export function getUserInfo() {
  return request({
    url: '/user/info',
    method: 'get'
  })
}

export function updateUser(data) {
  return request({
    url: '/user/update',
    method: 'post',
    data
  })
}

export function changePassword(data) {
  return request({
    url: '/user/changePassword',
    method: 'post',
    data
  })
}

export function getAllUsers(params) {
  return request({
    url: '/admin/user/list',
    method: 'get',
    params
  })
}

export function deleteUser(id) {
  return request({
    url: '/admin/user/delete',
    method: 'post',
    data: { id }
  })
}
