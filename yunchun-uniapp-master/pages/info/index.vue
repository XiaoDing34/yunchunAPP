<template>
  <view class="info-container">
    <view class="header">

    </view>

    <view class="avatar-container">
      <button open-type="chooseAvatar" @chooseavatar="onChooseAvatar">
        <image class="avatar" :src="avatarUrl" mode="aspectFit"></image>
      </button>
    </view>

    <view class="form-container">
      <view class="input-item">
        <view class="input-wrapper">
          <text class="label">昵称</text>
          <input type="nickname" v-model="formData.nickname" placeholder="请输入昵称" class="input-field" />
        </view>
      </view>

      <view class="input-item">
        <view class="input-wrapper">
          <text class="label">手机号</text>
          <input v-model="formData.phone" placeholder="请输入手机号" class="input-field" type="number" maxlength="11" />
        </view>
      </view>

      <view class="input-item">
        <view class="input-wrapper verify-wrapper">
          <text class="label">验证码</text>
          <input v-model="formData.verifyCode" placeholder="请输入验证码" class="input-field verify-input" type="number"
            maxlength="6" />
          <button class="verify-btn" :disabled="countdown > 0" @click="sendVerifyCode">
            {{ countdown > 0 ? `${countdown}s后重发` : '获取验证码' }}
          </button>
        </view>
      </view>


      <view class="input-item">
        <view class="input-wrapper" @click="showCompanyPicker">
          <text class="label">所属单位</text>
          <text class="selector-text">{{ formData.organization || '请输入所属单位' }}</text>
          <text class="selector-icon">›</text>
        </view>
      </view>

      <view class="input-item">
        <view class="input-wrapper" @click="showPositionPicker">
          <text class="label">职位</text>
          <text class="selector-text">{{ formData.position || '请选择职位' }}</text>
          <text class="selector-icon">›</text>
        </view>
      </view>
    </view>

    <view class="button-container">
      <button class="confirm-button" @click="handleConfirm" :disabled="!formData.nickname">确定</button>
    </view>
  </view>
</template>

<script>
  import {
    getToken
  } from '../../untils/untils';
  import api from '@/untils/request.js';
  export default {
    data() {
      return {
        formData: {
          nickname: '',
          phone: '',
          verifyCode: '',
          organization: '',
          position: ''
        },
        countdown: 0,
        timer: null,
        avatarUrl: '../../static/我的-资料.png',
        positions: ['请选择职位', '普通员工', '负责人', '其他'],
        organizations: ['请输入所属单位', '天津农学院', '东润发鸡舍', '珍宝岛药业', '其他'],
        flag: ''
      };
    },
    onUnload() {
      if (this.timer) {
        clearInterval(this.timer);
        this.timer = null;
      }
    },
    methods: {
      onChooseAvatar(e) {
        this.avatarUrl = e.detail.avatarUrl;
        console.log(this.avatarUrl);
      },
      async sendVerifyCode() {
        const phone = this.formData.phone;
        if (!phone || phone.length !== 11) {
          uni.showToast({
            title: '请输入正确的手机号',
            icon: 'none'
          });
          return;
        }

        try {
          uni.showLoading({
            title: '发送中...'
          });
          const res = await api.post('/appUser/sendVerifyCode', phone);
          uni.hideLoading();

          if (res.code === 200) {
            uni.showToast({
              title: '验证码已发送',
              icon: 'success'
            });
            // 开始倒计时
            this.countdown = 60;
            this.timer = setInterval(() => {
              this.countdown--;
              if (this.countdown <= 0) {
                clearInterval(this.timer);
                this.timer = null;
              }
            }, 1000);
          } else {
            uni.showToast({
              title: res.msg || '发送失败',
              icon: 'none'
            });
          }
        } catch (err) {
          uni.hideLoading();
          uni.showToast({
            title: '发送失败',
            icon: 'none'
          });
        }
      },
      showPositionPicker() {
        uni.showActionSheet({
          itemList: this.positions,
          success: (res) => {
            if (res.tapIndex > 0) {
              this.formData.position = this.positions[res.tapIndex];
            }
          }
        });
      },
      showCompanyPicker() {
        uni.showActionSheet({
          itemList: this.organizations,
          success: (res) => {
            if (res.tapIndex > 0) {
              this.formData.organization = this.organizations[res.tapIndex];
            }
          }
        });
      },
      async handleConfirm() {
        if (!this.formData.nickname) {
          uni.showToast({
            title: '请输入昵称',
            icon: 'none'
          });
          return;
        }
        if (!this.formData.phone) {
          uni.showToast({
            title: '请输入手机号',
            icon: 'none'
          });
          return;
        }
        if (!this.formData.organization) {
          uni.showToast({
            title: '请输入所属单位',
            icon: 'none'
          });
          return;
        }
        if (!this.formData.position) {
          uni.showToast({
            title: '请输入职位',
            icon: 'none'
          });
          return;
        }

        uni.showLoading({
          title: '保存中...'
        });

        setTimeout(() => {
          uni.hideLoading();

          uni.uploadFile({
            url: 'http://localhost:9095/appUser/avatar',
            filePath: this.avatarUrl,
            name: 'file',
            header: {
              'Authorization': getToken()
            },
            success: (res) => {
              console.log('微信头像保存成功', res);
            },
            fail: (res) => {
              console.log('微信头像保存失败')
            }
          });
        }, 1000);
        try {
          const res = await api.put('/appUser/nickname', this.formData.nickname)
        } catch (err) {
          console.log(err)
        };
        try {
          const res = await api.put('/appUser/updateLoginInfo', {
            phone: this.formData.phone,
            code: this.formData.verifyCode,
            organization: this.formData.organization,
            position: this.formData.position
          })
          if (!res.code == 200) {
            uni.showToast({
              title: '验证码错误或过期，请重新获取！',
              icon: 'fail'
            })
          } else {
            this.flag = 3
          }
        } catch (err) {
          console.log(err)
        };
        if (this.flag == 3) {
          uni.showToast({
            title: '保存成功',
            icon: 'success'
          });
          uni.switchTab({
            url: '/pages/index/index'
          })
        } else {
          uni.showToast({
            title: '保存失败',
            icon: 'fail'
          })
        }

      }
    }
  };
</script>

<style scoped>
  .info-container {
    display: flex;
    flex-direction: column;
    min-height: 100vh;
    background-color: #fff;
  }

  .header {
    height: 80rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    border-bottom: 1rpx solid #e5e5e5;
  }

  .avatar-container {
    display: flex;
    justify-content: center;
    align-items: center;
    margin: 80rpx 0;
  }

  .avatar {
    width: 160rpx;
    height: 160rpx;
    border-radius: 80rpx;
  }

  .form-container {
    padding: 0 60rpx;
  }

  .input-item {
    margin-bottom: 32rpx;
  }

  .input-wrapper {
    background: #ffffff;
    border-radius: 16rpx;
    padding: 24rpx 28rpx;
    display: flex;
    align-items: center;
    gap: 20rpx;
    box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);
  }

  .label {
    font-size: 28rpx;
    color: #666;
    width: 140rpx;
    flex-shrink: 0;
  }

  .input-field {
    flex: 1;
    font-size: 28rpx;
    color: #333;
  }

  .selector-text {
    flex: 1;
    font-size: 28rpx;
    color: #333;
    text-align: left;
  }

  .selector-icon {
    font-size: 32rpx;
    color: #999;
  }

  .verify-wrapper {
    padding: 16rpx 20rpx 16rpx 28rpx;
  }

  .verify-input {
    flex: 1;
    min-width: 160rpx;
  }

  .verify-btn {
    width: 180rpx;
    height: 64rpx;
    line-height: 64rpx;
    font-size: 24rpx;
    color: #fff;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border-radius: 32rpx;
    border: none;
    padding: 0;
    margin: 0;
    flex-shrink: 0;
  }

  .verify-btn:disabled {
    background: #c0c4cc;
    color: #fff;
  }

  .button-container {
    padding: 60rpx;
    margin-top: 100rpx;
  }

  .confirm-button {
    width: 100%;
    height: 80rpx;
    background-color: #1890ff;
    color: #fff;
    font-size: 32rpx;
    font-weight: bold;
    border-radius: 40rpx;
    border: none;
  }

  .confirm-button:disabled {
    background-color: #ccc;
  }
</style>