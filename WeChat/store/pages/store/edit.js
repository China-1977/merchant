import { windowWidth, checkStore, domain, wxRequest, chooseImages, chooseImage } from '../../utils/util.js';
Page({
  data: {
    domain, windowWidth, videos: [], pictures: [], openTime: '07:30', closeTime: '22:30'
  },
  bindPickerChange: function (e) {
    const id = e.currentTarget.id;
    const index = e.currentTarget.dataset.index;
    const range = e.currentTarget.dataset.range;
    const ranges = this.data[range];
    const value = e.detail.value;
    this.setData({
      [id]: ranges[value].id,
      [index]: value
    })
  },
  bindRegionChange: function (e) {
    this.setData({ ['addressCode']: e.detail.code, ['postcode']: e.detail.postcode, ['addressValue']: e.detail.value });
  },
  chooseLocation: function (e) {
    const { location, addressName } = this.data;
    if (location && location.x && location.y) {
      wx.chooseLocation({
        longitude: location.x,
        latitude: location.y,
        name: addressName,
        success: (res) => {
          this.setData({ ['location']: { x: res.longitude, y: res.latitude }, ['addressDetail']: res.address, ['addressName']: res.name });
        }
      })
    } else {
      wx.getLocation({
        type: 'gcj02',
        success: (res) => {
          wx.chooseLocation({
            longitude: parseFloat(res.longitude),
            latitude: parseFloat(res.latitude),
            success: (res) => {
              this.setData({ ['location']: { x: res.longitude, y: res.latitude }, ['addressDetail']: res.address, ['addressName']: res.name });
            }
          })
        }
      });
    }
  },

  chooseImages: function (e) {
    const id = e.currentTarget.id;
    let count = e.currentTarget.dataset.count
    const pictures = this.data[id] == null ? [] : this.data[id];
    count = count - pictures.length;
    checkStore().then(({ authorization, info }) => {
      chooseImages(authorization, info, count, `${domain}/store/stores/uploadPicture`).then((filePath) => {
        if (!pictures.includes(filePath)) {
          this.setData({
            [`${id}[${pictures.length}]`]: filePath
          })
        }
      })
    })
  },

  chooseImage: function (e) {
    const id = e.currentTarget.id;
    checkStore().then(({ authorization, info }) => {
      chooseImage(authorization, info, `${domain}/store/stores/uploadPicture`).then((filePath) => {
        this.setData({
          [`${id}`]: filePath
        })
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

  deletePicture: function (e) {
    const id = e.currentTarget.id;
    this.setData({
      [id]: null
    })
  },

  clearPictues: function (e) {
    const id = e.currentTarget.id;
    this.setData({
      [id]: []
    })
  },
  videosBindInput: function (e) {
    this.setData({
      videos: e.detail.value.split(",")
    })
  },

  updateStore: function (e) {
    const { addressValue, addressCode, location, postcode, pictures, videos } = this.data;
    const store = { ...e.detail.value, addressValue, addressCode, location, postcode, pictures, videos }
    checkStore().then(({ authorization, info }) => {
      wxRequest({
        url: `${domain}/store/stores/${info.sid}`,
        data: store,
        method: "PUT",
        header: { authorization, ...info },
      }).then((data) => {
        this.setData({
          store
        })
        let pages = getCurrentPages();//当前页面栈
        let detail = pages[pages.length - 2];//详情页面
        detail.setData({
          ...store
        });
      })
    })
  },

  onLoad: function () {
    checkStore().then(({ authorization, info }) => {
      wxRequest({
        url: `${domain}/store/stores/${info.sid}`,
        header: { authorization, ...info },
      }).then((store) => {
        this.setData({
          ...store, store
        })
      })
    })
  },

  resetForm: function (e) {
    const store = this.data.store;
    this.setData({
      ...store, index
    })
  },

  timeChange: function (e) {
    const key = e.currentTarget.id;
    const value = e.detail.value;
    this.setData({
      [key]: value
    });
  }
})