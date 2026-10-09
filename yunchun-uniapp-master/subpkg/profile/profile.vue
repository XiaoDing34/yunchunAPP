<template>
  <view class="profile-page">
    <view class="header">

    </view>
    <view class="content">
      <view class="info-item">
        <text class="label">头像</text>
        <image class="avatar" :src="userInfo.avatar || 'https://via.placeholder.com/80x80'" mode="aspectFill"></image>
      </view>
      <view class="info-item">
        <text class="label">昵称</text>
        <text class="value">{{ userInfo.username || userInfo.nickname || '未设置' }}</text>
      </view>
      <view class="info-item">
        <text class="label">绑定手机</text>
        <text class="value">{{ formatPhone(userInfo.phone) || '未绑定' }}</text>
      </view>
      <view class="info-item">
        <text class="label">所属单位</text>
        <text class="value">{{ userInfo.organization || '未填写' }}</text>
      </view>
      <view class="info-item">
        <text class="label">职位</text>
        <text class="value">{{ userInfo.position || '未填写' }}</text>
      </view>
      <view class="info-item">
        <text class="label">注册时间</text>
        <text class="value">{{ formatDate(userInfo.createTime) || '-' }}</text>
      </view>
    </view>
  </view>
</template>

<script>
  export default {
    data() {
      return {
        userInfo: {}
      };
    },
    onLoad() {
      const userInfoStr = uni.getStorageSync('userInfo');
      this.userInfo = userInfoStr ? (typeof userInfoStr === 'object' ? userInfoStr : JSON.parse(userInfoStr)) : {};
    },
    methods: {
      formatDate(dateStr) {
        if (!dateStr) return '';
        try {
          const date = new Date(dateStr);
          const year = date.getFullYear();
          const month = String(date.getMonth() + 1).padStart(2, '0');
          const day = String(date.getDate()).padStart(2, '0');
          const hours = String(date.getHours()).padStart(2, '0');
          const minutes = String(date.getMinutes()).padStart(2, '0');
          return `${year}-${month}-${day} ${hours}:${minutes}`;
        } catch (e) {
          return dateStr;
        }
      },
      formatPhone(phone) {
        if (!phone) return '';
        return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2');
      }
    }
  };
</script>

<style>
  .profile-page {
    min-height: 100vh;
    background: #f5f5f5;
  }

  .header {
    background: #fff;
    padding: 30rpx;
    border-bottom: 1rpx solid #eee;
  }

  .title {
    font-size: 36rpx;
    font-weight: bold;
    color: #333;
  }

  .content {
    margin: 20rpx;
    background: #fff;
    border-radius: 16rpx;
    padding: 20rpx;
  }

  .info-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 24rpx 0;
    border-bottom: 1rpx solid #f0f0f0;
  }

  .info-item:last-child {
    border-bottom: none;
  }

  .label {
    font-size: 28rpx;
    color: #666;
  }

  .value {
    font-size: 28rpx;
    color: #333;
  }

  .avatar {
    width: 80rpx;
    height: 80rpx;
    border-radius: 50%;
  }
</style>