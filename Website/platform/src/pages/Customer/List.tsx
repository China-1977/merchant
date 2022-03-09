/**
 * 营业员列表
 */
import React, {useRef, useState} from 'react';
import ProTable, {ActionType} from '@ant-design/pro-table';
import {request, useRequest} from "@@/plugin-request/request";
import {DatePicker, Select} from 'antd';

const CustomerList = () => {
  const ref = useRef<ActionType>();

  const columns: any = [
    {
      title: '序号',
      dataIndex: 'index',
      valueType: 'indexBorder',
      align: 'center'
    },
    {
      title: '手机号',
      dataIndex: 'phone',
      align: 'center',
      copyable:true,
    },
    {
      title: 'OPENID',
      dataIndex: 'subOpenid',
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
    {
      title: '更新时间',
      dataIndex: 'updateTime',
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
        request={(params => request("/platform/customers", {params}).then((res: any) => {
          return {data: res.content, total: res.totalElements}
        }))}
      />
    </div>
  );
}

export default CustomerList;
