import { domain, wxLogin, windowWidth, getProduct, wxRequest } from '../../utils/util.js';
Page({
  data: {
    windowWidth, domain, options: {}
  },
  onLoad: function (options) {
    const authorization = wx.getStorageSync('authorization');
    const info = wx.getStorageSync('info');
    getProduct(options.id, authorization, info.aid).then((product) => {
      this.setData({
        ...product, ...options
      });
    })
  },

  switch2Change: function (e) {
    wxLogin().then(({ authorization, info }) => {
      if (e.detail.value) {
        const product = this.data.product;
        wxRequest({
          url: `${domain}/shop/prefers?productId=${product.id}`,
          method: 'POST',
          header: { authorization, aid: info.aid },
        }).then((prefer) => {
          this.setData({
            prefer
          });
        });
      } else {
        const prefer = this.data.prefer;
        wxRequest({
          url: `${domain}/shop/prefers/${prefer.id}`,
          method: 'DELETE',
          header: { authorization, aid: info.aid },
        }).then(() => {
          this.setData({
            prefer: null
          });
        });
      }
    })
  },


  addCount: function () {
    this.updateCart(1);
  },

  subtractCount: function () {
    this.updateCart(-1);
  },

  updateCart: function (count) {
    wxLogin().then(({ authorization, info }) => {
      let { cart, index, product } = this.data;
      if (cart) {
        wxRequest({
          url: `${domain}/shop/carts`,
          method: 'POST',
          header: { authorization, aid: info.aid },
          data: { ...cart, num: cart.num + count },
        }).then((cart) => {
          this.setCart(cart, index, product.average, count)
        });
      } else {
        wxRequest({
          url: `${domain}/shop/carts`,
          method: 'POST',
          header: { authorization, aid: info.aid },
          data: { storeId: product.storeId, productId: product.id, num: 1 },
        }).then((cart) => {
          this.setCart(cart, index, product.average, count)
        });
      }
    })
  },

  setCart: function (cart, index, average, count) {
    this.setData({ cart });
    let pages = getCurrentPages();//当前页面栈
    let prevPage = pages[pages.length - 2];//上一页面
    let { sum } = prevPage.data;
    if (cart.checked) {
      sum = parseFloat(sum) + parseFloat(average * count);
      sum = sum.toFixed(2)
    }
    prevPage.setData({
      [`products[${index}].cart`]: cart,
      sum
    })
  }
})
