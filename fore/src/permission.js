import router from './router'
import store from './store'
import { Message } from 'element-ui'
import NProgress from 'nprogress' // progress bar
import 'nprogress/nprogress.css' // progress bar style
import { getToken } from '@/utils/auth' // get token from cookie
import settings from '@/settings'

NProgress.configure({ showSpinner: false }) // NProgress Configuration

const whiteList = ['/login'] // no redirect whitelist

router.beforeEach(async(to, from, next) => {
  // start progress bar
  NProgress.start()

  // determine whether the user has logged in
  const hasToken = getToken()

  // set page title
  var routeTitle = to.meta.title
  var defaultTitle = settings.title
  var currentName = store.getters.name
  if (hasToken) {
    document.title = (routeTitle || defaultTitle) + (currentName ? `_${currentName}` : '')
  } else {
    document.title = defaultTitle
  }

  if (hasToken) {
    if (to.path === '/login') {
      // if is logged in, redirect to the home page
      next({ path: '/' })
      NProgress.done()
    } else {
      // debugger
      const hasName = store.getters.name
      const { routes } = await store.dispatch('user/getInfo')
      if (!hasName) {
        try {
          // generate accessible routes map based on roles
          const accessRoutes = await store.dispatch('permission/generateRoutes', routes)
          // dynamically add accessible routes
          router.addRoutes(accessRoutes)
          // hack method to ensure that addRoutes is complete
          // set the replace: true, so the navigation will not leave a history record
          next({ ...to, replace: true })
        } catch (error) {
        // remove token and go to login page to re-login
          await store.dispatch('user/resetToken')
          Message.error(error || 'Has Error')
          next(`/login?redirect=${to.path}`)
          NProgress.done()
        }
      } else {
        // 未匹配到路由，界面会出现白屏
        if (to.matched.length === 0) {
          const accessRoutes = await store.dispatch('permission/generateRoutes', routes)
          router.addRoutes(accessRoutes)
          next({ ...to, replace: true })
        } else { next() }
      }
    }
  } else {
    /* has no token*/

    if (whiteList.indexOf(to.path) !== -1) {
      // in the free login whitelist, go directly
      next()
    } else {
      // other pages that do not have permission to access are redirected to the login page.
      next(`/login?redirect=${to.path}`)
      NProgress.done()
    }
  }
})

router.afterEach(() => {
  // finish progress bar
  NProgress.done()
})
