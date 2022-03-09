import { domain, wxLogin, wxRequest, scoreStatus } from '../../utils/util.js';
Page({
  data: {
    domain,
    scoreStatus
  },
  onLoad: function (options) {
    wxLogin().then(({ authorization, info }) => {
      wxRequest({
        url: `${domain}/shop/scores/${options.id}`,
        header: { authorization, aid: info.aid },
      }).then((data) => {
        this.setData({
          ...options,
          score: data
        });
      });
    })
  },

  pay: function () {
    wxLogin().then(({ authorization, info }) => {
      wxRequest({
        url: `${domain}/shop/scores/continuePay`,
        method: 'POST',
        data: this.data.score,
        header: { authorization, aid: info.aid },
      }).then((data) => {
        const index = this.data.index;
        wx.requestPayment(
          {
            appId: data.order.appId,
            timeStamp: data.order.timeStamp,
            nonceStr: data.order.nonceStr,
            package: data.order.packageValue,
            signType: 'RSA',
            paySign: data.order.paySign,
            success: (res) => {
              if (index) {
                let pages = getCurrentPages();//当前页面栈
                let prevPage = pages[pages.length - 2];//上一页面
                prevPage.setData({
                  [`scores[${index}].status`]: 'WAIT_PACKAGE'
                })
              }
              this.setData({
                [`score.status`]: 'WAIT_PACKAGE'
              })
            },
          })
      });
    })
  },

  refund: function (e) {
    wx.showModal({
      title: '警示',
      content: '是否申请退款？',
      success: (res) => {
        if (res.confirm) {
          wxLogin().then(({ authorization, info }) => {
            wxRequest({
              url: `${domain}/shop/scores/${this.data.score.id}/refund`,
              method: 'PUT',
              header: { authorization, aid: info.aid },
            }).then((score) => {
              const index = this.data.index;
              if (index) {
                let pages = getCurrentPages();//当前页面栈
                let prevPage = pages[pages.length - 2];//上一页面
                prevPage.setData({
                  [`scores[${index}].status`]: score.status
                })
              }
              this.setData({
                [`score.status`]: score.status
              })
            });
          })
        }
      }
    })


  },

  finishScore: function () {
    wx.showModal({
      title: '警示',
      content: '是否确定收货？',
      success: (res) => {
        if (res.confirm) {
          wxLogin().then(({ authorization, info }) => {
            wxRequest({
              url: `${domain}/shop/scores/${this.data.score.id}/finishScore`,
              method: 'POST',
              header: { authorization, aid: info.aid },
            }).then((score) => {
              const index = this.data.index;
              if (index) {
                let pages = getCurrentPages();//当前页面栈
                let prevPage = pages[pages.length - 2];//上一页面
                prevPage.setData({
                  [`scores[${index}].status`]: score.status
                })
              }
              this.setData({
                [`score.status`]: score.status
              })
            });
          })
        }
      }
    })
  },

  clipBoard: function (e) {
    wx.setClipboardData({
      data: this.data.score.outTradeNo
    })
  },

  makePhoneCall: function (e) {
    wx.makePhoneCall({
      phoneNumber: e.currentTarget.dataset.phone
    })
  },


  getLocation: function (e) {
    const x = e.currentTarget.dataset.x;
    const y = e.currentTarget.dataset.y;
    const name = e.currentTarget.dataset.name;
    wx.openLocation({
      latitude: parseFloat(y),
      longitude: parseFloat(x),
      name: name,
    })
  },
})
