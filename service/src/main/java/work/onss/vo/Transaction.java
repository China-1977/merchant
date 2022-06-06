package work.onss.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class Transaction {

    private Amount amount;
    private String attach;
    private String bankType;
    private String outTradeNo;
    private Payer payer;
    private List<?> promotionDetail;
    private String spAppid;
    private String spMchid;
    private String subAppid;
    private String subMchid;
    private String successTime;
    private String tradeState;
    private String tradeStateDesc;
    private String tradeType;
    private String transactionId;

    @NoArgsConstructor
    @Data
    public static class Amount {
        private String currency;
        private String payerCurrency;
        private Integer payerTotal;
        private Integer total;
    }

    @NoArgsConstructor
    @Data
    public static class Payer {
        private String spOpenid;
        private String subOpenid;
    }
}
