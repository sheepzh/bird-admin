import request from '@/utils/request'
import { search } from '@/utils/common'

export function queryLog(cond, page) {
  const params = {
    ua: cond.userAccount,
    des: cond.des,
    is: cond.isSuccess,
    ht_min: cond.handlingTimeMin,
    ht_max: cond.handlingTimeMax,
    page: page.num,
    size: page.limit
  }
  const { requestDate } = cond
  if (requestDate && requestDate.length === 2) {
    params.rs = requestDate[0].getTime() // 请求时间开始
    params.re = requestDate[1].getTime() // 请求时间结束
  }
  return request({
    url: '/logs?' + search(params),
    method: 'get'
  })
}
