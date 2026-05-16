<template>
  <view>
    <view class="warn-item" :class="'level-' + warnLevelType">
      <view class="warn-indicator"></view>
      <view class="warn-content">
        <view class="warn-top">
          <view class="warn-title">
            <text class="warn-icon">{{ getIcon() }}</text>
            <text class="warn-title-text">{{ messageTitle }}</text>
            <text class="warn-badge" :class="'badge-' + warnLevelType">{{ warnLevel }}</text>
          </view>
          <view class="warn-meta">
            <view class="warn-source">
              <image class="source-icon" src="/static/icons/message-user.svg" mode="aspectFit"></image>
              <text class="source-text">来源：{{ sender }}</text>
            </view>
            <text class="warn-time">{{ formatTime(createTime) }}</text>
          </view>
        </view>
        <view class="warn-divider"></view>
        <view class="warn-bottom">
          <view class="warn-detail-btn" @click="handleDetailClick">
            <text class="btn-text">查看详情</text>
            <text class="btn-arrow">→</text>
          </view>
        </view>
      </view>
    </view>
    
    <!-- 报警详情弹窗 -->
    <uni-popup ref="detailPopup" type="center" :animation="true">
      <view class="detail-popup">
        <!-- 顶部红色警灯条 -->
        <view class="detail-top-bar" :class="'bar-' + warnLevelType"></view>
        
        <!-- 1. 顶部栏 -->
        <view class="detail-header">
          <view class="detail-header-left">
            <view class="detail-level-badge" :class="'level-' + warnLevelType">
              <text class="level-icon">❗</text>
              <text class="level-text">{{ warnLevel }}</text>
            </view>
          </view>
          <view class="detail-header-right">
            <text class="detail-time">{{ formatFullTime(createTime) }}</text>
          </view>
        </view>
        
        <!-- 2. 主告警区 -->
        <view class="detail-alert-area" :class="'alert-' + warnLevelType">
          <view class="alert-indicator"></view>
          <view class="alert-content">
            <text class="alert-icon">🔥</text>
            <view class="alert-text">
              <text class="alert-title">{{ messageTitle }}</text>
              <text class="alert-subtitle">触发紧急报警，需立即关注</text>
            </view>
          </view>
        </view>
        
        <!-- 3. 详情区 -->
        <view class="detail-info-section">
          <view class="info-row">
            <text class="info-icon">📋</text>
            <view class="info-content">
              <text class="info-label">报警内容</text>
              <text class="info-value">{{ messageContent || '-' }}</text>
            </view>
          </view>
          <view class="info-divider"></view>
          <view class="info-row">
            <text class="info-icon">⚠️</text>
            <view class="info-content">
              <text class="info-label">报警原因</text>
              <view class="info-value-wrap">
                <text class="reason-badge" :class="'reason-' + warnLevelType">{{ messageTitle }}</text>
              </view>
            </view>
          </view>
          <view class="info-divider"></view>
          <view class="info-row">
            <text class="info-icon">🏭</text>
            <view class="info-content">
              <text class="info-label">报警来源</text>
              <text class="info-value">{{ sender || '-' }}</text>
            </view>
          </view>
        </view>
        
        <!-- 4. 操作建议区 -->
        <view class="detail-advice-section" v-if="advice">
          <view class="advice-header">
            <text class="advice-icon">⚙️</text>
            <text class="advice-title">操作建议</text>
          </view>
          <view class="advice-content">
            <text class="advice-text">{{ advice }}</text>
          </view>
        </view>
        
        <!-- 5. 关闭按钮 -->
        <view class="detail-footer">
          <view class="detail-close-btn" @click="closeDetailPopup">关闭</view>
        </view>
      </view>
    </uni-popup>
  </view>
</template>

<script>
  export default {
    props: {
      warnLevel: {
        type: String,
        default: '提示',
        validator: (val) => ['紧急', '重要', '提示'].includes(val)
      },
      messageTitle: {
        type: String,
        default: ''
      },
      sender: {
        type: String,
        default: ''
      },
      createTime: {
        type: String,
        default: ''
      },
      messageContent: {
        type: String,
        default: ''
      },
      advice: {
        type: String,
        default: ''
      }
    },
    computed: {
      warnLevelType() {
        const levelMap = {
          '紧急': 'urgent',
          '重要': 'important',
          '提示': 'warning'
        };
        return levelMap[this.warnLevel];
      }
    },
    methods: {
      formatTime(time) {
        if (!time) return '';
        const date = new Date(time);
        if (isNaN(date.getTime())) return time;
        const month = (date.getMonth() + 1).toString().padStart(2, '0');
        const day = date.getDate().toString().padStart(2, '0');
        const hours = date.getHours().toString().padStart(2, '0');
        const minutes = date.getMinutes().toString().padStart(2, '0');
        return `${month}-${day} ${hours}:${minutes}`;
      },
      formatFullTime(time) {
        if (!time) return '-';
        const date = new Date(time);
        if (isNaN(date.getTime())) return time;
        const year = date.getFullYear();
        const month = (date.getMonth() + 1).toString().padStart(2, '0');
        const day = date.getDate().toString().padStart(2, '0');
        const hours = date.getHours().toString().padStart(2, '0');
        const minutes = date.getMinutes().toString().padStart(2, '0');
        return `${year}-${month}-${day} ${hours}:${minutes}`;
      },
      getIcon() {
        const iconMap = {
          urgent: '⚠️',
          important: '⚡',
          warning: '🔔'
        };
        return iconMap[this.warnLevelType] || '🔔';
      },
      handleDetailClick() {
        this.$refs.detailPopup.open();
      },
      closeDetailPopup() {
        this.$refs.detailPopup.close();
      }
    }
  };
</script>

<style scoped>
  .warn-item {
    position: relative;
    border-radius: 24rpx;
    width: 92%;
    margin: 0 auto 24rpx;
    padding: 28rpx 24rpx;
    padding-left: 36rpx;
    overflow: hidden;
  }

  .warn-indicator {
    position: absolute;
    left: 0;
    top: 0;
    bottom: 0;
    width: 12rpx;
  }

  /* 紧急报警 - 红色 */
  .level-urgent {
    background-color: #FEF2F2;
    box-shadow: 0 4rpx 12rpx rgba(220, 38, 38, 0.15);
  }

  .level-urgent .warn-indicator {
    background-color: #DC2626;
  }

  .level-urgent .warn-title-text {
    color: #DC2626;
  }

  .level-urgent .warn-icon {
    tint-color: #DC2626;
  }

  .level-urgent .warn-badge {
    background-color: #DC2626;
    color: #fff;
  }

  .level-urgent .warn-detail-btn {
    background-color: #FEE2E2;
  }

  .level-urgent .btn-text,
  .level-urgent .btn-arrow {
    color: #DC2626;
  }

  /* 重要报警 - 橙色 */
  .level-important {
    background-color: #FFF7ED;
    box-shadow: 0 4rpx 12rpx rgba(234, 88, 12, 0.12);
  }

  .level-important .warn-indicator {
    background-color: #EA580C;
  }

  .level-important .warn-title-text {
    color: #EA580C;
  }

  .level-important .warn-icon {
    tint-color: #EA580C;
  }

  .level-important .warn-badge {
    background-color: #EA580C;
    color: #fff;
  }

  .level-important .warn-detail-btn {
    background-color: #FED7AA;
  }

  .level-important .btn-text,
  .level-important .btn-arrow {
    color: #EA580C;
  }

  /* 一般提示 - 黄色 */
  .level-warning {
    background-color: #FEFCE8;
    box-shadow: 0 4rpx 12rpx rgba(234, 179, 8, 0.1);
  }

  .level-warning .warn-indicator {
    background-color: #EAB308;
  }

  .level-warning .warn-title-text {
    color: #B45309;
  }

  .level-warning .warn-icon {
    tint-color: #EAB308;
  }

  .level-warning .warn-badge {
    background-color: #EAB308;
    color: #fff;
  }

  .level-warning .warn-detail-btn {
    background-color: #FEF3C7;
  }

  .level-warning .btn-text,
  .level-warning .btn-arrow {
    color: #B45309;
  }

  .warn-content {
    position: relative;
  }

  .warn-top {
    margin-bottom: 24rpx;
  }

  .warn-title {
    display: flex;
    align-items: center;
    gap: 12rpx;
    flex-wrap: wrap;
  }

  .warn-icon {
    font-size: 48rpx;
    line-height: 1;
  }

  .warn-title-text {
    font-size: 34rpx;
    font-weight: 700;
    color: #1F2937;
  }

  .warn-badge {
    display: inline-block;
    padding: 4rpx 12rpx;
    border-radius: 30rpx;
    font-size: 22rpx;
    font-weight: 600;
    margin-left: 12rpx;
  }

  .warn-meta {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-top: 16rpx;
  }

  .warn-source {
    display: flex;
    align-items: center;
    gap: 8rpx;
  }

  .source-icon {
    width: 32rpx;
    height: 32rpx;
    vertical-align: middle;
    margin-right: 8rpx;
  }

  .source-text {
    font-size: 28rpx;
    color: #6B7280;
  }

  .warn-time {
    font-size: 28rpx;
    color: #9CA3AF;
  }

  .warn-divider {
    height: 1rpx;
    background-color: #F1F5F9;
    margin-bottom: 20rpx;
  }

  .warn-bottom {
    display: flex;
    justify-content: flex-end;
  }

  .warn-detail-btn {
    display: flex;
    align-items: center;
    gap: 8rpx;
    padding: 10rpx 20rpx;
    border-radius: 40rpx;
    transition: all 0.2s ease;
  }

  .warn-detail-btn:active {
    opacity: 0.8;
    transform: scale(0.98);
  }

  .btn-text {
    font-size: 26rpx;
    font-weight: 500;
  }

  .btn-arrow {
    font-size: 24rpx;
  }

  /* 弹窗样式 */
  .detail-popup {
    width: 680rpx;
    background-color: #ffffff;
    border-radius: 24rpx;
    overflow: hidden;
    box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.1);
  }

  /* 顶部警灯条 */
  .detail-top-bar {
    height: 12rpx;
  }

  .bar-urgent {
    background-color: #D32F2F;
    box-shadow: 0 0 12rpx rgba(211, 47, 47, 0.6);
  }

  .bar-important {
    background-color: #EA580C;
    box-shadow: 0 0 12rpx rgba(234, 88, 12, 0.6);
  }

  .bar-warning {
    background-color: #EAB308;
    box-shadow: 0 0 12rpx rgba(234, 179, 8, 0.6);
  }

  /* 1. 顶部栏 */
  .detail-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 24rpx 28rpx;
  }

  .detail-header-left {
    display: flex;
    align-items: center;
  }

  .detail-level-badge {
    display: flex;
    align-items: center;
    gap: 8rpx;
    padding: 8rpx 16rpx;
    border-radius: 30rpx;
    box-shadow: inset 0 2rpx 4rpx rgba(0, 0, 0, 0.2);
  }

  .detail-level-badge.level-urgent {
    background-color: #D32F2F;
  }

  .detail-level-badge.level-important {
    background-color: #EA580C;
  }

  .detail-level-badge.level-warning {
    background-color: #EAB308;
  }

  .level-icon {
    font-size: 24rpx;
  }

  .level-text {
    font-size: 24rpx;
    font-weight: 600;
    color: #ffffff;
  }

  .detail-header-right {
    text-align: right;
  }

  .detail-time {
    font-size: 26rpx;
    color: #6C7A8E;
  }

  /* 2. 主告警区 */
  .detail-alert-area {
    display: flex;
    margin: 0 24rpx;
    padding: 24rpx;
    border-radius: 16rpx;
    overflow: hidden;
  }

  .detail-alert-area.alert-urgent {
    background-color: #FFF5F5;
  }

  .detail-alert-area.alert-important {
    background-color: #FFF7ED;
  }

  .detail-alert-area.alert-warning {
    background-color: #FFFBEB;
  }

  .alert-indicator {
    width: 8rpx;
    border-radius: 4rpx;
    margin-right: 20rpx;
    flex-shrink: 0;
  }

  .alert-urgent .alert-indicator {
    background-color: #D32F2F;
  }

  .alert-important .alert-indicator {
    background-color: #EA580C;
  }

  .alert-warning .alert-indicator {
    background-color: #EAB308;
  }

  .alert-content {
    display: flex;
    align-items: flex-start;
    gap: 16rpx;
    flex: 1;
  }

  .alert-icon {
    font-size: 48rpx;
    flex-shrink: 0;
  }

  .alert-text {
    flex: 1;
  }

  .alert-title {
    font-size: 36rpx;
    font-weight: 700;
    display: block;
    line-height: 1.4;
  }

  .alert-urgent .alert-title {
    color: #D32F2F;
  }

  .alert-important .alert-title {
    color: #EA580C;
  }

  .alert-warning .alert-title {
    color: #B45309;
  }

  .alert-subtitle {
    font-size: 24rpx;
    color: #6C7A8E;
    display: block;
    margin-top: 8rpx;
  }

  /* 3. 详情区 */
  .detail-info-section {
    margin: 24rpx;
    padding: 20rpx;
    background-color: #FAFBFC;
    border-radius: 16rpx;
  }

  .info-row {
    display: flex;
    align-items: flex-start;
    gap: 16rpx;
    padding: 12rpx 0;
  }

  .info-icon {
    font-size: 32rpx;
    flex-shrink: 0;
  }

  .info-content {
    flex: 1;
  }

  .info-label {
    font-size: 24rpx;
    color: #6C7A8E;
    display: block;
    margin-bottom: 6rpx;
  }

  .info-value {
    font-size: 28rpx;
    color: #1E2A3E;
    font-weight: 500;
  }

  .info-value-wrap {
    display: flex;
    align-items: center;
  }

  .reason-badge {
    padding: 6rpx 14rpx;
    border-radius: 8rpx;
    font-size: 26rpx;
    font-weight: 600;
  }

  .reason-urgent {
    background-color: #D32F2F;
    color: #ffffff;
  }

  .reason-important {
    background-color: #EA580C;
    color: #ffffff;
  }

  .reason-warning {
    background-color: #EAB308;
    color: #ffffff;
  }

  .info-divider {
    height: 1rpx;
    background-color: #E8ECF0;
    margin: 8rpx 0;
  }

  /* 4. 操作建议区 */
  .detail-advice-section {
    margin: 0 24rpx 24rpx;
    padding: 20rpx;
    background-color: #F5F7FA;
    border-radius: 16rpx;
  }

  .advice-header {
    display: flex;
    align-items: center;
    gap: 10rpx;
    margin-bottom: 12rpx;
  }

  .advice-icon {
    font-size: 28rpx;
  }

  .advice-title {
    font-size: 26rpx;
    font-weight: 600;
    color: #1E2A3E;
  }

  .advice-content {
    background-color: #ffffff;
    padding: 16rpx;
    border-radius: 12rpx;
  }

  .advice-text {
    font-size: 28rpx;
    color: #1E2A3E;
    line-height: 1.6;
  }

  /* 5. 关闭按钮 */
  .detail-footer {
    padding: 16rpx 28rpx 28rpx;
    display: flex;
    justify-content: flex-end;
  }

  .detail-close-btn {
    padding: 14rpx 40rpx;
    border: 2rpx solid #D0D5DD;
    border-radius: 40rpx;
    font-size: 28rpx;
    color: #6C7A8E;
    font-weight: 500;
    transition: all 0.2s ease;
  }

  .detail-close-btn:active {
    border-color: #D32F2F;
    color: #D32F2F;
    background-color: #FFF5F5;
  }
</style>