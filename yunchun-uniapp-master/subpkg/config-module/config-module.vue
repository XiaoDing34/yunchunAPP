<template>
  <view class="config-page">
    <view class="module-list">
      <config-item v-for="(module, index) in modules" :key="index" :config-name="module"
        @item-click="handleItemClick"></config-item>
    </view>
  </view>
</template>

<script>
  import ConfigItem from '../../compoents/config-item/config-item.vue';

  export default {
    name: 'config-page',
    components: {
      ConfigItem
    },
    data() {
      return {
        deviceId: '',
        modules: [
          '上料模块',
          '燃烧模块',
          '水系统模块',
          '除渣模块'
        ]
      };
    },
    onLoad(options) {
      if (options.deviceId) {
        this.deviceId = options.deviceId;
        console.log('设备ID:', this.deviceId);
      }
    },
    methods: {
      handleItemClick(data) {
        const moduleName = data.configName;
        uni.navigateTo({
          url: `/subpkg/config-detail/config-detail?moduleName=${encodeURIComponent(moduleName)}`
        });
      }
    }
  };
</script>

<style scoped>
  .config-page {
    min-height: 100vh;
    background: #F5F7FA;
    padding: 20rpx;
  }

  .module-list {
    display: flex;
    flex-direction: column;
    gap: 24rpx;
  }
</style>