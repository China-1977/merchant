import { domain, checkStore, wxRequest } from '../../utils/util.js';
Page({
    data: {

        vip: {},
        discounts: [10, 9, 8, 7, 6, 5, 4, 3, 2, 1],
    },

    onLoad(options) {
        if (options.index) {
            let pages = getCurrentPages();//当前页面栈
            let prevPage = pages[pages.length - 2];//上一页面
            const vip = prevPage.data.vips[options.index];
            console.log(vip);
            this.setData({
                vip, index: options.index
            })
        }
    },

    bindDiscountChange: function ({ detail }) {
        this.setData({
            discount: this.data.discounts[detail.value]
        })
    },

    saveVip: function ({ detail }) {
        checkStore().then(({ authorization, info }) => {
            wxRequest({
                url: `${domain}/store/vips/${detail.value.id}`,
                header: { authorization, ...info },
                data: detail.value,
                method: 'POST'
            }).then((vip) => {
                let pages = getCurrentPages();//当前页面栈
                let prevPage = pages[pages.length - 2];//上一页面
                const key = `vips[${this.data.index}]`;
                prevPage.setData({
                    [key]: { ...this.data.vip, balance: vip.balance, discount: vip.discount }
                });
                wx.navigateBack({
                    delta: 1
                });
            });
        })
    }
})
