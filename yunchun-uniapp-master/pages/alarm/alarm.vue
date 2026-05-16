<template>
  <view class="alarm-container">
    <view class="alarm-list">
      <warn-item v-for="item in warnMessageList" :key="item.id" :warnLevel="item.warnLevel"
        :messageTitle="item.messageTitle" :sender="item.sender" :createTime="item.createTime"
        :messageContent="item.messageContent" :advice="item.advice" @click="handleWarnClick" />
    </view>
  </view>
</template>

<script>
  import api from '@/untils/request.js'
  import WarnItem from '@/compoents/warn-item/warn-item.vue'
  export default {
    components: {
      WarnItem
    },
    data() {
      return {
        warnMessageList: []
      };
    },
    onLoad() {
      this.getAllMessage()
    },
    methods: {
      async getAllMessage() {
        try {
          const res = await api.get('/warnMessage/getAllMessage')
          if (res.code == 200) {
            this.warnMessageList = res.data
            console.log(this.warnMessageList)
          } else {
            console.log(res.data)
          }
        } catch (err) {
          console.log(err.message)
        }
      },
      handleWarnClick(item) {
        console.log('点击报警项:', item)
        // 可以在这里跳转到详情页面或弹出详情弹窗
      }
    }
  };
</script>

<style scoped>
  .alarm-container {
    min-height: 100vh;
    background-color: #f5f5f5;
    padding-bottom: 120rpx;
  }

  .alarm-header {
    height: 80rpx;
    background-color: #fff;
    display: flex;
    align-items: center;
    justify-content: center;
    border-bottom: 1rpx solid #e5e5e5;
  }

  .header-title {
    font-size: 32rpx;
    font-weight: bold;
    color: #333;
  }

  .alarm-list {
    padding: 20rpx 0;
  }
</style>