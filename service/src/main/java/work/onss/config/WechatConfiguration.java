package work.onss.config;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.config.WxMaConfig;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.service.Applyment4SubService;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.Applyment4SubServiceImpl;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Data
@Log4j2
@Configuration
public class WechatConfiguration {
    @Autowired
    private WechatMpProperties wechatMpProperties;

    @Bean
    public WxPayService wxPayService() {
        WxPayConfig wxPayConfig = new WxPayConfig();
        wxPayConfig.setUseSandboxEnv(wechatMpProperties.getUseSandboxEnv());
        wxPayConfig.setAppId(wechatMpProperties.getAppId());
        wxPayConfig.setMchId(wechatMpProperties.getMchId());
        wxPayConfig.setMchKey(wechatMpProperties.getMchKey());

        wxPayConfig.setKeyPath(wechatMpProperties.getKeyPath());
        wxPayConfig.setNotifyUrl(wechatMpProperties.getNotifyUrl());
        //以下是apiv3以及支付分相关
        wxPayConfig.setServiceId(wechatMpProperties.getServiceId());
        wxPayConfig.setPayScoreNotifyUrl(wechatMpProperties.getPayScoreNotifyUrl());
        wxPayConfig.setPrivateKeyPath(wechatMpProperties.getPrivateKeyPath());
        wxPayConfig.setPrivateCertPath(wechatMpProperties.getPrivateCertPath());
        wxPayConfig.setCertSerialNo(wechatMpProperties.getCertSerialNo());
        wxPayConfig.setApiV3Key(wechatMpProperties.getApiv3Key());
        wxPayConfig.getVerifier();
        WxPayService wxPayService = new WxPayServiceImpl();
        wxPayService.setConfig(wxPayConfig);
        return wxPayService;
    }

    @Bean
    public WxMaService wxMaService() {
        WxMaService wxMaService = new WxMaServiceImpl();
        Map<String, WxMaConfig> configMap = new HashMap<>();
        wechatMpProperties.getAppConfigs().forEach(appConfig -> {
            WxMaDefaultConfigImpl wxMaConfig = new WxMaDefaultConfigImpl();
            wxMaConfig.setAppid(appConfig.getSubAppid());
            wxMaConfig.setSecret(appConfig.getSecret());
            configMap.put(appConfig.getSubAppid(), wxMaConfig);
        });
        wxMaService.setMultiConfigs(configMap);
        return wxMaService;
    }

    @Bean
    public Applyment4SubService applyment4SubService(WxPayService wxPayService) {
        return new Applyment4SubServiceImpl(wxPayService);
    }
}
