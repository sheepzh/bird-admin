import request from '@/utils/request'
import { search } from '@/utils/common'

export function addInform(inform) {
  var expiration = inform.expiration
  if (expiration && expiration.getTime) {
    inform.expiration = expiration.getTime()
  }
  return request({
    url: '/inform',
    method: 'post',
    data: inform
  })
}

export function queryInform(cond, page) {
  const params = {
    status: cond.status,
    title: cond.title,
    creator: cond.operator,
    tf: cond.topFirst,
    page: page.num,
    limit: page.size
  }
  const { createDate } = cond
  if (createDate && createDate.length === 2) {
    params.sd = params[0].getTime()
    params.ed = params[1].getTime()
  }
  return request({
    url: `/informs?${search(params)}`,
    method: 'get'
  })
}

export function topInform(id) {
  return request({
    url: `/inform/${id}/top`,
    method: 'put'
  })
}
export function untopInform(id) {
  return request({
    url: `/inform/${id}/untop`,
    method: 'put'
  })
}

export function cancelInform(id) {
  return request({
    url: `/inform/${id}/cancel`,
    method: 'post'
  })
}

export function outdateInform(id) {
  return request({
    url: `/inform/${id}/outdate`,
    method: 'post'
  })
}

export function informInfo(id) {
  return request({
    url: `/inform/${id}`,
    method: 'get'
  })
}
