const { windowWidth } = wx.getSystemInfoSync();
App({
  globalData: {
    windowWidth
  },
  onLaunch: function (e) {
  },

  onShow: function (e) {
    const updateManager = wx.getUpdateManager();
    updateManager.onCheckForUpdate(function (res) {
      if (res.hasUpdate) {
        wx.showLoading({
          mask: true,
          title: '应用更新中',
        })
      }
    })
    updateManager.onUpdateReady(function () {
      wx.hideLoading({
        success: (res) => {
          updateManager.applyUpdate()
        }
      })
    })
    updateManager.onUpdateFailed(function () {
      wx.showToast({
        title: '小程序加载失败',
        icon: 'error',
        duration: 2000
      })
    })
  },
})
