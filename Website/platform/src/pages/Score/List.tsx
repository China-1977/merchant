/**
 * 订单列表
 */
import React, {useRef, useState} from 'react';
import ProTable, {ActionType} from '@ant-design/pro-table';
import {request, useRequest} from "@@/plugin-request/request";
import {DatePicker, Select} from "antd";

const ScoreList = () => {
  const ref = useRef<ActionType>();

  const scoreStatusEnum = {
    WAIT_PAY: {text: '待支付', status: 'Default'},
    REFUND_PROCESSING: {text: '退款处理中', status: 'Default'},
    WAIT_PACKAGE: {text: '待配货', status: 'Error'},
    WAIT_TAKE: {text: '待配送', status: 'Error'},
    WAIT_SIGN: {text: '待签收', status: 'Warning'},
    WAIT_DELIVER: {text: '待取货', status: 'Warning'},
    REFUND_SCCESS: {text: '退款成功', status: 'Success'},
    REFUND_CLOSED: {text: '退款关闭', status: 'Success'},
    REFUND_ABNORMAL: {text: '退款异常', status: 'Success'},
    FINISH: {text: '已完成', status: 'Success'},
  };

  const scoreDeliveryEnum = {
    true: {text: '是', status: 'Processing'},
    false: {text: '否', status: 'Default'},
  };


  const [accountOptions, setAccountOptions] = useState<any>([]);
  const {run: accounts} = useRequest((params: any) => {
    return {
      url: `/platform/accounts`,
      params
    }
  }, {
    manual: true
  });
  const [storeOptions, setStoreOptions] = useState<any>([]);
  const {run: stores} = useRequest((params: any) => {
    return {
      url: `/platform/stores`,
      params
    }
  }, {
    manual: true
  });

  const columns: any = [
    {
      title: '序号',
      dataIndex: 'index',
      valueType: 'indexBorder',
      align: 'center'
    },
    {
      title: '编号',
      dataIndex: 'outTradeNo',
      align: 'center',
      copyable: true,
      order: 98,
    },
    {
      title: '用户手机号',
      dataIndex: 'accountId',
      align: 'center',
      copyable: true,
      fieldProps: {mode: 'multiple'},
      renderFormItem: () => {
        return <Select
          showSearch
          filterOption={false}
          options={accountOptions}
          onSearch={(key) => {
            accounts({phone: key}).then((res: any) =>
              res.content.map((account: any) => ({
                label: `${account.phone}:${account.id}`,
                value: account.id,
              }))
            ).then((res) => {
              setAccountOptions(res)
            })
          }}>
        </Select>;
      }
    },
    {
      title: '姓名',
      dataIndex: 'username',
      align: 'center',
      copyable: true,
    },
    {
      title: '手机号',
      dataIndex: 'phone',
      align: 'center',
      copyable: true,
    },
    {
      title: '是否配送',
      dataIndex: 'delivery',
      align: 'center',
      fieldProps: {mode: 'multiple'},
      valueEnum: scoreDeliveryEnum,
      order: 96,
    },
    {
      title: '收货地址',
      dataIndex: 'addressDetail',
      copyable: true,
    },
    {
      title: '金额',
      dataIndex: 'total',
      align: 'right'
    },
    {
      title: '状态',
      dataIndex: 'status',
      align: 'center',
      fieldProps: {mode: 'multiple'},
      valueEnum: scoreStatusEnum,
      order: 97,
    },
    {
      title: '商户简称',
      dataIndex: 'storeId',
      align: 'center',
      copyable: true,
      fieldProps: {mode: 'multiple'},
      renderFormItem: () => {
        return <Select
          showSearch
          filterOption={false}
          options={storeOptions}
          onSearch={(key) => {
            stores({shortname: key}).then((res: any) =>
              res.content.map((store: any) => ({
                label: `${store.shortname}:${store.id}`,
                value: store.id,
              }))
            ).then((res) => {
              setStoreOptions(res)
            })
          }}>
        </Select>;
      }
    },
    {
      title: '简称',
      dataIndex: 'storeShortname',
      copyable: true,
    },
    {
      title: '负责人',
      dataIndex: 'storeUsername',
      align: 'center',
      copyable: true,
    },
    {
      title: '客服电话',
      dataIndex: 'storePhone',
      align: 'center',
      copyable: true,
    },
    {
      title: '门店地址',
      dataIndex: 'storeAddressDetail',
      copyable: true,
    },
    {
      title: '创建时间',
      dataIndex: 'insertTime',
      align: 'center',
      copyable:true,
      renderFormItem: () => {
        return <DatePicker.RangePicker/>;
      },
      order: 99,
    },
  ];

  return (
    <div>
      <ProTable
        rowKey="id"
        headerTitle="查询表格"
        actionRef={ref}
        columns={columns}
        search={{defaultCollapsed: false,}}
        columnsStateMap={{'accountId': {show: false}, 'storeId': {show: false}}}
        request={(params => request("/platform/scores", {params}).then((res: any) => {
          return {data: res.content, total: res.totalElements}
        }))}
      />
    </div>

  );
}

export default ScoreList;
