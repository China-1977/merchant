import { domain, checkStore, wxRequest } from '../../utils/util.js';
Page({
  data: {
    sites: [],
    lock: false,
    scrollLeft: 0,
  },
  onLoad: function (options) {
    checkStore().then(({ authorization, info }) => {
      wxRequest({
        url: `${domain}/store/sites`,
        header: { authorization, ...info },
      }).then((sites) => {
        this.setData({
          sites
        });
      });
    })
  },
 
});