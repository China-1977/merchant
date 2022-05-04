/**
 * 商户列表
 */
import React, {useRef} from 'react';
import ProTable, {ActionType} from '@ant-design/pro-table';
import {request} from "@@/plugin-request/request";
import {DatePicker} from "antd";
import {storeStatusEnum} from "@/app";

const StoreList = () => {
  const ref = useRef<ActionType>();

  const columns: any = [
    {
      title: '序号',
      dataIndex: 'index',
      valueType: 'indexBorder',
      align: 'center'
    },
    {
      title: '简称',
      dataIndex: 'shortname',
      copyable:true,
    },
    {
      title: '代码',
      dataIndex: 'licenseNumber',
      align: 'center',
      copyable:true,
    },
    {
      title: '商户号',
      dataIndex: 'subMchId',
      align: 'center',
      copyable:true,
    },
    {
      title: '是否经营',
      dataIndex: 'status',
      align: 'center',
      fieldProps: {mode: 'multiple'},
      valueEnum: storeStatusEnum,
    },
    {
      title: '姓名',
      dataIndex: 'username',
      align: 'center',
      copyable:true,
    },
    {
      title: '手机号',
      dataIndex: 'phone',
      align: 'center',
      copyable:true,
    },
    {
      title: '地址',
      dataIndex: 'addressDetail',
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
        request={(params => request("/platform/stores", {params}).then((res: any) => {
          return {data: res.content, total: res.totalElements}
        }))}
      />
    </div>

  );
}

export default StoreList;
