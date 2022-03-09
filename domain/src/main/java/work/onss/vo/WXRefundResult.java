package work.onss.vo;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@Builder
@Data
public class WXRefundResult implements Serializable {

    private Amount amount;
    private String channel;
    private String createTime;
    private String fundsAccount;
    private String outRefundNo;
    private String outTradeNo;
    private List<PromotionDetail> promotionDetail;
    private String refundId;
    /**
     * SUCCESS：退款成功
     * CLOSED：退款关闭
     * PROCESSING：退款处理中
     * ABNORMAL：退款异常
     */
    private Status status;
    private String successTime;
    private String transactionId;
    private String userReceivedAccount;

    @Getter
    @AllArgsConstructor
    public enum Status implements Serializable {
        SUCCESS,
        CLOSED,
        PROCESSING,
        ABNORMAL;
    }


    @Data
    @NoArgsConstructor
    public static class Amount implements Serializable {
        private String currency;
        private Long discountRefund;
        private List<From> from;
        private Long payerRefund;
        private Long payerTotal;
        private Long refund;
        private Long settlementRefund;
        private Long settlementTotal;
        private Long total;
    }

    @Data
    @NoArgsConstructor
    public static class From implements Serializable {
        private String account;
        private Long amount;
    }

    @Data
    @NoArgsConstructor
    public static class GoodsDetail implements Serializable {
        private String goodsName;
        private String merchantGoodsId;
        private Long refundAmount;
        private Long refundQuantity;
        private Long unitPrice;
        private String wechatpayGoodsId;
    }

    @Data
    @NoArgsConstructor
    public static class PromotionDetail implements Serializable {

        private Long amount;
        private GoodsDetail goodsDetail;
        private String promotionId;
        private Long refundAmount;
        private String scope;
        private String type;
    }
}
