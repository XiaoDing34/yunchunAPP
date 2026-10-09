<template>
  <view class="device-item" @click="handleCardClick">
    <!-- 第一行：设备名称 -->
    <view class="device-name">{{ name }}</view>
    
    <!-- 第二行：位置 + 查看组态 -->
    <view class="device-row">
      <view class="location">
        <text class="location-icon">📍</text>
        <text class="location-text">{{ value }}</text>
      </view>
      <view class="config-btn" @click.stop="handleConfigClick">
        <text class="btn-text">查看组态</text>
        <text class="btn-arrow">→</text>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  name: 'DeviceItem',
  props: {
    id: {
      type: [String, Number],
      default: ''
    },
    name: {
      type: String,
      default: ''
    },
    value: {
      type: String,
      default: ''
    }
  },
  methods: {
    handleCardClick() {
      this.$emit('click', {
        id: this.id,
        name: this.name,
        value: this.value
      });
    },
    handleConfigClick() {
      this.$emit('config-click', {
        id: this.id,
        name: this.name,
        value: this.value
      });
    }
  }
};
</script>

<style lang="scss" scoped>
.device-item {
  background: #FFFFFF;
  border-radius: 20px;
  padding: 16px;
  margin: 12px 16px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.03), 0 4px 12px rgba(0, 0, 0, 0.02);
  transition: background 0.2s;
  
  &:active {
    background: #F9FAFB;
  }
}

.device-name {
  font-size: 18px;
  font-weight: 600;
  color: #0B1120;
  margin-bottom: 12px;
}

.device-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.location {
  display: flex;
  align-items: center;
}

.location-icon {
  font-size: 14px;
  margin-right: 4px;
}

.location-text {
  font-size: 14px;
  font-weight: 400;
  color: #4B5563;
}

.config-btn {
  display: flex;
  align-items: center;
}

.btn-text {
  font-size: 14px;
  font-weight: 500;
  color: #2563EB;
}

.btn-arrow {
  font-size: 14px;
  color: #2563EB;
  margin-left: 4px;
}
</style>
