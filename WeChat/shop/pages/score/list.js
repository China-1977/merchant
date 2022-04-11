import { domain, wxLogin, wxRequest, scoreStatus, size, scoreWay } from '../../utils/util.js';
Page({
  data: {
    scoreWay,domain, scores: [], scoreStatus, number: 0, year: new Date().getFullYear()
  },

  onLoad: function () {
    wxLogin().then(({ authorization, info }) => {
      wxRequest({
        url: `${domain}/shop/scores?page=0&size=${size}&year=${this.data.year}`,
        header: { authorization, aid: info.aid },
      }).then((scores) => {
        this.setData({
          scores
        });
      });
    });
  },

  onPullDownRefresh: function () {
    wxLogin().then(({ authorization, info }) => {
      wxRequest({
        url: `${domain}/shop/scores?page=0&size=${size}&year=${this.data.year}`,
        header: { authorization, aid: info.aid },
      }).then((scores) => {
        this.setData({
          scores, number: 0
        });
        wx.stopPullDownRefresh()
      });
    });
  },

  onReachBottom: function () {
    wxLogin().then(({ authorization, info }) => {
      wxRequest({
        url: `${domain}/shop/scores?page=${this.data.number + 1}&size=${size}&year=${this.data.year}`,
        header: { authorization, aid: info.aid },
      }).then((content) => {
        if (content.length > 0)
          this.setData({
            scores: [...this.data.scores, ...content], number: this.data.number + 1
          });
      });
    });
  },

  bindDateChange: function (e) {
    const year = e.detail.value;
    wxLogin().then(({ authorization, info }) => {
      wxRequest({
        url: `${domain}/shop/scores?page=0&size=${size}&year=${year}`,
        header: { authorization, aid: info.aid },
      }).then((scores) => {
        this.setData({
          scores, number: 0, year
        });
      });
    });
  }
})