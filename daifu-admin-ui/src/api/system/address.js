import request from '@/utils/request'

// 查询商户付款地址列表
export function listAddress(query) {
  return request({
    url: '/system/address/list',
    method: 'get',
    params: query
  })
}

// 查询商户付款地址详细
export function getAddress(address) {
  return request({
    url: '/system/address/' + address,
    method: 'get'
  })
}

// 新增商户付款地址
export function addAddress(data) {
  return request({
    url: '/system/address',
    method: 'post',
    data: data
  })
}

// 修改商户付款地址
export function updateAddress(data) {
  return request({
    url: '/system/address',
    method: 'put',
    data: data
  })
}

// 删除商户付款地址
export function delAddress(address) {
  return request({
    url: '/system/address/' + address,
    method: 'delete'
  })
}

// 导出商户付款地址
export function exportAddress(query) {
  return request({
    url: '/system/address/export',
    method: 'get',
    params: query
  })
}

export function huidiaoApi(query){
  return request({
    url: '/system/address/huidiao',
    method: 'get',
    params: query
  })

}
