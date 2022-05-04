export default [
  {
    path: '/consumer',
    name: '用户信息',
    icon: 'dashboard',
    routes: [
      {
        path: '/consumer/accounts',
        name: '用户列表',
        icon: 'dashboard',
        component: '@/pages/Account/List',
      },
      {
        path: '/consumer/addresses',
        name: '收货地址',
        icon: 'dashboard',
        component: '@/pages/Address/List',
      },
    ],
  },
  {
    path: '/merchant',
    name: '商户信息',
    icon: 'dashboard',
    routes: [
      {
        path: '/merchant/customers',
        name: '营业员列表',
        icon: 'dashboard',
        component: '@/pages/Customer/List',
      },
      {
        path: '/merchant/stores',
        name: '商户列表',
        icon: 'dashboard',
        component: '@/pages/Store/List',
      },
      {
        path: '/merchant/products',
        name: '商品列表',
        icon: 'dashboard',
        component: '@/pages/Product/List',
      },
      {
        path: '/merchant/scores',
        name: '订单列表',
        icon: 'dashboard',
        component: '@/pages/Score/List',
      },
    ],
  },
  {
    path: '/setting',
    name: '系统设置',
    icon: 'dashboard',
    title: '系统设置',
    value: '/setting',
    routes: [
      {
        path: '/setting/sites',
        name: '驿站列表',
        icon: 'dashboard',
        component: '@/pages/Site/List',
      },
      {
        path: '/setting/members',
        name: '用户列表',
        icon: 'dashboard',
        component: '@/pages/Member/List',
      },
      {
        path: '/setting/applications',
        name: '资源列表',
        icon: 'dashboard',
        component: '@/pages/Application/List',
      }
    ],
  },
  {
    path: '/login',
    name: '登录',
    component: '@/pages/Login/Index',
    layout: false,
    hideInMenu: true,
  },
  {
    path: '/register',
    name: '注册',
    component: '@/pages/Login/Register',
    layout: false,
    hideInMenu: true,
  },
  {
    path: '/',
    name: '首页',
    component: '@/pages/Index',
    hideInMenu: true,
  },
];
