<template>
  <view class="login-container">
    <view class="logo-container">
      <image class="logo" src="../../static/yunchun.jpg" mode="aspectFit"></image>
    </view>
    <view class="login-form">
      <view class="login-header">
        <text class="login-title" @click="navigateToRegister">用户注册</text>
      </view>
      <!-- 手机号输入 -->
      <view class="input-group">
        <image class="input-icon" src="../../static/账号.png" mode="aspectFit"></image>
        <input type="number" v-model="phone" placeholder="请输入用户名" class="input-field" />
        <text class="input-arrow">></text>
      </view>

      <!-- 密码输入 -->
      <view class="input-group">
        <image class="input-icon" src="../../static/密码.png" mode="aspectFit"></image>
        <input :type="showPassword ? 'text' : 'password'" v-model="password" placeholder="请输入密码" class="input-field" />
        <view class="input-eye" @click="togglePasswordVisibility">
          <image :src="showPassword ? '../../static/显示.png' : '../../static/隐藏.png'" mode="aspectFit"
            style="width: 28rpx; height: 28rpx;"></image>
        </view>
      </view>

      <!-- 忘记密码和记住密码 -->
      <view class="form-options">
        <text class="forgot-password">忘记密码</text>
        <view class="remember-password">
          <text class="checkbox" :class="{ 'checked': rememberPassword }" @click="toggleRememberPassword">
            {{ rememberPassword ? '✓' : '' }}
          </text>
          <text class="remember-text">记住密码</text>
        </view>
      </view>

      <!-- 登录按钮 -->
      <button class="login-button" @click="handleLogin">登录</button>

      <!-- 微信一键登录 -->
      <button v-if="agreeProtocol" class="wechat-login-button" type="primary" @click="handleWechatLogin">微信一键登录</button>
      <button v-else class="wechat-login-button" type="default">微信一键登录</button>

      <!-- 用户协议 -->
      <view class="agreement">
        <text class="checkbox" :class="{ 'checked': agreeProtocol }" @click="toggleAgreeProtocol">
          {{ agreeProtocol ? '✓' : '' }}
        </text>
        <text class="agreement-text">我已阅读并同意《用户协议》</text>
      </view>
    </view>
  </view>
</template>

<script>
  import api from "@/untils/request.js"
  import {
    getToken,
    setToken
  } from "../../untils/untils";

  export default {
    data() {
      return {
        phone: '',
        password: '',
        showPassword: false,
        rememberPassword: false,
        agreeProtocol: false
      };
    },
    methods: {
      togglePasswordVisibility() {
        const currentPassword = this.password;
        this.showPassword = !this.showPassword;
        this.$nextTick(() => {
          this.password = currentPassword;
        });
        console.log(this.showPassword);
      },
      toggleRememberPassword() {
        this.rememberPassword = !this.rememberPassword;
      },
      toggleAgreeProtocol() {
        this.agreeProtocol = !this.agreeProtocol;
      },
      async handleLogin() {
        if (!this.phone) {
          uni.showToast({
            title: '请输入账号',
            icon: 'none'
          });
          return;
        }
        if (!this.password) {
          uni.showToast({
            title: '请输入密码',
            icon: 'none'
          });
          return;
        }
        if (!this.agreeProtocol) {
          uni.showToast({
            title: '请阅读并同意用户协议',
            icon: 'none'
          });
          return;
        }
        //uni.removeStorageSync('yunchun');
        try {
          let res = await api.post('/appUser/login', {
            username: this.phone,
            password: this.password
          });
          uni.showToast({
            title: '登录成功',
            icon: 'success'
          });
          console.log('账号登录返回的数据：', res);
          setToken(res.data);
          uni.switchTab({
            url: '/pages/index/index'
          });
        } catch (err) {
          console.log(err);
          uni.showToast({
            title: err.error || '登录失败',
            icon: "error"
          })
        }
      },

      getPhoneNumber({
        detail
      }) {
        console.log(detail);
      },

      handleWechatLogin() {
        if (!this.agreeProtocol) {
          uni.showToast({
            title: '请阅读并同意用户协议',
            icon: 'none'
          });
          return;
        }
        // if (getToken()) {
        //   console.log('获取到的token:', getToken())
        //   uni.getStorageInfo({
        //     success: (res) => {
        //       console.log(res.keys),
        //         console.log(res.currentSize),
        //         console.log(res.limitSize)
        //     }
        //   })
        //   uni.switchTab({
        //     url: '/pages/index/index'
        //   });
        //   return;
        // }
        uni.login({
          provider: 'weixin',
          success: async (loginRes) => {
            if (loginRes.errMsg === 'login:ok') {
              const code = loginRes.code;
              console.log('获取到的code:', code);

              try {
                const response = await api.post('/appUser/wechatLogin', {
                  code: code
                }, {
                  'Content-Type': 'application/x-www-form-urlencoded'
                });
                console.log('封装返回的数据：', response);
                if (response.code == 200) {
                  uni.showToast({
                    title: '登录成功',
                    icon: 'success'
                  });
                  setToken(response.data.token);
                  uni.setStorageSync("userInfo")
                  if (response.data.needCompleteProfile) {
                    uni.navigateTo({
                      url: '/pages/info/index'
                    })
                  } else {
                    uni.switchTab({
                      url: '/pages/index/index'
                    })
                  }
                } else {
                  uni.showToast({
                    title: '登录失败',
                    icon: 'error'
                  })
                }
              } catch (err) {
                console.error('请求失败', err);
                uni.showToast({
                  title: err.data.message,
                  icon: "error"
                })
              }
            } else {
              console.error('登录失败', loginRes.errMsg);
              uni.showToast({
                title: '登录失败',
                icon: 'error'
              });
            }
          },
          fail: (err) => {
            console.error('调用 uni.login 失败', err);
            uni.showToast({
              title: '登录失败',
              icon: 'error'
            });
          }
        });
      },

      navigateToRegister() {
        uni.navigateTo({
          url: '/pages/register/register',
        });
      }
    }
  };
</script>

<style scoped>
  .login-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 40rpx 60rpx;
  }

  .logo-container {
    width: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
    margin-top: 120rpx;
    margin-bottom: 80rpx;
  }

  .logo {
    width: 240rpx;
    height: 240rpx;
  }

  .login-header {
    text-align: right;
    margin-bottom: 20rpx;
  }

  .login-title {
    font-size: 28rpx;
    font-weight: bold;
    color: #1890ff;
    cursor: pointer;
  }

  .login-form {
    width: 100%;
    background-color: #fff;
    border-radius: 16rpx;
    padding: 30rpx;
    box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
  }

  .input-group {
    display: flex;
    align-items: center;
    border-bottom: 1rpx solid #e5e5e5;
    padding: 20rpx 0;
  }

  .input-icon {
    width: 28rpx;
    height: 28rpx;
    margin-right: 20rpx;
  }

  .input-field {
    flex: 1;
    font-size: 28rpx;
    color: #333;
  }

  .input-arrow {
    font-size: 28rpx;
    color: #999;
  }

  .input-eye {
    width: 28rpx;
    height: 28rpx;
    cursor: pointer;
  }

  .form-options {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-top: 30rpx;
    margin-bottom: 40rpx;
  }

  .forgot-password {
    font-size: 24rpx;
    color: #1890ff;
  }

  .remember-password {
    display: flex;
    align-items: center;
  }

  .checkbox {
    width: 28rpx;
    height: 28rpx;
    border: 2rpx solid #d9d9d9;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 20rpx;
    color: #fff;
    margin-right: 10rpx;
    cursor: pointer;
  }

  .checkbox.checked {
    background-color: #1890ff;
    border-color: #1890ff;
  }

  .remember-text {
    font-size: 24rpx;
    color: #666;
  }

  .login-button {
    width: 100%;
    height: 80rpx;
    background-color: #1890ff;
    color: #fff;
    font-size: 32rpx;
    font-weight: bold;
    border-radius: 40rpx;
    margin-bottom: 30rpx;
    border: none;
  }

  .wechat-login-button {
    width: 100%;
    height: 80rpx;
    font-size: 32rpx;
    font-weight: bold;
    border-radius: 40rpx;
    margin-bottom: 40rpx;
    border: 2rpx solid #1890ff;
  }

  .agreement {
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .agreement-text {
    font-size: 24rpx;
    color: #666;
  }
</style>