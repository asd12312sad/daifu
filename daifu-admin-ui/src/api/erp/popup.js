import request from '@/utils/request'

// 查询弹窗列表
export function listPopup(query) {
  return request({
    url: '/erp/popup/list',
    method: 'get',
    params: query
  })
}

// 查询弹窗详细
export function getPopup(id) {
  return request({
    url: '/erp/popup/' + id,
    method: 'get'
  })
}

// 新增弹窗
export function addPopup(data) {
  return request({
    url: '/erp/popup',
    method: 'post',
    data: data
  })
}

// 修改弹窗
export function updatePopup(data) {
  return request({
    url: '/erp/popup',
    method: 'put',
    data: data
  })
}

// 删除弹窗
export function delPopup(id) {
  return request({
    url: '/erp/popup/' + id,
    method: 'delete'
  })
}

// 导出弹窗
export function exportPopup(query) {
  return request({
    url: '/erp/popup/export',
    method: 'get',
    params: query
  })
}