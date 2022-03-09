import { windowWidth } from '../../utils/util.js';
Page({
  data: {
    windowWidth
  },

  onReady: function () {
    let pages = getCurrentPages();//当前页面栈
    let prevPage = pages[pages.length - 2];//上一页面
    this.setData({
      ...prevPage.data
    })
  },
 
  makePhoneCall: function (e) {
    wx.makePhoneCall({
      phoneNumber: e.currentTarget.dataset.phone
    })
  },

  openLocation: function (e) {
    const { location, addressName } = this.data.store;
    wx.openLocation({
      latitude: parseFloat(location.y),
      longitude: parseFloat(location.x),
      name: addressName
    })
  }
})