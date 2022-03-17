package work.onss.service;

import com.github.binarywang.wxpay.bean.order.WxPayMpOrderResult;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.v3.util.AesUtils;
import com.github.binarywang.wxpay.v3.util.SignUtils;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Service;
import work.onss.config.WechatMpProperties;
import work.onss.domain.Account;
import work.onss.domain.Product;
import work.onss.domain.Score;
import work.onss.domain.Store;
import work.onss.vo.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Log4j2
@Service
public class WxPay {

    private final WxPayService wxPayService;
    private final WechatMpProperties wechatMpProperties;

    public WxPay(WxPayService wxPayService, WechatMpProperties wechatMpProperties) {
        this.wxPayService = wxPayService;
        this.wechatMpProperties = wechatMpProperties;
    }


    public Score jsapi(ConfirmScore confirmScore, Store store, List<Product> products, Map<Long, Integer> cart, Account account) throws WxPayException {

        LocalDateTime localDateTime = LocalDateTime.now();
        String nowStr = localDateTime.format(DateTimeFormatter.ofPattern("yyMMddHHmmssSSS"));
        int i = RandomUtils.nextInt(0, 9999);
        String code = String.format("%s%04d", nowStr, i);
        Score score = new Score(confirmScore, account.getId(), cart, products, store, wechatMpProperties.getAppId(), wechatMpProperties.getMchId(), code);


        WXScore.Amount amount = WXScore.Amount.builder().currency("CNY").total(1).build();
        WXScore.Payer payer = WXScore.Payer.builder().subOpenid(account.getSubOpenid()).build();
//        WXScore.SettleInfo settleInfo = WXScore.SettleInfo.builder().profitSharing(true).build();

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        String timeExpire = localDateTime.plusHours(2).atZone(ZoneOffset.ofHours(8)).format(dateTimeFormatter);

        WXScore wxScore = WXScore.builder()
//                .settleInfo(settleInfo)
                .amount(amount)
                .payer(payer)
                .spAppid(wechatMpProperties.getAppId())
                .spMchid(wechatMpProperties.getMchId())
                .subAppid(confirmScore.getSubAppId())
                .subMchid(store.getSubMchId())
                .timeExpire(timeExpire)
                .notifyUrl(wechatMpProperties.getNotifyUrl())
                .description(store.getShortname())
                .outTradeNo(code)
                .build();

        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();

        String wxScoreStr = gson.toJson(wxScore);
        String transactionStr = wxPayService.postV3("https://api.mch.weixin.qq.com/v3/pay/partner/transactions/jsapi", wxScoreStr);
        Map<String, String> prepayMap = gson.fromJson(transactionStr, new TypeToken<Map<String, String>>() {
        }.getType());
        score.setPrepayId(prepayMap.get("prepay_id"));
        return score;
    }

    public WxPayMpOrderResult continuePay(Score score) {
        WxPayConfig wxPayConfig = wxPayService.getConfig();
        String timeStamp = String.valueOf(score.getInsertTime());
        String nonceStr = SignUtils.genRandomStr();
        String packageValue = "prepay_id=".concat(score.getPrepayId());
        String data = String.join("\n", score.getSubAppid(), timeStamp, nonceStr, packageValue, "");
        String sign = SignUtils.sign(data, wxPayConfig.getPrivateKey());

        return WxPayMpOrderResult.builder()
                .appId(score.getSubAppid())
                .timeStamp(timeStamp)
                .nonceStr(nonceStr)
                .packageValue(packageValue)
                .signType("RSA")
                .paySign(sign)
                .build();

    }

    public WXRefundResult refund(Score score) throws WxPayException {
        WXRefundRequest.Amount amount = WXRefundRequest.Amount.builder().refund(1L).total(1L).currency("CNY").build();
        String refundNotifyUrl = wechatMpProperties.getRefundNotifyUrl();
        String notifyUrl = String.format(refundNotifyUrl, score.getId());
        WXRefundRequest wxRefundRequest = WXRefundRequest.builder().amount(amount)
                .outRefundNo(score.getOutTradeNo())
                .subMchid(score.getSubMchid())
                .transactionId(score.getTransactionId())
                .notifyUrl(notifyUrl)
                .build();

        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
        String wxRefundStr = gson.toJson(wxRefundRequest);
        String refundsResultStr = wxPayService.postV3("https://api.mch.weixin.qq.com/v3/refund/domestic/refunds", wxRefundStr);
        return gson.fromJson(refundsResultStr, WXRefundResult.class);
    }

    public String decryptToString(WXNotify wxNotify) throws GeneralSecurityException, IOException {
        WXNotify.Resource resource = wxNotify.getResource();
        String associatedData = resource.getAssociatedData();
        String nonce = resource.getNonce();
        String ciphertext = resource.getCiphertext();
        return AesUtils.decryptToString(associatedData, nonce, ciphertext, wechatMpProperties.getApiv3Key());
    }

    public Transaction queryOrderV3(String outTradeNo, String transactionId, String subMchId) throws WxPayException {
        if (transactionId == null) {
            String url = String.format("https://api.mch.weixin.qq.com/v3/pay/partner/transactions/out-trade-no/%s?sp_mchid=%s&sub_mchid=%s", outTradeNo, wechatMpProperties.getMchId(), subMchId);
            String result = wxPayService.getV3(url);
            return new Gson().fromJson(result, Transaction.class);
        } else {
            String url = String.format("https://api.mch.weixin.qq.com/v3/pay/partner/transactions/id/%s?sp_mchid=%s&sub_mchid=%s", transactionId, wechatMpProperties.getMchId(), subMchId);
            String result = wxPayService.getV3(url);
            return new Gson().fromJson(result, Transaction.class);
        }
    }

    public Refund queryOrderV3(String outRefundNo, String subMchId) throws WxPayException {
        String url = String.format("https://api.mch.weixin.qq.com/v3/refund/domestic/refunds/%s?sub_mchid=%s", outRefundNo, subMchId);
        String result = wxPayService.getV3(url);
        return new Gson().fromJson(result, Refund.class);
    }
}
