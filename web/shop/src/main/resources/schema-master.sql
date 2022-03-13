CREATE SCHEMA IF NOT EXISTS master AUTHORIZATION postgres;

COMMENT ON SCHEMA master IS '线上环境';

create table if not exists master.store
(
    id             bigserial primary key,
    license_number varchar(20)  not null
        constraint uk_store_license_number unique,
    address_detail varchar(50)  not null,
    address_name   varchar(50)  not null,
    close_time     time         not null,
    customer_id    bigint       not null,
    open_time      time         not null,
    phone          varchar(11)  not null,
    location       point        not null,
    shortname      varchar(20)  not null,
    trademark      varchar(255) not null,
    username       varchar(20)  not null,
    insert_time    timestamp    not null default current_timestamp,
    update_time    timestamp    not null default current_timestamp,
    postcode       char(6)      not null,
    address_value  text[3]      not null,
    address_code   text[3]      not null,
    status         boolean               default false,
    applyment_id   varchar(50),
    business_code  varchar(50),
    description    varchar(255),
    pictures       text[],
    sub_mch_id     varchar(50),
    videos         text[]
);

comment on table master.store is '特约商户';
comment on column master.store.address_detail is '详细地址';
comment on column master.store.address_name is '地址名称';
comment on column master.store.applyment_id is '特约商户进件申请ID';
comment on column master.store.address_value is '省市区';
comment on column master.store.address_code is '省市区编号';
comment on column master.store.business_code is '特约商户进件申请业务编号';
comment on column master.store.close_time is '关闭时间';
comment on column master.store.customer_id is '营业员ID';
comment on column master.store.description is '商户介绍';
comment on column master.store.license_number is '统一社会信用代码';
comment on column master.store.open_time is '开门时间';
comment on column master.store.phone is '联系方式';
comment on column master.store.pictures is '图集';
comment on column master.store.location is '坐标';
comment on column master.store.postcode is '邮编';
comment on column master.store.shortname is '简称';
comment on column master.store.status is '是否营业';
comment on column master.store.sub_mch_id is '特约商户编号';
comment on column master.store.trademark is 'LOGO';
comment on column master.store.insert_time is '创建时间';
comment on column master.store.update_time is '更新时间';
comment on column master.store.username is '联系人';
comment on column master.store.videos is '视频ID';
alter table master.store
    owner to postgres;

create table if not exists master.score
(
    id                   bigserial primary key,
    address_detail       varchar(255) not null,
    address_name         varchar(100) not null,
    account_id           bigint       not null,
    username             varchar(20)  not null,
    address_value        text[]       not null,
    address_code         text[]       not null,
    delivery             boolean      not null,
    phone                char(11)     not null,
    location             point        not null,
    postcode             char(6)      not null,
    prepay_id            varchar(70)  not null,
    products             jsonb        not null,
    store_id             bigint       not null,
    sp_appid             varchar(255) not null,
    sp_mchid             varchar(255) not null,
    store_address_detail varchar(50)  not null,
    store_address_name   varchar(50)  not null,
    store_address_value  text[]       not null,
    store_address_code   text[]       not null,
    store_phone          char(11)     not null,
    store_location       point        not null,
    store_postcode       char(6)      not null,
    store_shortname      varchar(20)  not null,
    store_username       varchar(10)  not null,
    sub_appid            varchar(50)  not null,
    sub_mchid            varchar(50)  not null,
    total                numeric      not null,
    status               varchar(50)  not null default 'WAIT_PAY',
    out_trade_no         char(19)     not null
        constraint uk_score_out_trade_no unique,
    pay_time             timestamp,
    transaction_id       varchar(50),
    insert_time          timestamp    not null default current_timestamp,
    update_time          timestamp    not null default current_timestamp
);

comment on table master.score is '订单';
comment on column master.score.address_detail is '收货地址详情';
comment on column master.score.address_name is '收货地址名称';
comment on column master.score.account_id is '消费者ID';
comment on column master.score.username is '消费者联系人';
comment on column master.score.address_value is '省市区';
comment on column master.score.address_code is '省市区编号';
comment on column master.score.delivery is '是否配送';
comment on column master.score.phone is '联系方式';
comment on column master.score.location is '坐标';
comment on column master.score.postcode is '邮编';
comment on column master.score.prepay_id is '微信订单ID';
comment on column master.score.products is '商品列表';
comment on column master.score.store_id is '商户ID';
comment on column master.score.sp_appid is '服务商应用ID';
comment on column master.score.sp_mchid is '服务商户号';
comment on column master.score.store_address_detail is '商户地址详情';
comment on column master.score.store_address_name is '商户地址名称';
comment on column master.score.store_address_value is '商户省市区';
comment on column master.score.store_address_code is '商户省市区编号';
comment on column master.score.store_phone is '商户联系方式';
comment on column master.score.store_location is '商户坐标';
comment on column master.score.store_postcode is '商户邮编';
comment on column master.score.store_shortname is '商户简称';
comment on column master.score.store_username is '商户联系人';
comment on column master.score.sub_appid is '子商户应用ID';
comment on column master.score.sub_mchid is '子商户号';
comment on column master.score.total is '订单总金额';
comment on column master.score.status is '订单状态';
comment on column master.score.out_trade_no is '订单编号';
comment on column master.score.insert_time is '创建时间';
comment on column master.score.update_time is '更新时间';
alter table master.score
    owner to postgres;
create index if not exists i_score_insert_date_store_id on master.score (insert_time, store_id);
create index if not exists i_score_insert_date_account_id on master.score (insert_time, account_id);

create table if not exists master.application
(
    id           bigserial primary key,
    context_path varchar(20)  not null,
    name         varchar(50)  not null,
    type         varchar(10)  not null,
    value        varchar(255) not null,
    insert_time  timestamp    not null default current_timestamp,
    update_time  timestamp    not null default current_timestamp
);
comment on table master.application is '服务资源';
comment on column master.application.context_path is '服务名称';
comment on column master.application.name is '名称';
comment on column master.application.type is '类型';
comment on column master.application.value is '路径';
comment on column master.application.insert_time is '创建时间';
comment on column master.application.update_time is '更新时间';
alter table master.application
    owner to postgres;

create table if not exists master.customer
(
    id          bigserial primary key,
    phone       char(11)  not null
        constraint uk_customer_phone unique,
    session_key varchar(50),
    sp_appid    varchar(50),
    sp_openid   varchar(50),
    sub_appid   varchar(50),
    sub_openid  varchar(50),
    insert_time timestamp not null default current_timestamp,
    update_time timestamp not null default current_timestamp
);
comment on table master.customer is '营业员';
comment on column master.customer.phone is '联系方式';
comment on column master.customer.session_key is '小程序session_key';
comment on column master.customer.sp_appid is '公众号APPID';
comment on column master.customer.sp_openid is '用户在服务商appid下的唯一标识';
comment on column master.customer.sub_appid is '子商户申请的应用ID';
comment on column master.customer.sub_openid is '用户在子商户appid下的唯一标识';
comment on column master.customer.insert_time is '创建时间';
comment on column master.customer.update_time is '更新时间';
alter table master.customer
    owner to postgres;

create table if not exists master.member
(
    id          bigserial primary key,
    phone       char(11)     not null
        constraint uk_member_phone unique,
    name        varchar(20)  not null,
    id_card     char(18)     not null
        constraint uk_member_id_card unique,
    password    varchar(255) not null,
    insert_time timestamp    not null default current_timestamp,
    update_time timestamp    not null default current_timestamp
);
comment on table master.member is '管理员';
comment on column master.member.phone is '手机号';
comment on column master.member.name is '姓名';
comment on column master.member.id_card is '身份证号';
comment on column master.member.password is '密码';
comment on column master.member.insert_time is '创建时间';
comment on column master.member.update_time is '更新时间';
alter table master.member
    owner to postgres;

create table if not exists master.account
(
    id          bigserial primary key,
    phone       char(11)  not null
        constraint uk_account_phone unique,
    session_key varchar(50),
    sp_appid    varchar(50),
    sp_openid   varchar(50),
    sub_appid   varchar(50),
    sub_openid  varchar(50),
    insert_time timestamp not null default current_timestamp,
    update_time timestamp not null default current_timestamp
);
comment on table master.account is '消费者';
comment on column master.account.phone is '联系方式';
comment on column master.account.session_key is '小程序session_key';
comment on column master.account.sp_appid is '公众号APPID';
comment on column master.account.sp_openid is '用户在服务商appid下的唯一标识';
comment on column master.account.sub_appid is '子商户申请的应用ID';
comment on column master.account.sub_openid is '用户在子商户appid下的唯一标识';
comment on column master.account.insert_time is '创建时间';
comment on column master.account.update_time is '更新时间';
alter table master.account
    owner to postgres;

create table if not exists master.product
(
    id           bigserial primary key,
    average_unit varchar(15) not null,
    price_unit   varchar(15) not null,
    name         varchar(90) not null,
    store_id     bigint      not null
        constraint product_store_id_fk references store on delete cascade,
    status       boolean     not null default false,
    average      numeric     not null default 0.00,
    price        numeric     not null default 0.00,
    stock        integer     not null default 0
        constraint product_stock_check check (stock >= 0),
    max          integer     not null default 1
        constraint product_max_check check (max >= 1),
    min          integer     not null default 1
        constraint product_min_check check (min >= 1),
    vid          varchar(50),
    description  varchar(255),
    label        varchar(50),
    pictures     text[],
    insert_time  timestamp   not null default current_timestamp,
    update_time  timestamp   not null default current_timestamp
);
comment on table master.product is '商品';
comment on column master.product.status is '是否上架';
comment on column master.product.average is '平均价';
comment on column master.product.price is '单价';
comment on column master.product.average_unit is '平均价单位';
comment on column master.product.price_unit is '单价单位';
comment on column master.product.name is '商品名称';
comment on column master.product.store_id is '商户ID';
comment on column master.product.stock is '库存';
comment on column master.product.max is '最大购买量';
comment on column master.product.min is '最小购买量';
comment on column master.product.vid is '视频ID';
comment on column master.product.description is '商品描述';
comment on column master.product.label is '商品标签';
comment on column master.product.pictures is '商品图集';
comment on column master.product.insert_time is '创建时间';
comment on column master.product.update_time is '更新时间';
alter table master.product
    owner to postgres;
create index if not exists i_product_store_id on master.product (store_id);

create table if not exists master.application_member
(
    id             bigserial primary key,
    member_id      bigint not null
        constraint application_member_member_id_fk references member on delete cascade,
    application_id bigint not null
        constraint application_member_application_id_fk references application on delete cascade
);
comment on table master.application_member is '管理员与平台资源关联';
comment on column master.application_member.member_id is '管理员ID';
comment on column master.application_member.application_id is '平台资源ID';
alter table master.application_member
    owner to postgres;

create table if not exists master.application_customer
(
    id             bigserial primary key,
    customer_id    bigint not null
        constraint application_customer_customer_id_fk references customer on delete cascade,
    application_id bigint not null
        constraint application_customer_application_id_fk references application on delete cascade,
    store_id       bigint not null
        constraint application_customer_store_id_fk references store on delete cascade
);
comment on table master.application_customer is '营业员与服务资源关联';
comment on column master.application_customer.customer_id is '营业员ID';
comment on column master.application_customer.application_id is '服务资源ID';
comment on column master.application_customer.store_id is '商户ID';
alter table master.application_customer
    owner to postgres;

create table if not exists master.store_customer
(
    id          bigserial primary key,
    customer_id bigint not null
        constraint store_customer_customer_id_fk references customer on delete cascade,
    store_id    bigint not null
        constraint store_customer_store_id_fk references store on delete cascade
);
comment on table master.store_customer is '营业员与商户关联';
comment on column master.store_customer.customer_id is '营业员ID';
comment on column master.store_customer.store_id is '商户ID';
alter table master.store_customer
    owner to postgres;

create table if not exists master.prefer
(
    id          bigserial primary key,
    account_id  bigint    not null
        constraint prefer_account_id_fk references account on delete cascade,
    product_id  bigint    not null
        constraint prefer_product_id_fk references product on delete cascade,
    store_id    bigint    not null
        constraint prefer_store_id_fk references store on delete cascade,
    insert_time timestamp not null default current_timestamp,
    update_time timestamp not null default current_timestamp
);
comment on table master.prefer is '收藏';
comment on column master.prefer.account_id is '消费者ID';
comment on column master.prefer.product_id is '商品ID';
comment on column master.prefer.store_id is '商户ID';
comment on column master.prefer.insert_time is '创建时间';
comment on column master.prefer.update_time is '更新时间';
alter table master.prefer
    owner to postgres;
create unique index if not exists ui_prefer_store_id_account_id_product_id on master.prefer (store_id, account_id, product_id);

create table if not exists master.cart
(
    id          bigserial primary key,
    account_id  bigint    not null
        constraint cart_account_id_fk references account on delete cascade,
    product_id  bigint    not null
        constraint cart_product_id_fk references product on delete cascade,
    store_id    bigint    not null
        constraint cart_store_id_fk references store on delete cascade,
    total       numeric   not null,
    checked     boolean   not null default false,
    num         integer   not null default 1
        constraint cart_num_check check (num >= 1),
    insert_time timestamp not null default current_timestamp,
    update_time timestamp not null default current_timestamp
);
comment on table master.cart is '购物车';
comment on column master.cart.account_id is '消费者ID';
comment on column master.cart.product_id is '商品ID';
comment on column master.cart.store_id is '商户ID';
comment on column master.cart.total is '小计';
comment on column master.cart.checked is '是否选中';
comment on column master.cart.num is '购买数量';
comment on column master.cart.insert_time is '创建时间';
comment on column master.cart.update_time is '更新时间';
alter table master.cart
    owner to postgres;
create unique index if not exists ui_cart_store_id_account_id_product_id on master.cart (store_id, account_id, product_id);

create table if not exists master.address
(
    id          bigserial primary key,
    account_id  bigint      not null
        constraint address_account_id_fk references account on delete cascade,
    username    varchar(20) not null,
    phone       char(11)    not null,
    location    point       not null,
    name        varchar(50) not null,
    detail      varchar(50) not null,
    postcode    char(6)     not null,
    code        text[]      not null,
    value       text[]      not null,
    insert_time timestamp   not null default current_timestamp,
    update_time timestamp   not null default current_timestamp
);
comment on table master.address is '收货地址';
comment on column master.address.account_id is '消费者ID';
comment on column master.address.username is '联系人';
comment on column master.address.phone is '联系方式';
comment on column master.address.location is '坐标';
comment on column master.address.name is '地址名称';
comment on column master.address.detail is '地址详情';
comment on column master.address.postcode is '邮编';
comment on column master.address.code is '省市区编号';
comment on column master.address.value is '省市区';
comment on column master.address.insert_time is '创建时间';
comment on column master.address.update_time is '更新时间';
alter table master.address
    owner to postgres;
