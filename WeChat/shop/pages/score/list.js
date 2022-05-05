import { domain, wxLogin, wxRequest, scoreStatus, size, scoreWay, number } from '../../utils/util.js';
Page({
  data: {
    scoreWay, domain, scores: [], scoreStatus, number, year: new Date().getFullYear()
  },

  onLoad: function () {
    wxLogin().then(({ authorization, info }) => {
      wxRequest({
        url: `${domain}/shop/scores?page=${number}&size=${size}&year=${this.data.year}`,
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
        url: `${domain}/shop/scores?page=${number}&size=${size}&year=${this.data.year}`,
        header: { authorization, aid: info.aid },
      }).then((scores) => {
        this.setData({
          scores, number
        });
        wx.stopPullDownRefresh()
      });
    });
  },

  onReachBottom: function () {
    wxLogin().then(({ authorization, info }) => {
      const nextNumber = this.data.number + 1;
      wxRequest({
        url: `${domain}/shop/scores?page=${nextNumber}&size=${size}&year=${this.data.year}`,
        header: { authorization, aid: info.aid },
      }).then((content) => {
        if (content.length > 0)
          this.setData({
            scores: [...this.data.scores, ...content], number: nextNumber
          });
      });
    });
  },

  bindDateChange: function (e) {
    const year = e.detail.value;
    wxLogin().then(({ authorization, info }) => {
      wxRequest({
        url: `${domain}/shop/scores?page=${number}&size=${size}&year=${year}`,
        header: { authorization, aid: info.aid },
      }).then((scores) => {
        this.setData({
          scores, number, year
        });
      });
    });
  }
})