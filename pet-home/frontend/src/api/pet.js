import request from '@/utils/request'

export function getPetList() {
  return request({
    url: '/pet/list',
    method: 'get'
  })
}

export function getPetDetail(id) {
  return request({
    url: '/pet/detail',
    method: 'get',
    params: { id }
  })
}

export function getAllPets() {
  return request({
    url: '/pet/all',
    method: 'get'
  })
}

export function addPet(data) {
  return request({
    url: '/pet/add',
    method: 'post',
    data
  })
}

export function updatePet(data) {
  return request({
    url: '/pet/update',
    method: 'post',
    data
  })
}

export function deletePet(id) {
  return request({
    url: '/pet/delete',
    method: 'post',
    params: { id }
  })
}
