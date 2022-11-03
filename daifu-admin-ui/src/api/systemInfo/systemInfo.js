import request from '@/utils/request'

// 查询系统日志列表
export function listSystemInfo(query) {
  return request({
    url: '/systemInfo/systemInfo/list',
    method: 'get',
    params: query
  })
}

// 查询系统日志详细
export function getSystemInfo(id) {
  return request({
    url: '/systemInfo/systemInfo/' + id,
    method: 'get'
  })
}

// 新增系统日志
export function addSystemInfo(data) {
  return request({
    url: '/systemInfo/systemInfo',
    method: 'post',
    data: data
  })
}

// 修改系统日志
export function updateSystemInfo(data,query) {
  return request({
    url: '/systemInfo/systemInfo',
    method: 'put',
    data: data,
    params: query
  })
}

// 删除系统日志
export function delSystemInfo(id) {
  return request({
    url: '/systemInfo/systemInfo/' + id,
    method: 'delete'
  })
}

// 导出系统日志
export function exportSystemInfo(query) {
  return request({
    url: '/systemInfo/systemInfo/export',
    method: 'get',
    params: query
  })
}