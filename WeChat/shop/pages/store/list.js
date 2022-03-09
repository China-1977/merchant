import { domain, getStores } from '../../utils/util.js';
Page({
  data: {
    domain,
    stores: []
  },

  onShow: function (options) {
    wx.getLocation({
      type: 'gcj02',
      success: (res) => {
        getStores(res.longitude, res.latitude).then((stores) => {
          this.setData({
            stores,
            latitude: res.latitude,
            longitude: res.longitude
          })
        });
      }
    })
  },

  changeStore: function (e) {
    const store = this.data.stores[e.detail.value].content
    wx.setStorageSync('store',store);
    wx.navigateBack({
      delta: 1
    })
  },


  onPullDownRefresh: function () {
    const { latitude, longitude } = this.data;
    getStores(longitude, latitude).then((data) => {
      this.setData({
        number: 0,
        last: false,
        stores: data
      })
      wx.stopPullDownRefresh()
    });
  },

  onReachBottom: function () {
    if (this.data.last) {
    } else {
      const { latitude, longitude, number } = this.data;
      getStores(longitude, latitude, number + 1).then((data) => {
        if (data.length === 0) {
          this.setData({
            last: true,
          });
        } else {
          this.setData({
            number,
            stores: [...this.data.stores, ...data],
          });
        }
      });
    }
  },
})