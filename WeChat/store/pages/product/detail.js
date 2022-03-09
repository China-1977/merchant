import { windowWidth, checkStore, domain, wxRequest } from '../../utils/util.js';
Page({
  data: {
    domain,
    windowWidth
  },

  onLoad: function (options) {
    checkStore().then(({ authorization, info }) => {
      wxRequest({
        url: `${domain}/store/products/${options.id}`,
        method: "GET",
        header: { authorization,...info },
      }).then((product) => {
        this.setData({
          index: options.index,
          product
        })
      })
    });
  },
})