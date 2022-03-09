import { checkStore, domain, wxRequest, scoreStatus, size } from '../../utils/util.js';

Page({
  /**
   * 页面的初始数据
   */
  data: {
    domain,
    scoreStatus,
    scores: [],
    year: new Date().getFullYear(),
    number: 0,
    keyword: ''
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    this.setData({
      status: options.status
    })
    checkStore().then(({ authorization, info }) => {
      wxRequest({
        url: `${domain}/store/scores?status=${options.status}&page=0&size=${size}&year=${this.data.year}`,
        header: { authorization, ...info },
      }).then((scores) => {
        this.setData({
          scores
        })
      })
    })
  },

  onPullDownRefresh: function () {
    checkStore().then(({ authorization, info }) => {
      const { status, year } = this.data
      wxRequest({
        url: `${domain}/store/scores?status=${status}&page=0&size=${size}&year=${year}&keyword=${this.data.keyword}`,
        header: { authorization, ...info },
      }).then((scores) => {
        this.setData({
          scores, number: 0
        });
        wx.stopPullDownRefresh()
      });
    });
  },

  onReachBottom: function () {
    checkStore().then(({ authorization, info }) => {
      let { number, status, scores, year } = this.data
      wxRequest({
        url: `${domain}/store/scores?status=${status}&page=${number + 1}&size=${size}&year=${year}&keyword=${this.data.keyword}`,
        header: { authorization, ...info },
      }).then((content) => {
        if (content.length > 0) {
          this.setData({
            scores: [...scores, ...content], number: number + 1
          })
        }
      })
    })
  },

  bindDateChange: function (e) {
    const year = e.detail.value;
    checkStore().then(({ authorization, info }) => {
      wxRequest({
        url: `${domain}/store/scores?status=${this.data.status}&page=0&size=${size}&year=${year}&keyword=${this.data.keyword}`,
        header: { authorization, ...info },
      }).then((scores) => {
        this.setData({
          scores, number: 0, year
        })
      })
    })
  },

  inputChange: function ({ detail }) {
    this.setData({
      keyword: detail.value
    })
  },

  searchScore: function (e) {
    checkStore().then(({ authorization, info }) => {
      const { status, year } = this.data
      wxRequest({
        url: `${domain}/store/scores?status=${status}&page=0&size=${size}&year=${year}&keyword=${this.data.keyword}`,
        header: { authorization, ...info },
      }).then((scores) => {
        this.setData({
          scores, number: 0
        });
        wx.stopPullDownRefresh()
      });
    });
  }

})