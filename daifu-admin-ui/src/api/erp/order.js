import request from '@/utils/request'

// 查询下注记录列表
export function listOrder(query) {
  return request({
    url: '/erp/order/list',
    method: 'get',
    params: query
  })
}
// 查询下注记录列表
export function listfanbi(query) {
  return request({
    url: '/erp/order/returnMoney',
    method: 'get',
    params: query
  })
}
export function homeSum(query) {
  return request({
    url: '/home/sum',
    method: 'get',
    params: query
  })
}

// 查询下注记录详细
export function getOrder(id) {
  return request({
    url: '/erp/order/' + id,
    method: 'get'
  })
}

// 新增下注记录
export function addOrder(data) {
  return request({
    url: '/erp/order',
    method: 'post',
    data: data
  })
}

// 修改下注记录
export function updateOrder(data) {
  return request({
    url: '/erp/order',
    method: 'put',
    data: data
  })
}

// 删除下注记录
export function delOrder(id) {
  return request({
    url: '/erp/order/' + id,
    method: 'delete'
  })
}

// 导出下注记录
export function exportOrder(query) {
  return request({
    url: '/erp/order/export',
    method: 'get',
    params: query
  })
}
