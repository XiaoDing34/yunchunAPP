// utils/userCache.js
import api from '@/untils/request.js'
class UserCache {
  constructor() {
    this.userInfo = null;
    this.loadPromise = null;
  }

  // 获取用户信息（确保有最新数据）
  async getUserInfo(forceRefresh = false) {
    if (forceRefresh || !this.userInfo) {
      await this.refresh();
    }
    return this.userInfo;
  }

  // 刷新数据
  async refresh() {
    const res = await api.get('/appUser/info');
    if (res.code === 200) {
      this.userInfo = res.data;
      uni.setStorageSync('userInfo', res.data);
    }
    return this.userInfo;
  }

  // 更新本地缓存（头像/昵称修改后调用）
  updateLocal(newInfo) {
    this.userInfo = {
      ...this.userInfo,
      ...newInfo
    };
    uni.setStorageSync('userInfo', this.userInfo);
  }
}

export default new UserCache();