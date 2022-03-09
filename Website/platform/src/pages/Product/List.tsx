/**
 * 商品列表
 */
import React, {useRef, useState} from 'react';
import ProTable, {ActionType} from '@ant-design/pro-table';
import {request, useRequest} from "@@/plugin-request/request";
import {DatePicker, Select} from 'antd';

const ProductList = () => {
  const ref = useRef<ActionType>();

  const productStatusEnum = {
    true: {text: '是', status: 'Processing'},
    false: {text: '否', status: 'Default'},
  };

  const [options, setOptions] = useState<any>([]);

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
      title: '商户简称',
      dataIndex: 'storeId',
      align: 'center',
      copyable: true,
      fieldProps: {mode: 'multiple'},
      renderFormItem: () => {
        return <Select
          showSearch
          filterOption={false}
          options={options}
          onSearch={(key) => {
            stores({shortname: key}).then((res: any) =>
              res.content.map((store: any) => ({
                label: `${store.shortname}:${store.id}`,
                value: store.id,
              }))
            ).then((res) => {
              setOptions(res)
            })
          }}>
        </Select>;
      }
    },
    {
      title: '名称',
      dataIndex: 'name',
      align: 'center',
      copyable: true,
      order: 98
    },
    {
      title: '单价',
      dataIndex: 'price',
      align: 'right',
      render: (price: any, row: any) => {
        return `${price}${row.priceUnit}`
      }
    },
    {
      title: '均价',
      dataIndex: 'average',
      align: 'right',
      render: (average: any, row: any) => {
        return `${row.average}${row.averageUnit}`
      }
    },
    {
      title: '库存',
      dataIndex: 'stock',
      align: 'right',
      order:96
    },
    {
      title: '标签',
      dataIndex: 'label',
      align: 'center',
      copyable:true,
    },
    {
      title: '是否上架',
      dataIndex: 'status',
      align: 'center',
      fieldProps: {mode: 'multiple'},
      valueEnum: productStatusEnum,
      order:97
    },
    {
      title: '创建时间',
      dataIndex: 'insertTime',
      align: 'center',
      copyable:true,
      renderFormItem: () => {
        return <DatePicker.RangePicker/>;
      },
      order: 99
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
        columnsStateMap={{'storeId': {show: false}}}
        request={(params => request("/platform/products", {params}).then((res: any) => {
          return {data: res.content, total: res.totalElements}
        }))}
      />
    </div>
  );
}

export default ProductList;
