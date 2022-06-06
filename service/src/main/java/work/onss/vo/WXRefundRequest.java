package work.onss.vo;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Builder
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class WXRefundRequest implements Serializable {

    private Amount amount;
    private String fundsAccount;
    private List<GoodsDetail> goodsDetail;
    private String notifyUrl;
    private String outRefundNo;
    private String reason;
    private String subMchid;
    private String transactionId;

    @Data
    @Builder
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class Amount {
        private String currency;
        private List<From> from;
        private Long refund;
        private Long total;
    }

    @Data
    @NoArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class From implements Serializable {
        private String account;
        private Long amount;
    }

    @Data
    @NoArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class GoodsDetail implements Serializable {
        private String goodsName;
        private String merchantGoodsId;
        private Long refundAmount;
        private Long refundQuantity;
        private Long unitPrice;
        private String wechatpayGoodsId;
    }
}
