import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    user: null,
    token: null,
    cartCount: 0
  },
  mutations: {
    setUser(state, user) {
      state.user = user
      if (user) {
        localStorage.setItem('user', JSON.stringify(user))
      } else {
        localStorage.removeItem('user')
      }
    },
    setToken(state, token) {
      state.token = token
      if (token) {
        localStorage.setItem('token', token)
      } else {
        localStorage.removeItem('token')
      }
    },
    setCartCount(state, count) {
      state.cartCount = count
    },
    initState(state) {
      const user = localStorage.getItem('user')
      const token = localStorage.getItem('token')
      if (user) {
        state.user = JSON.parse(user)
      }
      if (token) {
        state.token = token
      }
    }
  },
  actions: {
    login({ commit }, { user, token }) {
      commit('setUser', user)
      commit('setToken', token)
    },
    logout({ commit }) {
      commit('setUser', null)
      commit('setToken', null)
      commit('setCartCount', 0)
    }
  }
})
