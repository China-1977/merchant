/**
 * 收货地址
 */
import React, {useRef, useState} from 'react';
import ProTable, {ActionType} from '@ant-design/pro-table';
import {request, useRequest} from "@@/plugin-request/request";
import {DatePicker, Select} from 'antd';

const AddressList = () => {
  const ref = useRef<ActionType>();

  const [options, setOptions] = useState<any>([]);

  const {run: accounts} = useRequest((params: any) => {
    return {
      url: `/platform/accounts`,
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
      title: '用户手机号',
      dataIndex: 'accountId',
      align: 'center',
      copyable: true,
      fieldProps: {mode: 'multiple'},
      renderFormItem: () => {
        return <Select
          showSearch
          filterOption={false}
          options={options}
          onSearch={(key) => {
            accounts({phone: key}).then((res: any) =>
              res.content.map((account: any) => ({
                label: `${account.phone}:${account.id}`,
                value: account.id,
              }))
            ).then((res) => {
              setOptions(res)
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
      copyable:true,
    },
    {
      title: '地址详情',
      dataIndex: 'detail',
      copyable:true,
    },
    {
      title: '创建时间',
      dataIndex: 'insertTime',
      align: 'center',
      copyable:true,
      renderFormItem: () => {
        return <DatePicker.RangePicker/>;
      },
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
        columnsStateMap={{'accountId': {show: false}}}
        request={(params => request("/platform/addresses", {params}).then((res: any) => {
          return {data: res.content, total: res.totalElements}
        }))}
      />
    </div>
  );
}

export default AddressList;
