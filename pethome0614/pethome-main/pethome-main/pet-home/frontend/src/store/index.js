import Vue from 'vue'
import Vuex from 'vuex'
import request from '@/utils/request'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    user: JSON.parse(localStorage.getItem('user') || 'null'),
    pendingCount: 0
  },
  getters: {
    isLoggedIn: state => !!state.user,
    username: state => state.user ? state.user.username : '',
    hasPending: state => state.pendingCount > 0
  },
  mutations: {
    SET_USER(state, user) {
      state.user = user
      if (user) {
        localStorage.setItem('user', JSON.stringify(user))
      } else {
        localStorage.removeItem('user')
      }
    },
    SET_PENDING_COUNT(state, count) {
      state.pendingCount = count
    }
  },
  actions: {
    login({ commit }, user) {
      commit('SET_USER', user)
    },
    logout({ commit }) {
      commit('SET_USER', null)
      commit('SET_PENDING_COUNT', 0)
    },
    updatePendingCount({ commit }, count) {
      commit('SET_PENDING_COUNT', count)
    },
    async refreshBadge({ commit, state }) {
      if (!state.user) return
      try {
        const res = await request.get('/order/list', { params: { status: 3, page: 1, pageSize: 1 } })
        commit('SET_PENDING_COUNT', res.data.total || 0)
      } catch (e) {
        console.error('refreshBadge failed', e)
      }
    }
  }
})
