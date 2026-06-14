import request from '@/utils/request'

export function getStoreList() {
  return request({
    url: '/store/list',
    method: 'get'
  })
}
