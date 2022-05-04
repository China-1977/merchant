const app = getApp();
const { windowWidth } = app.globalData;
const size = 15;
const domain = 'https://1977.work';
// const domain = 'http://127.0.0.1:5010';
const appid = "wx950ae546eec14733";
const scoreStatus = {
  WAIT_PAY: "待支付",
  WAIT_PACKAGE: "待配货",
  WAIT_DELIVER: "待配送",
  WAIT_SIGN: "待签收",
  FINISH: "已完成",
  REFUND_SUCCESS: "退款成功",
  REFUND_CLOSED: "退款关闭",
  REFUND_PROCESSING: "退款处理中",
  REFUND_ABNORMAL: "退款异常"
};

const storeState = {
  APPLYMENT_STATE_EDITTING: "编辑中",
  APPLYMENT_STATE_AUDITING: "审核中",
  APPLYMENT_STATE_REJECTED: "已驳回",
  APPLYMENT_STATE_TO_BE_CONFIRMED: "待账户验证",
  APPLYMENT_STATE_TO_BE_SIGNED: "待签约",
  APPLYMENT_STATE_SIGNING: "开通权限中",
  APPLYMENT_STATE_FINISHED: "已完成",
  APPLYMENT_STATE_CANCELED: "已作废",
};
const qualification = {
  "SUBJECT_TYPE_INDIVIDUAL": [
    { settlementId: "719", qualificationType: "餐饮" },
    { settlementId: "719", qualificationType: "食品生鲜" },
    { settlementId: "719", qualificationType: "私立/民营医院/诊所" },
    { settlementId: "719", qualificationType: "保健器械/医疗器械/非处方药品" },
    { settlementId: "719", qualificationType: "游艺厅/KTV/网吧" },
    { settlementId: "719", qualificationType: "机票/机票代理" },
    { settlementId: "719", qualificationType: "宠物医院" },
    { settlementId: "719", qualificationType: "培训机构" },
    { settlementId: "719", qualificationType: "零售批发/生活娱乐/其他" },
    { settlementId: "720", qualificationType: "话费通讯" },
    { settlementId: "746", qualificationType: "门户论坛/网络广告及推广/软件开发/其他" },
    { settlementId: "746", qualificationType: "游戏" },
    { settlementId: "721", qualificationType: "加油" }
  ],
  "SUBJECT_TYPE_ENTERPRISE": [
    { settlementId: "716", qualificationType: "餐饮" },
    { settlementId: "716", qualificationType: "食品生鲜" },
    { settlementId: "716", qualificationType: "私立/民营医院/诊所" },
    { settlementId: "716", qualificationType: "保健器械/医疗器械/非处方药品" },
    { settlementId: "716", qualificationType: "游艺厅/KTV/网吧" },
    { settlementId: "716", qualificationType: "机票/机票代理" },
    { settlementId: "716", qualificationType: "宠物医院" },
    { settlementId: "716", qualificationType: "培训机构" },
    { settlementId: "716", qualificationType: "零售批发/生活娱乐/其他" },
    { settlementId: "716", qualificationType: "电信运营商/宽带收费" },
    { settlementId: "716", qualificationType: "旅行社" },
    { settlementId: "716", qualificationType: "宗教组织" },
    { settlementId: "716", qualificationType: "房地产/房产中介" },
    { settlementId: "716", qualificationType: "共享服务" },
    { settlementId: "716", qualificationType: "文物经营/文物复制品销售" },
    { settlementId: "716", qualificationType: "拍卖典当" },
    { settlementId: "715", qualificationType: "保险业务" },
    { settlementId: "714", qualificationType: "众筹" },
    { settlementId: "713", qualificationType: "财经/股票类资讯" },
    { settlementId: "728", qualificationType: "话费通讯" },
    { settlementId: "728", qualificationType: "婚介平台/就业信息平台/其他" },
    { settlementId: "711", qualificationType: "在线图书/视频/音乐/网络直播" },
    { settlementId: "711", qualificationType: "游戏" },
    { settlementId: "711", qualificationType: "门户论坛/网络广告及推广/软件开发/其他" },
    { settlementId: "717", qualificationType: "物流/快递" },
    { settlementId: "717", qualificationType: "加油" },
    { settlementId: "717", qualificationType: "民办中小学及幼儿园" },
    { settlementId: "730", qualificationType: "公共事业（水电煤气）" },
    { settlementId: "718", qualificationType: "信用还款" },
    { settlementId: "739", qualificationType: "民办大学及院校" }
  ],
  "SUBJECT_TYPE_INSTITUTIONS": [
    { settlementId: "725", qualificationType: "其他缴费" },
    { settlementId: "722", qualificationType: "公共事业（水电煤气）" },
    { settlementId: "723", qualificationType: "交通罚款" },
    { settlementId: "724", qualificationType: "公立医院" },
    { settlementId: "724", qualificationType: "公立学校" },
    { settlementId: "724", qualificationType: "挂号平台" }
  ],
  "SUBJECT_TYPE_OTHERS": [
    { settlementId: "727", qualificationType: "宗教组织" },
    { settlementId: "727", qualificationType: "机票/机票代理" },
    { settlementId: "727", qualificationType: "私立/民营医院/诊所" },
    { settlementId: "727", qualificationType: "咨询/娱乐票务/其他" },
    { settlementId: "738", qualificationType: "民办中小学及幼儿园" },
    { settlementId: "726", qualificationType: "民办大学及院校" },
    { settlementId: "726", qualificationType: "公益" }
  ]
};
const banks = [
  "工商银行",
  "交通银行",
  "招商银行",
  "民生银行",
  "中信银行",
  "浦发银行",
  "兴业银行",
  "光大银行",
  "广发银行",
  "平安银行",
  "北京银行",
  "华夏银行",
  "农业银行",
  "建设银行",
  "邮政储蓄银行",
  "中国银行",
  "宁波银行",
  "其他银行"
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

function checkStore() {
  return new Promise((resolve) => {
    const authorization = wx.getStorageSync('authorization');
    if (authorization) {
      const info = wx.getStorageSync('info');
      if (info.sid) {
        resolve({ authorization, info });
      } else {
        wx.reLaunch({
          url: `/pages/login/stores?customerId=${info.cid}`
        });
      }
    } else {
      wx.reLaunch({
        url: '/pages/login/login'
      })
    }
  })
}
function checkCustomer() {
  return new Promise((resolve) => {
    const authorization = wx.getStorageSync('authorization');
    if (authorization) {
      const info = wx.getStorageSync('info');
      resolve({ authorization, info });
    } else {
      wx.reLaunch({
        url: '/pages/login/login'
      })
    }
  })
}

/**多个文件上传
 * @param {string} authorization 秘钥
 * @param {object} info 用户信息
 * @param {number} count 上传数量
 * @param {number} url 上传地址
 */
function chooseImages(authorization, info, count, url) {
  return new Promise((resolve, reject) => {
    wx.chooseImage({
      count,
      sizeType: ['original', 'compressed'],
      sourceType: ['album', 'camera'],
      success: res => {
        wx.showLoading({ title: '加载中', mask: true })
        for (let filePath of res.tempFilePaths) {
          wx.uploadFile({
            header: { authorization, ...info },
            url,
            filePath: filePath,
            name: 'file',
            success: res => {
              if (res.statusCode === 200) {
                resolve(res.data)
              } else {
                const { code, message } = JSON.parse(res.data);
                switch (code) {
                  case 'SESSION_EXPIRE':
                    wx.removeStorageSync('authorization');
                    wx.removeStorageSync('info');
                    wx.reLaunch({
                      url: '/pages/login/login'
                    });
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
              const { code, message, content } = data;
              wx.showModal({
                title: '提示',
                content: message,
                showCancel: false,
              })
            },
            complete: (data) => {
              wx.hideLoading()
            }
          })
        }
      }
    })
  })
}
/**单文件上传
 * @param {string} authorization 秘钥
 * @param {object} info 用户信息
 * @param {number} url 上传地址
 */
function chooseImage(authorization, info, url) {
  return new Promise((resolve, reject) => {
    wx.chooseImage({
      count: 1,
      sizeType: ['original', 'compressed'],
      sourceType: ['album', 'camera'],
      success: res => {
        wx.showLoading({
          title: '加载中',
          mask: true
        })
        wx.uploadFile({
          header: { authorization, ...info },
          url,
          filePath: res.tempFilePaths[0],
          name: 'file',
          success: res => {
            if (res.statusCode === 200) {
              resolve(res.data)
            } else {
              const { code, message } = JSON.parse(res.data);;
              switch (code) {
                case 'SESSION_EXPIRE':
                  wx.removeStorageSync('authorization');
                  wx.removeStorageSync('info');
                  wx.reLaunch({
                    url: '/pages/login/login'
                  });
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
          complete: (data) => {
            wx.hideLoading()
          }
        })
      }
    })
  });
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
                url: '/pages/login/login'
              });
              break;
            case 'FAIL':
                wx.showModal({
                  title: '警告',
                  content: message,
                  confirmColor: '#e64340',
                  showCancel: false,
                })
                break;
            default:
              wx.showModal({
                title: '警告',
                content: "请求失败",
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
              url: '/pages/login/login'
            });
            break;
            case 'FAIL':
              wx.showModal({
                title: '警告',
                content: message,
                confirmColor: '#e64340',
                showCancel: false,
              })
              break;
          default:
            wx.showModal({
              title: '警告',
              content: "请求失败",
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
  qualification,
  banks,
  storeState,
  scoreStatus,
  checkStore,
  checkCustomer,
  formatTime,
  app,
  windowWidth,
  wxRequest,
  chooseImages,
  chooseImage,
  size
}
