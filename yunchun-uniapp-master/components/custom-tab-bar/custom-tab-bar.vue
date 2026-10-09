<template>
  <view class="custom-tab-bar">
    <view class="tab-bar-container">
      <view class="tab-item" v-for="(item, index) in tabbarList" :key="index" @click="switchTab(index)">
        <text class="tab-icon" :class="{ 'active': currentIndex === index }">{{ item.iconText }}</text>
        <text class="tab-text" :class="{ 'active': currentIndex === index }">{{ item.text }}</text>
      </view>
    </view>
  </view>
</template>

<script>
import { mapState } from 'vuex'

export default {
  name: 'CustomTabbar',
  computed: {
    ...mapState(['tabbarList', 'currentIndex'])
  },
  created() {
    console.log('CustomTabbar created, tabbarList length:', this.tabbarList.length)
    console.log('CustomTabbar created, tabbarList:', this.tabbarList)
  },
  watch: {
    tabbarList(newVal) {
      console.log('CustomTabbar tabbarList changed:', newVal)
    }
  },
  methods: {
    switchTab(index) {
      if (this.currentIndex === index) return
      
      this.$store.commit('setCurrentIndex', index)
      
      uni.switchTab({
        url: this.tabbarList[index].pagePath
      })
    }
  }
}
</script>

<style scoped>
.custom-tab-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 999;
  background-color: #ffffff;
  border-top: 1rpx solid #f1f5f9;
  padding-bottom: env(safe-area-inset-bottom);
  box-shadow: 0 -2rpx 12rpx rgba(0, 0, 0, 0.04);
}

.tab-bar-container {
  display: flex;
  justify-content: space-around;
  align-items: center;
  height: 100rpx;
}

.tab-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  flex: 1;
  height: 100%;
}

.tab-icon {
  font-size: 48rpx;
  margin-bottom: 4rpx;
  opacity: 0.6;
}

.tab-icon.active {
  opacity: 1;
}

.tab-text {
  font-size: 22rpx;
  color: #94a3b8;
}

.tab-text.active {
  color: #1890ff;
  font-weight: 600;
}
</style>