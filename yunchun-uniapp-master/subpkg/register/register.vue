<template>
  <view class="register-container">
    <view class="register-header">
    </view>
    <view class="header-content">
      <text class="register-title">信息注册</text>
      <text class="register-subtitle">创建您的账户</text>
    </view>

    <view class="form-container">
      <view class="input-item">
        <view class="input-wrapper">
          <view class="input-icon">
            <image class="icon" src="../../static/icons/user.svg"></image>
          </view>
          <input class="input" type="text" placeholder="请输入用户名" v-model="formData.username"
            placeholder-class="input-placeholder" />
        </view>
      </view>

      <view class="input-item">
        <view class="input-wrapper">
          <view class="input-icon">
            <image class="icon" src="../../static/icons/phone.svg"></image>
          </view>
          <input class="input" type="number" placeholder="请输入手机号码" v-model="formData.phone"
            placeholder-class="input-placeholder" maxlength="11" />
        </view>
      </view>

      <view class="input-item">
        <view class="input-wrapper verify-wrapper">
          <view class="input-icon">
            <image class="icon" src="../../static/icons/验证码.png"></image>
          </view>
          <input class="input verify-input" type="number" placeholder="请输入验证码" v-model="formData.code"
            placeholder-class="input-placeholder" maxlength="6" />
          <button class="verify-btn" :disabled="countdown > 0" @click="sendVerifyCode">
            {{ countdown > 0 ? `${countdown}s后重发` : '获取验证码' }}
          </button>
        </view>
      </view>

      <view class="input-item">
        <view class="input-wrapper" @click="showCompanyPicker" style="cursor: pointer; z-index: 10;">
          <view class="input-icon">
            <image class="icon" src="../../static/icons/company.svg"></image>
          </view>
          <text class="selector-text">{{ formData.organization || '请输入所属单位' }}</text>
          <uni-icons class="selector-icon" type="arrowright" size="20" color="#c4b5fd"></uni-icons>
        </view>
      </view>

      <view class="input-item">
        <view class="input-wrapper" @click="showPositionPicker" style="cursor: pointer; z-index: 10;">
          <view class="input-icon">
            <image class="icon" src="../../static/icons/position.svg"></image>
          </view>
          <text class="selector-text">{{ formData.position || '请选择职位' }}</text>
          <uni-icons class="selector-icon" type="arrowright" size="20" color="#c4b5fd"></uni-icons>
        </view>
      </view>

      <view class="input-item">
        <view class="input-wrapper">
          <view class="input-icon">
            <image class="icon" src="../../static/icons/password.svg"></image>
          </view>
          <input v-if="!showPassword" class="input" type="password" placeholder="请输入密码" v-model="formData.password"
            placeholder-class="input-placeholder" />
          <input v-else class="input" type="text" placeholder="请输入密码" v-model="formData.password"
            placeholder-class="input-placeholder" />
          <view class="password-toggle" @tap="togglePassword">
            <text class="password-icon">👁️</text>
          </view>
        </view>
      </view>

      <view class="input-item">
        <view class="input-wrapper">
          <view class="input-icon">
            <image class="icon" src="../../static/icons/confirm-password.svg"></image>
          </view>
          <input v-if="!showConfirmPassword" class="input" type="password" placeholder="请再次输入密码"
            v-model="formData.confirmPassword" placeholder-class="input-placeholder" />
          <input v-else class="input" type="text" placeholder="请再次输入密码" v-model="formData.confirmPassword"
            placeholder-class="input-placeholder" />
          <view class="password-toggle" @tap="toggleConfirmPassword">
            <text class="password-icon">👁️</text>
          </view>
        </view>
      </view>
    </view>

    <button class="register-button" @click="register">注册</button>
  </view>
</template>

<script>
  import api from '@/untils/request.js'
  export default {
    data() {
      return {
        formData: {
          username: '',
          phone: '',
          code: '',
          organization: '',
          position: '',
          password: '',
          confirmPassword: ''
        },
        positions: ['请选择职位', '普通员工', '负责人', '其他'],
        organizations: ['请输入所属单位', '天津农学院', '东润发鸡舍', '珍宝岛药业', '其他'],
        showPassword: false,
        showConfirmPassword: false,
        countdown: 0,
        timer: null
      }
    },
    onUnload() {
      if (this.timer) {
        clearInterval(this.timer);
        this.timer = null;
      }
    },
    methods: {
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
      togglePassword() {
        this.showPassword = !this.showPassword;
      },
      toggleConfirmPassword() {
        this.showConfirmPassword = !this.showConfirmPassword;
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
          const res = await api.post('/appUser/sendVerifyCode', this.formData.phone)
          uni.hideLoading();
          if (res.data && res.code === 200) {
            uni.showToast({
              title: '验证码已发送',
              icon: 'success'
            });
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
              title: res.data?.msg || '发送失败',
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
      async register() {
        if (!this.formData.username || !this.formData.phone || !this.formData.organization || !this.formData
          .position ||
          !
          this
          .formData.password || !this.formData.confirmPassword) {
          uni.showToast({
            title: '请填写所有必填字段',
            icon: 'none'
          });
          return;
        }

        let phone = this.formData.phone.replace(/\D/g, '');

        if (phone.startsWith('86') && phone.length > 11) {
          phone = phone.substring(2);
        }

        if (!/^1[3-9]\d{9}$/.test(phone)) {
          uni.showToast({
            title: '请输入正确的11位手机号码',
            icon: 'none'
          });
          return;
        }

        if (this.formData.password !== this.formData.confirmPassword) {
          uni.showToast({
            title: '两次密码输入不一致',
            icon: 'none'
          });
          return;
        }

        try {
          //await api.user.register(this.formData);
          const res = await api.post("/appUser/saveAppUser", this.formData)
          if (res.data && res.code === 200) {
            uni.showToast({
              title: '注册成功',
              icon: 'success'
            });
          } else if (res.data && res.code === 400) {
            uni.showToast({
              title: '验证码错误或过期，请重新获取！',
              icon: 'error'
            });
          } else {
            uni.showToast({
              title: '注册失败',
              icon: 'error'
            });
          }
          setTimeout(() => {
            uni.navigateBack({
              delta: 1
            });
          }, 1500);
        } catch (err) {
          console.error('注册请求失败：', err);
          uni.showToast({
            title: '注册失败',
            icon: 'none'
          });
          setTimeout(() => {
            uni.navigateBack({
              delta: 1
            });
          }, 1500);
        }
      }
    }
  }
</script>

<style scoped>
  .register-container {
    display: flex;
    flex-direction: column;
    padding: 40rpx 40rpx 0 40rpx;
    background: linear-gradient(180deg, #fafbfc 0%, #f5f7fa 100%);
    min-height: 100vh;
  }

  .register-header {
    display: flex;
    align-items: center;
    margin-bottom: 40rpx;
  }

  .back-button {
    width: 80rpx;
    height: 80rpx;
    background: #ffffff;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.06);
  }

  .header-content {
    margin-bottom: 60rpx;
  }

  .register-title {
    font-size: 48rpx;
    font-weight: 700;
    color: #2c3e50;
    letter-spacing: 1rpx;
    margin-bottom: 12rpx;
    display: block;
  }

  .register-subtitle {
    font-size: 28rpx;
    color: #6b7280;
    font-weight: 400;
    letter-spacing: 0.3rpx;
    display: block;
  }

  .form-container {
    width: 100%;
    margin-bottom: 40rpx;
  }

  .input-item {
    margin-bottom: 32rpx;
  }

  .input-wrapper {
    background: #ffffff;
    border-radius: 20rpx;
    padding: 24rpx 28rpx;
    display: flex;
    align-items: center;
    gap: 20rpx;
    box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);
    transition: all 0.3s ease;
  }

  .input-wrapper:active {
    box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.08);
  }

  .input-icon {
    width: 56rpx;
    height: 56rpx;
    background: linear-gradient(135deg, #f8f9fa 0%, #ffffff 100%);
    border-radius: 12rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
  }

  .icon {
    width: 28rpx;
    height: 28rpx;
  }

  .input {
    flex: 1;
    font-size: 30rpx;
    color: #2c3e50;
    font-weight: 500;
    border: none;
    background: transparent;
    padding: 0;
  }

  .input-placeholder {
    color: #9ca3af;
    font-weight: 400;
  }

  .selector-text {
    flex: 1;
    font-size: 30rpx;
    color: #2c3e50;
    font-weight: 500;
  }

  .selector-icon {
    flex-shrink: 0;
  }

  .password-toggle {
    cursor: pointer;
    flex-shrink: 0;
    width: 56rpx;
    height: 56rpx;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .password-icon {
    font-size: 28rpx;
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
    background: #1890ff;
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

  .register-button {
    width: 100%;
    height: 100rpx;
    font-size: 32rpx;
    font-weight: 600;
    border-radius: 20rpx;
    background: #1890ff;
    color: #ffffff;
    border: none;
    margin-top: 40rpx;
    box-shadow: 0 8rpx 24rpx rgba(24, 144, 255, 0.3);
    letter-spacing: 1rpx;
  }

  .register-button:active {
    opacity: 0.9;
  }
</style>