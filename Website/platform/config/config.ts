import {defineConfig} from 'umi';
import routes from './routes';

export default defineConfig({
  mfsu: {},
  hash: true,
  history: {type: 'hash'},
  nodeModulesTransform: {
    type: 'none',
  },
  antd: {},
  layout: {
    name: '微信服务商之特约商户',
    locale: false,
    layout: 'side',
  },
  routes: routes,
  fastRefresh: {},
  request: {
    dataField: '',
  },
});
