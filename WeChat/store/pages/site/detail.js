import { domain, checkStore, wxRequest } from '../../utils/util.js';
Page({
  data: {
    location: {},
    code: [],
    value: []
  },

  onLoad: function (options) {
    this.setData({
      code: [],
      value: []
    })
    if (options.index) {
      let pages = getCurrentPages();//当前页面栈
      let prevPage = pages[pages.length - 2];//上一页面
      const site = prevPage.data.sites[options.index];
      this.setData({
        ...site, index: options.index
      })
    }
  },

  chooseLocation: function (e) {
    wx.authorize({
      scope: 'scope.userLocation',
      success: (res) => {
        const { location, detail } = this.data;
        if (location && location.x && location.y) {
          wx.chooseLocation({
            longitude: location.x,
            latitude: location.y,
            name: detail,
            success: (res) => {
              this.setData({ location: { x: res.longitude, y: res.latitude }, detail: res.site, name: res.name });
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
                  this.setData({ location: { x: res.longitude, y: res.latitude }, detail: res.site, name: res.name });
                }
              })
            }
          });
        }
      },
      fail: (res) => {
        wx.showModal({
          title: '提示',
          content: '请允许小程序使用位置消息',
          confirmColor: '#e64340',
          showCancel: false,
          success: () => {
            wx.openSetting();
          }
        })
      }
    })


  },

  saveSite: function (e) {
    checkStore().then(({ authorization, info }) => {
      const { value, code, postcode, location, index } = this.data;
      let site = { ...e.detail.value, value, code, postcode, location };
      wxRequest({
        url: `${domain}/store/sites`,
        header: { authorization, ...info },
        data: site,
        method: "POST",
      }).then((data) => {
        let pages = getCurrentPages();//当前页面栈
        let prevPage = pages[pages.length - 2];//上一页面
        if (index) {
          const key = `sites[${index}]`;
          prevPage.setData({
            [key]: site
          });
        } else {
          let sites = prevPage.data.sites;
          sites.unshift(site);
          prevPage.setData({
            sites
          });
        }
        this.setData({
          ...data
        });
        wx.navigateBack({
          delta: 1
        });
      });
    })
  },

  deleteSite: function (e) {

    wx.showModal({
      title: '警示',
      content: '确定删除吗？',
      success: (res) => {
        if (res.confirm) {
          checkStore().then(({ authorization, info }) => {
            const { id } = this.data;
            wxRequest({
              url: `${domain}/store/sites/${id}`,
              method: "DELETE",
              header: { authorization, ...info },
            }).then((data) => {
              wx.navigateBack({
                delta: 2
              });
            });
          })
        }
      }
    })
  },

  bindRegionChange: function (e) {
    this.setData({ ['code']: e.detail.code, ['postcode']: e.detail.postcode, ['value']: e.detail.value });
  },
})