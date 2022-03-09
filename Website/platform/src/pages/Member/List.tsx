/**
 * 用户列表
 */
import React, {useRef, useState} from 'react';
import ProTable, {ActionType} from '@ant-design/pro-table';
import {request, useRequest} from "@@/plugin-request/request";
import {DatePicker, Dropdown, Form, Input, Menu, message, Modal, TreeSelect} from "antd";
import {useToggle} from "@umijs/hooks";

const UserList = () => {
  const ref = useRef<ActionType>();
  const [form2] = Form.useForm();
  const {state: isModalVisible2, toggle: toggle2} = useToggle();

  const [mappings, setMappings] = useState<any>([]);

  const {run: remove} = useRequest((id: any) => {
    return {
      url: `/platform/members/${id}`,
      method: 'DELETE',
    }
  }, {
    manual: true
  });

  const {run: mappingMembers} = useRequest((id: any) => {
    return {
      url: `/platform/members/${id}/mappings`,
    }
  }, {
    manual: true
  });

  const {run: updateMappings, loading: updateMappingsLoading} = useRequest((id: any, mappingsId: []) => {
    console.log(id,mappingsId)
    return {
      url: `/platform/members/${id}/authorize`,
      method: 'POST',
      data: mappingsId
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
      title: '姓名',
      dataIndex: 'name',
      align: 'center',
      copyable:true,
    },
    {
      title: '身份证',
      dataIndex: 'idCard',
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
      valueType: 'dateTime'
    },
    {
      title: '操作',
      valueType: 'option',
      align: 'center',
      render: (text: any, record: any, _: any) =>
        <Dropdown.Button key={record.id} overlay={
          <Menu>
            <Menu.Item key="remove" onClick={() => {
              remove(record.id).then(() => {
                message.success("删除成功").then(() => ref.current?.reload());
              })
            }}>删除</Menu.Item>
            <Menu.Item key="edit" onClick={() => {
              mappingMembers(record.id).then((res: any) => {
                let mappingsId = [];
                let newMappings: { title: any, value: any }[] = [];
                for (const item of res) {
                  if (item.mappingMember) {
                    mappingsId.push(item.mapping.id)
                  }
                  newMappings.push({title: item.mapping.name, value: item.mapping.id})
                }
                setMappings(newMappings);
                form2.setFieldsValue({...record, mappingsId});
                toggle2();
              })
            }}>授权</Menu.Item>
          </Menu>
        }>
          操作
        </Dropdown.Button>
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
        request={(params => request("/platform/members", {params}).then((res: any) => {
          return {data: res.content, total: res.totalElements}
        }))}
      />

      <Form labelCol={{span: 5}}
            form={form2}
            onFinish={(values) => {
              updateMappings(values.id, values.mappingsId).then(() => {
                form2.resetFields();
                toggle2();
                ref.current?.reload();
              });
            }}>
        <Modal centered={true} title="用户权限更新"
               visible={isModalVisible2}
               onOk={() => {
                 form2.submit();
               }}
               onCancel={() => {
                 form2.resetFields();
                 toggle2();
               }}
               confirmLoading={updateMappingsLoading}>
          <Form.Item hidden={true} name="id">
            <Input/>
          </Form.Item>

          <Form.Item label="姓名" name="name">
            <Input placeholder="请输入姓名" disabled={true}/>
          </Form.Item>

          <Form.Item label="手机号" name="phone">
            <Input placeholder="请输入手机号" disabled={true}/>
          </Form.Item>

          <Form.Item label="身份证" name="idCard">
            <Input placeholder="请输入职位" disabled={true}/>
          </Form.Item>

          <Form.Item label="接口权限" name="mappingsId" rules={[{required: true, message: '请选择接口选项'}]}>
            <TreeSelect
              style={{width: '100%'}}
              dropdownStyle={{maxHeight: 400, overflow: 'auto'}}
              treeData={mappings}
              placeholder="Please select"
              treeDefaultExpandAll
              multiple
            />
          </Form.Item>
        </Modal>
      </Form>
    </div>
  );
}

export default UserList;
