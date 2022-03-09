import {defineConfig} from 'dumi';

export default defineConfig({
  title: '一九七七',
  favicon: '/images/favicon.png',
  logo: '/images/favicon.png',
  mode: 'site',
  locales: [['zh-CN', '中文']],
  metas: [
    {
      name: 'description',
      content: '茌平壹玖柒柒软件有限公司是专注特约商户、产品、用户等信息服务。货运平台、供应链、网约车、微信小程序、Java、MySQL、Linux、MongoDB、Redis、React',
    },
    {
      name: 'keywords',
      content: '一九七七,壹玖柒柒,茌平壹玖柒柒软件有限公司',
    },
  ],

  navs: {
    'zh-CN': [
      {title: '特约商户', path: '/merchant'},
      {title: '平台', path: 'http://platform.1977.work'},
    ],
  },
});
