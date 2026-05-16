<template>
  <view class="settings-page">
    <view class="settings-content">
      <view class="setting-item" @click="handleAvatarClick">
        <text class="label">头像</text>
        <view class="right-content">
          <image class="avatar" :src="getAvatarUrl()" mode="aspectFill"></image>
          <text class="arrow">›</text>
        </view>
      </view>
      <view class="setting-item" @click="handleNicknameClick">
        <text class="label">昵称</text>
        <view class="right-content">
          <text class="value">{{ userInfo.nickname || '未设置' }}</text>
          <text class="arrow">›</text>
        </view>
      </view>
      <view class="setting-item">
        <text class="label">邮箱</text>
        <text class="value">{{ userInfo.email || '未设置' }}</text>
      </view>
    </view>
  </view>
</template>

<script>
import api from '@/untils/request.js';

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
    getAvatarUrl() {
      const avatar = this.userInfo.avatar;
      if (!avatar) {
        return '../../static/我的-资料.png';
      }
      return 'http://localhost:9095' + avatar;
    },
    handleAvatarClick() {
      uni.chooseImage({
        count: 1,
        sizeType: ['compressed'],
        sourceType: ['album', 'camera'],
        success: (res) => {
          const tempFilePath = res.tempFilePaths[0];
          this.uploadAvatar(tempFilePath);
        }
      });
    },
    async uploadAvatar(filePath) {
      try {
        uni.showLoading({ title: '上传中...' });
        
        uni.uploadFile({
          url: 'http://localhost:9095/appUser/avatar',
          filePath: filePath,
          name: 'file',
          header: {
            'Authorization': uni.getStorageSync('yunchun')
          },
          success: (uploadRes) => {
            const data = JSON.parse(uploadRes.data);
            if (data.code === 200) {
              this.userInfo.avatar = data.data;
              uni.setStorageSync('userInfo', this.userInfo);
              uni.showToast({ title: '头像修改成功', icon: 'success' });
            } else {
              uni.showToast({ title: data.msg || '上传失败', icon: 'none' });
            }
          },
          fail: (err) => {
            uni.showToast({ title: '上传失败', icon: 'none' });
          },
          complete: () => {
            uni.hideLoading();
          }
        });
      } catch (error) {
        uni.hideLoading();
        uni.showToast({ title: '上传失败', icon: 'none' });
      }
    },
    handleNicknameClick() {
      uni.showModal({
        title: '修改昵称',
        editable: true,
        placeholderText: '请输入新昵称',
        content: this.userInfo.nickname || '',
        success: async (res) => {
          if (res.confirm && res.content && res.content.trim()) {
            await this.updateNickname(res.content.trim());
          }
        }
      });
    },
    async updateNickname(nickname) {
      try {
        const res = await api.put('/appUser/nickname', { nickname });
        if (res.code === 200) {
          this.userInfo.nickname = nickname;
          uni.setStorageSync('userInfo', this.userInfo);
          uni.showToast({ title: '昵称修改成功', icon: 'success' });
        } else {
          uni.showToast({ title: res.msg || '修改失败', icon: 'none' });
        }
      } catch (error) {
        uni.showToast({ title: '修改失败', icon: 'none' });
      }
    }
  }
};
</script>

<style scoped>
.settings-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.settings-content {
  margin: 20rpx;
  background: #fff;
  border-radius: 16rpx;
  overflow: hidden;
}

.setting-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 30rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.setting-item:last-child {
  border-bottom: none;
}

.label {
  font-size: 28rpx;
  color: #333;
  font-weight: 500;
}

.right-content {
  display: flex;
  align-items: center;
}

.value {
  font-size: 28rpx;
  color: #666;
  margin-right: 10rpx;
}

.avatar {
  width: 80rpx;
  height: 80rpx;
  border-radius: 50%;
  margin-right: 10rpx;
}

.arrow {
  font-size: 40rpx;
  color: #999;
}
</style>