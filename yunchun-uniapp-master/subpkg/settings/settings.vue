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
          <text class="value">{{ userInfo.nickname||userInfo.username}}</text>
          <text class="arrow">›</text>
        </view>
      </view>
      <view class="setting-item">
        <text class="label">职位</text>
        <text class="value">{{ userInfo.position || '未设置' }}</text>
      </view>
      <view class="setting-item" @click="handlePasswordClick">
        <text v-if="hasPassword" class="label">修改密码</text>
        <text v-else class="label">设置密码</text>
        <!--        <view class="right-content">
          <text class="arrow">›</text>
        </view> -->
      </view>
    </view>
  </view>
</template>

<script>
  import api from '@/untils/request.js';
  import {
    mapState
  } from 'vuex';

  export default {
    data() {
      return {
        hasPassword: false
      };
    },
    computed: {
      ...mapState(['userInfo'])
    },
    onLoad() {
      console.log('我在设置页面获取到的用户信息：', this.userInfo),
        this.checkHasPassword();
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
          uni.showLoading({
            title: '上传中...'
          });

          uni.uploadFile({
            url: 'http://localhost:9095/appUser/upload/avatar',
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
                uni.showToast({
                  title: '头像修改成功',
                  icon: 'success'
                });
              } else {
                uni.showToast({
                  title: data.msg || '上传失败',
                  icon: 'none'
                });
              }
            },
            fail: (err) => {
              uni.showToast({
                title: '上传失败',
                icon: 'none'
              });
            },
            complete: () => {
              uni.hideLoading();
            }
          });
        } catch (error) {
          uni.hideLoading();
          uni.showToast({
            title: '上传失败',
            icon: 'none'
          });
        }
      },
      handleNicknameClick() {
        uni.showModal({
          title: '修改昵称',
          editable: true,
          placeholderText: '请输入新昵称',
          content: this.userInfo.nickname || this.userInfo.username,
          success: async (res) => {
            if (res.confirm && res.content && res.content.trim()) {
              await this.updateNickname(res.content.trim());
            }
          }
        });
      },
      async updateNickname(nickname) {
        //
        try {
          const res = await api.put('/appUser/nickname?nickname=' + encodeURIComponent(nickname));
          if (res.code === 200) {
            this.userInfo.nickname = nickname;
            uni.setStorageSync('userInfo', this.userInfo);
            uni.showToast({
              title: '昵称修改成功',
              icon: 'success'
            });
          } else {
            uni.showToast({
              title: res.msg || '修改失败',
              icon: 'none'
            });
          }
        } catch (error) {
          uni.showToast({
            title: '修改失败',
            icon: 'none'
          });
        }
      },
      async checkHasPassword() {
        const res = await api.get('/appUser/hasPassword')
        console.log("接口返回的res：", res)
        if (res.code == 200) {
          if (res.data == 1) {
            this.hasPassword = true
            console.log(this.hasPassword)
          }
        }
      },
      handlePasswordClick() {
        if (this.hasPassword) {
          this.changePasswordFlow();
        } else {
          this.setPasswordFlow();
        }
      },
      changePasswordFlow() {
        uni.showModal({
          title: '修改密码',
          editable: true,
          placeholderText: '请输入原密码',
          success: (res1) => {
            if (!res1.confirm) return;
            const oldPassword = (res1.content || '').trim();
            if (!oldPassword) {
              uni.showToast({
                title: '原密码不能为空',
                icon: 'none'
              });
              return;
            }
            uni.showModal({
              title: '设置新密码',
              editable: true,
              placeholderText: '请输入新密码',
              success: (res2) => {
                if (!res2.confirm) return;
                const newPassword = (res2.content || '').trim();
                if (!newPassword) {
                  uni.showToast({
                    title: '新密码不能为空',
                    icon: 'none'
                  });
                  return;
                }
                uni.showModal({
                  title: '确认新密码',
                  editable: true,
                  placeholderText: '请再次输入新密码',
                  success: (res3) => {
                    if (!res3.confirm) return;
                    const confirmPassword = (res3.content || '').trim();
                    if (newPassword !== confirmPassword) {
                      uni.showToast({
                        title: '两次输入的新密码不一致',
                        icon: 'none'
                      });
                      return;
                    }
                    this.changePassword(oldPassword, newPassword, confirmPassword);
                  }
                });
              }
            });
          }
        });
      },
      setPasswordFlow() {
        uni.showModal({
          title: '设置密码',
          editable: true,
          placeholderText: '请输入新密码',
          success: (res1) => {
            if (!res1.confirm) return;
            const newPassword = (res1.content || '').trim();
            if (!newPassword) {
              uni.showToast({
                title: '新密码不能为空',
                icon: 'none'
              });
              return;
            }
            uni.showModal({
              title: '确认新密码',
              editable: true,
              placeholderText: '请再次输入新密码',
              success: (res2) => {
                if (!res2.confirm) return;
                const confirmPassword = (res2.content || '').trim();
                if (newPassword !== confirmPassword) {
                  uni.showToast({
                    title: '两次输入的新密码不一致',
                    icon: 'none'
                  });
                  return;
                }
                this.createPassword(newPassword, confirmPassword);
              }
            });
          }
        });
      },
      async createPassword(newPassword, confirmPassword) {
        try {
          const res = await api.post('/appUser/setPassword', {
            newPassword,
            confirmPassword
          });
          if (res.code === 200) {
            uni.showToast({
              title: res.message || '密码设置成功',
              icon: 'success'
            });
            this.hasPassword = true;
          }
        } catch (error) {
          uni.showToast({
            title: (error.data && error.data.message) || '密码设置失败',
            icon: 'none'
          });
        }
      },
      async changePassword(oldPassword, newPassword, confirmPassword) {
        try {
          const res = await api.put('/appUser/password', {
            oldPassword,
            newPassword,
            confirmPassword
          });
          if (res.code === 200) {
            uni.showToast({
              title: res.message || '密码修改成功',
              icon: 'success'
            });
          }
        } catch (error) {
          uni.showToast({
            title: (error.data && error.data.message) || '密码修改失败',
            icon: 'none'
          });
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