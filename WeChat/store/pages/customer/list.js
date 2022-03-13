import { checkStore, domain, wxRequest } from '../../utils/util.js';
Page({

    /**
     * 页面的初始数据
     */
    data: {
        currentID: -1,
        slideButtons: [
            {
                type: "default",
                text: '授权',
                extClass: 'default',
            }, {
                type: 'warn',
                text: '删除',
                extClass: 'warn',
            }
        ],
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
        const customer = this.data.customers[index];
        if (e.detail.index === 0) {
            wx.navigateTo({
                url: '/pages/restful/application',
                success: function (res) {
                    res.eventChannel.emit('acceptDataFromOpenerPage', { customer })
                }
            })
        } else {
            wx.showModal({
                title: '警示',
                content: '是否真的删除？',
                success: (res) => {
                    if (res.confirm) {
                        checkStore().then(({ authorization, info }) => {
                            const customer = this.data.customers[index];
                            wxRequest({
                                url: `${domain}/store/customers/${customer.id}`,
                                method: "DELETE",
                                header: { authorization, ...info },
                            }).then((data) => {
                                this.data.customers.splice(index, 1)
                                this.setData({
                                    customers: this.data.customers
                                })
                            })
                        })
                    }
                }
            })
        }
    },

    inviteCustomer: function (e) {
        checkStore().then(({ authorization, info }) => {
            wxRequest({
                url: `${domain}/store/customers/inviteCustomer`,
                data: e.detail.value,
                method: "POST",
                header: { authorization, ...info },
            })
        })
    },


    /**
     * 生命周期函数--监听页面加载
     */
    onLoad: function (options) {
        checkStore().then(({ authorization, info }) => {
            wxRequest({
                url: `${domain}/store/customers`,
                header: { authorization, ...info },
            }).then((customers) => {
                this.setData({
                    customers
                })
            })
        })
    },
    makePhoneCall: function (e) {
        wx.makePhoneCall({
            phoneNumber: e.currentTarget.dataset.phone
        })
    },

    clipBoard: function (e) {
        wx.setClipboardData({
            data: e.currentTarget.dataset.phone,
        })
    },

})
