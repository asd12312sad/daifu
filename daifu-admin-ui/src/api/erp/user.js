import request from '@/utils/request'

// 查询会员列表
export function listUser(query) {
  return request({
    url: '/erp/user/list',
    method: 'get',
    params: query
  })
}

// 查询会员详细
export function getUser(id) {
  return request({
    url: '/erp/user/' + id,
    method: 'get'
  })
}

// 新增会员
export function addUser(data) {
  return request({
    url: '/erp/user',
    method: 'post',
    data: data
  })
}

// 修改会员
export function updateUser(data) {
  return request({
    url: '/erp/user',
    method: 'put',
    data: data
  })
}

// 删除会员
export function delUser(id) {
  return request({
    url: '/erp/user/' + id,
    method: 'delete'
  })
}

// 导出会员
export function exportUser(query) {
  return request({
    url: '/erp/user/export',
    method: 'get',
    params: query
  })
}