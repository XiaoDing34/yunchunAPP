<script>
  import api from '@/untils/request.js'

  export default {
    onLaunch: async function() {
      this.$store.dispatch('restoreTabBarState')
      // 登录页作为启动页，登录逻辑由登录页处理
      // 如果已登录且tabbarList为空，初始化tabbar状态
      const token = uni.getStorageSync('yunchun')
      if (token && this.$store.state.tabbarList.length === 0) {
        try {
          const userRes = await api.get('/appUser/info')
          if (userRes.code == 200) {
            this.$store.dispatch('loginSuccess', userRes.data)
          }
        } catch (err) {
          console.error('获取用户信息失败:', err)
        }
      }
    },
    onShow: function() {
      console.log('App Show')
    },
    onHide: function() {
      console.log('App Hide')
    }
  }
</script>

<style>
</style>