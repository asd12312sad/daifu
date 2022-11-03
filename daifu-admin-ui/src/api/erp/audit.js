import request from '@/utils/request'

// 查询提币审核列表
export function listAudit(query) {
  return request({
    url: '/erp/audit/list',
    method: 'get',
    params: query
  })
}

// 查询提币审核详细
export function getAudit(id) {
  return request({
    url: '/erp/audit/' + id,
    method: 'get'
  })
}


export function audit(data) {
  return request({
    url: '/erp/audit',
    method: 'post',
    data: data
  })
}

// 修改提币审核
export function updateAudit(data) {
  return request({
    url: '/erp/audit',
    method: 'put',
    data: data
  })
}

// 删除提币审核
export function delAudit(id) {
  return request({
    url: '/erp/audit/' + id,
    method: 'delete'
  })
}

// 导出提币审核
export function exportAudit(query) {
  return request({
    url: '/erp/audit/export',
    method: 'get',
    params: query
  })
}
