<template>
  <view class="data-item">
    <!-- 头部区域 -->
    <view class="header-section">
      <view class="title-wrapper">
        <view class="title-bar"></view>
        <text class="data-name">{{ dataName }}</text>
      </view>
      <view class="status-wrapper">
        <text class="status-dot"></text>
        <view class="status-badge">实时监测</view>
      </view>
    </view>

    <!-- 温度数值区域 -->
    <view class="value-section">
      <text class="main-value">{{ dataValue }}</text>
      <text class="unit">{{ getUnit(dataName) }}</text>
    </view>

    <!-- 趋势/范围指示器 -->
    <view class="range-section">
      <view class="range-labels">
        <text class="range-label">{{ (parseFloat(dataValue) - 5).toFixed(1) }}{{ getUnit(dataName) }}</text>
        <text class="range-label">{{ (parseFloat(dataValue) + 15).toFixed(1) }}{{ getUnit(dataName) }}</text>
      </view>
      <view class="progress-bar">
        <view class="progress-fill" :style="{ width: getProgressWidth() + '%' }"></view>
      </view>
      <view class="scale-labels">
        <text class="scale-label">{{ (parseFloat(dataValue) - 5).toFixed(1) }}</text>
        <text class="scale-label">{{ parseFloat(dataValue).toFixed(1) }}</text>
        <text class="scale-label">{{ (parseFloat(dataValue) + 5).toFixed(1) }}</text>
        <text class="scale-label">{{ (parseFloat(dataValue) + 10).toFixed(1) }}</text>
        <text class="scale-label">{{ (parseFloat(dataValue) + 15).toFixed(1) }}</text>
      </view>
    </view>

    <!-- 分隔线 -->
    <view class="divider"></view>

    <!-- 底部信息区域 -->
    <view class="footer-section">
      <text class="update-time">更新时间：{{ formatTime(updateTime) }}</text>
      <view class="footer-buttons-wrapper">
        <view class="alarm-button" @click="open">数据报警</view>
        <view class="history-button" @click="handleHistoryClick">
          <text>历史数据</text>
          <text class="history-arrow">→</text>
        </view>
      </view>
      <!-- 数据报警弹出层 -->
      <uni-popup ref="customPopup" type="center">
        <view class="alarm-dialog">
          <!-- 警告提示 -->
          <view class="dialog-warning">
            <text class="warning-icon">⚠️</text>
            <text class="warning-text">该操作可能会产生报警信息，请谨慎使用</text>
          </view>

          <!-- 分割线 -->
          <view class="dialog-divider"></view>

          <!-- 数据项 -->
          <view class="data-info-section">
            <view class="data-info-row">
              <text class="data-info-label">数据项</text>
              <text class="data-info-value">{{ dataName }}</text>
            </view>
            <view class="data-info-row">
              <text class="data-info-label">当前值</text>
              <text class="data-info-value">{{ dataValue }}{{ getUnit(dataName) }}</text>
            </view>
          </view>
          <view class="alarm-reason-section">
            <text class="alarm-reason-label">报警原因</text>
            <view class="alarm-reason-picker" @click="showWarnReasonPicker">
              <input class="alarm-reason-input" placeholder="请输入报警原因" :value="formData.warnReason" disabled />
              <text class="alarm-reason-arrow">></text>
            </view>
          </view>
          <view class="normal-range-section">
            <text class="normal-range-label">变量正常范围</text>
            <view class="normal-range-inputs">
              <view class="range-input-item">
                <text class="range-input-label">最低值</text>
                <input class="range-input" type="digit" placeholder="请输入最低值" v-model="formData.minValue" />
              </view>
              <view class="range-input-divider"></view>
              <view class="range-input-item">
                <text class="range-input-label">最高值</text>
                <input class="range-input" type="digit" placeholder="请输入最高值" v-model="formData.maxValue" />
              </view>
            </view>
          </view>

          <!-- 报警紧急状态 -->
          <view class="warn-status-section">
            <text class="warn-status-label">报警等级</text>
            <radio-group class="warn-status-options" @change="onWarnStatusChange">
              <view class="warn-status-option status-urgent">
                <radio :value="'紧急'" :checked="formData.warnLevel === '紧急'" />
                <text class="warn-status-text">紧急</text>
              </view>
              <view class="warn-status-option status-important">
                <radio :value="'重要'" :checked="formData.warnLevel === '重要'" />
                <text class="warn-status-text">重要</text>
              </view>
              <view class="warn-status-option status-hint">
                <radio :value="'提示'" :checked="formData.warnLevel === '提示'" />
                <text class="warn-status-text">提示</text>
              </view>
            </radio-group>
          </view>

          <!-- 操作建议 -->
          <view class="suggestion-section">
            <text class="suggestion-label">操作建议</text>
            <textarea class="suggestion-textarea" placeholder="请输入操作建议（选填）" maxlength="200"
              v-model="formData.advice"></textarea>
          </view>

          <!-- 按钮组 -->
          <view class="dialog-buttons">
            <view class="dialog-btn cancel-btn" @click="close">取消</view>
            <view class="dialog-btn confirm-btn" @click="handleConfirm">确定</view>
          </view>
        </view>
      </uni-popup>
    </view>
  </view>
</template>

<script>
  import api from '@/untils/request.js'
  export default {
    props: {
      valueId: {
        type: Number,
        default: ''
      },
      pollCount: {
        type: Number,
        default: 0
      },
      dataName: {
        type: String,
        default: ''
      },
      dataValue: {
        type: [String, Number],
        default: ''
      },
      updateTime: {
        type: String,
        default: ''
      },
      deviceData: {
        type: Array,
        default: () => []
      },
      activeIndex: {
        type: [Number, String],
        default: null
      }
    },
    data() {
      return {
        unitMap: {
          '温度': '°C',
          '频率': 'Hz',
          '液位': 'mm',
          '压': 'MPa',
          '电压': 'V',
          '电流': 'A',
          '流量': 'm³/h'
        },
        formData: {
          warnReason: '',
          advice: '',
          maxValue: '',
          minValue: '',
          deviceId: null,
          valueId: null,
          warnLevel: '紧急'
        },
        warnReasons: ['请输入报警原因', '该变量值过低', '该变量值过高', '其他'],
      }
    },
    mounted() {
      this.formData.deviceId = this.activeIndex,
        this.formData.valueId = this.valueId
    },
    watch: {
      activeIndex(newVal) {
        this.formData.activeIndex = newVal
      }
    },
    methods: {
      open() {
        this.$refs.customPopup.open('center');
      },
      close() {
        this.formData = {
          warnReason: '',
          advice: '',
          maxValue: '',
          minValue: '',
          deviceId: this.activeIndex,
          warnLevel: ''
        };
        this.$refs.customPopup.close();
      },
      onWarnStatusChange(e) {
        this.formData.warnLevel = e.detail.value;
      },
      async handleConfirm() {
        try {
          const res = await api.post('/warnMessage/addWarnMessage', this.formData)
          if (res.code == 200) {
            console.log(this.formData);
            uni.showToast({
              title: '报警已提交',
              icon: 'success'
            });
          } else {
            console.log(res.data)
          }
        } catch (err) {
          console.log(err.message)
        };
        this.close();
      },
      showWarnReasonPicker() {
        uni.showActionSheet({
          itemList: this.warnReasons,
          success: (res) => {
            if (res.tapIndex > 0) {
              this.formData.warnReason = this.warnReasons[res.tapIndex]
            }
          }
        })
      },
      formatTime(time) {
        if (!time) return '';
        const date = new Date(time);
        if (isNaN(date.getTime())) return '';
        const hours = date.getHours().toString().padStart(2, '0');
        const minutes = date.getMinutes().toString().padStart(2, '0');
        return `${hours}:${minutes}`;
      },
      getUnit(name) {
        if (!name) return '';
        for (const key in this.unitMap) {
          if (name.includes(key)) {
            return this.unitMap[key];
          }
        }
        return '';
      },
      getProgressWidth() {
        const value = parseFloat(this.dataValue);
        if (isNaN(value)) return 50;
        const min = value - 5;
        const max = value + 15;
        const range = max - min;
        const normalized = (value - min) / range;
        return Math.max(0, Math.min(100, normalized * 100));
      },
      handleHistoryClick() {
        console.log('组件的valueId', this.valueId);
        uni.navigateTo({
          url: `/pages/history/history?valueId=${this.valueId}&valueName=${encodeURIComponent(this.dataName)}`
        });
      }
    }
  }
</script>

<style scoped>
  .data-item {
    background-color: #ffffff;
    border-radius: 32rpx;
    padding: 40rpx 32rpx;
    margin: 0 auto 24rpx;
    width: 90%;
    box-shadow: 0 8rpx 24rpx rgba(0, 0, 0, 0.05);
    position: relative;
    border: 1rpx solid #F3F4F6;
  }

  .header-section {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 32rpx;
  }

  .status-wrapper {
    display: flex;
    align-items: center;
    gap: 8rpx;
  }

  .status-dot {
    width: 12rpx;
    height: 12rpx;
    border-radius: 50%;
    background-color: #22c55e;
    box-shadow: 0 0 8rpx rgba(34, 197, 94, 0.5);
  }

  .title-wrapper {
    display: flex;
    align-items: center;
    gap: 16rpx;
  }

  .title-bar {
    width: 6rpx;
    height: 36rpx;
    background-color: #3B82F6;
    border-radius: 3rpx;
  }

  .data-name {
    font-size: 34rpx;
    font-weight: 600;
    color: #1F2937;
    font-family: 'PingFang SC', -apple-system, sans-serif;
  }

  .status-badge {
    padding: 8rpx 16rpx;
    background-color: #EFF6FF;
    border-radius: 20rpx;
    font-size: 22rpx;
    color: #3B82F6;
    font-weight: 500;
  }

  .value-section {
    margin-bottom: 32rpx;
    display: flex;
    align-items: baseline;
  }

  .main-value {
    font-size: 120rpx;
    font-weight: 800;
    color: #0F172A;
    font-family: 'Roboto Mono', 'Fira Mono', 'JetBrains Mono', monospace;
    line-height: 1.1;
  }

  .unit {
    font-size: 44rpx;
    font-weight: 500;
    color: #64748B;
    margin-left: 8rpx;
    vertical-align: top;
    margin-top: 16rpx;
  }

  .range-section {
    margin-bottom: 24rpx;
  }

  .range-labels {
    display: flex;
    justify-content: space-between;
    margin-bottom: 12rpx;
  }

  .range-label {
    font-size: 24rpx;
    color: #94A3B8;
    font-family: 'Roboto Mono', monospace;
  }

  .progress-bar {
    height: 8rpx;
    background-color: #E2E8F0;
    border-radius: 4rpx;
    overflow: hidden;
    position: relative;
  }

  .progress-fill {
    height: 100%;
    background: linear-gradient(90deg, #3B82F6 0%, #60A5FA 100%);
    border-radius: 4rpx;
    transition: width 0.3s ease;
  }

  .scale-labels {
    display: flex;
    justify-content: space-between;
    margin-top: 8rpx;
  }

  .scale-label {
    font-size: 22rpx;
    color: #94A3B8;
    font-family: 'Roboto Mono', monospace;
  }

  .divider {
    height: 1rpx;
    background-color: #F1F5F9;
    margin: 24rpx 0;
  }

  .footer-section {
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 16rpx;
  }

  .footer-buttons-wrapper {
    display: flex;
    gap: 12rpx;
    align-items: center;
  }

  .update-time {
    font-size: 26rpx;
    color: #9CA3AF;
    font-family: 'PingFang SC', -apple-system, sans-serif;
  }

  .history-button {
    display: flex;
    align-items: center;
    padding: 8rpx 20rpx;
    background-color: #F8FAFC;
    border-radius: 60rpx;
    font-size: 26rpx;
    color: #3B82F6;
    font-weight: 500;
    transition: all 0.2s ease;
  }

  .history-button:active {
    background-color: #EFF6FF;
    transform: scale(0.96);
  }

  .history-arrow {
    margin-left: 6rpx;
    font-size: 24rpx;
    font-weight: bold;
  }

  .alarm-button {
    display: flex;
    align-items: center;
    padding: 8rpx 20rpx;
    background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
    border-radius: 60rpx;
    font-size: 26rpx;
    color: #fff;
    font-weight: 500;
    transition: all 0.2s ease;
    box-shadow: 0 4rpx 12rpx rgba(239, 68, 68, 0.3);
  }

  .alarm-button:active {
    transform: scale(0.96);
    box-shadow: 0 2rpx 8rpx rgba(239, 68, 68, 0.4);
  }

  .alarm-dialog {
    width: 560rpx;
    background-color: #ffffff;
    border-radius: 24rpx;
    padding: 40rpx 32rpx;
    box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.15);
  }

  .dialog-warning {
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 24rpx;
    gap: 12rpx;
  }

  .warning-icon {
    font-size: 40rpx;
  }

  .warning-text {
    font-size: 26rpx;
    color: #dc2626;
    font-weight: 500;
    text-align: center;
  }

  .dialog-divider {
    height: 1rpx;
    background-color: #f0f0f0;
    margin-bottom: 32rpx;
  }

  .data-info-section {
    margin-bottom: 32rpx;
  }

  .data-info-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 16rpx 0;
    border-bottom: 1rpx solid #f5f5f5;
  }

  .data-info-row:last-child {
    border-bottom: none;
  }

  .data-info-label {
    font-size: 26rpx;
    color: #64748b;
  }

  .data-info-value {
    font-size: 28rpx;
    color: #1f2937;
    font-weight: 600;
  }

  .normal-range-section {
    margin-bottom: 32rpx;
  }

  .normal-range-label {
    display: block;
    font-size: 26rpx;
    color: #64748b;
    margin-bottom: 16rpx;
  }

  .normal-range-inputs {
    display: flex;
    align-items: center;
    gap: 20rpx;
  }

  .range-input-item {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: 8rpx;
  }

  .range-input-label {
    font-size: 24rpx;
    color: #64748b;
  }

  .range-input {
    padding: 16rpx 20rpx;
    background-color: #f8fafc;
    border: 1rpx solid #e2e8f0;
    border-radius: 12rpx;
    font-size: 28rpx;
    color: #1f2937;
  }

  .range-input-divider {
    width: 1rpx;
    height: 80rpx;
    background-color: #e2e8f0;
  }

  .warn-status-section {
    margin-bottom: 32rpx;
  }

  .warn-status-label {
    display: block;
    font-size: 28rpx;
    color: #374151;
    font-weight: 600;
    margin-bottom: 16rpx;
  }

  .warn-status-options {
    display: flex;
    gap: 24rpx;
  }

  .warn-status-option {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8rpx;
    padding: 16rpx 20rpx;
    background-color: #f8fafc;
    border-radius: 12rpx;
    border: 1rpx solid #e2e8f0;
  }

  /* 紧急状态 - 深红色 */
  .status-urgent {
    border-color: #fecaca;
    background-color: #fef2f2;
  }

  .status-urgent .warn-status-text {
    color: #b91c1c;
    font-weight: 700;
  }

  .status-urgent radio {
    color: #b91c1c;
  }

  /* 重要状态 - 深橙色 */
  .status-important {
    border-color: #fed7aa;
    background-color: #fff7ed;
  }

  .status-important .warn-status-text {
    color: #c2410c;
    font-weight: 700;
  }

  .status-important radio {
    color: #c2410c;
  }

  /* 提示状态 - 深黄色 */
  .status-hint {
    border-color: #fde68a;
    background-color: #fffbeb;
  }

  .status-hint .warn-status-text {
    color: #b45309;
    font-weight: 700;
  }

  .status-hint radio {
    color: #b45309;
  }

  .warn-status-text {
    font-size: 28rpx;
  }

  .alarm-reason-section {
    margin-bottom: 32rpx;
  }

  .alarm-reason-label {
    display: block;
    font-size: 26rpx;
    color: #64748b;
    margin-bottom: 16rpx;
  }

  .alarm-reason-picker {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 20rpx 24rpx;
    background-color: #f8fafc;
    border: 1rpx solid #e2e8f0;
    border-radius: 12rpx;
  }

  .alarm-reason-input {
    flex: 1;
    font-size: 28rpx;
    color: #1f2937;
    background-color: transparent;
  }

  .alarm-reason-arrow {
    font-size: 28rpx;
    color: #94a3b8;
    margin-left: 16rpx;
  }

  .suggestion-section {
    margin-bottom: 36rpx;
  }

  .suggestion-label {
    display: block;
    font-size: 26rpx;
    color: #64748b;
    margin-bottom: 16rpx;
  }

  .suggestion-textarea {
    width: 100%;
    min-height: 120rpx;
    padding: 20rpx;
    background-color: #f8fafc;
    border: 1rpx solid #e2e8f0;
    border-radius: 12rpx;
    font-size: 26rpx;
    color: #1f2937;
    box-sizing: border-box;
  }

  .suggestion-textarea::placeholder {
    color: #9ca3af;
  }

  .dialog-buttons {
    display: flex;
    gap: 20rpx;
  }

  .dialog-btn {
    flex: 1;
    height: 80rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 40rpx;
    font-size: 28rpx;
    font-weight: 600;
    transition: all 0.2s ease;
  }

  .cancel-btn {
    background-color: #f1f5f9;
    color: #64748b;
  }

  .cancel-btn:active {
    background-color: #e2e8f0;
  }

  .confirm-btn {
    background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
    color: #ffffff;
    box-shadow: 0 4rpx 12rpx rgba(239, 68, 68, 0.3);
  }

  .confirm-btn:active {
    box-shadow: 0 2rpx 8rpx rgba(239, 68, 68, 0.4);
    transform: scale(0.98);
  }
</style>