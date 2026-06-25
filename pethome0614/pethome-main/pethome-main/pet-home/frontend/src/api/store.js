import request from '@/utils/request'
// 【功能：获取门店列表】—
export function getStoreList() {
  return request({
    url: '/store/list',
    method: 'get'
  })
}
