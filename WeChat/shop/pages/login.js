import { domain, appid, wxRequest } from '../utils/util.js'
Page({
  data: {},

  onShow: function name(params) {
    wx.login();
  },

  wxLogin: function ({ detail }) {
    if (detail.encryptedData && detail.iv) {
      wx.checkSession({
        success(res) {
          wx.login({
            success: ({ code }) => {
              wxRequest({
                url: `${domain}/shop/wxLogin`,
                method: 'POST',
                data: { code, subAppId: appid, encryptedData: detail.encryptedData, iv: detail.iv }
              }).then((content) => {
                wx.setStorageSync('authorization', content.authorization);
                wx.setStorageSync('info', content.info);
                wx.reLaunch({
                  url: '/pages/index/index',
                });
              });
            },
            fail: () => {
              wx.showModal({
                title: '警告',
                content: '获取微信code失败',
                confirmColor: '#e64340',
                showCancel: false,
              })
            },
          })
        },
        fail(res) {
          wx.showModal({
            title: '警告',
            content: '微信CODE超时,请重试！',
            confirmColor: '#e64340',
            showCancel: false,
          })
        }
      })
    } else {
      wx.showModal({
        title: '警告',
        content: '登录失败',
        confirmColor: '#e64340',
        showCancel: false,
      })
    }

  },
})