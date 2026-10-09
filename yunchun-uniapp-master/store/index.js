import Vue from 'vue'
import Vuex from 'vuex'
import {
  getTabbarList
} from '@/utils/tabbar-config.js'

Vue.use(Vuex)

const STORAGE_KEY = 'yunchun_tabbar_state'

export default new Vuex.Store({
  state: {
    userInfo: {},
    tabbarList: [],
    currentIndex: 0,
    userRole: ''
  },
  mutations: {
    setTabBarState(state, {
      tabbarList,
      currentIndex,
      userRole
    }) {
      state.tabbarList = tabbarList
      state.currentIndex = currentIndex || 0
      state.userRole = userRole || ''

      uni.setStorageSync(STORAGE_KEY, {
        tabbarList,
        currentIndex: currentIndex || 0,
        userRole: userRole || ''
      })
    },
    setCurrentIndex(state, index) {
      state.currentIndex = index

      const savedState = uni.getStorageSync(STORAGE_KEY) || {}
      uni.setStorageSync(STORAGE_KEY, {
        ...savedState,
        currentIndex: index
      })
    },
    resetTabBarState(state) {
      state.tabbarList = []
      state.currentIndex = 0
      state.userRole = ''

      uni.removeStorageSync(STORAGE_KEY)
    },
    SET_USER_INFO(state, userInfo) {
      state.userInfo = userInfo
      if (userInfo) {
        uni.setStorageSync('userInfo', userInfo)
      } else {
        uni.removeStorageSync('userInfo')
      }
    },
    //UPDATE_NICKNAME
  },
  actions: {
    loginSuccess({
      commit
    }, userInfo) {
      const role = userInfo.position || userInfo.role || '普通员工'
      const tabbarList = getTabbarList(role)

      commit('setTabBarState', {
        tabbarList,
        currentIndex: 0,
        userRole: role
      })
    },
    logout({
      commit
    }) {
      commit('resetTabBarState')

      uni.reLaunch({
        url: '/pages/login/login'
      })
    },
    restoreTabBarState({
      commit
    }) {
      const savedState = uni.getStorageSync(STORAGE_KEY)

      if (savedState && savedState.tabbarList && savedState.tabbarList.length > 0) {
        // 检测旧路径（staff-management 已改名为 management），若存在则重新生成
        const hasStalePath = savedState.tabbarList.some(item => item.pagePath.includes('staff-management'))
        if (hasStalePath) {
          const role = savedState.userRole || '普通员工'
          const tabbarList = getTabbarList(role)
          commit('setTabBarState', {
            tabbarList,
            currentIndex: 0,
            userRole: role
          })
        } else {
          commit('setTabBarState', {
            tabbarList: savedState.tabbarList,
            currentIndex: savedState.currentIndex || 0,
            userRole: savedState.userRole || ''
          })
        }
      }
    }
  },
  getters: {
    tabbarList: state => state.tabbarList,
    currentIndex: state => state.currentIndex,
    userRole: state => state.userRole,
    isLoggedIn: state => state.userRole !== ''
  }
})