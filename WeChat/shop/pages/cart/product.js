import { domain, wxLogin, wxRequest, getCarts } from '../../utils/util.js';
Page({
  data: {
    domain,
    options: {},
    store: {},
    sum: '0.00',
    products: [],
    checkAll: false,
    currentID: -1,
    deleteCart: [
      {
        type: "default",
        text: '删除',
        extClass: 'default',
      }
    ],
  },
  onLoad: function (options) {
    if (options.storeId) {
      wxLogin().then(({ authorization, info }) => {
        getCarts(info.aid, options.storeId, authorization).then((data) => {
          this.setData({
            ...data
          })
        })
      })
    } else {
      wx.reLaunch({
        url: '/pages/cart/cart'
      });
    }
  },

  addCount: function (e) {
    const index = e.currentTarget.id;
    this.updateCart(index, 1);
  },

  subtractCount: function (e) {
    const index = e.currentTarget.id;
    this.updateCart(index, -1);
  },

  updateCart: function (index, count) {
    wxLogin().then(({ authorization, info }) => {
      let cart = this.data.products[index].cart;
      wxRequest({
        url: `${domain}/shop/carts`,
        method: 'POST',
        header: { authorization, aid: info.aid },
        data: { id: cart.id, storeId: cart.storeId, productId: cart.productId, num: cart.num + count },
      }).then((data) => {
        getCarts(info.aid, cart.storeId, authorization).then((data) => {
          this.setData({
            ...data
          })
        })
      });
    })
  },

  checkedChange: function (e) {
    const index = e.currentTarget.dataset.index;
    const cart = this.data.products[index].cart;
    wxLogin().then(({ authorization, info }) => {
      wxRequest({
        url: `${domain}/shop/carts/${cart.id}/setChecked?checked=${cart.checked}&storeId=${cart.storeId}`,
        method: 'POST',
        header: { authorization, aid: info.aid },
      }).then((data) => {
        getCarts(info.aid, cart.storeId, authorization).then((data) => {
          this.setData({
            ...data
          })
        })
      })
    })
  },

  switchChange: function (e) {
    wxLogin().then(({ authorization, info }) => {
      const store = this.data.store;
      const checkAll = e.detail.value;
      wxRequest({
        url: `${domain}/shop/carts/setCheckAll?checkAll=${checkAll}&storeId=${store.id}`,
        method: 'POST',
        header: { authorization, aid: info.aid },
      }).then((data) => {
        getCarts(info.aid, store.id, authorization).then((data) => {
          this.setData({ ...data });
        });
      })
    })
  },

  bindButtonTap: function (e) {
    wxLogin().then(({ authorization, info }) => {
      let { products } = this.data;
      const index = e.currentTarget.id;
      const product = products[index];
      const cart = product.cart;
      wxRequest({
        url: `${domain}/shop/carts/${cart.id}`,
        method: 'DELETE',
        header: { authorization, aid: info.aid },
      }).then((data) => {
        getCarts(info.aid, cart.storeId, authorization).then((data) => {
          this.setData({
            ...data
          })
        })
      })
    })
  }
})
