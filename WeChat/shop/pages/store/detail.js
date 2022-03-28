import { windowWidth,domain,wxLogin,wxRequest } from '../../utils/util.js';
Page({
  data: {
    windowWidth,domain
  },

  onLoad: function (options) {
    wxLogin().then(({ authorization, info }) => {
      wxRequest({
        url: `${domain}/shop/stores/${options.id}`,
        header: { authorization, aid: info.aid },
      }).then((data) => {
        this.setData({
          ...options,
          store: data
        });
      });
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