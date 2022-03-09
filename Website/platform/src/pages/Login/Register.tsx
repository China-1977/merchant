/**
 * 注册
 */
import React from 'react';
import {history} from 'umi';
import {Button, Card, Col, Divider, Form, Input, Layout, message, Row} from 'antd';
import {useRequest} from "@@/plugin-request/request";
import {useSize} from '@umijs/hooks';

const Register = () => {

  const [form] = Form.useForm();
  const [state] = useSize(document.querySelector('body'));

  const {run: register, loading} = useRequest((values: any) => {
    return {
      url: `/platform/register`,
      method: 'POST',
      data: values
    }
  }, {
    manual: true
  });

  const onFinish = (values: any) => {
    register(values).then((r: any) => {
      message.success("注册成功").then(r => {
        history.push("/login");
      });
    });
  };

  return (
    <Layout style={{height: state.height}}>
      <Layout.Header>Header</Layout.Header>
      <Layout.Content style={{paddingBlock: 140}}>
        <Row justify="space-around" align="middle">
          <Col span={6}>
            <Card title="注册" bordered={false} style={{textAlign: "center"}}>
              <Form labelCol={{span: 5}} form={form} onFinish={onFinish}>
                <Form.Item
                  label="姓名"
                  name="name"
                  rules={[{required: true, message: '请输入姓名'}]}
                >
                  <Input/>
                </Form.Item>

                <Form.Item
                  label="身份证"
                  name="idCard"
                  rules={[{required: true, message: '请输入18位身份证'}]}
                >
                  <Input/>
                </Form.Item>

                <Form.Item
                  label="手机号"
                  name="phone"
                  rules={[{required: true, message: '请输入11位手机号'}]}
                >
                  <Input/>
                </Form.Item>

                <Form.Item
                  label="密码"
                  name="password"
                  rules={[{required: true, message: '请输入密码'}]}
                >
                  <Input.Password/>
                </Form.Item>
                <Form.Item>
                  <Button href={'/login'} size='large'>登录</Button>
                  <Divider type="vertical"/>
                  <Button type="primary" htmlType="submit" loading={loading} size='large'>注册</Button>
                </Form.Item>
              </Form>
            </Card>
          </Col>
        </Row>
      </Layout.Content>
      <Layout.Footer>Footer</Layout.Footer>
    </Layout>
  );
}

export default Register;
