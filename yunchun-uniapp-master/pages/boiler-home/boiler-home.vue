<template>
  <view class="boiler-home-container">
    <view class="header-card">
      <view class="header-title">锅炉监控中心</view>
      <view class="header-subtitle">实时数据监控</view>
    </view>
    <view class="boiler-stats">
      <view class="stat-item">
        <view class="stat-icon">🔥</view>
        <view class="stat-info">
          <text class="stat-value">{{ furnaceTemp || '--' }}</text>
          <text class="stat-label">炉膛温度</text>
        </view>
      </view>
      <view class="stat-item">
        <view class="stat-icon">💧</view>
        <view class="stat-info">
          <text class="stat-value">{{ waterLevel || '--' }}</text>
          <text class="stat-label">水位</text>
        </view>
      </view>
      <view class="stat-item">
        <view class="stat-icon">⚡</view>
        <view class="stat-info">
          <text class="stat-value">{{ power || '--' }}</text>
          <text class="stat-label">功率</text>
        </view>
      </view>
      <view class="stat-item">
        <view class="stat-icon">⏱️</view>
        <view class="stat-info">
          <text class="stat-value">{{ runTime || '--' }}</text>
          <text class="stat-label">运行时长</text>
        </view>
      </view>
    </view>
    <view class="boiler-status">
      <view class="status-header">
        <text class="status-title">运行状态</text>
        <view :class="['status-badge', isRunning ? 'running' : 'stopped']">
          {{ isRunning ? '运行中' : '已停止' }}
        </view>
      </view>
      <view class="status-grid">
        <view class="status-item">
          <text class="status-label">出水温度</text>
          <text class="status-value">{{ outletTemp || '--' }}°C</text>
        </view>
        <view class="status-item">
          <text class="status-label">回水温度</text>
          <text class="status-value">{{ returnTemp || '--' }}°C</text>
        </view>
        <view class="status-item">
          <text class="status-label">蒸汽压力</text>
          <text class="status-value">{{ steamPressure || '--' }} MPa</text>
        </view>
        <view class="status-item">
          <text class="status-label">燃料消耗</text>
          <text class="status-value">{{ fuelConsumption || '--' }} kg/h</text>
        </view>
      </view>
    </view>
    <view class="quick-actions">
      <view class="action-item" @click="handleAction('start')">
        <view class="action-icon">▶️</view>
        <text class="action-text">启动锅炉</text>
      </view>
      <view class="action-item" @click="handleAction('stop')">
        <view class="action-icon">⏹️</view>
        <text class="action-text">停止锅炉</text>
      </view>
      <view class="action-item" @click="handleAction('reset')">
        <view class="action-icon">🔄</view>
        <text class="action-text">重置数据</text>
      </view>
    </view>
    <CustomTabbar />
  </view>
</template>

<script>
import CustomTabbar from '@/components/custom-tab-bar/custom-tab-bar.vue'

export default {
  name: 'BoilerHome',
  components: {
    CustomTabbar
  },
  data() {
    return {
      furnaceTemp: '20.75',
      waterLevel: '85',
      power: '120',
      runTime: '4h 32m',
      isRunning: true,
      outletTemp: '95.2',
      returnTemp: '55.8',
      steamPressure: '1.25',
      fuelConsumption: '45.6'
    }
  },
  methods: {
    handleAction(action) {
      const messages = {
        start: '锅炉启动中...',
        stop: '锅炉停止中...',
        reset: '数据重置成功'
      }
      uni.showToast({
        title: messages[action],
        icon: 'none'
      })
    }
  }
}
</script>

<style scoped>
.boiler-home-container {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding: 20rpx;
  padding-bottom: 140rpx;
}

.header-card {
  background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%);
  border-radius: 24rpx;
  padding: 40rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 8rpx 24rpx rgba(37, 99, 235, 0.3);
}

.header-title {
  font-size: 40rpx;
  font-weight: 700;
  color: #fff;
  margin-bottom: 8rpx;
}

.header-subtitle {
  font-size: 28rpx;
  color: rgba(255, 255, 255, 0.8);
}

.boiler-stats {
  display: flex;
  justify-content: space-between;
  background-color: #fff;
  border-radius: 20rpx;
  padding: 24rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.04);
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  flex: 1;
}

.stat-icon {
  font-size: 40rpx;
  margin-bottom: 8rpx;
}

.stat-info {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-value {
  font-size: 28rpx;
  font-weight: 600;
  color: #1e293b;
}

.stat-label {
  font-size: 22rpx;
  color: #94a3b8;
  margin-top: 4rpx;
}

.boiler-status {
  background-color: #fff;
  border-radius: 20rpx;
  padding: 24rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.04);
}

.status-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24rpx;
}

.status-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #1e293b;
}

.status-badge {
  padding: 8rpx 24rpx;
  border-radius: 20rpx;
  font-size: 24rpx;
  font-weight: 600;
}

.status-badge.running {
  background-color: #dcfce7;
  color: #22c55e;
}

.status-badge.stopped {
  background-color: #fee2e2;
  color: #ef4444;
}

.status-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20rpx;
}

.status-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx;
  background-color: #f8fafc;
  border-radius: 16rpx;
}

.status-label {
  font-size: 26rpx;
  color: #64748b;
}

.status-value {
  font-size: 28rpx;
  font-weight: 600;
  color: #1e293b;
}

.quick-actions {
  display: flex;
  justify-content: space-between;
  background-color: #fff;
  border-radius: 20rpx;
  padding: 24rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.04);
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  flex: 1;
  padding: 16rpx;
  border-radius: 16rpx;
  transition: background-color 0.2s;
}

.action-item:active {
  background-color: #f1f5f9;
}

.action-icon {
  font-size: 48rpx;
  margin-bottom: 8rpx;
}

.action-text {
  font-size: 24rpx;
  color: #475569;
}
</style>