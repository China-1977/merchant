import { checkStore, wxRequest, domain, scoreStatus } from '../../utils/util.js';
Page({
  data: {
    domain,
    scoreStatus
  },
  onLoad: function (options) {
    checkStore().then(({ authorization, info }) => {
      wxRequest({
        url: `${domain}/store/scores/${options.id}`,
        header: { authorization, ...info },
      }).then((score) => {
        this.setData({
          index: options.index,
          score
        });
      });
    })
  },

  clipBoard: function (e) {
    wx.setClipboardData({
      data: this.data.score.outTradeNo,
    })
  },

  waitPackage: function (e) {
    const oldScore = this.data.score;
    checkStore().then(({ authorization, info }) => {
      wx.showModal({
        title: '订单状态',
        content: scoreStatus[oldScore.status],
        confirmColor: '#e64340',
        success: (res) => {
          if (res.confirm) {
            wxRequest({
              url: `${domain}/store/scores/${oldScore.id}/waitPackage`,
              header: { authorization, ...info },
              method: 'POST',
              data: oldScore
            }).then((score) => {
              this.setData({
                score
              });
            });
          }
        }
      })
    })
  },

  waitDeliver: function (e) {
    const oldScore = this.data.score;
    checkStore().then(({ authorization, info }) => {
      wx.showModal({
        title: '订单状态',
        content: scoreStatus[oldScore.status],
        confirmColor: '#e64340',
        success: (res) => {
          if (res.confirm) {
            wxRequest({
              url: `${domain}/store/scores/${oldScore.id}/waitDeliver`,
              header: { authorization, ...info },
              method: 'POST',
              data: oldScore
            }).then((score) => {
              this.setData({
                score
              });
            });
          }
        }
      })
    })
  },

  refund: function (e) {
    const oldScore = this.data.score;
    checkStore().then(({ authorization, info }) => {
      wx.showModal({
        title: '订单状态',
        content: scoreStatus[oldScore.status],
        confirmColor: '#e64340',
        success: (res) => {
          if (res.confirm) {
            wxRequest({
              url: `${domain}/store/scores/${oldScore.id}-${e.detail.value.outTradeNo}/refund`,
              header: { authorization, ...info },
              method: 'POST',
              data: oldScore
            }).then((score) => {
              this.setData({
                score
              });
            });
          }
        }
      })
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
