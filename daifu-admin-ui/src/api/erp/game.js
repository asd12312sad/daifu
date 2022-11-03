import request from '@/utils/request'

// 查询游戏列表
export function listGame(query) {
  return request({
    url: '/erp/game/list',
    method: 'get',
    params: query
  })
}

// 查询游戏详细
export function getGame(id) {
  return request({
    url: '/erp/game/' + id,
    method: 'get'
  })
}

// 新增游戏
export function addGame(data) {
  return request({
    url: '/erp/game',
    method: 'post',
    data: data
  })
}

// 修改游戏
export function updateGame(data) {
  return request({
    url: '/erp/game',
    method: 'put',
    data: data
  })
}

// 删除游戏
export function delGame(id) {
  return request({
    url: '/erp/game/' + id,
    method: 'delete'
  })
}

// 导出游戏
export function exportGame(query) {
  return request({
    url: '/erp/game/export',
    method: 'get',
    params: query
  })
}