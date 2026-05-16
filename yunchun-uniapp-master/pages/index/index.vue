<template>
  <view class="container">
    <scroll-view class="tabs-wrapper" scroll-x="true" enable-flex="true">
      <view class="tabs-container">
        <view v-for="item in dimensions" :key="item.id" class="tab-item" :class="{ active: activeIndex === item.id }"
          @click="switchTab(item.id)">
          <text class="tab-text">{{ item.name }}</text>
          <view v-if="activeIndex === item.id" class="tab-indicator"></view>
        </view>
      </view>
    </scroll-view>

    <view class="content">
      <!-- 头部信息卡片 -->
      <view class="header-card">
        <view class="header-left">
          <view class="header-title">{{ currentDevice.name }}</view>
          <view class="header-subtitle">实时监控数据</view>
          <view class="header-status" :class="statusClass">
            <text class="status-dot"></text>
            <text class="status-text">{{ currentDevice.state }}</text>
          </view>
        </view>
        <view class="header-right">
          <view class="stat-box">
            <text class="stat-value">{{ deviceData.length }}</text>
            <text class="stat-label">监控项</text>
          </view>
        </view>
      </view>

      <!-- 数据项列表 -->
      <view class="data-items-container">
        <DataItem v-for="(item, index) in deviceData" :key="activeIndex + '-' + index" :valueId="item.id"
          :pollCount="pollCount" :dataName="item.notes" :dataValue="Number(item.value).toFixed(2)"
          :updateTime="item.updateTime" :deviceData="deviceData" :activeIndex="activeIndex" />
      </view>
    </view>
  </view>
</template>

<script>
  import api from '@/untils/request.js';
  import DataItem from '@/compoents/data-item/data-item.vue';
  export default {
    components: {
      DataItem
    },
    data() {
      return {
        dimensions: [],
        deviceData: [],
        activeIndex: null,
        chartData: [], // 传递给子组件的数据
        timer: null,
        pollInterval: 60000, // 轮询间隔 3 秒
        pollCount: 0 // 轮询计数器
      };
    },
    computed: {
      currentIndex() {
        return this.dimensions.findIndex(item => item.id === this.activeIndex);
      },
      currentDevice() {
        const idx = this.currentIndex;
        const device = idx >= 0 ? (this.dimensions[idx] || {}) : {};
        const data = idx >= 0 ? (this.deviceData[idx] || {}) : {};
        const state = device.state === '在线' ? '运行中' : '已停止';
        return {
          name: device.name,
          state: state
        };
      },
      statusClass() {
        return this.currentDevice.state === '运行中' ? 'running' : 'stopped';
      }
    },
    watch: {
      activeIndex(newVal, oldVal) {
        if (newVal && newVal !== oldVal) {
          this.fetchDeviceData(newVal);
        }
      }
    },
    async onLoad() {
      await this.fetchDimensions();
      await this.fetchDeviceData();
      this.startPolling();
    },
    onUnload() {
      this.stopPolling();
    },
    methods: {
      startPolling() {
        this.stopPolling();
        this.timer = setInterval(() => {
          this.fetchDeviceData();
        }, this.pollInterval);
      },
      stopPolling() {
        if (this.timer) {
          clearInterval(this.timer);
          this.timer = null;
        }
      },
      async fetchDimensions() {
        try {
          const res = await api.get('/device/getDeviceById');
          this.dimensions = res.data || [];
          const defaultItem = this.dimensions[0];
          if (defaultItem) {
            this.activeIndex = defaultItem.id;
          }
        } catch (err) {
          console.log('获取设备失败', err);
        }
      },
      async fetchDeviceData() {
        try {
          const res = await api.get('/deviceValue/getDeviceValueByDeviceId', {
            deviceId: this.activeIndex
          });
          this.deviceData = res.data || [];
          this.pollCount++;
        } catch (err) {
          console.log('获取设备数据失败', err);
        }
      },
      switchTab(id) {
        this.activeIndex = id;
      }
    }
  };
</script>

<style scoped>
  .container {
    min-height: 100vh;
    background-color: #f5f5f5;
    padding: 20rpx;
  }

  .tabs-wrapper {
    width: 100%;
    white-space: nowrap;
    background-color: #fff;
    border-radius: 16rpx;
    box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);
    margin-bottom: 30rpx;
  }

  .tabs-container {
    display: inline-flex;
    padding: 10rpx;
  }

  .tab-item {
    flex-shrink: 0;
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 24rpx 40rpx;
    position: relative;
    transition: all 0.3s ease;
  }

  .tab-text {
    font-size: 28rpx;
    color: #333;
    transition: color 0.3s ease;
  }

  .tab-item.active .tab-text {
    color: #1890ff;
    font-weight: bold;
  }

  .tab-indicator {
    position: absolute;
    bottom: 0;
    left: 50%;
    transform: translateX(-50%);
    width: 48rpx;
    height: 4rpx;
    background-color: #1890ff;
    border-radius: 2rpx;
  }

  .content {
    background-color: #fff;
    border-radius: 16rpx;
    padding: 30rpx;
    box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);
  }

  .header-card {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 40rpx;
    background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
    border-radius: 24rpx;
    color: #fff;
    margin-bottom: 30rpx;
    overflow: hidden;
    box-shadow: 0 8rpx 32rpx rgba(15, 52, 96, 0.3);
  }

  .header-left {
    flex: 1;
  }

  .header-title {
    font-size: 44rpx;
    font-weight: bold;
    margin-bottom: 8rpx;
    letter-spacing: 2rpx;
    text-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.3);
  }

  .header-subtitle {
    font-size: 22rpx;
    color: rgba(255, 255, 255, 0.7);
    margin-bottom: 20rpx;
  }

  .header-status {
    display: inline-flex;
    align-items: center;
    padding: 10rpx 24rpx;
    border-radius: 30rpx;
    font-size: 22rpx;
  }

  .status-dot {
    width: 12rpx;
    height: 12rpx;
    border-radius: 50%;
    margin-right: 12rpx;
  }

  .header-status.running {
    background: rgba(82, 196, 26, 0.25);
    border: 1rpx solid rgba(82, 196, 26, 0.5);
  }

  .header-status.running .status-dot {
    background-color: #52c41a;
    box-shadow: 0 0 8rpx #52c41a;
    animation: pulse 2s infinite;
  }

  .header-status.stopped {
    background: rgba(255, 77, 79, 0.25);
    border: 1rpx solid rgba(255, 77, 79, 0.5);
  }

  .header-status.stopped .status-dot {
    background-color: #ff4d4f;
    box-shadow: 0 0 8rpx #ff4d4f;
  }

  @keyframes pulse {

    0%,
    100% {
      opacity: 1;
    }

    50% {
      opacity: 0.5;
    }
  }

  .header-right {
    flex-shrink: 0;
    margin-left: 30rpx;
  }

  .stat-box {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    width: 160rpx;
    height: 160rpx;
    background: rgba(255, 255, 255, 0.15);
    border-radius: 20rpx;
    backdrop-filter: blur(10rpx);
    border: 1rpx solid rgba(255, 255, 255, 0.2);
  }

  .stat-box .stat-value {
    font-size: 56rpx;
    font-weight: bold;
    background: linear-gradient(135deg, #ffffff 0%, #52c41a 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
    line-height: 1.2;
  }

  .stat-box .stat-label {
    font-size: 22rpx;
    color: rgba(255, 255, 255, 0.8);
    margin-top: 8rpx;
  }
</style>