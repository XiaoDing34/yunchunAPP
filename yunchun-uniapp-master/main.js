import App from './App'

import Vue from 'vue'
import './uni.promisify.adaptor'
import Vuex from 'vuex'
import store from './store/index.js'

Vue.use(Vuex)
Vue.config.productionTip = false

Vue.mixin({
  onShow() {
    const pages = getCurrentPages()
    if (pages.length > 0) {
      const currentPage = pages[pages.length - 1]
      const currentPath = '/' + currentPage.route
      const tabbarList = store.state.tabbarList
      const index = tabbarList.findIndex(item => item.pagePath === currentPath)
      if (index !== -1 && index !== store.state.currentIndex) {
        store.commit('setCurrentIndex', index)
      }
    }
  }
})

App.mpType = 'app'
const app = new Vue({
  store,
  ...App
})
app.$mount()