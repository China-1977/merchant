import { domain, wxRequest } from '../../utils/util.js';
Page({
  data: {
    sites: [],
    lock: false,
    scrollLeft: 0,
  },
  onLoad: function (options) {
    wxRequest({
      url: `${domain}/shop/sites`,
    }).then((sites) => {
      this.setData({
        sites
      });
    });
  },

  changeAddress: function (e) {
    let pages = getCurrentPages();//当前页面栈
    let prevPage = pages[pages.length - 2];//上一页面
    const site = this.data.sites[e.detail.value]
    prevPage.setData({
      site
    });
    wx.navigateBack({
      delta: 1
    })
  }
});