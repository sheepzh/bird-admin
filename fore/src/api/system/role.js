
import request from '@/utils/request'
import { search } from '@/utils/common'

export function queryAllRoles(cond) {
  if (!cond) cond = {}
  return request({
    url: '/roles?' + search(cond),
    method: 'get'
  })
}

export function addRole(temp) {
  return request({
    url: '/role',
    method: 'post',
    data: temp
  })
}

export function updateRole(temp) {
  return request({
    url: '/role',
    method: 'put',
    data: temp
  })
}

/**
 * 禁用角色
 * @param {角色ID} id
 */
export function banRole(id) {
  if (id === undefined) id = -1
  return request({
    url: '/role/ban/' + id,
    method: 'post'
  })
}

/**
 * 解禁角色
 * @param {角色ID} id
 */
export function liftRole(id) {
  if (id === undefined) id = -1
  return request({
    url: `/role/lift/${id}`,
    method: 'post'
  })
}

// 以下开发模式使用
export function deleteRole(id) {
  return request({
    url: `/role/${id}`,
    method: 'delete'
  })
}
