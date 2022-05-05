import { checkCustomer, domain, wxRequest, size } from '../../utils/util.js';
Page({
    data: {
        domain,
        stores: [],
        currentID: -1,
        slideButtons: [
            {
                type: 'warn',
                text: '删除',
                extClass: 'warn',
            }
        ],
    },

    /**绑定商户
     *
     * @param {*} e
     */
    bindStore: function (e) {
        const { id } = e.currentTarget.dataset;
        checkCustomer().then(({ authorization, info }) => {
            wxRequest({
                url: `${domain}/store/bindings/${id}`,
                method: 'POST',
                header: { authorization, cid: info.cid },
            }).then((data) => {
                wx.setStorageSync('authorization', data.authorization);
                wx.setStorageSync('info', data.info);
                wx.reLaunch({
                    url: '/pages/score/score'
                })
            })
        })
    },

    /**获取所有商户
     *
     * @param {*} options
     */
    onLoad: function (options) {
        checkCustomer().then(({ authorization, info }) => {
            wxRequest({
                url: `${domain}/store/bindings/getStores?size=${size}`,
                header: { authorization, cid: info.cid },
            }).then(({ content, number }) => {
                if (content.length > 0) {
                    this.setData({
                        stores: content, number, info
                    })
                } else {
                    wx.reLaunch({
                        url: '/pages/login/register'
                    })
                }
            })
        })
    },

    onPullDownRefresh: function () {
        checkCustomer().then(({ authorization, info }) => {
            wxRequest({
                url: `${domain}/store/bindings/getStores?size=${size}`,
                header: { authorization, cid: info.cid },
            }).then(({ content, number }) => {
                wx.stopPullDownRefresh();
                if (content.length > 0) {
                    this.setData({
                        stores: content, number, info
                    })
                } else {
                    wx.reLaunch({
                        url: '/pages/login/register'
                    })
                }
            })
        })
    },

    onReachBottom: function () {
        checkCustomer().then(({ authorization, info }) => {
            wxRequest({
                url: `${domain}/store/bindings/getStores?page=${this.data.number + 1}&size=${size}`,
                header: { authorization, cid: info.cid },
            }).then(({ content, number }) => {
                if (content.length > 0)
                    this.setData({
                        stores: [...this.data.stores, ...content], number
                    });
            });
        })
    },

    bindShow: function (e) {
        this.setData({
            currentID: e.target.id
        });
        setTimeout(() => {
            this.setData({
                currentID: -1
            })
        }, 3000)
    },


    bindButtonTap: function (e) {
        const index = e.currentTarget.id;
        const store = this.data.stores[index];
        wx.showModal({
            title: store.shortname,
            content: '是否删除商户？',
            success: (res) => {
                if (res.confirm) {
                    checkCustomer().then(({ authorization, info }) => {
                        wxRequest({
                            url: `${domain}/store/bindings/${store.id}/deleteStore`,
                            header: { authorization, cid: info.cid },
                            method: 'DELETE',
                        }).then(() => {
                            this.data.stores.splice(index, 1)
                            this.setData({
                                stores: this.data.stores
                            })
                        })
                    })
                }
            }
        })
    },
})
