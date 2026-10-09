<template>
  <view class="alarm-manage-item" :class="'level-' + warnLevelType">
    <view class="item-indicator"></view>
    <view class="item-content">
      <!-- 顶部：等级 + 时间 -->
      <view class="item-header">
        <view class="level-badge" :class="'badge-' + warnLevelType">{{ warnLevel }}</view>
        <text class="item-time">{{ formatTime(createTime) }}</text>
      </view>

      <!-- 报警内容 -->
      <view class="item-row">
        <text class="row-label">报警内容</text>
        <text class="row-value">{{ messageContent || '-' }}</text>
      </view>

      <!-- 报警来源 -->
      <view class="item-row">
        <text class="row-label">报警来源</text>
        <text class="row-value">{{ sender || '-' }}</text>
      </view>

      <!-- 报警时间 -->
      <view class="item-row">
        <text class="row-label">报警时间</text>
        <text class="row-value">{{ formatFullTime(createTime) }}</text>
      </view>

      <view class="item-divider"></view>

      <!-- 是否有效 -->
      <view class="item-row">
        <text class="row-label">是否有效</text>
        <view class="status-tag" :class="validTagClass">{{ validText }}</view>
      </view>

      <!-- 是否处理 -->
      <view class="item-row">
        <text class="row-label">是否处理</text>
        <view class="status-tag" :class="handleTagClass">{{ handleText }}</view>
      </view>

      <!-- 处理人 -->
      <view class="item-row">
        <text class="row-label">处理人</text>
        <text class="row-value row-value-highlight">{{ handlerName || '-' }}</text>
      </view>
    </view>
  </view>
</template>

<script>
  export default {
    name: 'AlarmManageItem',
    props: {
      warnLevel: {
        type: String,
        default: '提示'
      },
      messageContent: {
        type: String,
        default: ''
      },
      sender: {
        type: String,
        default: ''
      },
      createTime: {
        type: [String, Number, Date],
        default: ''
      },
      feedbackStatus: {
        type: [String, Number],
        default: 0
      },
      handleStatus: {
        type: [String, Number],
        default: 0
      },
      handlerName: {
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
        return levelMap[this.warnLevel] || 'warning';
      },
      feedbackStatusNum() {
        return Number(this.feedbackStatus) || 0;
      },
      handleStatusNum() {
        return Number(this.handleStatus) || 0;
      },
      validText() {
        if (this.feedbackStatusNum === 1) return '有效';
        if (this.feedbackStatusNum === 2) return '无效';
        return '未反馈';
      },
      validTagClass() {
        if (this.feedbackStatusNum === 1) return 'tag-valid';
        if (this.feedbackStatusNum === 2) return 'tag-invalid';
        return 'tag-pending';
      },
      handleText() {
        return this.handleStatusNum === 1 ? '已处理' : '未处理';
      },
      handleTagClass() {
        return this.handleStatusNum === 1 ? 'tag-processed' : 'tag-pending';
      }
    },
    methods: {
      formatTime(time) {
        if (!time) return '-';
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
      }
    }
  };
</script>

<style scoped>
  .alarm-manage-item {
    position: relative;
    border-radius: 20rpx;
    background-color: #ffffff;
    margin: 0 auto 24rpx;
    padding: 24rpx;
    padding-left: 32rpx;
    overflow: hidden;
    box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.06);
  }

  .item-indicator {
    position: absolute;
    left: 0;
    top: 0;
    bottom: 0;
    width: 10rpx;
  }

  /* 紧急 - 红色 */
  .level-urgent .item-indicator { background-color: #DC2626; }
  /* 重要 - 橙色 */
  .level-important .item-indicator { background-color: #EA580C; }
  /* 提示 - 黄色 */
  .level-warning .item-indicator { background-color: #EAB308; }

  .item-content {
    flex: 1;
  }

  .item-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16rpx;
  }

  .level-badge {
    padding: 4rpx 16rpx;
    border-radius: 20rpx;
    font-size: 22rpx;
    font-weight: 600;
    color: #fff;
  }

  .badge-urgent { background-color: #DC2626; }
  .badge-important { background-color: #EA580C; }
  .badge-warning { background-color: #EAB308; }

  .item-time {
    font-size: 26rpx;
    color: #9CA3AF;
  }

  .item-row {
    display: flex;
    align-items: center;
    margin-bottom: 12rpx;
  }

  .row-label {
    font-size: 26rpx;
    color: #6B7280;
    flex-shrink: 0;
    width: 140rpx;
  }

  .row-value {
    font-size: 28rpx;
    color: #1F2937;
    flex: 1;
    line-height: 1.5;
  }

  .row-value-highlight {
    color: #3B82F6;
    font-weight: 600;
  }

  .item-divider {
    height: 1rpx;
    background-color: #F1F5F9;
    margin: 16rpx 0;
  }

  .status-tag {
    padding: 8rpx 24rpx;
    border-radius: 30rpx;
    font-size: 26rpx;
    font-weight: 600;
    color: #fff;
  }

  .tag-valid { background-color: #10B981; }
  .tag-invalid { background-color: #EF4444; }
  .tag-processed { background-color: #3B82F6; }
  .tag-pending { background-color: #9CA3AF; }
</style>
