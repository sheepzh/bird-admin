import request from '@/utils/request'
import { search } from '@/utils/common'

export function queryPermissions(cond) {
  return request({
    url: '/permissions?' + search(cond),
    method: 'get'
  })
}

export function addPermission(temp) {
  return request({
    url: '/permission',
    method: 'post',
    data: temp
  })
}

export function updatePermission(temp) {
  return request({
    url: '/permission',
    method: 'put',
    data: temp
  })
}

export function deletePermission(id) {
  return request({
    url: `/permission/${id}`,
    method: 'delete'
  })
}

/**
 * 绑定权限的角色
 * @param {权限ID} id
 * @param {角色ID} roleIds
 */
export function updatePermissionRoles(id, roleIds) {
  return request({
    url: `/permission/${id}/role`,
    method: 'put',
    data: roleIds || []
  })
}

export function flushPermissionCache() {
  return request({
    url: '/permission/flush',
    method: 'put'
  })
}

/**
 * 获取该组件的所有权限点
 * @param {组件}} c
 */
export function queryPermissionByComponent(c) {
  return request({
    url: `/permission/${c.$options.name}`,
    method: 'get'
  })
}
