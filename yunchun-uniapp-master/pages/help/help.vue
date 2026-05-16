<template>
  <view class="help-page">
    <view class="help-content">
      <view class="contact-section">
        <text class="section-title">联系我们</text>
        <view class="phone-item">
          <text class="phone-label">客服电话</text>
          <text class="phone-number">15090210904</text>
        </view>
      </view>

      <view class="feedback-section">
        <text class="section-title">反馈BUG</text>
        <textarea class="feedback-textarea" v-model="feedbackContent" placeholder="请详细描述您遇到的问题或建议..." maxlength="500"
          :show-confirm-bar="false"></textarea>
        <view class="char-count">{{ feedbackContent.length }}/500</view>
        <button class="submit-button" @click="handleSubmit">提交反馈</button>
      </view>
    </view>
  </view>
</template>

<script>
  import api from '@/untils/request.js';

  export default {
    data() {
      return {
        feedbackContent: ''
      };
    },
    methods: {
      async handleSubmit() {
        if (!this.feedbackContent.trim()) {
          uni.showToast({
            title: '请输入反馈内容',
            icon: 'none'
          });
          return;
        }

        try {
          uni.showLoading({
            title: '提交中...'
          });

          const res = await api.post('/appUser/feedback', {
            content: this.feedbackContent
          });
          if (res.code === 200) {
            uni.showToast({
              title: '反馈提交成功',
              icon: 'success'
            });
            this.feedbackContent = '';
          } else {
            uni.showToast({
              title: res.msg || '提交失败',
              icon: 'none'
            });
          }
        } catch (error) {
          uni.showToast({
            title: '提交失败',
            icon: 'none'
          });
        } finally {
          uni.hideLoading();
        }
      }
    }
  };
</script>

<style scoped>
  .help-page {
    min-height: 100vh;
    background: #f5f5f5;
  }

  .help-content {
    padding: 20rpx;
  }

  .contact-section {
    background: #fff;
    border-radius: 16rpx;
    padding: 30rpx;
    margin-bottom: 20rpx;
  }

  .section-title {
    font-size: 32rpx;
    font-weight: bold;
    color: #333;
    display: block;
    margin-bottom: 24rpx;
  }

  .phone-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 24rpx 0;
    border-bottom: 1rpx solid #f0f0f0;
  }

  .phone-label {
    font-size: 28rpx;
    color: #666;
  }

  .phone-number {
    font-size: 28rpx;
    color: #1890ff;
    font-weight: 500;
  }

  .feedback-section {
    background: #fff;
    border-radius: 16rpx;
    padding: 30rpx;
  }

  .feedback-textarea {
    width: 100%;
    min-height: 300rpx;
    padding: 20rpx;
    font-size: 28rpx;
    color: #333;
    background: #f9f9f9;
    border-radius: 8rpx;
    border: 1rpx solid #e8e8e8;
    box-sizing: border-box;
    margin-bottom: 16rpx;
  }

  .char-count {
    text-align: right;
    font-size: 24rpx;
    color: #999;
    margin-bottom: 24rpx;
  }

  .submit-button {
    width: 100%;
    height: 88rpx;
    background: #1890ff;
    color: #fff;
    font-size: 32rpx;
    border: none;
    border-radius: 44rpx;
    font-weight: 500;
  }

  .submit-button:active {
    opacity: 0.8;
  }
</style>