import { domain, getSites, app } from '../../utils/util.js';
Page({
  data: {
    domain,
    sites: [],
    name: '请选择地理位置',
    keyword: ''
  },

  onLoad: function (options) {
    this.init();
  },

  onPullDownRefresh: function () {
    this.init();
    wx.stopPullDownRefresh();
  },

  init: function () {
    const { address, latitude, longitude, name } = app.globalData;
    if (latitude && longitude && address && name) {
      getSites(longitude, latitude, 0, this.data.keyword).then((sites) => {
        this.setData({
          address, latitude, longitude, name, number: 0, last: false, sites
        })
        app.globalData.address = address;
        app.globalData.latitude = latitude;
        app.globalData.longitude = longitude;
        app.globalData.name = name;
      });
    } else {
      wx.authorize({
        scope: 'scope.userLocation',
        success: (res) => {
          wx.getLocation({
            type: 'gcj02',
            success: (res) => {
              wx.chooseLocation({
                latitude: res.latitude,
                longitude: res.longitude,
                success: ({ address, latitude, longitude, name }) => {
                  getSites(longitude, latitude, 0, this.data.keyword).then((sites) => {
                    this.setData({
                      address: address === '' ? '默认地址' : address, latitude, longitude, name: name === '' ? '默认地址' : name, number: 0, last: false, sites
                    })
                    app.globalData.name = name === '' ? '默认地址' : name;
                    app.globalData.address = address === '' ? '默认地址' : address;
                    app.globalData.latitude = latitude;
                    app.globalData.longitude = longitude;
                  });
                },
                fail: () => {
                  getSites(res.longitude, res.latitude, 0, this.data.keyword).then((sites) => {
                    this.setData({
                      address: '默认地址',
                      latitude: res.latitude,
                      longitude: res.longitude,
                      name: '默认地址',
                      number: 0,
                      last: false,
                      sites
                    })
                    app.globalData.name = '默认地址';
                    app.globalData.address = '默认地址';
                    app.globalData.latitude = res.latitude;
                    app.globalData.longitude = res.longitude;
                  });
                }
              })
            }
          })
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
    }
  },

  onReachBottom: function () {
    if (this.data.last) {
    } else {
      let { latitude, longitude, number, keyword } = this.data;
      number = number + 1;
      getSites(longitude, latitude, number, keyword).then((sites) => {
        this.setData({
          number, last: sites.length == 0,
          sites: [...this.data.sites, ...sites],
        });
      });
    }
  },

  chooseLocation: function () {
    wx.authorize({
      scope: 'scope.userLocation',
      success: (res) => {
        wx.chooseLocation({
          latitude: this.data.latitude,
          longitude: this.data.longitude,
          success: ({ address, latitude, longitude, name }) => {
            this.setData({
              address, latitude, longitude, name
            })
          },
          fail: (res) => {
            const { address, latitude, longitude, name } = app.globalData;
            if (latitude && longitude && address && name) {

            } else {
              wx.getLocation({
                type: 'gcj02',
                success: (res) => {
                  getSites(res.longitude, res.latitude, 0, this.data.keyword).then((sites) => {
                    this.setData({
                      address: '默认地址',
                      latitude: res.latitude,
                      longitude: res.longitude,
                      name: '默认地址',
                      number: 0,
                      last: false,
                      sites
                    })
                    app.globalData.name = '默认地址';
                    app.globalData.address = '默认地址';
                    app.globalData.latitude = res.latitude;
                    app.globalData.longitude = res.longitude;
                  });
                }
              })
            }
          }
        })
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

  search: function ({ detail }) {
    const { address, latitude, longitude, name } = app.globalData;
    if (latitude && longitude && address && name) {
      getSites(longitude, latitude, 0, detail.value.keyword).then((sites) => {
        this.setData({
          address, latitude, longitude, name, number: 0, last: false, sites
        })
        app.globalData.address = address;
        app.globalData.latitude = latitude;
        app.globalData.longitude = longitude;
        app.globalData.name = name;
      });
    } else {
      wx.authorize({
        scope: 'scope.userLocation',
        success: (res) => {
          wx.getLocation({
            type: 'gcj02',
            success: (res) => {
              wx.chooseLocation({
                latitude: res.latitude,
                longitude: res.longitude,
                success: ({ address, latitude, longitude, name }) => {
                  getSites(longitude, latitude, 0, detail.value.keyword).then((sites) => {
                    this.setData({
                      address: address === '' ? '默认地址' : address, latitude, longitude, name: name === '' ? '默认地址' : name, number: 0, last: false, sites
                    })
                    app.globalData.name = name === '' ? '默认地址' : name;
                    app.globalData.address = address === '' ? '默认地址' : address;
                    app.globalData.latitude = latitude;
                    app.globalData.longitude = longitude;
                  });
                },
                fail: () => {
                  getSites(res.longitude, res.latitude).then((sites) => {
                    this.setData({
                      address: '默认地址',
                      latitude: res.latitude,
                      longitude: res.longitude,
                      name: '默认地址',
                      number: 0,
                      last: false,
                      sites
                    })
                    app.globalData.name = '默认地址';
                    app.globalData.address = '默认地址';
                    app.globalData.latitude = res.latitude;
                    app.globalData.longitude = res.longitude;
                  });
                }
              })
            }
          })
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
    }
  },
  inputChange: function ({ detail }) {
    this.setData({
      keyword: detail.value
    })
  }
});
