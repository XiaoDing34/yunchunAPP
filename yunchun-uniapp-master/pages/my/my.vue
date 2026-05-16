<template>
  <view class="my-container">
    <view class="user-info">
      <image class="avatar" :src="getAvatarUrl()" mode="aspectFit"></image>
      <view class="user-detail">
        <text class="user-name">{{userInfo.nickname||userInfo.username}}</text>
        <text class="user-role">{{userInfo.position}}</text>
      </view>
      <text class="arrow">›</text>
    </view>

    <view class="menu-list">
      <view class="menu-item" @click="handleProfile">
        <text class="menu-icon">👤</text>
        <text class="menu-text">个人资料</text>
        <text class="menu-arrow">›</text>
      </view>
      <view class="menu-item" @click="handleSettings">
        <text class="menu-icon">⚙️</text>
        <text class="menu-text">设置</text>
        <text class="menu-arrow">›</text>
      </view>
      <view class="menu-item" @click="handleHelp">
        <text class="menu-icon">❓</text>
        <text class="menu-text">帮助中心</text>
        <text class="menu-arrow">›</text>
      </view>
      <view class="menu-item" @click="handleAbout">
        <text class="menu-icon">ℹ️</text>
        <text class="menu-text">关于我们</text>
        <text class="menu-arrow">›</text>
      </view>
    </view>

    <view class="logout-section">
      <button class="logout-button" @click="handleLogout">退出登录</button>
    </view>
  </view>
</template>

<script>
  import userCache from '@/untils/userCache.js';
  import api from "@/untils/request.js"
  export default {
    data() {
      return {
        userInfo: {}
      };
    },
    async mounted() {
      // 先显示缓存
      const cached = uni.getStorageSync('userInfo');
      if (cached) this.userInfo = cached;
      // 后台刷新
      const latest = await userCache.getUserInfo(true);
      this.userInfo = latest;
    },
    methods: {
      getAvatarUrl() {
        const avatar = this.userInfo.avatar;
        if (!avatar) {
          return '../../static/我的-资料.png';
        }
        return 'http://localhost:9095' + avatar;
      },
      handleProfile() {
        uni.navigateTo({
          url: '/pages/profile/profile'
        })
      },
      handleSettings() {
        uni.navigateTo({
          url: '/pages/settings/settings'
        });
      },
      handleHelp() {
        uni.navigateTo({
          url: '/pages/help/help'
        });
      },
      handleAbout() {
        const url = 'http://www.xcglc.com/';
        uni.setClipboardData({
          data: url,
          success: () => {
            uni.showToast({
              title: '链接已复制，可粘贴到浏览器访问公司官网：http://www.xcglc.com/',
              icon: 'none'
            });
          }
        });
      },
      handleLogout() {
        uni.showModal({
          title: '确认退出',
          content: '确定要退出登录吗？',
          success: async (res) => {
            if (res.confirm) {
              await api.get('/appUser/logout')
              uni.reLaunch({
                url: '/pages/login/login'
              });
              uni.removeStorageSync('yunchun');
            }
          }
        });
      }
    }
  };
</script>

<style scoped>
  .my-container {
    min-height: 100vh;
    background-color: #f5f5f5;
    padding-bottom: 120rpx;
  }

  .user-info {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    padding: 60rpx 30rpx;
    display: flex;
    align-items: center;
  }

  .avatar {
    width: 120rpx;
    height: 120rpx;
    border-radius: 60rpx;
    border: 4rpx solid rgba(255, 255, 255, 0.3);
    margin-right: 24rpx;
  }

  .user-detail {
    flex: 1;
  }

  .user-name {
    font-size: 32rpx;
    font-weight: bold;
    color: #fff;
    display: block;
    margin-bottom: 8rpx;
  }

  .user-role {
    font-size: 24rpx;
    color: rgba(255, 255, 255, 0.8);
  }

  .arrow {
    font-size: 40rpx;
    color: rgba(255, 255, 255, 0.6);
  }

  .menu-list {
    margin: 20rpx;
    background-color: #fff;
    border-radius: 16rpx;
    overflow: hidden;
    box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);
  }

  .menu-item {
    display: flex;
    align-items: center;
    padding: 28rpx 30rpx;
    border-bottom: 1rpx solid #f0f0f0;
  }

  .menu-item:last-child {
    border-bottom: none;
  }

  .menu-icon {
    font-size: 36rpx;
    margin-right: 20rpx;
  }

  .menu-text {
    flex: 1;
    font-size: 28rpx;
    color: #333;
  }

  .menu-arrow {
    font-size: 32rpx;
    color: #999;
  }

  .logout-section {
    padding: 30rpx;
  }

  .logout-button {
    width: 100%;
    height: 80rpx;
    background-color: #fff;
    color: #ff4d4f;
    font-size: 28rpx;
    border: 1rpx solid #ff4d4f;
    border-radius: 40rpx;
  }
</style>