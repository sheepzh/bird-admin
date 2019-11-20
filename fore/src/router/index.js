import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router)

/* Layout */
import Layout from '@/layout'

/**
 * Note: sub-menu only appear when route children.length >= 1
 * Detail see: https://panjiachen.github.io/vue-element-admin-site/guide/essentials/router-and-nav.html
 *
 * hidden: true                   if set true, item will not show in the sidebar(default is false)
 * alwaysShow: true               if set true, will always show the root menu
 *                                if not set alwaysShow, when item has more than one children route,
 *                                it will becomes nested mode, otherwise not show the root menu
 * redirect: noRedirect           if set noRedirect will no redirect in the breadcrumb
 * name:'router-name'             the name is used by <keep-alive> (must set!!!)
 * meta : {
    roles: ['admin','editor']    control the page roles (you can set multiple roles)
    title: 'title'               the name show in sidebar and breadcrumb (recommend set)
    icon: 'svg-name'             the icon show in the sidebar
    breadcrumb: false            if set false, the item will hidden in breadcrumb(default is true)
    activeMenu: '/example/list'  if set path, the sidebar will highlight the path you set
  }
 */

/**
 * constantRoutes
 * a base page that does not have permission requirements
 * all roles can be accessed
 */
export const constantRoutes = [
  // 页签关闭/刷新重定向
  {
    path: '/redirect',
    component: Layout,
    hidden: true,
    children: [
      {
        path: '/redirect/:path*',
        component: () => import('@/views/redirect/index')
      }
    ]
  },
  {
    path: '/login',
    component: () => import('@/views/login/index'),
    hidden: true
  },
  {
    path: '/me',
    component: Layout,
    hidden: true,
    redirect: '/me/index',
    children: [
      {
        path: 'index',
        name: 'PersonalCenter',
        component: () => import('@/views/personal_center/index'),
        meta: {
          title: '个人中心'
        }
      }
    ]
  },
  {
    path: '/inform',
    component: Layout,
    hidden: true,
    redirect: '/inform/:id',
    children: [{
      path: ':id',
      name: 'InformInfo',
      component: () => import('@/views/system/inform/info'),
      meta: {
        title: '公告'
      }
    }]

  },
  {
    path: '/404',
    component: () => import('@/views/404'),
    hidden: true
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [{
      path: 'dashboard',
      name: 'Dashboard',
      component: () => import('@/views/dashboard/index'),
      meta: { title: '我的主页', icon: 'dashboard' }
    }]
  }
]

// 动态加载的角色
export const asyncRoutes = [
  {
    path: '/system',
    component: Layout,
    redirect: '/system/settings',
    name: 'System config',
    meta: {
      title: '系统功能',
      icon: 'system'
    },
    children: [
      {
        path: 'user',
        name: 'UserManagement',
        component: () => import('@/views/system/user/index'),
        meta: {
          title: '用户管理',
          icon: 'user'
        }
      }, {
        path: 'inform',
        name: 'InformManagement',
        component: () => import('@/views/system/inform/index'),
        meta: {
          title: '系统公告',
          icon: 'inform'
        }
      }, {
        path: 'log',
        name: 'UserActionLog',
        component: () => import('@/views/system/log/index'),
        meta: {
          title: '操作日志',
          icon: 'log'
        }
      }
    ]
  },
  // root角色专用路由，与系统配置相关
  {
    path: '/root',
    component: Layout,
    name: 'Permission',
    meta: {
      title: 'Root功能',
      icon: 'root'
    },
    redirect: '/root/menu',
    children: [
      {
        name: 'MenuPermission',
        path: 'menu',
        meta: {
          title: '路由权限',
          icon: 'router'
        },
        component: () => import('@/views/system/root/route')
      },
      {
        name: 'NodePermission',
        path: 'permission',
        meta: {
          title: '权限细节',
          icon: 'permission'
        },
        component: () => import('@/views/system/root/permission')
      },
      {
        path: 'role',
        name: 'RoleManagement',
        component: () => import('@/views/system/root/role/index'),
        meta: {
          title: '角色管理',
          icon: 'role'
        }
      }
    ]
  }
]

export const rootRoutes = {

}

const createRouter = () => new Router({
  // mode: 'history', // require service support
  scrollBehavior: () => ({ y: 0 }),
  routes: constantRoutes
})

const router = createRouter()

// Detail see: https://github.com/vuejs/vue-router/issues/1234#issuecomment-357941465
export function resetRouter() {
  const newRouter = createRouter()
  router.matcher = newRouter.matcher // reset router
}

export default router
