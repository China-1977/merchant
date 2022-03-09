import { getProducts, windowWidth, domain, wxLogin, wxRequest } from '../../utils/util.js';
Page({
  data: {
    windowWidth, domain, store: {}, products: [], sum: '0.00', checkAll: false
  },

  onLoad: function (option) {
    getProducts(option.storeId).then((data) => {
      this.setData({ ...data });
    });
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
      let { product, cart } = this.data.products[index];
      if (cart) {
        wxRequest({
          url: `${domain}/shop/carts`,
          method: 'POST',
          header: { authorization, aid: info.aid },
          data: { id: cart.id, storeId: cart.storeId, productId: cart.productId, num: cart.num + count },
        }).then((data) => {
          getProducts(cart.storeId).then((data) => {
            this.setData({ ...data });
          });
        });
      } else {
        wxRequest({
          url: `${domain}/shop/carts`,
          method: 'POST',
          header: { authorization, aid: info.aid },
          data: { storeId: product.storeId, productId: product.id, num: 1 },
        }).then((data) => {
          getProducts(product.storeId).then((data) => {
            this.setData({ ...data });
          });
        });
      }
    })
  },

  checkedChange: function (e) {
    const index = e.currentTarget.dataset.index;
    const  cart = this.data.products[index].cart;
    wxLogin().then(({ authorization, info }) => {
      wxRequest({
        url: `${domain}/shop/carts/${cart.id}/setChecked?checked=${cart.checked}&storeId=${cart.storeId}`,
        method: 'POST',
        header: { authorization, aid: info.aid },
      }).then((data) => {
        getProducts(cart.storeId).then((data) => {
          this.setData({ ...data });
        });
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
        getProducts(store.id).then((data) => {
          this.setData({ ...data });
        });
      })
    })
  }
})
