<template>
  <view class="energy-container">
    <view class="header">
      <view class="header-line"></view>
    </view>
    <view class="content">
      <view class="page-title">能耗分析</view>
      <view class="stats-card">
        <view class="stat-item">
          <view class="stat-icon-wrap">⚡</view>
          <view class="stat-info">
            <text class="stat-value">1258.6</text>
            <text class="stat-label">本月用电量(kWh)</text>
          </view>
        </view>
        <view class="stat-item">
          <view class="stat-icon-wrap">📦</view>
          <view class="stat-info">
            <text class="stat-value">3256.8</text>
            <text class="stat-label">本月用料量(kg)</text>
          </view>
        </view>
      </view>
      <view class="chart-card">
        <text class="card-title">能耗趋势</text>
        <view class="chart-placeholder">
          <text class="chart-text">能耗趋势图表区域</text>
        </view>
      </view>
      <view class="analysis-card">
        <text class="card-title">分析报告</text>
        <view class="analysis-item">
          <text class="analysis-label">同比上月</text>
          <text class="analysis-value positive">↓ 12.5%</text>
        </view>
        <view class="analysis-item">
          <text class="analysis-label">环比上周</text>
          <text class="analysis-value negative">↑ 3.2%</text>
        </view>
      </view>
    </view>
    <CustomTabbar />
  </view>
</template>

<script>
  import CustomTabbar from '@/components/custom-tab-bar/custom-tab-bar.vue'

  export default {
    name: 'EnergyAnalysis',
    components: {
      CustomTabbar
    },
    data() {
      return {}
    }
  }
</script>

<style scoped>
  .energy-container {
    min-height: 100vh;
    background-color: #f5f5f5;
    padding-bottom: 120rpx;
  }

  .header {
    padding-top: 20rpx;
  }

  .header-line {
    height: 6rpx;
    background: linear-gradient(90deg, #3b82f6 0%, #6366f1 50%, #8b5cf6 100%);
  }

  .content {
    padding: 24rpx;
  }

  .page-title {
    font-size: 36rpx;
    font-weight: 700;
    color: #1e293b;
    margin-bottom: 24rpx;
  }

  .stats-card {
    background-color: #ffffff;
    border-radius: 24rpx;
    padding: 32rpx;
    margin-bottom: 24rpx;
    box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);
  }

  .stat-item {
    display: flex;
    align-items: center;
    padding: 20rpx 0;
    border-bottom: 1rpx solid #f1f5f9;
  }

  .stat-item:last-child {
    border-bottom: none;
  }

  .stat-icon-wrap {
    width: 80rpx;
    height: 80rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    background: linear-gradient(135deg, #e0f2fe 0%, #bae6fd 100%);
    border-radius: 20rpx;
    font-size: 40rpx;
    margin-right: 24rpx;
  }

  .stat-info {
    flex: 1;
  }

  .stat-value {
    font-size: 40rpx;
    font-weight: 700;
    color: #1e293b;
    display: block;
  }

  .stat-label {
    font-size: 24rpx;
    color: #64748b;
    margin-top: 8rpx;
    display: block;
  }

  .chart-card,
  .analysis-card {
    background-color: #ffffff;
    border-radius: 24rpx;
    padding: 24rpx;
    margin-bottom: 24rpx;
    box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);
  }

  .card-title {
    font-size: 30rpx;
    font-weight: 600;
    color: #1e293b;
    margin-bottom: 20rpx;
    display: block;
  }

  .chart-placeholder {
    height: 400rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    background-color: #f8fafc;
    border-radius: 16rpx;
  }

  .chart-text {
    font-size: 28rpx;
    color: #94a3b8;
  }

  .analysis-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 20rpx 0;
    border-bottom: 1rpx solid #f1f5f9;
  }

  .analysis-item:last-child {
    border-bottom: none;
  }

  .analysis-label {
    font-size: 28rpx;
    color: #64748b;
  }

  .analysis-value {
    font-size: 28rpx;
    font-weight: 600;
  }

  .analysis-value.positive {
    color: #10b981;
  }

  .analysis-value.negative {
    color: #ef4444;
  }
</style>