import { domain, wxRequest, appid } from '../../utils/util.js';
Page({
  data: {},

  onShow: function name(params) {
    wx.login({
      timeout: 0,
    });
  },
  wxLogin: function ({ detail }) {
    wx.checkSession({
      success: (res) => {
        wx.login({
          success: ({ code }) => {
            wxRequest({
              url: `${domain}/store/wxLogin`,
              method: 'POST',
              data: { code, subAppId: appid, encryptedData: detail.encryptedData, iv: detail.iv }
            }).then((content ) => {
              wx.setStorageSync('authorization', content.authorization);
              wx.setStorageSync('info', content.info);
              wx.reLaunch({
                url: `/pages/login/stores?customerId=${content.info.cid}`
              });
            });
          },
          fail: (res) => {
            wx.showModal({
              title: '警告',
              content: '获取微信code失败',
              confirmColor: '#e64340',
              showCancel: false,
            })
          },
        })
      },
      fail: () => {
        wx.login();
        wx.showModal({
          title: '警告',
          content: '微信CODE超时,请重试！',
          confirmColor: '#e64340',
          showCancel: false,
        })
      }
    })
  },
})
