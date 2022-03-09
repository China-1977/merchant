package work.onss.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class Refund {
    private String refundId;
    private String outRefundNo;
    private String transactionId;
    private String outTradeNo;
    private String channel;
    private String userReceivedAccount;
    private String successTime;
    private String createTime;
    private String status;
    private String fundsAccount;
    private Amount amount;
    private List<PromotionDetail> promotionDetail;

    @NoArgsConstructor
    @Data
    public static class Amount {
        private Integer total;
        private Integer refund;
        private List<From> from;
        private Integer payerTotal;
        private Integer payerRefund;
        private Integer settlementRefund;
        private Integer settlementTotal;
        private Integer discountRefund;
        private String currency;

        @NoArgsConstructor
        @Data
        public static class From {
            private String account;
            private Integer amount;
        }
    }

    @NoArgsConstructor
    @Data
    public static class PromotionDetail {
        private String promotionId;
        private String scope;
        private String type;
        private Integer amount;
        private Integer refundAmount;
        private GoodsDetail goodsDetail;

        @NoArgsConstructor
        @Data
        public static class GoodsDetail {
            private String merchantGoodsId;
            private String wechatpayGoodsId;
            private String goodsName;
            private Integer unitPrice;
            private Integer refundAmount;
            private Integer refundQuantity;
        }
    }
}
