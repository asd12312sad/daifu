import request from '@/utils/request'

// 查询商户代付记录列表
export function listPayOrder(query) {
  return request({
    url: '/erp/payOrder/list',
    method: 'get',
    params: query
  })
}

// 查询商户代付记录详细
export function getPayOrder(id) {
  return request({
    url: '/erp/payOrder/' + id,
    method: 'get'
  })
}

// 新增商户代付记录
export function addPayOrder(data) {
  return request({
    url: '/erp/payOrder',
    method: 'post',
    data: data
  })
}

// 修改商户代付记录
export function updatePayOrder(data) {
  return request({
    url: '/erp/payOrder',
    method: 'put',
    data: data
  })
}
// 修改商户代付记录
export function huidiaoApi(params) {
  return request({
    url: '/erp/payOrder/huidiao',
    method: 'get',
    params: params
  })
}


// 删除商户代付记录
export function delPayOrder(id) {
  return request({
    url: '/erp/payOrder/' + id,
    method: 'delete'
  })
}

// 导出商户代付记录
export function exportPayOrder(query) {
  return request({
    url: '/erp/payOrder/export',
    method: 'get',
    params: query
  })
}
