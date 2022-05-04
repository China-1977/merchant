import {history, RequestConfig} from 'umi';

const authHeaderInterceptor = (url: string, options: any) => {
  getInitialState().then((res) => {
    if (res?.authorization) {
      options.headers.authorization = res?.authorization
    }
    if (res?.mid) {
      options.headers.mid = res?.mid
    }
  });
  return {url, options};
};

export const request: RequestConfig = {
  prefix: process.env.platform,
  requestInterceptors: [authHeaderInterceptor],
  errorConfig: {
    adaptor: (resData: any) => {
      console.log(resData)
      if (resData.code) {
        switch (resData.code) {
          case  'SESSION_EXPIRE':
            localStorage.removeItem("info")
            localStorage.removeItem("authorization")
            history.push("/login");
            break;
          default:
            break;
        }
      }
      return {...resData, errorMessage: resData.message}
    },
  },
};

export async function getInitialState() {
  const infoStr = localStorage.getItem('info');
  const authorization = localStorage.getItem('authorization')?.replace(/\"/g, "");
  if (infoStr && authorization) {
    const info = JSON.parse(infoStr, function (key, value) {
      if (value === null) {
        return undefined;
      }
      return value;
    });
    return {authorization, ...info};
  }
}

export const layout = () => {
  return {
    logout: () => {
      localStorage.clear();
      history.push("/login");
    }
  };
};

export const scoreStatusEnum = {
  WAIT_PAY: {text: '待支付', status: 'Default'},
  REFUND_PROCESSING: {text: '退款处理中', status: 'Default'},
  WAIT_PACKAGE: {text: '待配货', status: 'Error'},
  WAIT_SIGN: {text: '待签收', status: 'Warning'},
  WAIT_DELIVER: {text: '待配送', status: 'Warning'},
  REFUND_SUCCESS: {text: '退款成功', status: 'Success'},
  REFUND_CLOSED: {text: '退款关闭', status: 'Success'},
  REFUND_ABNORMAL: {text: '退款异常', status: 'Success'},
  FINISH: {text: '已完成', status: 'Success'},
};

export const scoreWayEnum = {
  MD: {text: '门店', status: 'Processing'},
  YZ: {text: '驿站', status: 'Default'},
  PS: {text: '配送', status: 'Default'},
};

export const storeStatusEnum = {
  true: {text: '是', status: 'Processing'},
  false: {text: '否', status: 'Default'},
};

export const productStatusEnum = {
  true: {text: '是', status: 'Processing'},
  false: {text: '否', status: 'Default'},
};
