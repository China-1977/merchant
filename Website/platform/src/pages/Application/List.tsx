/**
 * 资源列表
 */
import React from 'react';
import ProTable from '@ant-design/pro-table';
import {request} from "@@/plugin-request/request";

const ApplicationList = () => {
  const columns: any = [
    {
      title: '序号',
      dataIndex: 'index',
      valueType: 'indexBorder',
      align: 'center'
    },
    {
      title: '服务',
      dataIndex: 'contextPath',
      align: 'center',
      copyable:true,
    },
    {
      title: '名称',
      dataIndex: 'name',
      copyable:true,
    },
    {
      title: '路径',
      dataIndex: 'value',
      copyable:true,
    },
    {
      title: '类型',
      dataIndex: 'type',
      align: 'center',
      copyable:true,
    },
  ];

  return (
    <ProTable
      rowKey="id"
      headerTitle="查询表格"
      columns={columns}
      search={{defaultCollapsed: false,}}
      request={(params => request("/platform/applications", {params}).then((res: any) => {
        return {data: res.content, total: res.totalElements}
      }))}
    />
  );
}

export default ApplicationList;
