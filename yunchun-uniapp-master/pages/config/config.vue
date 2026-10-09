<template>
  <view class="config-page">
    <view v-if="deviceList.length > 0" class="device-list">
      <device-item v-for="(item, index) in deviceList" :key="'device-' + index" :id="item.id" :name="item.notes"
        :value="item.locationVar" @click="handleDeviceClick" @config-click="handleConfigClick" />
    </view>
    <view v-else class="empty-tip">
      <text>暂无设备数据</text>
    </view>

    <CustomTabbar />
  </view>
</template>

<script>
  import api from '@/untils/request.js';
  import DeviceItem from '@/compoents/device-item/device-item.vue';
  import CustomTabBar from '@/components/custom-tab-bar/custom-tab-bar.vue';

  export default {
    name: 'ConfigPage',
    components: {
      DeviceItem,
      CustomTabbar: CustomTabBar
    },
    data() {
      return {
        deviceList: []
      };
    },
    onLoad() {
      this.getDeviceList();
    },
    methods: {
      async getDeviceList() {
        try {
          const res = await api.get('/device/getDeviceById');
          if (res && res.data) {
            this.deviceList = res.data;
          }
        } catch (error) {
          console.error('获取设备列表失败:', error);
          uni.showToast({
            title: '获取设备列表失败',
            icon: 'none'
          });
        }
      },
      getIcon(notes) {
        if (!notes) return '⚙️';
        if (notes.includes('锅炉')) return '🔥';
        if (notes.includes('泵')) return '⚙️';
        if (notes.includes('风机')) return '💨';
        return '⚙️';
      },
      handleDeviceClick(data) {
        console.log('点击设备:', data);
      },
      handleConfigClick(data) {
        console.log('查看组态:', data);
        // 跳转到组态模块页面，传递 deviceId
        uni.navigateTo({
          url: '/subpkg/config-module/config-module?deviceId=' + data.id
        });
      }
    }
  };
</script>

<style scoped>
  .config-page {
    min-height: 100vh;
    background: #F5F7FA;
    padding-top: 8px;
    padding-bottom: 140rpx;
  }

  .device-list {
    padding-bottom: 20px;
  }

  .empty-tip {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 300px;
    color: #999;
    font-size: 14px;
  }
</style>