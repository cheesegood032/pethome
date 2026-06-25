import request from '@/utils/request'
// 【功能：获取我的宠物列表】
export function getPetList() {
  return request({
    url: '/pet/list',
    method: 'get'
  })
}
// 【功能：获取宠物详情】
export function getPetDetail(id) {
  return request({
    url: '/pet/detail',
    method: 'get',
    params: { id }
  })
}
// 【功能：获取所有宠物（管理员）】
export function getAllPets() {
  return request({
    url: '/pet/all',
    method: 'get'
  })
}
// 【功能：添加宠物】
export function addPet(data) {
  return request({
    url: '/pet/add',
    method: 'post',
    data
  })
}
// 【功能：更新宠物信息】
export function updatePet(data) {
  return request({
    url: '/pet/update',
    method: 'post',
    data
  })
}
// 【功能：删除宠物】
export function deletePet(id) {
  return request({
    url: '/pet/delete',
    method: 'post',
    params: { id }
  })
}
