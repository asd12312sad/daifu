import request from '@/utils/request'

// 查询商户管理列表
export function listMerchant(query) {
  return request({
    url: '/erp/merchant/list',
    method: 'get',
    params: query
  })
}

//获得验证码
export function getEmailCode(query){
  return request({
    url: '/erp/merchant/updateEmailCode',
    method: 'get',
    params: query
  })
}
//修改安全邮箱
export function updateEmailCode(query){
  return request({
    url: '/erp/merchant/updateEmail',
    method: 'get',
    params: query
  })
}

// 查询商户管理详细
export function getMerchant(id) {
  return request({
    url: '/erp/merchant/' + id,
    method: 'get'
  })
}
export function testMerchant(id,query){
  return request({
    url: '/erp/merchant/test/' + id,
    method: 'get',
    params: query
  })
}
export function recharge(id,query){
  return request({
    url: '/erp/merchant/recharge/' + id,
    method: 'get',
    params: query,
  })
}
export function addTrialQuota(id,query){
  return request({
    url: '/erp/merchant/trialQuota/' + id,
    method: 'get',
    params: query,
  })
}
export function testPay(id,query){
  return request({
    url: '/erp/merchant/testPay/' + id,
    method: 'get',
    params: query,
  })
}


// 新增商户管理
export function addMerchant(data,query) {
  return request({
    url: '/erp/merchant',
    method: 'post',
    data: data,
    params: query
  })
}

export function updateGoogleSignCodeApi(params){
  return request({
    url: '/erp/merchant/updateGoogleSignCode',
    method: 'get',
    params: params,
  })
}

// 修改商户管理
export function updateMerchant(data,query) {
  return request({
    url: '/erp/merchant',
    method: 'put',
    data: data,
    params: query
  })
}

// 删除商户管理
export function delMerchant(id,query) {
  return request({
    url: '/erp/merchant/' + id,
    method: 'delete',
    params: query
  })
}

// 导出商户管理
export function exportMerchant(query) {
  return request({
    url: '/erp/merchant/export',
    method: 'get',
    params: query
  })
}
