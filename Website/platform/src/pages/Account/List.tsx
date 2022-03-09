/**
 * 用户列表
 */
import React, {useRef} from 'react';
import ProTable, {ActionType} from '@ant-design/pro-table';
import {request} from "@@/plugin-request/request";
import {DatePicker} from 'antd';

const AccountList = () => {
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
        request={(params => request("/platform/accounts", {params}).then((res: any) => {
          return {data: res.content, total: res.totalElements}
        }))}
      />
    </div>
  );
}

export default AccountList;
