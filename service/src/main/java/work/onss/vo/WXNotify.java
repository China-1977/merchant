package work.onss.vo;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class WXNotify implements Serializable {
    private String id;
    private String createTime;
    private String resourceType;
    private String eventType;
    private Resource resource;
    private String summary;

    @Data
    @NoArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class Resource implements Serializable {
        private String algorithm;
        private String ciphertext;
        private String nonce;
        private String originalType;
        private String associatedData;
    }

    /**
     * 退款通知
     */
    @Data
    @NoArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class WXRefund implements Serializable {

        private String spMchid;
        private String subMchid;
        private String transactionId;
        private String outTradeNo;
        private String refundId;
        private String outRefundNo;
        private Status refundStatus;
        private String successTime;
        private String userReceivedAccount;
        private Amount amount;

        /**
         * SUCCESS：退款成功
         * CLOSE：退款关闭
         * ABNORMAL：退款异常，
         */
        @Getter
        @AllArgsConstructor
        public enum Status implements Serializable {
            SUCCESS,
            CLOSE,
            ABNORMAL
        }

        @Data
        @NoArgsConstructor
        @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
        public static class Amount implements Serializable {
            private int total;
            private int refund;
            private int payerTotal;
            private int payerRefund;
        }
    }

    /**
     * 支付通知
     */
    @Data
    @NoArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class WXTransaction implements Serializable {

        private String spMchid;
        private String subMchid;
        private String spAppid;
        private String subAppid;
        private String outTradeNo;
        private String transactionId;
        private String tradeType;
        private String tradeState;
        private String tradeStateDesc;
        private String bankType;
        private String attach;
        private String successTime;
        private WXTransaction.Payer payer;
        private WXTransaction.Amount amount;


        @Data
        @NoArgsConstructor
        @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
        public static class Payer implements Serializable {
            private String spOpenid;
            private String subOpenid;
        }

        @Data
        @NoArgsConstructor
        @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
        public static class Amount implements Serializable {
            private Integer total;
            private Integer payerTotal;
            private String currency;
            private String payerCurrency;
        }
    }
}
