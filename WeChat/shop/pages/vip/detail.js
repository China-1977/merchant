import { domain, wxLogin, wxRequest,windowWidth } from '../../utils/util.js';
Page({
    data: {
        domain,windowWidth
    },
    onLoad(options) {
        wxLogin().then(({ authorization, info }) => {
            wxRequest({
                url: `${domain}/shop/vips/${options.id}`,
                header: { authorization, aid: info.aid },
            }).then((vip) => {
                console.log(vip);
                this.setData({
                    vip
                });
            });
        })
    },

    makePhoneCall: function (e) {
        wx.makePhoneCall({
            phoneNumber: e.currentTarget.dataset.phone
        })
    },

    openLocation: function (e) {
        const { location, addressName } = this.data.vip;
        wx.openLocation({
            latitude: parseFloat(location.y),
            longitude: parseFloat(location.x),
            name: addressName
        })
    },
})