import { windowWidth, checkStore, domain, wxRequest, chooseImages } from '../../utils/util.js';
Page({
  data: {
    domain,pictures:[]
  },

  onLoad: function (options) {
    // const product = {
    //   name: "苹果",
    //   label: "水果",
    //   price: "0.01",
    //   priceUnit: "元/斤",
    //   average: 0.01,
    //   averageUnit: "元/斤",
    //   stock: 100,
    //   min: 1,
    //   max: 10,
    //   description: '描述',
    //   pictures: ["picture/5f7718b17df1fd2713364cfc/f1e5b71b609a059f09d58660f5a77c64.png"],
    //   vid: 'q3280h3clbq'
    // };
    // this.setData({
    //   ...product
    // })
  },

  chooseImages: function (e) {
    const id = e.currentTarget.id;
    let count = e.currentTarget.dataset.count
    const length = this.data[id].length;
    count = count - length;
    checkStore().then(({ authorization, info }) => {
      chooseImages(authorization, info, count, `${domain}/store/products/uploadPicture`).then((filePath) => {
        const pictures = this.data[id];
        if (!pictures.includes(filePath)) {
          this.setData({
            [`${id}[${length}]`]: filePath
          })
        }
      })
    })
  },

  deletePictures: function (e) {
    const id = e.currentTarget.id;
    const index = e.currentTarget.dataset.index;
    const files = this.data[id];
    files.splice(index, 1);
    this.setData({
      [id]: files
    })
  },

  clearPictues: function (e) {
    const id = e.currentTarget.id;
    this.setData({
      [id]: []
    })
  },

  textareaInput: function (e) {
    const id = e.currentTarget.id;
    const value = e.detail.value;
    this.setData({
      [id]: value
    })
  },

  bindInput: function (e) {
    const id = e.currentTarget.id;
    let count = e.currentTarget.dataset.count;
    const value = e.detail.value;
    if (value.length === count) {
      this.setData({
        [id]: value
      })
    }
  },

  createProduct: function (e) {
    const { description, pictures, id } = this.data;
    const data = { ...e.detail.value, description, pictures, id }
    checkStore().then(({ authorization, info }) => {
      wxRequest({
        url: `${domain}/store/products?storeId=${info.sid}`,
        data,
        method: "POST",
        header: { authorization, ...info },
      }).then((data) => {
        this.setData({
          ...data
        });
        let pages = getCurrentPages();
        let prevPage = pages[pages.length - 2];
        let { products = [] } = prevPage.data;
        products = [data, ...products];
        prevPage.setData({
          products
        });
        wx.navigateBack({
          delta: 1
        });
      })
    })
  },

  resetForm: function (e) {
    this.setData({
      pictures: [],
      vid: null,
      description: ''
    })
  },
})
