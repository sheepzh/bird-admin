import { asyncRoutes, constantRoutes } from '@/router'

/**
 * Filter asynchronous routing tables by recursion
 * @param routes asyncRoutes
 * @param roles
 */
export function filterAsyncRoutes(uriSet, routes = [], prefix = '') {
  const res = []
  routes.forEach(route => {
    const tmp = { ...route }
    let path = tmp.path
    if (path.indexOf('/') !== 0) path = '/' + path
    path = prefix + path
    if (uriSet.includes(path)) {
      if (tmp.children) {
        tmp.children = filterAsyncRoutes(uriSet, tmp.children, path)
      }
      res.push(tmp)
    }
  })
  return res
}

const state = {
  routes: [],
  addRoutes: []
}

const mutations = {
  SET_ROUTES: (state, routes) => {
    state.addRoutes = routes
    state.routes = constantRoutes.concat(routes)
  }
}

const actions = {
  generateRoutes({ commit }, uriSet) {
    return new Promise(resolve => {
      let accessedRoutes = []
      accessedRoutes = filterAsyncRoutes(uriSet, asyncRoutes)
      accessedRoutes.push({ path: '*', redirect: '/404', hidden: true })
      commit('SET_ROUTES', accessedRoutes)
      resolve(accessedRoutes)
    })
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
