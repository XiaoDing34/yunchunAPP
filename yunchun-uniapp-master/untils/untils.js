let token = 'yunchun'

export function setToken(value) {
  uni.setStorageSync(token, value)
}

export function getToken() {
  return uni.getStorageSync(token)
}