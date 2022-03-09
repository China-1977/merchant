import { wxLogin, domain, wxRequest } from '../../utils/util.js';
Page({
  data: {
    domain,
  },
  onShow: function (options) {
    wxLogin().then(({ authorization, info }) => {
      wxRequest({
        url: `${domain}/shop/carts/getStores`,
        header: { authorization, aid: info.aid }
      }).then((stores) => {
        this.setData({
          stores
        })
      })
    })
  },
})