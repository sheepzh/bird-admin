import { login, logout, getInfo } from '@/api/user'
import { getToken, setToken, removeToken } from '@/utils/auth'
import { resetRouter } from '@/router'

const state = {
  token: getToken(),
  name: '',
  account: '',
  roles: []
}

const mutations = {
  SET_TOKEN: (state, token) => {
    state.token = token
  },
  SET_NAME: (state, name) => {
    state.name = name
  },
  SET_ROLES: (state, roles) => {
    state.roles = roles
  },
  SET_ACCOUNT: (state, account) => {
    state.account = account
  },
  SET_PHONE: (state, phone) => {
    state.phone = phone
  }
}

const actions = {
  // user login
  login({ commit }, userInfo) {
    const { account, password } = userInfo
    return new Promise((resolve, reject) => {
      login({ ac: account.trim(), ps: password }).then(res => {
        commit('SET_TOKEN', res.token)
        commit('SET_ACCOUNT', res.user.account)
        setToken(res.token)
        resolve(res)
      }).catch(error => {
        reject(error)
      })
    })
  },

  // get user info
  getInfo({ commit, state }) {
    return new Promise((resolve, reject) => {
      getInfo().then(response => {
        if (response.code !== 1) {
          reject('账号校验失败，请重新登录')
        }
        commit('SET_NAME', response.user.name)
        commit('SET_ACCOUNT', response.user.account)
        commit('SET_ROLES', response.roles)
        commit('SET_PHONE', response.user.phone)
        resolve(response)
      }).catch(error => {
        reject(error)
      })
    })
  },

  // user logout
  logout({ commit, state }) {
    return new Promise((resolve, reject) => {
      logout(state.token).then(() => {
        // 清空用户信息
        commit('SET_TOKEN', '')
        commit('SET_ROLES', [])
        commit('SET_NAME', '')
        removeToken()
        resetRouter()
        resolve()
      }).catch(error => {
        reject(error)
      })
    })
  },

  // remove token
  resetToken({ commit }) {
    return new Promise(resolve => {
      commit('SET_TOKEN', '')
      removeToken()
      resolve()
    })
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}

