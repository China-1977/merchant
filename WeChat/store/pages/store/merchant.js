import { checkCustomer, domain, wxRequest, banks, qualification, chooseImages, chooseImage, appid, checkStore } from '../../utils/util.js';
Page({
  data: {
    domain,
    banks,
    qualification,
    qualifications: [],
    bankAddress: {
      code: [],
      value: []
    },
  },

  onLoad: function (options) {
    // this.setData({
    //   "miniProgramAppid": "wxe78290c2a5313de3",

    //   "contactName": "王长浩",
    //   "contactIdNumber": "371523199201251250",
    //   "mobilePhone": "15063517240",
    //   "contactEmail": "3166944856@qq.com",
    //   "subjectType": "SUBJECT_TYPE_ENTERPRISE",
    //   "licenseNumber": "91371523MA3PU9M67",
    //   "merchantName": "茌平壹玖柒柒软件有限公司",
    //   "legalPerson": "王长浩",
    //   "idCardNumber": "371523199201251250",
    //   "idCardName": "王长浩",
    //   "cardPeriodBegin": "2022-02-11",
    //   "cardPeriodEnd": "2022-02-11",
    //   "owner": true,
    //   "merchantShortname": "茌平壹玖柒柒",
    //   "servicePhone": "15063517240",
    //   "settlementId": "716",
    //   "qualificationType": "餐饮",
    //   "bankAccountType": "BANK_ACCOUNT_TYPE_CORPORATE",
    //   "accountName": "茌平壹玖柒柒软件有限公司",
    //   "accountBank": "其他银行",
    //   "accountNumber": "2830051304205000010114",
    //   "bankName": "山东茌平农村商业银行股份有限公司胡屯支行",
    //   "bankAddress": {
    //     "code": [
    //       "370000",
    //       "371500",
    //       "371523"
    //     ],
    //     "postcode": "252100",
    //     "value": [
    //       "山东省",
    //       "聊城市",
    //       "茌平区"
    //     ]
    //   },
    //   "qualifications": [
    //   ],
    // })
    checkStore().then(({ authorization, info }) => {
      wxRequest({
        url: `${domain}/store/stores/${info.sid}`,
        header: { authorization, ...info },
        method: 'GET',
      }).then((store) => {
        this.setData({
          store
        })

      })
    })
  },
  /** 申请特约商户 */
  saveMerchant: function (e) {
    wx.showLoading({ title: '加载中。。。' });
    checkStore().then(({ authorization, info }) => {
      const { bankAddress, qualifications } = this.data
      const merchant = {
        ...e.detail.value,
        miniProgramAppid: "wxe78290c2a5313de3",
        bankAddress,
        qualifications,
      };
      if (merchant.subjectType === '') {
        wx.hideLoading();
        wx.showModal({
          title: '警告',
          content: "请选择主体类型",
          confirmColor: '#e64340',
          showCancel: false,
        });
      } else if (merchant.owner == false && merchant.idCardA === '' && merchant.idCardB === '' && merchant.beneficiary === '' && merchant.idNumber === '' && merchant.idPeriodBegin === '' && merchant.idPeriodEnd === '') {
        wx.hideLoading();
        wx.showModal({
          title: '警告',
          content: "请完善最终受益人信息",
          confirmColor: '#e64340',
          showCancel: false,
        });
      } else if (merchant.bankAccountType === '') {
        wx.hideLoading();
        wx.showModal({
          title: '警告',
          content: "请选择账户类型",
          confirmColor: '#e64340',
          showCancel: false,
        });
      } else {
        wxRequest({
          url: `${domain}/store/stores/${info.sid}/setMerchant`,
          header: { authorization, ...info },
          method: 'POST',
          data: merchant,
        }).then(() => {
          wx.reLaunch({
            url: `pages/store/store`
          });
        })
      }

    })
  },
  /** 输入框 */
  bindInput: function (e) {
    this.setData({
      [e.currentTarget.id]: e.detail.value
    })
  },
  /** 数组picker */
  pickerChange: function (e) {
    this.setData({
      [e.currentTarget.id]: e.detail.value
    })
  },
  /** 单选框 */
  radioChange: function (e) {
    this.setData({
      [e.currentTarget.id]: e.detail.value
    })
  },
  /** 开关设置 */
  switchChange: function (e) {
    this.setData({
      [e.currentTarget.id]: e.detail.value
    })
  },
  /** 选择地址 */
  regionChange: function (e) {
    const { code, postcode, value } = e.detail
    this.setData({
      [e.currentTarget.id]: { code, postcode, value }
    })
  },
  /** 选择多张图片 */
  chooseImages: function (e) {
    checkCustomer().then(({ authorization, info }) => {
      const id = e.currentTarget.id;
      let count = e.currentTarget.dataset.count
      const length = this.data[id].length;
      count = count - length;
      chooseImages(authorization, info, count, `${domain}/store/stores/imageUploadV3?fileName=${id}${length}`).then((data) => {
        this.setData({
          [`${id}[${length}]`]: data
        })
      })
    })
  },
  /** 选择一张图片 */
  chooseImage: function (e) {
    const id = e.currentTarget.id;
    checkCustomer().then(({ authorization, info }) => {
      chooseImage(authorization, info, `${domain}/store/stores/imageUploadV3?fileName=${id}`,).then((data) => {
        this.setData({
          [`${id}`]: data
        })
      })
    })
  },
  /** 移除图片 */
  deletePictures: function (e) {
    const id = e.currentTarget.id;
    const index = e.currentTarget.dataset.index;
    const files = this.data[id];
    files.splice(index, 1);
    this.setData({
      [id]: files
    })
  },
  /** 删除图片 */
  deletePicture: function (e) {
    const id = e.currentTarget.id;
    this.setData({
      [id]: null
    })
  },
  /** 清空图片 */
  clearPictues: function (e) {
    const id = e.currentTarget.id;
    this.setData({
      [id]: []
    })
  },
  /** 日期 */
  datePickerChange: function (e) {
    this.setData({
      [e.currentTarget.id]: e.detail.value
    })
  },

  /** 所属行业 */
  qualificationTypeChange: function (e) {
    const { settlementId, qualificationType } = qualification[this.data.subjectType][e.detail.value];
    this.setData({
      settlementId, qualificationType
    })
  },

  /** 开户银行 */
  accountBankChange: function (e) {
    this.setData({
      accountBank: banks[e.detail.value]
    })
  },

})
