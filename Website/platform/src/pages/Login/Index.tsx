/**
 * 登陆
 */
import React from 'react';
import {history} from 'umi';
import {Button, Card, Col, Divider, Form, Input, Layout, Row} from 'antd';
import {useRequest} from "@@/plugin-request/request";
import {useLocalStorageState, useSize} from '@umijs/hooks';
import {useModel} from "@@/plugin-model/useModel";

const LoginIndex = () => {

  const [form] = Form.useForm();
  const [state] = useSize(document.querySelector('body'));

  const [info, setInfo] = useLocalStorageState('info', null);
  const [authorization, setAuthorization] = useLocalStorageState('authorization', null);
  const {refresh} = useModel('@@initialState');
  const {run: login, loading} = useRequest((values: any) => {
    return {
      url: `/platform/login`,
      method: 'POST',
      data: values
    }
  }, {
    manual: true
  });

  const onFinish = (values: any) => {
    console.log('Success:', values);
    login(values).then((res: any) => {
      setInfo(res.info);
      setAuthorization(res.authorization);
      refresh();
    }).then((res: any) => {
      history.push("/");
    });
  };


  return (
    <Layout style={{height: state.height}}>
      <Layout.Header>Header</Layout.Header>
      <Layout.Content style={{paddingBlock: 140}}>
        <Row justify="space-around" align="middle">
          <Col span={6}>
            <Card title="登录" bordered={false} style={{textAlign: "center",}}>
              <Form form={form} initialValues={{phone: '15063517240', password: 123456}} onFinish={onFinish}>
                <Form.Item
                  label="账号"
                  name="phone"
                  rules={[{required: true, message: '请输入手机号'}]}
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
                  <Button href={'/register'} size='large'>注册</Button>
                  <Divider type="vertical"/>
                  <Button type="primary" htmlType="submit" loading={loading} size='large'>登录</Button>
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

export default LoginIndex;
