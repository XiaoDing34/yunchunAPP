<template>
  <view class="config-item" @click="handleClick">
    <view class="item-content">
      <view class="icon-wrapper">
        <text class="icon">{{ getIconForModule(configName) }}</text>
      </view>
      <text class="config-name">{{ configName }}</text>
    </view>
  </view>
</template>

<script>
  export default {
    name: 'config-item',
    props: {
      configName: {
        type: String,
        default: ''
      }
    },
    methods: {
      getIconForModule(moduleName) {
        const iconMap = {
          '主机模块': '⚙️',
          '上料模块': '🚚',
          '脱硝模块': '🌿',
          '水系统模块': '🚰',
          '燃烧模块': '🔥',
          '除渣模块': '🏭'
        };
        return iconMap[moduleName] || '⚙️';
      },
      handleClick() {
        this.$emit('item-click', { configName: this.configName });
      }
    }
  };
</script>

<style scoped>
  .config-item {
    background: #FFFFFF;
    border-radius: 32rpx;
    padding: 40rpx 32rpx;
    margin-bottom: 24rpx;
    box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.05);
    border: 1rpx solid rgba(0, 0, 0, 0.03);
    transition: all 0.3s ease;
    position: relative;
    overflow: hidden;
  }

  .config-item:active {
    transform: translateY(-4rpx);
    box-shadow: 0 16rpx 40rpx rgba(45, 106, 255, 0.1);
    border-color: #2D6AFF;
  }

  .item-content {
    display: flex;
    align-items: center;
    gap: 24rpx;
  }

  .icon-wrapper {
    width: 80rpx;
    height: 80rpx;
    background: linear-gradient(135deg, #F5F7FA 0%, #FFFFFF 100%);
    border-radius: 24rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 0 2rpx 8rpx rgba(45, 106, 255, 0.08);
  }

  .icon {
    font-size: 48rpx;
    filter: drop-shadow(0 2rpx 4rpx rgba(45, 106, 255, 0.2));
  }

  .config-name {
    font-size: 40rpx;
    font-weight: 500;
    color: #1E293B;
    letter-spacing: 0.5rpx;
    flex: 1;
  }
</style>
