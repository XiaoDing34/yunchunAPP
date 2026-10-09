<template>
  <view class="config-detail-page">
    <view class="page-header" :class="moduleClass">
      <view class="header-content">
        <text class="header-icon">{{ moduleIcon }}</text>
        <text class="page-title">{{ moduleName }}</text>
      </view>
      <text class="device-count">{{ deviceList.length }} 个设备</text>
    </view>
    <view class="device-list">
      <config-detail-item v-for="(device, index) in deviceList" :key="index" :device-name="device.name"
        :frequency="device.frequency" :status="device.status" @start="handleStart"
        @stop="handleStop"></config-detail-item>
    </view>
  </view>
</template>

<script>
  import api from '@/untils/request.js';
  import ConfigDetailItem from '../../compoents/config-detail-item/config-detail-item.vue';

  export default {
    name: 'config-detail',
    components: {
      ConfigDetailItem
    },
    data() {
      return {
        moduleName: '',
        deviceId: '',
        deviceList: []
      };
    },
    computed: {
      moduleIcon() {
        const iconMap = {
          '上料模块': '🚚',
          '水系统模块': '💧',
          '燃烧模块': '🔥',
          '除渣模块': '🏭'
        };
        return iconMap[this.moduleName] || '📋';
      },
      moduleClass() {
        const classMap = {
          '上料模块': 'module-feed',
          '水系统模块': 'module-water',
          '燃烧模块': 'module-burn',
          '除渣模块': 'module-slag'
        };
        return classMap[this.moduleName] || 'module-default';
      }
    },
    onLoad(options) {
      if (options.deviceId) {
        this.deviceId = options.deviceId;
        this.loadDeviceByDeviceId();
      } else if (options.moduleName) {
        this.moduleName = decodeURIComponent(options.moduleName);
        this.loadDeviceList();
      }
    },
    methods: {
      loadDeviceByDeviceId() {
        const deviceConfigMap = {
          '1': {
            moduleName: '锅炉设备',
            devices: [{
                name: '引风机',
                frequency: 50,
                status: 'stopped'
              },
              {
                name: '鼓风机',
                frequency: 45,
                status: 'stopped'
              },
              {
                name: '炉排电机',
                frequency: 25,
                status: 'stopped'
              },
              {
                name: '点火器',
                frequency: 0,
                status: 'stopped'
              }
            ]
          },
          '2': {
            moduleName: '水系统',
            devices: [{
                name: '循环泵',
                frequency: 48,
                status: 'stopped'
              },
              {
                name: '补水泵',
                frequency: 22,
                status: 'stopped'
              },
              {
                name: '泄压电磁阀',
                frequency: 0,
                status: 'stopped'
              }
            ]
          },
          '3': {
            moduleName: '燃烧系统',
            devices: [{
                name: '点火风机',
                frequency: 38,
                status: 'stopped'
              },
              {
                name: '控料器',
                frequency: 15,
                status: 'stopped'
              }
            ]
          }
        };

        const config = deviceConfigMap[this.deviceId] || {
          moduleName: '未知设备',
          devices: []
        };
        this.moduleName = config.moduleName;
        this.deviceList = config.devices;
      },
      loadDeviceList() {
        const moduleDeviceMap = {
          '上料模块': [{
              name: '上料机',
              frequency: 0,
              status: 'stopped'
            },
            {
              name: '拨料机',
              frequency: 0,
              status: 'stopped'
            }
          ],
          '燃烧模块': [{
              name: '点火器',
              frequency: 0,
              status: 'stopped'
            },
            {
              name: '点火风机',
              frequency: 0,
              status: 'stopped'
            },
            {
              name: '鼓风机',
              frequency: 0,
              status: 'stopped'
            },
            {
              name: '引风机',
              frequency: 0,
              status: 'stopped'
            },
            {
              name: '炉排电机',
              frequency: 0,
              status: 'stopped'
            }
          ],
          '水系统模块': [{
              name: '补水泵',
              frequency: 0,
              status: 'stopped'
            },
            {
              name: '循环泵',
              frequency: 0,
              status: 'stopped'
            },
            {
              name: '泄压阀',
              frequency: 0,
              status: 'stopped'
            }
          ],
          '除渣模块': [{
            name: '出渣机',
            frequency: 0,
            status: 'stopped'
          }]
        };

        this.deviceList = moduleDeviceMap[this.moduleName] || [];
      },
      async handleStart(data) {
        console.log('启动设备:', data.deviceName);
        try {
          const res = await api.post('/deviceValue/toggleByName', {
            deviceId: 116,
            valueName: data.deviceName
          });
          if (res.code === 200) {
            uni.showToast({
              title: '启动成功',
              icon: 'success'
            });
            const device = this.deviceList.find(d => d.name === data.deviceName);
            if (device) {
              device.status = 'running';
              device.frequency = 50;
            }
          }
        } catch (error) {
          console.error('启动失败:', error);
          uni.showToast({
            title: '启动失败',
            icon: 'none'
          });
        }
      },
      async handleStop(data) {
        console.log('停止设备:', data.deviceName);
        try {
          const res = await api.post('/deviceValue/toggleByName', {
            deviceId: 116,
            valueName: data.deviceName
          });
          if (res.code === 200) {
            uni.showToast({
              title: '暂停成功',
              icon: 'success'
            });
            const device = this.deviceList.find(d => d.name === data.deviceName);
            if (device) {
              device.status = 'stopped';
              device.frequency = 0;
            }
          }
        } catch (error) {
          console.error('暂停失败:', error);
          uni.showToast({
            title: '暂停失败',
            icon: 'none'
          });
        }
      }
    }
  };
</script>

<style scoped>
  .config-detail-page {
    min-height: 100vh;
    background: #F5F7FA;
  }

  .page-header {
    padding: 60rpx 40rpx 80rpx;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    position: relative;
    overflow: hidden;
  }

  .page-header::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    opacity: 0.15;
    z-index: 0;
  }

  .module-feed {
    background: linear-gradient(135deg, #F093FB 0%, #F5576C 100%);
  }

  .module-feed::before {
    background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 200 200"><rect x="20" y="80" width="160" height="40" rx="20" fill="none" stroke="white" stroke-width="3" opacity="0.3"/><circle cx="50" cy="100" r="15" fill="none" stroke="white" stroke-width="2" opacity="0.3"/><circle cx="150" cy="100" r="15" fill="none" stroke="white" stroke-width="2" opacity="0.3"/></svg>') center center no-repeat;
    background-size: cover;
  }

  .module-water {
    background: linear-gradient(135deg, #89F7FE 0%, #66A6FF 100%);
  }

  .module-water::before {
    background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 200 200"><path d="M40 140 Q100 60 160 140" fill="none" stroke="white" stroke-width="4" opacity="0.3"/><path d="M40 160 Q100 80 160 160" fill="none" stroke="white" stroke-width="3" opacity="0.2"/><path d="M40 180 Q100 100 160 180" fill="none" stroke="white" stroke-width="2" opacity="0.1"/></svg>') center center no-repeat;
    background-size: cover;
  }

  .module-burn {
    background: linear-gradient(135deg, #FA709A 0%, #FEE140 100%);
  }

  .module-burn::before {
    background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 200 200"><path d="M100 30 Q130 80 130 110 Q130 150 100 170 Q70 150 70 110 Q70 80 100 30" fill="none" stroke="white" stroke-width="3" opacity="0.3"/><path d="M100 50 Q120 90 120 110 Q120 140 100 155 Q80 140 80 110 Q80 90 100 50" fill="none" stroke="white" stroke-width="2" opacity="0.2"/></svg>') center center no-repeat;
    background-size: cover;
  }

  .module-slag {
    background: linear-gradient(135deg, #A8EDEA 0%, #FED6E3 100%);
  }

  .module-slag::before {
    background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 200 200"><rect x="30" y="60" width="60" height="80" rx="5" fill="none" stroke="white" stroke-width="3" opacity="0.3"/><rect x="110" y="60" width="60" height="80" rx="5" fill="none" stroke="white" stroke-width="3" opacity="0.3"/><rect x="70" y="100" width="60" height="40" rx="5" fill="none" stroke="white" stroke-width="2" opacity="0.2"/></svg>') center center no-repeat;
    background-size: cover;
  }

  .module-default {
    background: linear-gradient(135deg, #2D6AFF 0%, #5B8DEF 100%);
  }

  .header-content {
    display: flex;
    align-items: center;
    gap: 20rpx;
    z-index: 1;
  }

  .header-icon {
    font-size: 56rpx;
    line-height: 1;
  }

  .page-title {
    font-size: 48rpx;
    font-weight: 700;
    color: #FFFFFF;
    letter-spacing: 2rpx;
    text-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.15);
  }

  .device-count {
    margin-top: 16rpx;
    font-size: 26rpx;
    font-weight: 400;
    color: rgba(255, 255, 255, 0.85);
    z-index: 1;
  }

  .device-list {
    padding: 24rpx 0;
    margin-top: -40rpx;
    position: relative;
    z-index: 2;
  }
</style>