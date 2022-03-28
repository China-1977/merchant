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
      let product = this.data.products[index];
      if (product.cartId) {
        wxRequest({
          url: `${domain}/shop/carts`,
          method: 'POST',
          header: { authorization, aid: info.aid },
          data: { id: product.cartId, storeId: product.storeId, productId: product.id, num: product.num + count },
        }).then((data) => {
          getProducts(product.storeId).then((data) => {
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
    const  product = this.data.products[index];
    wxLogin().then(({ authorization, info }) => {
      wxRequest({
        url: `${domain}/shop/carts/${product.cartId}/setChecked?checked=${product.checked}&storeId=${product.storeId}`,
        method: 'POST',
        header: { authorization, aid: info.aid },
      }).then((data) => {
        getProducts(product.storeId).then((data) => {
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
