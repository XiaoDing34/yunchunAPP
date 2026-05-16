// common/request.js
import config from './config.js';
import {
  getToken
} from './untils.js';

let isLoading = false;

const request = (options) => {
  if (!isLoading) {
    uni.showLoading({
      title: '加载中...',
      mask: true
    });
    isLoading = true;
  }

  return new Promise((resolve, reject) => {
    const token = getToken(); // 取出存储的 token 字符串
    const header = {
      'Content-Type': 'application/json',
      ...options.header
    };
    if (token) {
      // 直接将字符串赋值给 'satoken' 这个 header key
      header['Authorization'] = token;
    }

    uni.request({
      url: config.baseURL + options.url,
      method: options.method || 'GET',
      data: options.data || {},
      header,
      success: (res) => {
        if (res.statusCode === 200) {
          // 假设业务成功码为 200 或 0（可根据需要定制，或者全部交由业务层判断）
          if (res.data.code === 200 || res.data.code === 0) {
            console.log('请求成功', res.data)
            resolve(res.data);
          } else {
            // 业务失败：不再弹 Toast，而是把错误对象通过 reject 传递
            reject({
              type: 'business',
              statusCode: res.statusCode,
              data: res.data
            });
          }
        } else if (res.statusCode === 401) {
          // 未授权：同样不自动弹窗，只 reject，让业务层决定是否跳转登录
          console.log('请求失败2', res.data)
          reject({
            type: 'unauthorized',
            statusCode: 401,
            data: res.data
          });
        } else {
          console.log('请求失败3', res.data)
          reject({
            type: 'http',
            statusCode: res.statusCode,
            data: res.data
          });
        }
      },
      fail: (err) => {
        // 网络错误：同样不自动弹 Toast
        console.log('请求失败4', err)
        reject({
          type: 'network',
          error: err
        });
      },
      complete: () => {
        if (isLoading) {
          uni.hideLoading();
          isLoading = false;
        }
      }
    });
  });
};

// 导出便捷方法
export default {
  get(url, data = {}, header = {}) {
    return request({
      url,
      method: 'GET',
      data,
      header
    });
  },
  post(url, data = {}, header = {}) {
    return request({
      url,
      method: 'POST',
      data,
      header
    });
  },
  put(url, data = {}, header = {}) {
    return request({
      url,
      method: 'PUT',
      data,
      header
    });
  },
  delete(url, data = {}, header = {}) {
    return request({
      url,
      method: 'DELETE',
      data,
      header
    });
  }
};