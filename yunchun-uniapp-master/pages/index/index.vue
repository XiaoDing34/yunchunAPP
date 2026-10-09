<template>
  <view class="container">
    <!-- 报警信息滚动条（固定在屏幕顶部） -->
    <view v-if="warnCount > 0" class="warn-banner" @click="goToAlarm">
      <text class="warn-icon">🔔</text>
      <view class="warn-scroll-wrap">
        <view class="warn-scroll-inner">
          <text class="warn-scroll-text">您有 {{ warnCount }} 条报警信息，请尽快查看！</text>
          <text class="warn-scroll-text">您有 {{ warnCount }} 条报警信息，请尽快查看！</text>
        </view>
      </view>
      <text class="warn-arrow">›</text>
    </view>
    <!-- 占位元素，防止内容被固定滚动条遮挡 -->
    <view v-if="warnCount > 0" class="warn-banner-placeholder"></view>
    <scroll-view class="tabs-wrapper" scroll-x="true" enable-flex="true">
      <view class="tabs-container">
        <view v-for="item in dimensions" :key="item.id" class="tab-item" :class="{ active: activeIndex === item.id }"
          @click="switchTab(item.id)">
          <text class="tab-text">{{ item.name }}</text>
          <view v-if="activeIndex === item.id" class="tab-indicator"></view>
        </view>
      </view>
    </scroll-view>
    <view class="content">
      <!-- 综合信息卡片 -->
      <view class="header-card">
        <!-- 上半部分：设备信息 -->
        <view class="card-top">
          <view class="header-left">
            <view class="header-title">{{ currentDevice.name }}</view>
            <view class="header-subtitle">实时监控数据</view>
            <view class="header-status" :class="statusClass">
              <text class="status-dot"></text>
              <text class="status-text">{{ currentDevice.state }}</text>
            </view>
          </view>
          <view class="header-right">
            <view class="weather-box">
              <view class="weather-main">
                <text class="weather-icon">{{ weatherIcon }}</text>
                <text class="weather-desc">{{ weatherDesc || '--' }}</text>
              </view>
              <view class="weather-detail">
                <view class="weather-item">
                  <text class="weather-value">{{ temp ? temp + '°C' : '--' }}</text>
                  <text class="weather-label">温度</text>
                </view>
                <view class="weather-divider"></view>
                <view class="weather-item">
                  <text class="weather-value">{{ humidity ? humidity + '%' : '--' }}</text>
                  <text class="weather-label">空气湿度</text>
                </view>
              </view>
            </view>
          </view>
        </view>

        <!-- 分隔线 -->
        <view class="card-divider"></view>

        <!-- 下半部分：能源统计 -->
        <view class="card-bottom">
          <view class="stats-header">
            <text class="stats-title">耗能统计</text>
          </view>
          <view class="stats-content">
            <view class="stat-block">
              <view class="stat-icon-wrap power-icon">
                <text class="stat-emoji">⚡</text>
              </view>
              <view class="stat-detail">
                <text class="stat-value-lg">{{ currentPower || '--' }}</text>
                <text class="stat-unit-lg">kWh</text>
              </view>
              <text class="stat-label-lg">用电量</text>
            </view>
            <view class="stat-divider"></view>
            <view class="stat-block">
              <view class="stat-block-header">
                <view class="stat-icon-wrap material-icon">
                  <text class="stat-emoji">📦</text>
                </view>
                <view class="manual-input-btn" @click="openMaterialInput">
                  <text class="manual-input-icon">✏️</text>
                </view>
              </view>
              <view class="stat-detail">
                <text class="stat-value-lg">{{ calculatedMaterial || '--' }}</text>
                <text class="stat-unit-lg">kg</text>
              </view>
              <text class="stat-label-lg">用料量</text>
            </view>
          </view>
        </view>
      </view>

      <!-- 数据项列表 -->
      <view class="data-items-container">
        <DataItem v-for="(item, index) in filteredDeviceData" :key="item.id" :valueId="item.id" :pollCount="pollCount"
          :dataName="item.notes" :dataValue="Number(item.value).toFixed(2)" :updateTime="item.updateTime"
          :deviceData="filteredDeviceData" :activeIndex="activeIndex" />
      </view>
    </view>

    <!-- 热效率计算弹窗 -->
    <uni-popup ref="materialPopup" type="center">
      <view class="material-popup">
        <view class="popup-header">
          <text class="popup-title">热效率计算</text>
        </view>
        <scroll-view class="popup-scroll" scroll-y="true">
          <view class="popup-section">
            <view class="section-title">炉排参数</view>
            <view class="form-row">
              <view class="form-item half">
                <text class="form-label">炉排频率</text>
                <input class="form-input" type="digit" v-model="materialForm.grateFrequency" placeholder="Hz" />
              </view>
              <view class="form-item half">
                <text class="form-label">炉排运行时间</text>
                <input class="form-input" type="digit" v-model="materialForm.grateRuntime" placeholder="分钟" />
              </view>
            </view>
            <view class="form-row">
              <view class="form-item half">
                <text class="form-label">炉排宽度 (m)</text>
                <input class="form-input" type="digit" v-model="materialForm.grateWidth" placeholder="请输入" />
              </view>
              <view class="form-item half">
                <text class="form-label">燃料高度 (mm)</text>
                <input class="form-input" type="digit" v-model="materialForm.fuelHeight" placeholder="默认120" />
              </view>
            </view>
          </view>

          <view class="popup-section">
            <view class="section-title">燃料参数</view>
            <view class="form-row">
              <view class="form-item half">
                <text class="form-label">燃料密度 (kg/m³)</text>
                <input class="form-input" type="digit" v-model="materialForm.fuelDensity" placeholder="请输入" />
              </view>
              <view class="form-item half">
                <text class="form-label">燃料热值</text>
                <input class="form-input" type="digit" v-model="materialForm.fuelCalorific" placeholder="默认17760" />
              </view>
            </view>
          </view>

          <view class="popup-section">
            <view class="section-title">水箱参数</view>
            <view class="form-row">
              <view class="form-item half">
                <text class="form-label">水箱长度 (m)</text>
                <input class="form-input" type="digit" v-model="materialForm.tankLength" placeholder="请输入" />
              </view>
              <view class="form-item half">
                <text class="form-label">水箱宽度 (m)</text>
                <input class="form-input" type="digit" v-model="materialForm.tankWidth" placeholder="请输入" />
              </view>
            </view>
            <view class="form-row">
              <view class="form-item half">
                <text class="form-label">水箱液位 (m)</text>
                <input class="form-input" type="digit" v-model="materialForm.tankLevel" placeholder="请输入" />
              </view>
              <view class="form-item half">
                <text class="form-label">出水温度 (℃)</text>
                <input class="form-input" type="digit" v-model="materialForm.outTemp" placeholder="请输入" />
              </view>
            </view>
            <view class="form-item">
              <text class="form-label">回水温度 (℃)</text>
              <input class="form-input" type="digit" v-model="materialForm.backTemp" placeholder="请输入" />
            </view>
          </view>
        </scroll-view>

        <view class="popup-result">
          <view class="result-row">
            <text class="result-label">用料量：</text>
            <text class="result-value">{{ calculatedMaterial || '0.00' }} kg</text>
          </view>
          <view class="result-row">
            <text class="result-label">总热量：</text>
            <text class="result-value">{{ formatNumber(totalHeat / 1000) }} kJ</text>
          </view>
          <view class="result-row">
            <text class="result-label">有效热量：</text>
            <text class="result-value">{{ formatNumber(effectiveHeat / 1000) }} kJ</text>
          </view>
          <view class="result-row highlight">
            <text class="result-label">热效率：</text>
            <text class="result-value efficiency">{{ efficiencyRate }}%</text>
          </view>
        </view>
        <view class="popup-footer">
          <button class="btn-cancel" @click="closeMaterialInput">取消</button>
          <button class="btn-confirm" @click="confirmMaterial">确定</button>
        </view>
      </view>
    </uni-popup>

    <CustomTabbar />
  </view>
</template>

<script>
  import api from '@/untils/request.js';
  import DataItem from '@/compoents/data-item/data-item.vue';
  import CustomTabBar from '@/components/custom-tab-bar/custom-tab-bar.vue';
  import {
    mapState
  } from 'vuex';
  export default {
    components: {
      DataItem,
      CustomTabbar: CustomTabBar
    },
    data() {
      return {
        dimensions: [],
        deviceData: [],
        activeIndex: null,
        chartData: [],
        timer: null,
        pollInterval: 60000,
        pollCount: 0,
        warnCount: 0,
        warnTimer: null,
        materialForm: {
          grateFrequency: '',
          grateRuntime: '',
          grateWidth: '',
          fuelHeight: '120',
          fuelDensity: '',
          fuelCalorific: '17760',
          tankLength: '',
          tankWidth: '',
          tankLevel: '',
          outTemp: '',
          backTemp: ''
        },
        manualMaterial: null,
        weatherMap: {
          'Clear': '晴',
          'Sunny': '晴朗',
          'Partly cloudy': '局部多云',
          'Cloudy': '多云',
          'Overcast': '阴天',
          'Mist': '薄雾',
          'Patchy rain nearby': '附近有零星小雨',
          'Patchy snow nearby': '附近有零星小雪',
          'Patchy sleet nearby': '附近有零星冻雨',
          'Thundery outbreaks in nearby': '附近有雷暴',
          'Moderate rain': '中雨',
          'Heavy rain': '大雨',
          'Light rain': '小雨',
          'Light snow': '小雪',
          'Moderate snow': '中雪',
          'Heavy snow': '大雪',
          'Fog': '雾',
          'Freezing fog': '冻雾',
          'Torrential rain shower': '特大暴雨',
          'Smoky haze': '烟霾',
          'Light rain shower': '小阵雨'
          // ... 更多天气状况
        },
        temp: '',
        weatherDesc: '', // 天气状况描述
        humidity: ''
      };
    },
    computed: {
      currentIndex() {
        return this.dimensions.findIndex(item => item.id === this.activeIndex);
      },
      currentDevice() {
        const idx = this.currentIndex;
        const device = idx >= 0 ? (this.dimensions[idx] || {}) : {};
        const data = idx >= 0 ? (this.deviceData[idx] || {}) : {};
        const state = device.state === '在线' ? '运行中' : '已停止';
        return {
          name: device.name,
          state: state
        };
      },
      statusClass() {
        return this.currentDevice.state === '运行中' ? 'running' : 'stopped';
      },
      currentPower() {
        const powerItem = this.deviceData.find(item => item.notes && item.notes.includes('用电'));
        return powerItem ? Number(powerItem.value).toFixed(2) : null;
      },
      currentMaterial() {
        const materialItem = this.deviceData.find(item => item.notes && item.notes.includes('用料'));
        return materialItem ? Number(materialItem.value).toFixed(2) : null;
      },
      totalHeat() {
        const f = this.materialForm;
        if (f.grateFrequency && f.grateRuntime && f.grateWidth && f.fuelHeight && f.fuelDensity && f.fuelCalorific) {
          const fuelHeightM = Number(f.fuelHeight) / 1000;
          const calorificJ = Number(f.fuelCalorific) * 1000;
          return Number(f.grateFrequency * 60 / 2) * 1.5 * Number(f.grateRuntime / 60) * Number(f.grateWidth) *
            fuelHeightM *
            Number(f.fuelDensity) * calorificJ;
        }
        return 0;
      },
      effectiveHeat() {
        const f = this.materialForm;
        const p = 1000;
        const c = 4200;
        if (f.tankLength && f.tankWidth && f.tankLevel && f.outTemp && f.backTemp) {
          const tempDiff = Number(f.outTemp) - Number(f.backTemp);
          return Number(f.tankLength) * Number(f.tankWidth) * Number(f.tankLevel) * p * c * tempDiff;
        }
        return 0;
      },
      efficiencyRate() {
        if (this.totalHeat > 0) {
          return ((this.effectiveHeat / this.totalHeat) * 100).toFixed(2);
        }
        return '0.00';
      },
      calculatedMaterial() {
        const f = this.materialForm;
        if (f.grateFrequency && f.grateRuntime && f.grateWidth && f.fuelHeight && f.fuelDensity) {
          const fuelHeightM = Number(f.fuelHeight) / 1000;
          return (Number(f.grateFrequency) * 1.5 * Number(f.grateRuntime) * Number(f.grateWidth) * fuelHeightM * Number(
            f.fuelDensity)).toFixed(2);
        }
        return this.manualMaterial;
      },
      filteredDeviceData() {
        return this.deviceData.filter(item => {
          const notes = item.notes || '';
          return !notes.includes('用电') && !notes.includes('用料');
        });
      },
      weatherIcon() {
        const desc = this.weatherDesc || '';
        if (desc.includes('雷')) return '⛈️';
        if (desc.includes('雨')) return '🌧️';
        if (desc.includes('雪')) return '❄️';
        if (desc.includes('雾') || desc.includes('霾')) return '🌫️';
        if (desc.includes('阴')) return '☁️';
        if (desc.includes('多云')) return '⛅';
        if (desc.includes('晴')) return '☀️';
        return '🌤️';
      },
      ...mapState(['userInfo'])
    },
    watch: {
      activeIndex(newVal, oldVal) {
        if (newVal && newVal !== oldVal) {
          this.fetchDeviceData(newVal);
        }
      }
    },
    created() {
      console.log('用户的角色为：', this.$store.state.userRole)
      console.log('vuex中的用户信息为：', this.userInfo)
    },
    async onLoad() {
      await this.fetchDimensions();
      await this.fetchDeviceData();
      this.startPolling();
      await this.getOrganization();
      this.getWeatherInfo();
      this.fetchWarnCount();
      this.startWarnPolling();
    },
    onReady() {
      uni.setNavigationBarTitle({
        title: this.organization
      })
    },
    onUnload() {
      this.stopPolling();
      this.stopWarnPolling();
    },
    onShow() {
      // 页面再次显示时刷新报警数量
      this.fetchWarnCount();
    },
    onPullDownRefresh() {
      Promise.all([
        this.fetchDeviceData(),
        this.fetchWarnCount()
      ]).finally(() => {
        uni.stopPullDownRefresh();
      });
    },
    methods: {
      formatNumber(num) {
        if (num === 0) return '0';
        return Number(num).toLocaleString('zh-CN', {
          maximumFractionDigits: 2
        });
      },
      openMaterialInput() {
        this.$refs.materialPopup.open();
      },
      closeMaterialInput() {
        this.$refs.materialPopup.close();
      },
      confirmMaterial() {
        if (this.efficiencyRate > 0) {
          this.manualMaterial = this.efficiencyRate;
        }
        this.$refs.materialPopup.close();
      },
      startPolling() {
        this.stopPolling();
        this.timer = setInterval(() => {
          this.fetchDeviceData();
        }, this.pollInterval);
      },
      stopPolling() {
        if (this.timer) {
          clearInterval(this.timer);
          this.timer = null;
        }
      },
      async fetchDimensions() {
        try {
          const res = await api.get('/device/getDeviceById');
          this.dimensions = res.data || [];
          const defaultItem = this.dimensions[0];
          if (defaultItem) {
            this.activeIndex = defaultItem.id;
          }
        } catch (err) {
          console.log('获取设备失败', err);
        }
      },
      async fetchDeviceData() {
        if (!this.activeIndex && this.activeIndex !== 0) {
          return;
        }
        try {
          const res = await api.get('/deviceValue/getDeviceValueByDeviceId', {
            deviceId: this.activeIndex
          });
          this.deviceData = res.data || [];
          this.pollCount++;
        } catch (err) {
          console.log('获取设备数据失败', err);
        }
      },
      async getOrganization(err) {
        try {
          const res = await api.get('/appUser/info')
          // this.organization = res.data.organization
          //console.log(this.organization)
          uni.setNavigationBarTitle({
            title: res.data.organization
          })
        } catch {
          console.log('获取组织请求失败！', err)
        }
      },
      switchTab(id) {
        this.activeIndex = id;
      },
      // 获取报警信息数量
      async fetchWarnCount() {
        try {
          const res = await api.get('/warnMessage/getAllMessage')
          if (res.code == 200 && Array.isArray(res.data)) {
            this.warnCount = res.data.length
          }
        } catch (err) {
          console.log('获取报警信息失败', err)
        }
      },
      // 报警数量轮询
      startWarnPolling() {
        this.stopWarnPolling()
        this.warnTimer = setInterval(() => {
          this.fetchWarnCount()
        }, this.pollInterval)
      },
      stopWarnPolling() {
        if (this.warnTimer) {
          clearInterval(this.warnTimer)
          this.warnTimer = null
        }
      },
      // 跳转到报警页面
      goToAlarm() {
        uni.switchTab({
          url: '/pages/alarm/alarm'
        })
      },
      //获取天气
      getWeatherInfo() {
        uni.getLocation({
          type: 'gcj02', // 坐标系类型，建议使用 'gcj02'（国测局坐标）
          success: (res) => {
            const latitude = res.latitude; // 纬度
            const longitude = res.longitude; // 经度
            console.log('当前位置的经度：' + longitude + '，纬度：' + latitude);
            // 在这里调用天气API
            uni.request({
              url: `https://wttr.in/${latitude},${longitude}`, // 拼接经纬度
              data: {
                format: 'j1', // 关键：要求返回JSON格式
                lang: 'zh-cn', // 可选：使用中文
                m: '' // 可选：使用公制单位
              },
              success: (res) => {
                const current = res.data.current_condition[0];
                this.temp = current.temp_C; // 当前温度 (°C)
                console.log(this.temp)
                const englishDesc = current.weatherDesc[0].value.trim();
                console.log(current.weatherDesc[0].value)
                // 从映射表中获取中文，如果没有匹配项，则保留英文作为备选
                this.weatherDesc = this.weatherMap[current.weatherDesc[0].value.trim()] || current
                  .weatherDesc[0]
                  .value;
                console.log(this.weatherMap.Overcast)
                console.log(this.weatherDesc)
                this.humidity = current.humidity; // 湿度 (%)
                console.log(this.humidity)
              },
              fail: (err) => {
                console.error('获取天气失败:', err);
              }
            });
          },
          fail: (err) => {
            console.error('获取位置失败', err);
          }
        });
      }
    }
  };
</script>

<style scoped>
  .container {
    min-height: 100vh;
    background-color: #f5f5f5;
    padding: 20rpx;
    padding-bottom: 140rpx;
  }

  /* 报警信息滚动条（固定在屏幕顶部，不随页面滚动） */
  .warn-banner {
    position: fixed;
    top: 20rpx;
    left: 20rpx;
    right: 20rpx;
    z-index: 999;
    display: flex;
    align-items: center;
    height: 84rpx;
    background: linear-gradient(90deg, #d4380d 0%, #ff4d4f 50%, #ff7875 100%);
    border-radius: 14rpx;
    overflow: hidden;
    box-shadow: 0 4rpx 16rpx rgba(255, 77, 79, 0.35);
    border: 2rpx solid rgba(255, 240, 240, 0.6);
  }

  /* 占位元素，保持原布局间距 */
  .warn-banner-placeholder {
    height: 84rpx;
    margin-bottom: 24rpx;
  }

  .warn-icon {
    flex-shrink: 0;
    padding: 0 18rpx 0 22rpx;
    font-size: 40rpx;
  }

  .warn-scroll-wrap {
    flex: 1;
    overflow: hidden;
    position: relative;
    height: 84rpx;
  }

  .warn-scroll-inner {
    display: inline-block;
    white-space: nowrap;
    height: 84rpx;
    line-height: 84rpx;
    animation: warn-marquee 15s linear infinite;
  }

  .warn-scroll-text {
    font-size: 32rpx;
    color: #fff;
    font-weight: 800;
    letter-spacing: 2rpx;
    text-shadow: 0 2rpx 6rpx rgba(0, 0, 0, 0.45), 0 0 12rpx rgba(255, 255, 200, 0.5);
  }

  .warn-arrow {
    flex-shrink: 0;
    padding-right: 22rpx;
    font-size: 48rpx;
    color: #fff;
    font-weight: bold;
    text-shadow: 0 2rpx 6rpx rgba(0, 0, 0, 0.4);
    animation: warn-arrow-blink 1s ease-in-out infinite;
  }

  @keyframes warn-arrow-blink {

    0%,
    100% {
      opacity: 1;
    }

    50% {
      opacity: 0.4;
    }
  }

  @keyframes warn-marquee {
    0% {
      transform: translateX(0);
    }

    100% {
      transform: translateX(-50%);
    }
  }

  .tabs-wrapper {
    width: 100%;
    white-space: nowrap;
    background-color: #fff;
    border-radius: 16rpx;
    box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);
    margin-bottom: 30rpx;
  }

  .tabs-container {
    display: inline-flex;
    padding: 10rpx;
  }

  .tab-item {
    flex-shrink: 0;
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 24rpx 40rpx;
    position: relative;
    transition: all 0.3s ease;
  }

  .tab-text {
    font-size: 28rpx;
    color: #333;
    transition: color 0.3s ease;
  }

  .tab-item.active .tab-text {
    color: #1890ff;
    font-weight: bold;
  }

  .tab-indicator {
    position: absolute;
    bottom: 0;
    left: 50%;
    transform: translateX(-50%);
    width: 48rpx;
    height: 4rpx;
    background-color: #1890ff;
    border-radius: 2rpx;
  }

  .content {
    background-color: #fff;
    border-radius: 16rpx;
    padding: 30rpx;
    box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);
  }

  .header-card {
    display: flex;
    flex-direction: column;
    padding: 30rpx;
    background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
    border-radius: 24rpx;
    color: #fff;
    margin-bottom: 30rpx;
    overflow: hidden;
    box-shadow: 0 8rpx 32rpx rgba(15, 52, 96, 0.3);
  }

  .card-top {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .card-divider {
    height: 1rpx;
    background: linear-gradient(90deg, transparent 0%, rgba(255, 255, 255, 0.2) 50%, transparent 100%);
    margin: 24rpx 0;
  }

  .card-bottom {
    display: flex;
    flex-direction: column;
  }

  .header-left {
    flex: 1;
  }

  .header-title {
    font-size: 44rpx;
    font-weight: bold;
    margin-bottom: 8rpx;
    letter-spacing: 2rpx;
    text-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.3);
  }

  .header-subtitle {
    font-size: 22rpx;
    color: rgba(255, 255, 255, 0.7);
    margin-bottom: 20rpx;
  }

  .header-status {
    display: inline-flex;
    align-items: center;
    padding: 10rpx 24rpx;
    border-radius: 30rpx;
    font-size: 22rpx;
  }

  .status-dot {
    width: 12rpx;
    height: 12rpx;
    border-radius: 50%;
    margin-right: 12rpx;
  }

  .header-status.running {
    background: rgba(82, 196, 26, 0.25);
    border: 1rpx solid rgba(82, 196, 26, 0.5);
  }

  .header-status.running .status-dot {
    background-color: #52c41a;
    box-shadow: 0 0 8rpx #52c41a;
    animation: pulse 2s infinite;
  }

  .header-status.stopped {
    background: rgba(255, 77, 79, 0.25);
    border: 1rpx solid rgba(255, 77, 79, 0.5);
  }

  .header-status.stopped .status-dot {
    background-color: #ff4d4f;
    box-shadow: 0 0 8rpx #ff4d4f;
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

  .header-right {
    flex-shrink: 0;
    margin-left: 30rpx;
  }

  .weather-box {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 24rpx 32rpx;
  }

  .weather-main {
    display: flex;
    align-items: center;
    gap: 12rpx;
    margin-bottom: 18rpx;
  }

  .weather-icon {
    font-size: 48rpx;
    line-height: 1;
    filter: drop-shadow(0 2rpx 6rpx rgba(0, 0, 0, 0.3));
  }

  .weather-desc {
    font-size: 30rpx;
    font-weight: 600;
    color: #fff;
    letter-spacing: 2rpx;
    text-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.2);
  }

  .weather-detail {
    display: flex;
    align-items: center;
    gap: 24rpx;
  }

  .weather-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 6rpx;
  }

  .weather-value {
    font-size: 30rpx;
    font-weight: bold;
    color: #fff;
    font-family: 'DIN', 'Helvetica Neue', sans-serif;
  }

  .weather-label {
    font-size: 20rpx;
    color: rgba(255, 255, 255, 0.65);
  }

  .weather-divider {
    width: 1rpx;
    height: 52rpx;
    background: linear-gradient(180deg, transparent 0%, rgba(255, 255, 255, 0.35) 50%, transparent 100%);
  }

  .stats-header {
    margin-bottom: 16rpx;
  }

  .stats-title {
    font-size: 28rpx;
    font-weight: bold;
    color: #fff;
  }

  .stats-content {
    display: flex;
    justify-content: space-around;
    align-items: center;
  }

  .stat-block {
    display: flex;
    flex-direction: column;
    align-items: center;
    flex: 1;
  }

  .stat-icon-wrap {
    width: 56rpx;
    height: 56rpx;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 10rpx;
  }

  .power-icon {
    background: linear-gradient(135deg, #fff7e6 0%, #ffd591 100%);
  }

  .material-icon {
    background: linear-gradient(135deg, #e6f7ff 0%, #91d5ff 100%);
  }

  .stat-emoji {
    font-size: 28rpx;
  }

  .stat-detail {
    display: flex;
    align-items: baseline;
    gap: 6rpx;
    margin-bottom: 6rpx;
  }

  .stat-value-lg {
    font-size: 36rpx;
    font-weight: bold;
    color: #fff;
    font-family: 'DIN', 'Helvetica Neue', sans-serif;
  }

  .stat-unit-lg {
    font-size: 20rpx;
    color: rgba(255, 255, 255, 0.6);
  }

  .stat-label-lg {
    font-size: 22rpx;
    color: rgba(255, 255, 255, 0.8);
  }

  .stat-divider {
    width: 1rpx;
    height: 80rpx;
    background: linear-gradient(180deg, rgba(255, 255, 255, 0.3) 0%, rgba(255, 255, 255, 0.1) 100%);
    margin: 0 20rpx;
  }

  .stat-block-header {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 12rpx;
    margin-bottom: 10rpx;
  }

  .manual-input-btn {
    width: 40rpx;
    height: 40rpx;
    background: rgba(24, 144, 255, 0.2);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .manual-input-icon {
    font-size: 20rpx;
  }

  .manual-input-btn:active {
    transform: scale(0.95);
    background: rgba(24, 144, 255, 0.3);
  }

  .material-popup {
    width: 640rpx;
    max-height: 900rpx;
    background: #fff;
    border-radius: 24rpx;
    padding: 30rpx;
  }

  .popup-header {
    text-align: center;
    margin-bottom: 24rpx;
  }

  .popup-title {
    font-size: 34rpx;
    font-weight: bold;
    color: #333;
  }

  .popup-scroll {
    max-height: 500rpx;
  }

  .popup-section {
    margin-bottom: 24rpx;
  }

  .section-title {
    font-size: 26rpx;
    font-weight: bold;
    color: #1890ff;
    margin-bottom: 16rpx;
    padding-left: 12rpx;
    border-left: 4rpx solid #1890ff;
  }

  .form-row {
    display: flex;
    gap: 16rpx;
    margin-bottom: 16rpx;
  }

  .form-item {
    display: flex;
    flex-direction: column;
    gap: 8rpx;
  }

  .form-item.half {
    flex: 1;
  }

  .form-label {
    font-size: 24rpx;
    color: #666;
  }

  .form-input {
    height: 72rpx;
    background: #f5f5f5;
    border-radius: 12rpx;
    padding: 0 20rpx;
    font-size: 26rpx;
    color: #333;
  }

  .popup-result {
    margin-top: 24rpx;
    padding: 20rpx;
    background: linear-gradient(135deg, #e6f7ff 0%, #91d5ff 100%);
    border-radius: 16rpx;
  }

  .result-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 8rpx 0;
  }

  .result-row.highlight {
    margin-top: 8rpx;
    padding-top: 16rpx;
    border-top: 1rpx dashed rgba(24, 144, 255, 0.3);
  }

  .result-label {
    font-size: 26rpx;
    color: #333;
  }

  .result-value {
    font-size: 28rpx;
    font-weight: bold;
    color: #1890ff;
  }

  .result-value.efficiency {
    font-size: 36rpx;
    color: #ff4d4f;
  }

  .popup-footer {
    display: flex;
    gap: 24rpx;
    margin-top: 24rpx;
  }

  .btn-cancel,
  .btn-confirm {
    flex: 1;
    height: 80rpx;
    line-height: 80rpx;
    text-align: center;
    border-radius: 40rpx;
    font-size: 28rpx;
  }

  .btn-cancel {
    background: #f5f5f5;
    color: #666;
  }

  .btn-confirm {
    background: linear-gradient(135deg, #1890ff 0%, #52c41a 100%);
    color: #fff;
  }
</style>