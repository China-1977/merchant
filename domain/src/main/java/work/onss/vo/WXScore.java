package work.onss.vo;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@Data
public class WXScore implements Serializable {
    private String timeExpire;
    private Amount amount;
    private SettleInfo settleInfo;
    private String spMchid;
    private String description;
    private String subAppid;
    private String notifyUrl;
    private Payer payer;
    private String spAppid;
    private String outTradeNo;
    private String goodsTag;
    private String subMchid;
    private String attach;
    private Detail detail;
    private SceneInfo sceneInfo;


    @Builder
    @Data
    public static class Amount implements Serializable {
        private int total;
        private String currency;
    }

    @Builder
    @Data
    public static class SettleInfo implements Serializable {
        private boolean profitSharing;
    }

    @Builder
    @Data
    public static class Payer implements Serializable {
        private String spOpenid;
        private String subOpenid;
    }

    @Builder
    @Data
    public static class Detail implements Serializable {
        private String invoiceId;
        private int costPrice;
        private List<GoodsDetail> goodsDetail;

        @Builder
        @Data
        public static class GoodsDetail implements Serializable {
            private String goodsName;
            private String wechatpayGoodsId;
            private int quantity;
            private String merchantGoodsId;
            private int unitPrice;
        }
    }

    @Builder
    @Data
    public static class SceneInfo implements Serializable {
        private StoreInfo storeInfo;
        private String deviceId;
        private String payerClientIp;

        @Builder
        @Data
        public static class StoreInfo implements Serializable {
            private String address;
            private String areaCode;
            private String name;
            private String id;
        }
    }
}
