import request from '@/utils/request'

// 查询商户代付记录列表
export function sumApi() {
  return request({
    url: '/home/sum',
    method: 'get'
  })
}
// 查询商户代付记录列表
export function weekSumApi() {
  return request({
    url: '/home/week/sum',
    method: 'get'
  })
}
