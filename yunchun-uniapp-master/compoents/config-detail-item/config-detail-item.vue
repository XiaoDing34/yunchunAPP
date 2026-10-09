<template>
  <view class="config-detail-card">
    <!-- 顶部：设备名称 + 状态灯 -->
    <view class="card-header">
      <text class="device-name">{{ deviceName }}</text>
      <view class="status-section">
        <view class="status-light" :class="statusClass"></view>
        <text class="status-text">{{ statusText }}</text>
      </view>
    </view>

    <!-- 中部：频率设置 -->
    <view class="frequency-section">
      <view v-if="isFixedFreq" class="fixed-freq-tag">
        <text class="fixed-freq-lock">🔒</text>
        <text>公频·已禁用</text>
      </view>
      <view class="freq-stepper">
        <view class="step-btn step-minus" :class="{ 'is-disabled': isFixedFreq }">
          <text class="step-icon">−</text>
        </view>
        <view class="freq-input-wrap" :class="{ 'is-disabled': isFixedFreq }">
          <input class="freq-input" type="number" v-model="customFrequency" :disabled="isFixedFreq" />
          <text class="freq-input-unit">Hz</text>
        </view>
        <view class="step-btn step-plus" :class="{ 'is-disabled': isFixedFreq }">
          <text class="step-icon">＋</text>
        </view>
      </view>
      <text class="freq-tip">{{ isFixedFreq ? '公频模块，频率不可调节' : '自定义设置运行频率' }}</text>
    </view>

    <!-- 底部：启动/停止按钮 -->
    <view class="button-section">
      <button class="control-btn start-btn" :class="{ 'disabled': isRunning }" :disabled="isRunning"
        @click="handleStart">
        启动
      </button>
      <button class="control-btn stop-btn" :class="{ 'disabled': !isRunning }" :disabled="!isRunning"
        @click="handleStop">
        停止
      </button>
    </view>
  </view>
</template>

<script>
  export default {
    name: 'config-detail-item',
    props: {
      deviceName: {
        type: String,
        default: '设备名称'
      },
      frequency: {
        type: [Number, String],
        default: 0
      },
      status: {
        type: String,
        default: 'stopped',
        validator: (value) => {
          return ['running', 'stopped', 'fault'].includes(value);
        }
      }
    },
    data() {
      return {
        // 频率默认留空，待用户输入（仅样式展示，业务方法后续补充）
        customFrequency: ''
      };
    },
    computed: {
      // 公频模块：上料模块的上料机、除渣模块的出渣机，频率固定不可自定义
      isFixedFreq() {
        const fixedDevices = ['上料机', '出渣机'];
        return fixedDevices.includes(this.deviceName);
      },
      isRunning() {
        return this.status === 'running';
      },
      statusClass() {
        return this.status;
      },
      statusText() {
        const statusMap = {
          'running': '运行中',
          'stopped': '已停止',
          'fault': '故障'
        };
        return statusMap[this.status] || '未知';
      }
    },
    methods: {
      handleStart() {
        if (!this.isRunning) {
          this.$emit('start', {
            deviceName: this.deviceName
          });
        }
      },
      handleStop() {
        if (this.isRunning) {
          this.$emit('stop', {
            deviceName: this.deviceName
          });
        }
      }
    }
  };
</script>

<style scoped>
  .config-detail-card {
    background: #FFFFFF;
    border-radius: 40rpx;
    padding: 40rpx;
    margin: 24rpx;
    box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.08);
  }

  /* 顶部：设备名称 + 状态灯 */
  .card-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 32rpx;
  }

  .device-name {
    font-size: 36rpx;
    font-weight: 600;
    color: #1E293B;
  }

  .status-section {
    display: flex;
    align-items: center;
    gap: 12rpx;
  }

  .status-light {
    width: 24rpx;
    height: 24rpx;
    border-radius: 50%;
    box-shadow: inset 0 2rpx 4rpx rgba(0, 0, 0, 0.1);
  }

  .status-light.running {
    background: #22C55E;
    box-shadow: 0 0 12rpx rgba(34, 197, 94, 0.4), inset 0 2rpx 4rpx rgba(0, 0, 0, 0.1);
  }

  .status-light.stopped {
    background: #9CA3AF;
    box-shadow: inset 0 2rpx 4rpx rgba(0, 0, 0, 0.1);
  }

  .status-light.fault {
    background: #EF4444;
    box-shadow: 0 0 12rpx rgba(239, 68, 68, 0.4), inset 0 2rpx 4rpx rgba(0, 0, 0, 0.1);
    animation: pulse 1.5s ease-in-out infinite;
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

  .status-text {
    font-size: 26rpx;
    font-weight: 400;
    color: #6B7280;
  }

  /* 中部：频率设置 */
  .frequency-section {
    display: flex;
    flex-direction: column;
    align-items: center;
    margin-bottom: 48rpx;
    padding: 32rpx 0;
  }

  /* 公频禁用标签 */
  .fixed-freq-tag {
    display: inline-flex;
    align-items: center;
    gap: 8rpx;
    font-size: 24rpx;
    font-weight: 600;
    color: #B45309;
    background: linear-gradient(135deg, #FEF3C7 0%, #FDE68A 100%);
    border: 2rpx solid #F59E0B;
    border-radius: 10rpx;
    padding: 6rpx 18rpx;
    line-height: 1.2;
    margin-bottom: 20rpx;
  }

  .fixed-freq-lock {
    font-size: 24rpx;
    line-height: 1;
  }

  /* 步进器 + 输入框 */
  .freq-stepper {
    display: flex;
    align-items: center;
    gap: 24rpx;
  }

  .step-btn {
    width: 72rpx;
    height: 72rpx;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
  }

  .step-minus {
    background: #F3F4F6;
    border: 2rpx solid #D1D5DB;
  }

  .step-plus {
    background: linear-gradient(135deg, #3B82F6 0%, #2563EB 100%);
    box-shadow: 0 4rpx 12rpx rgba(59, 130, 246, 0.3);
  }

  .step-icon {
    font-size: 40rpx;
    line-height: 1;
  }

  .step-minus .step-icon {
    color: #4B5563;
  }

  .step-plus .step-icon {
    color: #FFFFFF;
  }

  /* 公频禁用态 */
  .step-btn.is-disabled {
    background: #F3F4F6;
    border: 2rpx solid #E5E7EB;
    box-shadow: none;
    opacity: 0.6;
  }

  .step-btn.is-disabled .step-icon {
    color: #C4C9D2;
  }

  .freq-input-wrap {
    display: flex;
    align-items: baseline;
    justify-content: center;
    background: #F8FAFC;
    border: 2rpx solid #E2E8F0;
    border-radius: 16rpx;
    padding: 12rpx 28rpx;
    min-width: 280rpx;
  }

  .freq-input-wrap.is-disabled {
    background: #F3F4F6;
    border-color: #E5E7EB;
  }

  .freq-input {
    width: 200rpx;
    font-size: 64rpx;
    font-weight: 600;
    color: #1E293B;
    text-align: center;
    line-height: 1.2;
  }

  .freq-placeholder {
    font-size: 15rpx;
    font-weight: 400;
    color: #C4C9D2;
  }

  .freq-input-unit {
    font-size: 32rpx;
    font-weight: 600;
    color: #9CA3AF;
    margin-left: 8rpx;
  }

  /* 频率区域提示文字 */
  .freq-tip {
    margin-top: 20rpx;
    font-size: 24rpx;
    color: #9CA3AF;
  }

  /* 底部：启动/停止按钮 */
  .button-section {
    display: flex;
    gap: 24rpx;
  }

  .control-btn {
    flex: 1;
    height: 96rpx;
    border-radius: 48rpx;
    font-size: 32rpx;
    font-weight: 600;
    border: none;
    transition: all 0.2s ease;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .control-btn:active:not(.disabled) {
    transform: scale(0.98);
  }

  .start-btn {
    background: linear-gradient(135deg, #3B82F6 0%, #2563EB 100%);
    color: #FFFFFF;
    box-shadow: 0 4rpx 12rpx rgba(59, 130, 246, 0.3);
  }

  .start-btn:active:not(.disabled) {
    background: linear-gradient(135deg, #2563EB 0%, #1D4ED8 100%);
    box-shadow: 0 2rpx 8rpx rgba(59, 130, 246, 0.4);
  }

  .start-btn.disabled {
    opacity: 0.5;
    background: linear-gradient(135deg, #93C5FD 0%, #60A5FA 100%);
    box-shadow: none;
  }

  .stop-btn {
    background: #F3F4F6;
    color: #4B5563;
    border: 2rpx solid #D1D5DB;
  }

  .stop-btn:active:not(.disabled) {
    background: #E5E7EB;
  }

  .stop-btn.disabled {
    opacity: 0.5;
    background: #F9FAFB;
    color: #9CA3AF;
  }
</style>