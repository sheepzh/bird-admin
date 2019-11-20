import axios from 'axios'
import { MessageBox, Message } from 'element-ui'
import store from '@/store'
import { getToken } from '@/utils/auth'

// create an axios instance
const service = axios.create({
  baseURL: process.env.VUE_APP_BASE_API, // url = base url + request url
  // withCredentials: true, // send cookies when cross-domain requests
  timeout: 60000 // request timeout
})

// request interceptor
service.interceptors.request.use(
  config => {
    // do something before request is sent

    if (store.getters.token) {
      // let each request carry token
      // ['B-Token'] is a custom headers key
      // please modify it according to the actual situation
      const { headers } = config
      headers['B-Token'] = getToken()
    }
    return config
  },
  error => {
    // do something with request error
    return Promise.reject(error)
  }
)

// response interceptor
service.interceptors.response.use(
  /**
   * If you want to get http information such as headers or status
   * Please return  response => response
  */

  /**
   * Determine the request status by custom code
   * Here is just an example
   * You can also judge the status by HTTP Status Code
   */
  response => {
    const res = response.data

    // // judge is Blog response for file download
    if (res && res.constructor && res.constructor.toString()) {
      const constructorName = res.constructor.name
      if (constructorName && constructorName === 'Blob') { return res }
      const str = res.constructor.toString()
      let arr
      if (str.charAt(0) === '[') {
        arr = str.match(/\w+\s∗(\w+)\w+\s∗(\w+)/)
      } else {
        arr = str.match(/function\s*(\w+)/)
      }
      if (arr && arr.length === 2 && arr[1] === 'Blob') return res
    }

    // if the custom code is not 1, it is judged as an error.
    if (res.code !== undefined && res.code !== null && res.code !== 1) {
      Message({
        message: res.msg ? res.msg : '请求错误',
        type: 'error',
        duration: 3 * 1000
      })

      // 50008: Illegal token; 50012: Other clients logged in; 50014: Token expired;
      if (res.code === 30005) {
        // to re-login
        MessageBox.confirm('登录已超时，请重新登录！', '非法请求', {
          confirmButtonText: '重新登陆',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          store.dispatch('user/resetToken').then(() => {
            location.reload()
          })
        })
      }
      return Promise.reject(new Error(res.msg || '请求错误'))
    } else {
      return res
    }
  },
  error => {
    Message({
      message: (error && error.response && error.response.status) ? error.response.status + '  错误' : '页面超时，请按F5刷新页面',
      type: 'error',
      duration: 5 * 1000
    })
    return Promise.reject(error)
  }
)

export default service
