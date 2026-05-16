<template>
  <view class="config-container">
    <view class="config-header">
      <text class="header-title">组态监控</text>
    </view>
    <view class="config-content">
      <view class="config-card">
        <view class="card-header">
          <text class="card-title">锅炉状态</text>
        </view>
        <view class="boiler-status">
          <view class="boiler-item">
            <view class="boiler-icon running">◐</view>
            <text class="boiler-name">锅炉一</text>
            <text class="boiler-status-text running">运行中</text>
          </view>
          <view class="boiler-item">
            <view class="boiler-icon running">◐</view>
            <text class="boiler-name">锅炉二</text>
            <text class="boiler-status-text running">运行中</text>
          </view>
          <view class="boiler-item">
            <view class="boiler-icon stopped">◎</view>
            <text class="boiler-name">锅炉三</text>
            <text class="boiler-status-text stopped">已停止</text>
          </view>
        </view>
      </view>
      <view class="config-card">
        <view class="card-header">
          <text class="card-title">实时数据</text>
        </view>
        <view class="data-grid">
          <view class="data-item">
            <text class="data-value">185°C</text>
            <text class="data-label">温度</text>
          </view>
          <view class="data-item">
            <text class="data-value">2.5MPa</text>
            <text class="data-label">压力</text>
          </view>
          <view class="data-item">
            <text class="data-value">85%</text>
            <text class="data-label">水位</text>
          </view>
          <view class="data-item">
            <text class="data-value">1200kW</text>
            <text class="data-label">功率</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
  export default {
    data() {
      return {};
    }
  };
</script>

<style scoped>
  .config-container {
    min-height: 100vh;
    background-color: #f5f5f5;
    padding-bottom: 120rpx;
  }

  .config-header {
    height: 80rpx;
    background-color: #fff;
    display: flex;
    align-items: center;
    justify-content: center;
    border-bottom: 1rpx solid #e5e5e5;
  }

  .header-title {
    font-size: 32rpx;
    font-weight: bold;
    color: #333;
  }

  .config-content {
    padding: 20rpx;
  }

  .config-card {
    background-color: #fff;
    border-radius: 16rpx;
    margin-bottom: 20rpx;
    box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);
  }

  .card-header {
    padding: 24rpx;
    border-bottom: 1rpx solid #f0f0f0;
  }

  .card-title {
    font-size: 30rpx;
    font-weight: bold;
    color: #333;
  }

  .boiler-status {
    padding: 24rpx;
  }

  .boiler-item {
    display: flex;
    align-items: center;
    padding: 16rpx 0;
    border-bottom: 1rpx dashed #f0f0f0;
  }

  .boiler-item:last-child {
    border-bottom: none;
  }

  .boiler-icon {
    width: 48rpx;
    height: 48rpx;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 28rpx;
    margin-right: 20rpx;
  }

  .boiler-icon.running {
    background-color: rgba(82, 196, 26, 0.1);
    color: #52c41a;
    animation: pulse 2s infinite;
  }

  .boiler-icon.stopped {
    background-color: rgba(153, 153, 153, 0.1);
    color: #999;
  }

  @keyframes pulse {
    0%, 100% {
      opacity: 1;
    }
    50% {
      opacity: 0.5;
    }
  }

  .boiler-name {
    flex: 1;
    font-size: 28rpx;
    color: #333;
  }

  .boiler-status-text {
    font-size: 24rpx;
    padding: 6rpx 16rpx;
    border-radius: 16rpx;
  }

  .boiler-status-text.running {
    color: #52c41a;
    background-color: rgba(82, 196, 26, 0.1);
  }

  .boiler-status-text.stopped {
    color: #999;
    background-color: rgba(153, 153, 153, 0.1);
  }

  .data-grid {
    display: flex;
    flex-wrap: wrap;
    padding: 24rpx;
  }

  .data-item {
    width: 50%;
    text-align: center;
    padding: 20rpx 0;
  }

  .data-value {
    font-size: 36rpx;
    font-weight: bold;
    color: #1890ff;
    display: block;
    margin-bottom: 8rpx;
  }

  .data-label {
    font-size: 24rpx;
    color: #999;
  }
</style>