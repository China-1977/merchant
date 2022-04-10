import { domain, checkStore, wxRequest, size } from '../../utils/util.js';
Page({
    data: {
        domain,
        vips: [],
        phone: ''
    },

    init: function (number, phone = '') {
        checkStore().then(({ authorization, info }) => {
            wxRequest({
                url: `${domain}/store/vips?page=${number}&size=${size}&phone=${phone}`,
                header: { authorization, ...info },
            }).then((vips) => {
                console.log(vips);
                this.setData({
                    phone,
                    number,
                    vips: [...this.data.vips, ...vips],
                });
            });
        })
    },

    onLoad(options) {
        this.init(0);
    },

    onPullDownRefresh: function () {
        this.setData({
            vips: [],
        });
        this.init(0);
        wx.stopPullDownRefresh();
    },

    onReachBottom: function () {
        if (this.data.last) {
        } else {
            let { number, phone } = this.data;
            this.init(number + 1, phone);
        }
    },

    inputChange: function ({ detail }) {
        this.setData({
            phone: detail.value
        })
    },
    search: function ({ detail }) {
        this.setData({
            vips: [],
        });
        this.init(0, detail.value.phone);
    },
})