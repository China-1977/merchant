const app = getApp();
const { windowWidth } = app.globalData;
const size = 15;
const domain = 'https://1977.work';
// const domain = 'http://192.168.0.4';

const appid = "wxe78290c2a5313de3";

const scoreStatus = {
  WAIT_PAY: "待支付",
  WAIT_PACKAGE: "待配货",
  WAIT_DELIVER: "待配送",
  WAIT_SIGN: "待签收",
  WAIT_TAKE: "待取货",
  FINISH: "已完成",
  REFUND_SCCESS: "退款成功",
  REFUND_CLOSED: "退款关闭",
  REFUND_PROCESSING: "退款处理中",
  REFUND_ABNORMAL: "退款异常"
};
const types = [
  { id: 1, title: '服装', icon: "/images/clothing.png" },
  { id: 2, title: '美食', icon: "/images/delicious.png" },
  { id: 3, title: '果蔬', icon: "/images/fruits.png" },
  { id: 4, title: '饮品', icon: "/images/drinks.png" },
  { id: 5, title: '超市', icon: "/images/supermarkets.png" },
  { id: 6, title: '书店', icon: "/images/bookstores.png" },
];
const formatTime = date => {
  const year = date.getFullYear()
  const month = date.getMonth() + 1
  const day = date.getDate()
  const hour = date.getHours()
  const minute = date.getMinutes()
  const second = date.getSeconds()
  return [year, month, day].map(formatNumber).join('/') + ' ' + [hour, minute, second].map(formatNumber).join(':')
};

const formatNumber = n => {
  n = n.toString()
  return n[1] ? n : '0' + n
};

function wxLogin() {
  return new Promise((resolve) => {
    const authorization = wx.getStorageSync('authorization');
    if (authorization) {
      const info = wx.getStorageSync('info');
      resolve({ authorization, info });
    } else {
      wx.reLaunch({
        url: '/pages/login'
      })
    }
  })
}

/** 根据商户ID分页商品
 * @param {String} storeId 商户ID
 */
function getProducts(storeId) {
  const authorization = wx.getStorageSync('authorization');
  const info = wx.getStorageSync('info');
  if (authorization && info) {
    return wxRequest({ url: `${domain}/shop/products?storeId=${storeId}`, header: { authorization, aid: info.aid } })
  } else {
    return wxRequest({ url: `${domain}/shop/products?storeId=${storeId}` })
  }
}

/**
 *
 * @param aid 用户ID
 * @param storeId 店铺ID
 * @param authorization 秘钥
 * @returns {Promise<any>}
 */
function getCarts(aid, storeId, authorization) {
  return wxRequest({
    url: `${domain}/shop/carts?storeId=${storeId}`,
    header: { authorization, aid },
  })
}

/** 根据经纬度分页获取商户
 * @param {Number} longitude 经度
 * @param {Number} latitude 维度
 * @param {Number} number 分页数
 * @param {string} keyword 关键字
 */
function getStores(longitude, latitude, number = 0, keyword) {
  let url = `${domain}/shop/stores/${longitude}-${latitude}/near?page=${number}`;
  if (keyword) {
    url = `${url}&keyword=${keyword}`
  }
  return wxRequest({ url })
}

/** 根据商户ID获取商户信息
 * @param {String} id 商户主键
 */
function getStore(id) {
  return wxRequest({ url: `${domain}/shop/stores/${id}` });
}
/** 商品详情
 * @param {string} id 商品ID
 * @param {string} authorization 密钥
 * @param {string} aid 用户ID
 */
function getProduct(id, authorization, aid) {
  let url = `${domain}/shop/products/${id}`
  if (authorization && aid) {
    return wxRequest({ url, header: { authorization, aid } });
  } else {
    return wxRequest({ url });
  }
}


function wxRequest({ url, data = {}, dataType = 'json', header, method = 'GET', responseType = 'text', timeout = 0 }) {
  return new Promise((resolve) => {
    wx.showLoading({
      title: '加载中',
      mask: true
    })
    wx.request({
      url,
      data,
      dataType,
      method,
      responseType,
      timeout,
      header: { 'Content-Type': 'application/json;charset=UTF-8', ...header },
      success: ({ data, statusCode }) => {
        if (statusCode === 200) {
          resolve(data);
        } else {
          const { code, message, content } = data;
          switch (code) {
            case 'MERCHANT_NOT_REGISTER':
              wx.showModal({
                title: '警告',
                content: message,
                confirmColor: '#e64340',
                showCancel: false,
                success: () => {
                  wx.reLaunch({
                    url: '/pages/store/merchant'
                  });
                }
              })
              break;
            case 'SESSION_EXPIRE':
              wx.removeStorageSync('authorization');
              wx.removeStorageSync('info');
              wx.reLaunch({
                url: '/pages/login'
              })
              break;
            default:
              wx.showModal({
                title: '警告',
                content: message,
                confirmColor: '#e64340',
                showCancel: false,
              })
              break;
          }
        }
      },
      fail: (data) => {
        wx.hideLoading()
        const { code, message, content } = data;
        switch (code) {
          case 'MERCHANT_NOT_REGISTER':
            wx.showModal({
              title: '警告',
              content: message,
              confirmColor: '#e64340',
              showCancel: false,
              success: () => {
                wx.reLaunch({
                  url: '/pages/store/merchant'
                });
              }
            })
            break;
          case 'SESSION_EXPIRE':
            wx.removeStorageSync('authorization');
            wx.removeStorageSync('info');
            wx.reLaunch({
              url: '/pages/login'
            })
            break;
          default:
            wx.showModal({
              title: '警告',
              content: message,
              confirmColor: '#e64340',
              showCancel: false,
            })
            break;
        }
      },
      complete: (res) => {
        wx.hideLoading()
      },
    })
  })
}

module.exports = {
  domain,
  appid,
  types,
  wxLogin,
  formatTime,
  app,
  getStores,
  getStore,
  getProducts,
  getCarts,
  getProduct,
  windowWidth,
  wxRequest,
  scoreStatus,
  size
}
