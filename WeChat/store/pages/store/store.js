import {  windowWidth, checkStore, domain, wxRequest } from '../../utils/util.js';
Page({
  data: {
    domain, windowWidth
  },
  updateStatus: function (e) {
    checkStore().then(({ authorization, info }) => {
      wxRequest({
        url: `${domain}/store/stores/${info.sid}/updateStatus`,
        header: { authorization, status: (!this.data.status).toString(),...info },
        method: 'PUT'
      }).then(() => {
        this.setData({
          status: !this.data.status
        });
      })
    })
  },

  getLocation: function (e) {
    const x = e.currentTarget.dataset.x;
    const y = e.currentTarget.dataset.y;
    const name = e.currentTarget.dataset.name;
    wx.openLocation({
      latitude: parseFloat(y),
      longitude: parseFloat(x),
      name: name,
    })
  },

  onShow: function (options) {
    checkStore().then(({ authorization, info }) => {
      wxRequest({
        url: `${domain}/store/stores/${info.sid}`,
        header: { authorization,...info },
      }).then((store) => {
        this.setData({
          ...store
        })
      })
    })
  },
})