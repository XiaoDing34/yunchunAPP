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
        avatarChanged: false,
        positions: ['请选择职位', '普通员工', '负责人', '其他'],
        organizations: ['请输入所属单位', '天津农学院', '东润发鸡舍', '珍宝岛药业', '其他']
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
        this.avatarChanged = true;
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
        const {
          nickname,
          phone,
          verifyCode,
          organization,
          position
        } = this.formData;

        if (!nickname) {
          uni.showToast({
            title: '请输入昵称',
            icon: 'none'
          });
          return;
        }
        if (!phone || phone.length !== 11) {
          uni.showToast({
            title: '请输入正确的手机号',
            icon: 'none'
          });
          return;
        }
        if (!verifyCode) {
          uni.showToast({
            title: '请输入验证码',
            icon: 'none'
          });
          return;
        }
        if (!organization) {
          uni.showToast({
            title: '请输入所属单位',
            icon: 'none'
          });
          return;
        }
        if (!position) {
          uni.showToast({
            title: '请选择职位',
            icon: 'none'
          });
          return;
        }

        uni.showLoading({
          title: '保存中...',
          mask: true
        });

        try {
          //1. 提交手机号、验证码、单位、职位（验证码最关键，先校验）
          await api.put('/appUser/updateLoginInfo', {
            phone,
            code: verifyCode,
            organization,
            position
          });

          //2. 更新昵称（后端 @RequestParam，必须走 query 参数）
          await api.put('/appUser/nickname?nickname=' + encodeURIComponent(nickname));

          //3. 仅当用户选择了新头像时才上传
          if (this.avatarChanged) {
            const avatarUrl = await this.uploadAvatar();
            this.avatarUrl = avatarUrl;
            //同步到 Vuex userInfo（SET_USER_INFO 内部会持久化到本地存储）
            this.$store.commit('SET_USER_INFO', {
              ...this.$store.state.userInfo,
              avatar: avatarUrl
            });
          }

          uni.hideLoading();
          uni.showToast({
            title: '保存成功',
            icon: 'success'
          });
          setTimeout(() => {
            uni.switchTab({
              url: '/pages/index/index'
            });
          }, 800);
        } catch (err) {
          uni.hideLoading();
          console.log('保存失败', err);
          const msg = (err.data && err.data.message) || '保存失败';
          uni.showToast({
            title: msg,
            icon: 'none'
          });
        }
      },
      uploadAvatar() {
        return new Promise((resolve, reject) => {
          uni.uploadFile({
            url: 'http://localhost:9095/appUser/upload/avatar',
            filePath: this.avatarUrl,
            name: 'file',
            header: {
              'Authorization': getToken()
            },
            success: (res) => {
              try {
                const data = JSON.parse(res.data);
                if (data.code === 200) {
                  //兼容 data 为 {avatar:'xxx'} 或直接是 URL 字符串两种返回
                  const avatarUrl = (data.data && data.data.avatar) ? data.data.avatar : data.data;
                  resolve(avatarUrl);
                } else {
                  reject({
                    data
                  });
                }
              } catch (e) {
                reject({
                  data: {
                    message: '头像上传响应解析失败'
                  }
                });
              }
            },
            fail: () => {
              reject({
                data: {
                  message: '头像上传失败'
                }
              });
            }
          });
        });
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