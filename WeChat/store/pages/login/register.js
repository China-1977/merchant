import { windowWidth, checkCustomer, domain, wxRequest } from '../../utils/util.js';
Page({
  data: {
    domain, windowWidth, openTime: '07:30', closeTime: '22:30', status: false, addressCode: [], addressValue: []
  },

  onLoad: function (params) {
    // const store = {
    //   shortname: "一九七七",
    //   description: "描述",
    //   licenseNumber: "91371523MA3PU9M466",
    //   openTime: "08:30",
    //   closeTime: "22:30",
    //   username: "王先生",
    //   phone: "15063517240",
    //   addressName: "星美城市广场小区2期",
    //   addressDetail: "山东省聊城市东昌府区滦河路南200米",
    //   location: {
    //     x: 116.06076945,
    //     y: 36.46128488
    //   },
    //   addressValue:['山东省','聊城市','东昌府区'],
    //   addressCode:['370000','371500','371502'],
    //   postcode: "252100",
    // };
    // this.setData({ store, ...store })
  },

  bindRegionChange: function (e) {
    this.setData({ ['addressCode']: e.detail.code, ['postcode']: e.detail.postcode, ['addressValue']: e.detail.value });
  },

  chooseLocation: function (e) {
    const { location, addressDetail } = this.data;
    if (location && location.x && location.y) {
      wx.chooseLocation({
        longitude: location.x,
        latitude: location.y,
        name: addressDetail,
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

  createStore: function (e) {
    const { addressValue, addressCode, location, postcode } = this.data;
    const store = { ...e.detail.value, addressValue, addressCode, location, postcode }

    checkCustomer().then(({ authorization, info }) => {
      wxRequest({
        url: `${domain}/store/bindings/insertStore`,
        data: store,
        method: "POST",
        header: { authorization, cid: info.cid },
      }).then((data) => {
        wx.reLaunch({
          url: `/pages/login/stores?customerId=${info.cid}`
        });
      })
    })
  },

  resetForm: function (e) {
    const store = this.data.store;
    this.setData({
      ...store
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
