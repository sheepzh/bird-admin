import request from '@/utils/request'
import md5 from 'js-md5'
import { search } from '@/utils/common'

export function login(data) {
  data.ps = md5(data.ps).toUpperCase()
  return request({
    url: '/user/login',
    method: 'post',
    data
  })
}

export function getInfo() {
  return request({
    url: '/user/info',
    method: 'get'
  })
}

export function logout() {
  return request({
    url: '/user/logout',
    method: 'post'
  })
}

export function allUserSimple() {
  return request({
    url: '/user/simple',
    method: 'get'
  })
}

export function queryUser(queryCond, page) {
  var param = {
    name: queryCond.name,
    account: queryCond.account,
    roles: queryCond.roles,
    status: queryCond.status,
    page: page.num,
    limit: page.limit
  }
  return request({
    url: '/users?' + search(param),
    method: 'get'
  })
}

export function forbiddenUser(account) {
  return request({
    url: `/user/${account}/forbidden`,
    method: 'put'
  })
}

export function unforbiddenUser(account) {
  return request({
    url: `/user/${account}/lift`,
    method: 'put'
  })
}

export function invalidUser(account) {
  return request({
    url: '/user/invalid?ac=' + account,
    method: 'put'
  })
}

export function resetUserPsw(account) {
  return request({
    url: `/user/${account}/reset`,
    method: 'put'
  })
}

export function createUser(temp) {
  return request({
    url: '/user',
    method: 'post',
    data: temp
  })
}

export function getUserByAccount(account) {
  return request({
    url: '/user/account/' + account,
    method: 'get'
  })
}
export function updateUserRoles(account, roles) {
  return request({
    url: `/user/${account}/role`,
    method: 'put',
    data: roles
  })
}

export function updateUserInfo(temp) {
  var body = {
    account: temp.account,
    name: temp.name,
    password: md5(temp.password).toUpperCase(),
    new_password: temp.new_password ? md5(temp.new_password).toUpperCase() : undefined,
    phone: temp.phone
  }
  return request({
    url: '/user',
    method: 'put',
    data: body
  })
}
