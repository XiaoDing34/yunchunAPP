<template>
  <view class="alarm-container">
    <view class="voice-bar">
      <view class="voice-broadcast-btn" @click="handlePlayAll">
        <text class="voice-broadcast-icon">🔊</text>
        <text>语音播报所有报警</text>
      </view>
    </view>
    <view class="alarm-list">
      <warn-item v-for="item in warnMessageList" :key="item.id" :warnId="item.id" :warnLevel="item.warnLevel"
        :messageTitle="item.messageTitle" :sender="item.sender" :createTime="item.createTime"
        :messageContent="item.messageContent" :advice="item.advice"
        :feedbackStatus="item.feedbackStatus" :handleStatus="item.handleStatus"
        :audioUrl="item.audioUrl"
        @delete="handleDelete" @feedback="handleFeedback" />
    </view>

    <!-- 空状态提示 -->
    <view v-if="warnMessageList.length === 0" class="empty-state">
      <view class="empty-icon">📭</view>
      <text class="empty-text">暂无报警信息</text>
      <text class="empty-hint">当前没有任何报警记录</text>
    </view>

    <CustomTabbar />
  </view>
</template>

<script>
  import api from '@/untils/request.js'
  import WarnItem from '@/compoents/warn-item/warn-item.vue'
  import CustomTabBar from '@/components/custom-tab-bar/custom-tab-bar.vue'
  export default {
    components: {
      WarnItem,
      CustomTabbar: CustomTabBar
    },
    data() {
      return {
        warnMessageList: [],
        audioCtx: null,
        playQueue: [],
        playIndex: 0
      };
    },
    async onLoad() {
      await this.getAllMessage()
      this.getFaultDeviceValue()
      // 数据加载完成后自动播放所有报警语音
      this.handlePlayAll()
    },
    onUnload() {
      this.destroyAudio();
    },
    onHide() {
      this.destroyAudio();
    },
    onReady() {
      // DOM 挂载完成，数据已在 onLoad 中加载，无需重复请求
    },
    onPullDownRefresh() {
      this.getAllMessage().finally(() => {
          uni.stopPullDownRefresh()
        }),
        this.getFaultDeviceValue().finally(() => {
          uni.stopPullDownRefresh()
        })
    },
    methods: {
      handlePlayAll() {
        console.log('handlePlayAll 被调用，warnMessageList 长度:', this.warnMessageList.length);
        // 先清理可能残留的旧上下文，避免回调串扰
        this.destroyAudio();
        if (this.warnMessageList.length === 0) {
          uni.showToast({ title: '暂无报警信息', icon: 'none' });
          return;
        }
        // 按报警等级降序排列，等级高的先播报
        const queue = this.warnMessageList
          .filter(item => item.audioUrl)
          .slice()
          .sort((a, b) => {
            const la = parseInt(a.warnLevel) || 0;
            const lb = parseInt(b.warnLevel) || 0;
            return lb - la;
          });
        if (queue.length === 0) {
          uni.showToast({ title: '暂无报警语音', icon: 'none' });
          return;
        }
        this.playQueue = queue;
        this.playIndex = 0;
        this.playNext();
      },
      playNext() {
        if (this.playIndex >= this.playQueue.length) {
          console.log('所有报警语音播报完毕');
          return;
        }
        const alert = this.playQueue[this.playIndex];
        console.log('playNext 准备播放第', this.playIndex + 1, '条，audioUrl:', alert.audioUrl);
        // 清理旧上下文：先置空阻断回调，再移除监听，最后销毁
        if (this.audioCtx) {
          const old = this.audioCtx;
          this.audioCtx = null;
          try { old.offEnded(); } catch (e) {}
          try { old.offError(); } catch (e) {}
          try { old.offPlay(); } catch (e) {}
          try { old.stop(); } catch (e) {}
          try { old.destroy(); } catch (e) {}
        }
        const audioCtx = uni.createInnerAudioContext();
        audioCtx.obeyMuteSwitch = false;
        audioCtx.onPlay(() => {
          if (this.audioCtx !== audioCtx) return;
          console.log(`播放第 ${this.playIndex + 1}/${this.playQueue.length} 条报警语音`);
        });
        audioCtx.onError((res) => {
          if (this.audioCtx !== audioCtx) return;  // 防止串扰
          console.error('语音播放失败', res, 'audioUrl:', alert.audioUrl);
          uni.showToast({ title: `第${this.playIndex + 1}条语音播放失败`, icon: 'none' });
          uni.vibrateLong();
          try { audioCtx.destroy(); } catch (e) {}
          this.audioCtx = null;
          this.playIndex++;
          this.playNext();
        });
        audioCtx.onEnded(() => {
          if (this.audioCtx !== audioCtx) return;  // 防止串扰
          try { audioCtx.destroy(); } catch (e) {}
          this.audioCtx = null;
          this.playIndex++;
          this.playNext();
        });

        this.audioCtx = audioCtx;
        // 设置 src 后显式调用 play()，部分平台 autoplay 对二次创建的实例不生效
        audioCtx.src = alert.audioUrl;
        audioCtx.play();
      },
      destroyAudio() {
        if (this.audioCtx) {
          const old = this.audioCtx;
          this.audioCtx = null;
          try { old.offEnded(); } catch (e) {}
          try { old.offError(); } catch (e) {}
          try { old.offPlay(); } catch (e) {}
          try { old.stop(); } catch (e) {}
          try { old.destroy(); } catch (e) {}
        }
      },
      async getAllMessage() {
        try {
          const res = await api.get('/warnMessage/getAllMessage')
          if (res.code == 200) {
            this.warnMessageList = res.data
            console.log('getAllMessage 成功调用，返回数据:', res)
            return res.data
          } else {
            console.log('getAllMessage 调用失败:', res)
            return null
          }
        } catch (err) {
          console.log('getAllMessage 异常:', err.message)
          return null
        }
      },
      async getFaultDeviceValue() {
        try {
          const res = await api.get('/deviceValue/getFaultDeviceValue')
          if (res.code == 200) {
            console.log('getFaultDeviceValue 成功调用，返回故障数据:', res.data)

            // 如果返回数据不为空，提取notes字段并弹窗提示
            if (res.data && res.data.length > 0) {
              const notesList = res.data.map(item => item.notes).filter(n => n)
              if (notesList.length > 0) {
                const messages = notesList.map(notes => `检测到${notes}，请迅速联系工作人员处理！`)
                const content = messages.join('\n\n')

                uni.showModal({
                  title: '故障告警',
                  content: content,
                  showCancel: false,
                  success: (res) => {
                    if (res.confirm) {
                      console.log('用户点击确定，弹窗关闭')
                    }
                  }
                })
              }
            }

            return res.data
          } else {
            console.log('getFaultDeviceValue 调用失败:', res)
            return null
          }
        } catch (err) {
          console.log('getFaultDeviceValue 异常:', err.message)
          return null
        }
      },
      handleWarnClick(item) {
        console.log('点击报警项:', item)
      },
      async handleDelete(id) {
        try {
          const res = await api.delete(`/warnMessage/delWarnMessage?id=${id}`)
          if (res.code == 200) {
            uni.showToast({
              title: '删除成功',
              icon: 'success'
            })
            this.getAllMessage()
          } else {
            uni.showToast({
              title: '删除失败',
              icon: 'none'
            })
          }
        } catch (err) {
          console.log(err.message)
          uni.showToast({
            title: '删除失败',
            icon: 'none'
          })
        }
      },
      async handleFeedback(data) {
        try {
          const params = `id=${data.id}`
            + (data.feedbackStatus != null ? `&feedbackStatus=${data.feedbackStatus}` : '')
            + (data.handleStatus != null ? `&handleStatus=${data.handleStatus}` : '')
          const res = await api.put(`/warnMessage/feedback?${params}`)
          if (res.code == 200) {
            uni.showToast({ title: '反馈成功', icon: 'success' })
            this.getAllMessage()
          } else {
            uni.showToast({ title: res.message || '反馈失败', icon: 'none' })
          }
        } catch (err) {
          uni.showToast({ title: '反馈失败', icon: 'none' })
        }
      }
    }
  };
</script>

<style scoped>
  .alarm-container {
    min-height: 100vh;
    background-color: #f5f5f5;
    padding-bottom: 140rpx;
  }

  .voice-bar {
    padding: 20rpx 24rpx 0;
  }

  .voice-broadcast-btn {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 10rpx;
    width: 100%;
    height: 84rpx;
    line-height: 84rpx;
    border-radius: 42rpx;
    background: linear-gradient(135deg, #10b981 0%, #059669 100%);
    color: #fff;
    font-size: 30rpx;
    font-weight: 600;
    border: none;
  }

  .voice-broadcast-btn::after {
    border: none;
  }

  .voice-broadcast-btn:active {
    opacity: 0.85;
  }

  .voice-broadcast-icon {
    font-size: 32rpx;
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

  .empty-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 120rpx 40rpx;
    background-color: #ffffff;
    margin: 20rpx;
    border-radius: 24rpx;
    box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);
  }

  .empty-icon {
    font-size: 120rpx;
    margin-bottom: 32rpx;
  }

  .empty-text {
    font-size: 34rpx;
    font-weight: 600;
    color: #374151;
    margin-bottom: 16rpx;
  }

  .empty-hint {
    font-size: 28rpx;
    color: #9CA3AF;
  }
</style>