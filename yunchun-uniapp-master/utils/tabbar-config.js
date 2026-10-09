export const TABBAR_CONFIG = {
  '负责人': [
    {
      pagePath: '/pages/index/index',
      iconPath: '/static/tabbar/home.png',
      selectedIconPath: '/static/tabbar/home-active.png',
      iconText: '🏠',
      text: '首页'
    },
    {
      pagePath: '/pages/energy-analysis/energy-analysis',
      iconPath: '/static/tabbar/energy.png',
      selectedIconPath: '/static/tabbar/energy-active.png',
      iconText: '📊',
      text: '能耗分析'
    },
    {
      pagePath: '/pages/management/management',
      iconPath: '/static/tabbar/staff.png',
      selectedIconPath: '/static/tabbar/staff-active.png',
      iconText: '👥',
      text: '管理'
    },
    {
      pagePath: '/pages/my/my',
      iconPath: '/static/tabbar/my.png',
      selectedIconPath: '/static/tabbar/my-active.png',
      iconText: '👤',
      text: '我的'
    }
  ],
  '普通员工': [
    {
      pagePath: '/pages/index/index',
      iconPath: '/static/tabbar/home.png',
      selectedIconPath: '/static/tabbar/home-active.png',
      iconText: '🏠',
      text: '首页'
    },
    {
      pagePath: '/pages/alarm/alarm',
      iconPath: '/static/tabbar/alarm.png',
      selectedIconPath: '/static/tabbar/alarm-active.png',
      iconText: '⚠️',
      text: '报警'
    },
    {
      pagePath: '/pages/config/config',
      iconPath: '/static/tabbar/config.png',
      selectedIconPath: '/static/tabbar/config-active.png',
      iconText: '⚙️',
      text: '控制'
    },
    {
      pagePath: '/pages/my/my',
      iconPath: '/static/tabbar/my.png',
      selectedIconPath: '/static/tabbar/my-active.png',
      iconText: '👤',
      text: '我的'
    }
  ],
  '管理员': [
    {
      pagePath: '/pages/index/index',
      iconPath: '/static/tabbar/home.png',
      selectedIconPath: '/static/tabbar/home-active.png',
      iconText: '🏠',
      text: '首页'
    },
    {
      pagePath: '/pages/boiler-home/boiler-home',
      iconPath: '/static/tabbar/boiler.png',
      selectedIconPath: '/static/tabbar/boiler-active.png',
      iconText: '🔥',
      text: '锅炉'
    },
    {
      pagePath: '/pages/energy-analysis/energy-analysis',
      iconPath: '/static/tabbar/energy.png',
      selectedIconPath: '/static/tabbar/energy-active.png',
      iconText: '📊',
      text: '能耗分析'
    },
    {
      pagePath: '/pages/management/management',
      iconPath: '/static/tabbar/staff.png',
      selectedIconPath: '/static/tabbar/staff-active.png',
      iconText: '👥',
      text: '管理'
    },
    {
      pagePath: '/pages/my/my',
      iconPath: '/static/tabbar/my.png',
      selectedIconPath: '/static/tabbar/my-active.png',
      iconText: '👤',
      text: '我的'
    }
  ]
}

export function getTabbarList(role) {
  return TABBAR_CONFIG[role] || TABBAR_CONFIG['普通员工']
}