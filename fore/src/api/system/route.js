import request from '@/utils/request'

/**
 * 获取路由以及角色权限
 */
export function queryAllRoutesWithRoles() {
  return request({
    url: '/route/roles',
    method: 'get'
  })
}

/**
 * 修改路由相应的角色权限
 * @param {路由ID} id
 * @param {角色ID集合} roleIds
 * @param {子路由是否级联} cascade
 */
export function updateRouteRoles(id, roleIds, cascade) {
  return request({
    url: `/route/${id}/role?cascade=${!!cascade}`,
    method: 'put',
    data: roleIds || []
  })
}

/**
 * 刷新缓存，刷新缓存后其他用户才会使用新的权限
 */
export function flushCache() {
  return request({
    url: '/route/flush',
    method: 'put'
  })
}

// 以下接口开发模式下才能调用

export function addRoute(temp) {
  return request({
    url: '/route',
    method: 'post',
    data: temp
  })
}

export function updateRoute(temp) {
  return request({
    url: '/route',
    method: 'put',
    data: temp
  })
}

export function deleteRoute(id) {
  return request({
    url: `/route/${id}`,
    method: 'delete'
  })
}
