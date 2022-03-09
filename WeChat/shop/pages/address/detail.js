import { domain, wxLogin, wxRequest } from '../../utils/util.js';
Page({
  data: {
    location: {},
    code: [],
    value: []
  },

  onLoad: function (options) {
    this.setData({
      code: [],
      value: []
    })
    if (options.index) {
      let pages = getCurrentPages();//当前页面栈
      let prevPage = pages[pages.length - 2];//上一页面
      const address = prevPage.data.addresses[options.index];
      this.setData({
        ...address, index: options.index
      })
    }
  },

  chooseLocation: function (e) {
    wx.authorize({
      scope: 'scope.userLocation',
      success: (res) => {
        const { location, detail } = this.data;
        if (location && location.x && location.y) {
          wx.chooseLocation({
            longitude: location.x,
            latitude: location.y,
            name: detail,
            success: (res) => {
              this.setData({ location: { x: res.longitude, y: res.latitude }, detail: res.address, name: res.name });
            }
          })
        } else {
          wx.getLocation({
            type: 'gcj02',
            success: (res) => {
              wx.chooseLocation({
                longitude: parseFloat(res.longitude),
                latitude: parseFloat(res.latitude),
                success: (res) => {
                  this.setData({ location: { x: res.longitude, y: res.latitude }, detail: res.address, name: res.name });
                }
              })
            }
          });
        }
      },
      fail: (res) => {
        wx.showModal({
          title: '提示',
          content: '请允许小程序使用位置消息',
          confirmColor: '#e64340',
          showCancel: false,
          success: () => {
            wx.openSetting();
          }
        })
      }
    })


  },

  saveAddress: function (e) {
    wxLogin().then(({ authorization, info }) => {
      const { value, code, postcode, location, index } = this.data;
      let address = { ...e.detail.value, value, code, postcode, location };
      wxRequest({
        url: `${domain}/shop/addresses`,
        header: { authorization, aid: info.aid },
        data: address,
        method: "POST",
      }).then((data) => {
        let pages = getCurrentPages();//当前页面栈
        let prevPage = pages[pages.length - 2];//上一页面
        if (index) {
          const key = `addresses[${index}]`;
          prevPage.setData({
            [key]: address
          });
        } else {
          let addresses = prevPage.data.addresses;
          addresses.unshift(address);
          prevPage.setData({
            addresses
          });
        }
        this.setData({
          ...data
        });
        wx.navigateBack({
          delta: 1
        });
      });
    })
  },

  bindRegionChange: function (e) {
    this.setData({ ['code']: e.detail.code, ['postcode']: e.detail.postcode, ['value']: e.detail.value });
  },
})