import Vue from 'vue'
import Cookies from 'js-cookie'

import 'normalize.css/normalize.css' // A modern alternative to CSS resets

import Element from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
// Set the language of element ui
import locale from 'element-ui/lib/locale/lang/zh-CN'

import '@/styles/index.scss' // global css

import App from './App'
import store from './store'
import router from './router'

import '@/icons' // icon
import './permission' // permission control

// set ElementUI lang to Chinese
Vue.use(Element, {
  locale: locale,
  size: Cookies.get('bird_font_size') || 'medium' // set element-ui default size
})

Vue.config.productionTip = false

// 自定义方法开始
import { getById } from './utils/common'
Vue.prototype.$getById = getById
import permission from './utils/permission'
// 需要使用权限控制的组件请在created()方法中调用该函数。且在data中定义permissions参数，以获取用户权限
Vue.prototype.$permission = permission
// 自定义方法结束

new Vue({
  el: '#app',
  router,
  store,
  render: h => h(App)
})
