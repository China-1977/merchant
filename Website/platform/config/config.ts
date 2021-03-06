import {defineConfig} from 'umi';
import routes from './routes';

export default defineConfig({
  define:{
    'process.env.platform': 'https://1977.work',
    'process.env.file': 'https://1977.work',
  },
  mfsu: {},
  webpack5:{},
  dynamicImport:{},
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
