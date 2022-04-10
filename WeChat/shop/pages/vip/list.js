import { domain, wxLogin, wxRequest } from '../../utils/util.js';
Page({
    data: {
        domain,
        vips: []
    },
    onLoad(options) {
        wxLogin().then(({ authorization, info }) => {
            wxRequest({
                url: `${domain}/shop/vips`,
                header: { authorization, aid: info.aid },
            }).then((vips) => {
                console.log(vips);
                this.setData({
                    vips
                });
            });
        })
    },
})