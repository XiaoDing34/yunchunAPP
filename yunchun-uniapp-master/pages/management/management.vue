<template>
  <view class="management-container">
    <!-- 顶部装饰线 -->
    <view class="header">
      <view class="header-line"></view>
    </view>

    <!-- Segmented Control -->
    <view class="segmented-control">
      <view
        class="segment-item"
        :class="{ active: activeIndex === 0 }"
        @click="switchTab(0)"
      >
        <text class="segment-text">员工管理</text>
      </view>
      <view
        class="segment-item"
        :class="{ active: activeIndex === 1 }"
        @click="switchTab(1)"
      >
        <text class="segment-text">报警管理</text>
      </view>
    </view>

    <!-- 员工管理模块 -->
    <view v-show="activeIndex === 0" class="module-content">
      <view class="search-bar">
        <input class="search-input" placeholder="搜索员工姓名" v-model="searchText" />
        <text class="search-icon">🔍</text>
      </view>
      <view class="staff-list">
        <view class="staff-item" v-for="(item, index) in filteredStaffList" :key="index">
          <view class="staff-avatar">
            <text class="avatar-text">{{ item.name.charAt(0) }}</text>
          </view>
          <view class="staff-info">
            <text class="staff-name">{{ item.name }}</text>
            <text class="staff-position">{{ item.position }}</text>
          </view>
          <view class="staff-status" :class="item.status">
            {{ item.status === 'active' ? '在岗' : '离岗' }}
          </view>
        </view>
      </view>
      <view v-if="filteredStaffList.length === 0" class="empty-state">
        <view class="empty-icon">👥</view>
        <text class="empty-text">暂无员工信息</text>
      </view>
    </view>

    <!-- 报警管理模块 -->
    <view v-show="activeIndex === 1" class="module-content">
      <!-- 统计概览卡片 -->
      <view class="stats-cards">
        <view class="stat-card stat-pending">
          <text class="stat-number">{{ pendingCount }}</text>
          <text class="stat-label">未处理</text>
        </view>
        <view class="stat-card stat-invalid">
          <text class="stat-number">{{ invalidCount }}</text>
          <text class="stat-label">无效报警</text>
        </view>
        <view class="stat-card stat-total">
          <text class="stat-number">{{ alarmList.length }}</text>
          <text class="stat-label">报警总数</text>
        </view>
      </view>

      <!-- 筛选下拉 -->
      <view class="filter-bar">
        <view
          v-for="(item, index) in filterOptions"
          :key="index"
          class="filter-item"
          :class="{ active: filterIndex === index }"
          @click="filterIndex = index"
        >
          <text>{{ item.label }}</text>
        </view>
      </view>

      <!-- 报警列表 -->
      <view class="alarm-list">
        <AlarmManageItem
          v-for="item in filteredAlarmList"
          :key="item.id"
          :warnLevel="item.warnLevel"
          :messageContent="item.messageContent"
          :sender="item.sender"
          :createTime="item.createTime"
          :feedbackStatus="item.feedbackStatus"
          :handleStatus="item.handleStatus"
          :handlerName="item.handlerName"
        />
      </view>
      <view v-if="filteredAlarmList.length === 0" class="empty-state">
        <view class="empty-icon">📋</view>
        <text class="empty-text">暂无报警信息</text>
      </view>
    </view>

    <CustomTabbar />
  </view>
</template>

<script>
  import api from '@/untils/request.js'
  import CustomTabbar from '@/components/custom-tab-bar/custom-tab-bar.vue'
  import AlarmManageItem from '@/compoents/alarm-manage-item/alarm-manage-item.vue'

  export default {
    name: 'Management',
    components: {
      CustomTabbar,
      AlarmManageItem
    },
    data() {
      return {
        activeIndex: 0,
        // 员工管理
        searchText: '',
        staffList: [],
        // 报警管理
        alarmList: [],
        filterIndex: 0,
        filterOptions: [
          { label: '全部', value: 'all' },
          { label: '未反馈', value: 'unfeedback' },
          { label: '未处理', value: 'unhandled' },
          { label: '已处理', value: 'handled' }
        ]
      }
    },
    computed: {
      filteredStaffList() {
        if (!this.searchText) return this.staffList;
        return this.staffList.filter(item =>
          item.name.includes(this.searchText)
        );
      },
      pendingCount() {
        return this.alarmList.filter(item =>
          Number(item.feedbackStatus) === 1 && Number(item.handleStatus) === 0
        ).length;
      },
      invalidCount() {
        return this.alarmList.filter(item =>
          Number(item.feedbackStatus) === 2
        ).length;
      },
      filteredAlarmList() {
        const filter = this.filterOptions[this.filterIndex].value;
        if (filter === 'all') return this.alarmList;
        if (filter === 'unfeedback') {
          return this.alarmList.filter(item => Number(item.feedbackStatus) === 0);
        }
        if (filter === 'unhandled') {
          return this.alarmList.filter(item =>
            Number(item.feedbackStatus) === 1 && Number(item.handleStatus) === 0
          );
        }
        if (filter === 'handled') {
          return this.alarmList.filter(item => Number(item.handleStatus) === 1);
        }
        return this.alarmList;
      }
    },
    onLoad() {
      this.loadStaffList();
      this.loadAlarmList();
    },
    onPullDownRefresh() {
      if (this.activeIndex === 0) {
        this.loadStaffList(() => uni.stopPullDownRefresh());
      } else {
        this.loadAlarmList(() => uni.stopPullDownRefresh());
      }
    },
    methods: {
      switchTab(index) {
        this.activeIndex = index;
      },
      async loadStaffList(callback) {
        try {
          const res = await api.get('/appUser/getAll');
          if (res.code == 200) {
            this.staffList = res.data.map(item => ({
              name: item.username || '未知',
              position: item.position || '普通员工',
              status: 'active'
            }));
          }
        } catch (err) {
          console.error('获取员工列表失败:', err);
        } finally {
          if (callback) callback();
        }
      },
      async loadAlarmList(callback) {
        try {
          const res = await api.get('/warnMessage/getAllMessage');
          if (res.code == 200) {
            this.alarmList = res.data || [];
          }
        } catch (err) {
          console.error('获取报警列表失败:', err);
        } finally {
          if (callback) callback();
        }
      }
    }
  }
</script>

<style scoped>
  .management-container {
    min-height: 100vh;
    background-color: #f5f5f5;
    padding-bottom: 120rpx;
  }

  .header {
    padding-top: 20rpx;
  }

  .header-line {
    height: 6rpx;
    background: linear-gradient(90deg, #3b82f6 0%, #6366f1 50%, #8b5cf6 100%);
  }

  /* Segmented Control */
  .segmented-control {
    display: flex;
    background-color: #ffffff;
    margin: 24rpx;
    border-radius: 16rpx;
    padding: 8rpx;
    box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.06);
  }

  .segment-item {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 20rpx 0;
    border-radius: 12rpx;
    transition: all 0.3s ease;
    position: relative;
  }

  .segment-item.active {
    background-color: #3b82f6;
  }

  .segment-text {
    font-size: 30rpx;
    font-weight: 600;
    color: #6B7280;
  }

  .segment-item.active .segment-text {
    color: #ffffff;
  }

  .module-content {
    padding: 0 24rpx;
  }

  /* 员工管理样式 */
  .search-bar {
    display: flex;
    align-items: center;
    background-color: #ffffff;
    border-radius: 40rpx;
    padding: 16rpx 24rpx;
    margin-bottom: 24rpx;
    box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);
  }

  .search-input {
    flex: 1;
    font-size: 28rpx;
    color: #333;
  }

  .search-icon {
    font-size: 28rpx;
    margin-left: 16rpx;
  }

  .staff-list {
    background-color: #ffffff;
    border-radius: 24rpx;
    padding: 16rpx;
    box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);
  }

  .staff-item {
    display: flex;
    align-items: center;
    padding: 24rpx 16rpx;
    border-bottom: 1rpx solid #f1f5f9;
  }

  .staff-item:last-child {
    border-bottom: none;
  }

  .staff-avatar {
    width: 88rpx;
    height: 88rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    background: linear-gradient(135deg, #dbeafe 0%, #bfdbfe 100%);
    border-radius: 50%;
    margin-right: 20rpx;
  }

  .avatar-text {
    font-size: 36rpx;
    font-weight: 600;
    color: #3b82f6;
  }

  .staff-info {
    flex: 1;
  }

  .staff-name {
    font-size: 30rpx;
    font-weight: 600;
    color: #1e293b;
    display: block;
  }

  .staff-position {
    font-size: 24rpx;
    color: #64748b;
    margin-top: 8rpx;
    display: block;
  }

  .staff-status {
    padding: 8rpx 20rpx;
    border-radius: 20rpx;
    font-size: 24rpx;
  }

  .staff-status.active {
    background-color: #dcfce7;
    color: #16a34a;
  }

  .staff-status.inactive {
    background-color: #fef3c7;
    color: #d97706;
  }

  /* 报警管理样式 */
  .stats-cards {
    display: flex;
    gap: 16rpx;
    margin-bottom: 24rpx;
  }

  .stat-card {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 24rpx 0;
    border-radius: 16rpx;
    box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.06);
  }

  .stat-pending {
    background-color: #FEF3C7;
  }

  .stat-invalid {
    background-color: #FEE2E2;
  }

  .stat-total {
    background-color: #DBEAFE;
  }

  .stat-number {
    font-size: 44rpx;
    font-weight: 700;
    color: #1e293b;
  }

  .stat-pending .stat-number { color: #B45309; }
  .stat-invalid .stat-number { color: #DC2626; }
  .stat-total .stat-number { color: #2563EB; }

  .stat-label {
    font-size: 24rpx;
    color: #64748b;
    margin-top: 8rpx;
  }

  /* 筛选栏 */
  .filter-bar {
    display: flex;
    background-color: #ffffff;
    border-radius: 16rpx;
    padding: 8rpx;
    margin-bottom: 24rpx;
    box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);
  }

  .filter-item {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 16rpx 0;
    border-radius: 12rpx;
    font-size: 26rpx;
    color: #6B7280;
    transition: all 0.2s ease;
  }

  .filter-item.active {
    background-color: #3b82f6;
    color: #ffffff;
    font-weight: 600;
  }

  .alarm-list {
    min-height: 200rpx;
  }

  /* 空状态 */
  .empty-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 120rpx;
    background-color: #ffffff;
    border-radius: 24rpx;
    box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);
  }

  .empty-icon {
    font-size: 120rpx;
    margin-bottom: 32rpx;
  }

  .empty-text {
    font-size: 28rpx;
    color: #94a3b8;
  }
</style>
