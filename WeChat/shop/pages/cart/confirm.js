import { wxLogin, appid, wxRequest, domain } from '../../utils/util.js';
Page({
  data: {
    domain
  },

  onLoad: function (options) {
    let pages = getCurrentPages();//当前页面栈
    let prevPage = pages[pages.length - 2];//上一页面
    const data = prevPage.data;
    this.setData({
      ...data
    })
  },

  changeWay: function (e) {
    this.setData({
      way: e.detail.value
    })

    if (e.detail.value === 'MD') {
      const store = this.data.store;
      this.setData({
        address: {
          code: store.addressCode,
          detail: store.addressDetail,
          name: store.addressName,
          value: store.addressValue,
          location: store.location,
          postcode: store.postcode,
        },
        way: e.detail.value
      })
    } else {
      this.setData({
        address: {},
        way: e.detail.value
      })
    }
  },

  createScore: function (e) {

    if (e.detail.value.way) {
      wxLogin().then(({ authorization, info }) => {
        const { address, store } = this.data;
        console.log(this.data);
        wxRequest({
          url: `${domain}/shop/scores`,
          header: { authorization, aid: info.aid },
          method: "POST",
          data: { ...e.detail.value, address, subAppId: appid, storeId: store.id },
        }).then((data) => {
          wx.requestPayment(
            {
              ...data.order, package: data.order.packageValue,
              'success': (res) => {
                setTimeout(() => {
                  wx.reLaunch({
                    url: `/pages/score/detail?id=${data.score.id}`,
                  })
                }, 300);
              },
              'fail': (res) => {
                wx.reLaunch({
                  url: `/pages/score/detail?id=${data.score.id}`,
                })
              }
            })
        })
      })
    } else {
      wx.showModal({
        title: '警告',
        content: "请选择配送方式",
        confirmColor: '#e64340',
        showCancel: false,
        success: () => {
          wx.reLaunch({
            url: '/pages/store/merchant'
          });
        }
      })
    }


  },
})
