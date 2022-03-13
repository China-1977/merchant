import { checkStore, domain, wxRequest } from '../../utils/util.js';
Page({
    data: {
    },

    checkboxChange: function (e) {
        const customer = this.data.customer;
        checkStore().then(({ authorization, info }) => {
            wxRequest({
                url: `${domain}/store/customers/${customer.id}/authorize`,
                data: e.detail.value,
                method: "POST",
                header: { authorization, ...info },
            })
        })
    },

    onLoad: function (options) {
        const eventChannel = this.getOpenerEventChannel()
        eventChannel.on('acceptDataFromOpenerPage', ({ customer }) => {
            checkStore().then(({ authorization, info }) => {
                wxRequest({
                    url: `${domain}/store/customers/${customer.id}/applications`,
                    header: { authorization, ...info },
                }).then((applications) => {
                    this.setData({
                        ...options,
                        customer,
                        applications
                    })
                })
            })
        })
    },

})
