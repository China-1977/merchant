import { checkStore, domain, wxRequest, size } from '../../utils/util.js';
Page({
    data: {
        domain,
        currentID: -1,
        ids: [],
        products: [],
        slideButtons: {
            'false': {
                type: "default",
                text: '上架',
                extClass: 'default',
            }, 'true': {
                type: 'warn',
                text: '下架',
                extClass: 'warn',
            }
        },
        icons: {
            'false': 'clear',
            'true': 'success'
        }
    },

    onShow: function () {
        checkStore().then(({ authorization, info }) => {
            wxRequest({
                url: `${domain}/store/products?size=${size}`,
                header: { authorization, ...info },
            }).then(({ content, number }) => {
                this.setData({ products: content, number }
                )
            })
        })
    },
    onPullDownRefresh: function () {
        checkCustomer().then(({ authorization, info }) => {
            wxRequest({
                url: `${domain}/store/products?size=${size}`,
                header: { authorization, cid: info.cid },
            }).then(({ content, number }) => {
                wx.stopPullDownRefresh();
                if (content.length > 0) {
                    this.setData({
                        products: content, number
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
                url: `${domain}/store/products?page=${this.data.number + 1}&size=${size}`,
                header: { authorization, cid: info.cid },
            }).then(({ content, number }) => {
                if (content.length > 0)
                    this.setData({
                        products: [...this.data.products, ...content], number
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
        wx.showModal({
            title: '警示',
            content: '请仔细审核商品信息！',
            success: (res) => {
                if (res.confirm) {
                    checkStore().then(({ authorization, info }) => {
                        const index = e.currentTarget.id;
                        const { id, status } = this.data.products[index];
                        wxRequest({
                            url: `${domain}/store/products/${id}/updateStatus?status=${!status}`,
                            method: 'PUT',
                            header: { authorization, ...info },
                        }).then(() => {
                            this.setData({
                                [`products[${index}].status`]: !status
                            })
                        });
                    })
                }
            }
        })
    },

    deleteProduct: function (e) {
        wx.showModal({
            title: '警示',
            content: '是否删除商品?',
            success: (res) => {
                if (res.confirm) {
                    checkStore().then(({ authorization, info }) => {
                        const index = e.currentTarget.id;
                        const { id } = this.data.products[index];
                        wxRequest({
                            url: `${domain}/store/products/${id}`,
                            method: 'DELETE',
                            header: { authorization, ...info },
                        }).then(() => {
                            this.data.products.splice(index, 1)
                            this.setData({
                                products: this.data.products
                            })
                        })
                    })
                }
            }
        })
    },
    checkboxChange: function (e) {
        const key = e.currentTarget.id;
        this.setData({
            [key]: e.detail.value
        });
    },

    /**上架
     * 
     */
    up: function () {
        checkStore().then(({ authorization, info }) => {
            let { ids, products } = this.data;
            products.forEach((product, index) => {
                if (ids.includes(product.id.toString())) {
                    product.status = true
                }
            });
            wxRequest({
                url: `${domain}/store/products?status=true`,
                method: 'PUT',
                header: { authorization, ...info },
                data: ids
            }).then(() => {
                this.setData({
                    products, ids: []
                })
            })
        })
    },
    /**下架
     * 
     */
    lower: function () {
        checkStore().then(({ authorization, info }) => {
            let { ids, products } = this.data;
            products.forEach((product, index) => {
                if (ids.includes(product.id.toString())) {
                    product.status = false
                }
            });
            wxRequest({
                url: `${domain}/store/products?status=false`,
                method: 'PUT',
                header: { authorization, ...info },
                data: ids
            }).then(() => {
                this.setData({
                    products, ids: []
                })
            })
        })
    },
    /**删除
     * 
     */
    delete: function () {
        checkStore().then(({ authorization, info }) => {
            let { ids, products } = this.data;
            products = this.data.products.filter((value) => !ids.includes(value.id.toString()));
            wxRequest({
                url: `${domain}/store/products`,
                method: 'DELETE',
                header: { authorization, ...info },
                data: ids
            }).then(() => {
                this.setData({
                    products, ids: []
                })
            })
        })
    },
    add: function () {
        wx.navigateTo({
            url: '/pages/product/create',
        })
    }
})