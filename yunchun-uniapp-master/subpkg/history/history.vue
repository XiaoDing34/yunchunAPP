<template>
  <view class="history-container">
    <view class="header">
      <view class="header-line"></view>
    </view>

    <view class="content">
      <view class="filter-section">
        <view class="filter-item" @click="showTimeRangePicker = true">
          <text class="filter-label">时间范围</text>
          <view class="filter-picker">
            <text class="filter-text">{{ timeRange }}</text>
            <uni-icons class="filter-icon" type="arrowdown" size="20" color="#6366F1"></uni-icons>
          </view>
        </view>
        <view class="filter-item">
          <text class="filter-label">数据类型</text>
          <view class="filter-picker">
            <text class="filter-text">{{ dataType }}</text>
            <uni-icons class="filter-icon" type="arrowdown" size="20" color="#6366F1" :canvas2d="true"></uni-icons>
          </view>
        </view>
      </view>

      <view class="chart-section">
        <text class="chart-title">数据趋势</text>
        <view class="charts-box">
          <qiun-data-charts type="line" :opts="opts" :chartData="chartData" :ontouch="true" />
        </view>
      </view>

      <view class="data-list">
        <text class="list-title">历史记录</text>
        <view class="data-item" v-for="(item, index) in historyData.slice(historyData.length-7,historyData.length)"
          :key="index">
          <view class="data-time">{{ item.time }}</view>
          <view class="data-value">{{ item.value }}</view>
          <view class="data-status" :class="item.status">{{ item.statusText }}</view>
        </view>
      </view>
    </view>

    <!-- 时间范围选择弹窗 -->
    <uni-popup ref="timeRangePopup" type="bottom" :animation="true">
      <view class="picker-popup">
        <view class="picker-header">
          <text class="picker-title">选择时间范围</text>
          <view class="picker-close" @click="showTimeRangePicker = false">✕</view>
        </view>
        <view class="picker-content">
          <view class="picker-option" v-for="option in timeRangeOptions" :key="option.value"
            @click="selectTimeRange(option)">
            <text class="option-text">{{ option.label }}</text>
            <view v-if="timeRange === option.label" class="option-check">✓</view>
          </view>
        </view>
      </view>
    </uni-popup>
  </view>
</template>

<script>
  import api from '@/untils/request.js';

  export default {
    name: 'history',
    data() {
      return {
        timeRange: '12小时',
        dataType: '温度',
        valueId: 0,
        valueName: '',
        historyData: [],
        chartData: {},
        showTimeRangePicker: false,
        timeRangeOptions: [{
            label: '12小时',
            value: '12'
          },
          {
            label: '24小时',
            value: '24'
          },
          {
            label: '7天',
            value: '168'
          }
        ],
        opts: {
          color: ["#91CB74", "#FAC858", "#EE6666", "#73C0DE", "#3CA272", "#FC8452", "#9A60B4", "#ea7ccc"],
          padding: [20, 20, 0, 20],
          enableScroll: true,
          legend: {
            show: false
          },
          xAxis: {
            itemCount: 5,
            type: 'category',
            disableGrid: true,
            axisLineColor: '#E2E8F0',
            labelColor: '#94A3B8',
            fontSize: 10,
            scrollShow: true,
            scrollAlign: 'left'
          },
          yAxis: {
            type: 'value',
            disableGrid: false,
            gridType: 'dash',
            gridColor: '#E2E8F0',
            labelColor: '#94A3B8',
            fontSize: 10,
            splitNumber: 4
          },
          extra: {
            line: {
              type: "curve",
              width: 2,
              activeType: "point"
            }
          }
        }
      };
    },

    onLoad(options) {
      if (options.valueId) {
        this.valueId = Number(options.valueId);
        console.log('Received valueId:', this.valueId);
      }
      if (options.valueName) {
        this.valueName = decodeURIComponent(options.valueName);
        console.log('Received valueName:', this.valueName);
        this.mapDataTypeByNotes(this.valueName);
      }
    },

    onShow() {
      console.log('onShow called');
      this.loadHistoryData();
    },
    watch: {
      showTimeRangePicker(val) {
        if (val) {
          this.$refs.timeRangePopup.open();
        } else {
          this.$refs.timeRangePopup.close();
        }
      }
    },
    methods: {
      selectTimeRange(option) {
        this.timeRange = option.label;
        this.showTimeRangePicker = false;
        this.loadHistoryData(option.value);
      },
      formatTime(dateString) {
        const date = new Date(dateString);
        const year = date.getFullYear().toString();
        const month = (date.getMonth() + 1).toString().padStart(2, '0');
        const day = date.getDate().toString().padStart(2, '0');
        const hours = date.getHours().toString().padStart(2, '0');
        const minutes = date.getMinutes().toString().padStart(2, '0');
        const seconds = date.getSeconds().toString().padStart(2, '0');
        return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
      },

      formatTimeToHHMMSS(timeStr) {
        if (!timeStr) return '';
        const date = new Date(timeStr);
        if (isNaN(date.getTime())) return timeStr;
        const hours = date.getHours().toString().padStart(2, '0');
        const minutes = date.getMinutes().toString().padStart(2, '0');
        return `${hours}:${minutes}`;
      },

      formatChartDataFromArray(rawArray) {
        const categories = rawArray.map(item => this.formatTimeToHHMMSS(item.updateTime));
        console.log('横坐标轴:', categories);
        const values = rawArray.map(item => Number(item.value).toFixed(2));
        console.log('图表数据值:', values);
        return {
          categories: categories,
          series: [{
            name: this.valueName,
            data: values
          }]
        };
      },

      mapDataTypeByNotes(notes) {
        if (notes.includes('温度')) {
          this.dataType = '温度';
        } else if (notes.includes('压力')) {
          this.dataType = '压力';
        } else if (notes.includes('频率')) {
          this.dataType = '频率';
        } else if (notes.includes('液位')) {
          this.dataType = '液位';
        } else {
          this.dataType = '数据';
        }
      },

      loadHistoryData(timeRange = '12') {
        console.log('loadHistoryData called, valueId:', this.valueId, 'timeRange:', timeRange);
        if (!this.valueId) {
          console.error('valueId is empty or 0');
          return;
        }
        api.post('/history/getValueByValueIdAndTime', {
          valueId: this.valueId,
          timeRange: timeRange
        }).then(res => {
          this.historyData = res.data.map(item => {
            return {
              time: this.formatTime(item.updateTime),
              value: Number(item.value).toFixed(2),
              status: 'normal',
              statusText: '正常'
            };
          });
          this.chartData = this.formatChartDataFromArray(res.data);
        }).catch(err => {
          console.error('获取历史数据失败:', err);
        });
      }
    }
  }
</script>

<style scoped>
  .history-container {
    min-height: 100vh;
    background-color: #f5f5f5;
  }

  .header {
    padding-top: 20rpx;
  }

  .header-line {
    height: 6rpx;
    background: linear-gradient(90deg, #3b82f6 0%, #6366f1 50%, #8b5cf6 100%);
  }

  .content {
    padding: 24rpx;
  }

  .filter-section {
    display: flex;
    gap: 24rpx;
    margin-bottom: 24rpx;
  }

  .filter-item {
    flex: 1;
    background-color: #ffffff;
    border-radius: 16rpx;
    padding: 20rpx;
    box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);
  }

  .filter-label {
    font-size: 24rpx;
    color: #64748b;
    margin-bottom: 12rpx;
    display: block;
  }

  .filter-picker {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .filter-text {
    font-size: 28rpx;
    font-weight: 600;
    color: #1e293b;
  }

  .filter-icon {
    margin-left: 12rpx;
  }

  .chart-section {
    background-color: #ffffff;
    border-radius: 16rpx;
    padding: 24rpx;
    margin-bottom: 24rpx;
    box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);
  }

  .chart-title {
    font-size: 30rpx;
    font-weight: 600;
    color: #1e293b;
    margin-bottom: 20rpx;
    display: block;
  }

  .charts-box {
    height: 400rpx;
  }

  .data-list {
    background-color: #ffffff;
    border-radius: 16rpx;
    padding: 24rpx;
    box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);
  }

  .list-title {
    font-size: 30rpx;
    font-weight: 600;
    color: #1e293b;
    margin-bottom: 20rpx;
    display: block;
  }

  .data-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 20rpx 0;
    border-bottom: 1rpx solid #f1f5f9;
  }

  .data-item:last-child {
    border-bottom: none;
  }

  .data-time {
    font-size: 26rpx;
    color: #64748b;
  }

  .data-value {
    font-size: 28rpx;
    font-weight: 600;
    color: #1e293b;
  }

  .data-status {
    padding: 8rpx 16rpx;
    border-radius: 20rpx;
    font-size: 22rpx;
  }

  .data-status.normal {
    background-color: #dcfce7;
    color: #16a34a;
  }

  .data-status.warning {
    background-color: #fef3c7;
    color: #d97706;
  }

  .data-status.error {
    background-color: #fee2e2;
    color: #dc2626;
  }

  .picker-popup {
    background-color: #ffffff;
    border-radius: 32rpx 32rpx 0 0;
    padding-bottom: env(safe-area-inset-bottom);
  }

  .picker-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 32rpx;
    border-bottom: 1rpx solid #f1f5f9;
  }

  .picker-title {
    font-size: 32rpx;
    font-weight: 600;
    color: #1e293b;
  }

  .picker-close {
    width: 56rpx;
    height: 56rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 32rpx;
    color: #94a3b8;
  }

  .picker-content {
    padding: 16rpx 0;
  }

  .picker-option {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 32rpx;
  }

  .picker-option:active {
    background-color: #f8fafc;
  }

  .option-text {
    font-size: 30rpx;
    color: #334155;
  }

  .option-check {
    width: 40rpx;
    height: 40rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    background-color: #3b82f6;
    color: #ffffff;
    border-radius: 50%;
    font-size: 24rpx;
  }
</style>