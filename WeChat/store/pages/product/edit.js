import { checkStore, domain, wxRequest, chooseImages } from '../../utils/util.js';

Page({
  data: { domain, },

  chooseImages: function (e) {
    const id = e.currentTarget.id;
    let count = e.currentTarget.dataset.count
    const length = this.data[id].length;
    count = count - length;
    checkStore().then(({ authorization, info }) => {
      chooseImages(authorization, info, count, `${domain}/store/stores/uploadPicture`).then((filePath) => {
        const pictures = this.data[id];
        if (!pictures.includes(filePath)) {
          this.setData({
            [`${id}[${length}]`]: filePath
          });
        }
      });
    });
  },

  deletePictures: function (e) {
    const id = e.currentTarget.id;
    const index = e.currentTarget.dataset.index;
    const pictures = this.data[id];
    pictures.splice(index, 1);
    this.setData({
      [id]: pictures
    });
  },

  clearPictues: function (e) {
    const id = e.currentTarget.id;
    this.setData({
      [id]: []
    });
  },

  resetForm: function (e) {
    this.setData({
      ...this.data.product
    });
  },

  bindInput: function (e) {
    const id = e.currentTarget.id;
    let count = e.currentTarget.dataset.count;
    const value = e.detail.value;
    if (value.length == count) {
      this.setData({
        [id]: value
      })
    }
  },

  textareaInput: function (e) {
    const id = e.currentTarget.id;
    const value = e.detail.value;
    this.setData({
      [id]: value
    });
  },

  updateProduct: function (e) {
    const { index, description, pictures, id } = this.data;
    checkStore().then(({ authorization, info }) => {
      wxRequest({
        url: `${domain}/store/products/${id}`,
        method: "PUT",
        data: { ...e.detail.value, description, pictures, id },
        header: { authorization, ...info },
      }).then((product) => {
        this.setData({
          ...product,
          product
        });
        let pages = getCurrentPages();//当前页面栈
        let detail = pages[pages.length - 2];//详情页面
        detail.setData({
          product
        });
        let list = pages[pages.length - 3];//列表页面
        const key = `products[${index}]`
        list.setData({
          [key]: product
        });
      });
    });
  },

  onLoad: function (options) {
    checkStore().then(({ authorization, info }) => {
      wxRequest({
        url: `${domain}/store/products/${options.id}`,
        header: { authorization, ...info },
      }).then((product) => {
        this.setData({
          index: options.index,
          ...product,
          product
        })
      });
    })
  },
})