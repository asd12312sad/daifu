import request from '@/utils/request'

// 查询代理列表
export function listAgent(query) {
  return request({
    url: '/erp/agent/list',
    method: 'get',
    params: query
  })
}

// 查询代理详细
export function getAgent(id) {
  return request({
    url: '/erp/agent/' + id,
    method: 'get'
  })
}

// 新增代理
export function addAgent(data,adminGoogleSign) {
  return request({
    url: '/erp/agent?adminGoogleSign=' + adminGoogleSign,
    method: 'post',
    data: data
  })
}

// 修改代理
export function updateAgent(data,query) {
  return request({
    url: '/erp/agent',
    method: 'put',
    data: data,
    params:query,
  })
}

// 删除代理
export function delAgent(id,adminGoogleSign) {
  return request({
    url: '/erp/agent/' + id + "?adminGoogleSign=" + adminGoogleSign,
    method: 'delete'
  })
}

// 导出代理
export function exportAgent(query) {
  return request({
    url: '/erp/agent/export',
    method: 'get',
    params: query
  })
}

export function updateGoogleSignCodeApi(query){
  return request({
    url: '/erp/agent/updateGoogleSignCode',
    method: 'post',
    params: query
  })
}
