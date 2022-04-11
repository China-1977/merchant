package work.onss.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vladmihalcea.hibernate.type.array.StringArrayType;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import com.vladmihalcea.hibernate.type.json.JsonStringType;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.postgresql.geometric.PGpoint;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import work.onss.config.PointType;
import work.onss.vo.ConfirmScore;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Log4j2
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@TypeDefs({
        @TypeDef(name = "string-array", typeClass = StringArrayType.class),
        @TypeDef(name = "json", typeClass = JsonStringType.class),
        @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class),
        @TypeDef(name = "point", typeClass = PointType.class),
})
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Score implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(value = EnumType.STRING)
    private Way way;
    @Enumerated(value = EnumType.STRING)
    private Status status = Status.WAIT_PAY;
    @JsonFormat(pattern = "#.00", shape = JsonFormat.Shape.STRING)
    private BigDecimal total = BigDecimal.ZERO;
    @Type(type = "jsonb")
    private List<Score.Product> products;
    @CreatedDate
    private Timestamp insertTime;
    @LastModifiedDate
    private Timestamp updateTime;
    @JsonFormat(pattern = "yyyy.MM.dd HH:mm:ss")
    private Timestamp payTime;

    /* 消费者信息 */
    private Long accountId;
    private String username;
    private String phone;
    private String addressName;
    private String addressDetail;
    @Type(type = "point")
    private PGpoint location;
    private String postcode;
    @Type(type = "string-array")
    private String[] addressCode;
    @Type(type = "string-array")
    private String[] addressValue;

    /* 商户信息 */
    private Long storeId;
    private String storeShortname;

    private String spAppid;
    private String spMchid;
    private String subAppid;
    private String subMchid;
    private String outTradeNo;
    private String prepayId;
    private String transactionId;

    public Score(ConfirmScore confirmScore, Long accountId, Map<Long, Integer> cart, List<work.onss.domain.Product> products, Store store, String spAppid, String spMchid, String outTradeNo) {
        this.way = confirmScore.getWay();
        this.products = new ArrayList<>(products.size());
        for (work.onss.domain.Product product : products) {
            Integer num = cart.get(product.getId());
            BigDecimal total = product.getAverage().multiply(BigDecimal.valueOf(num));
            this.products.add(new Product(product.getId(), product.getName(), product.getPictures()[0], num, product.getAverage(), product.getAverageUnit(), total));
            this.total = this.total.add(total);
        }
        this.accountId = accountId;
        Address address = confirmScore.getAddress();
        this.username = confirmScore.getUsername();
        this.phone = confirmScore.getPhone();
        this.addressName = address.getName();
        this.addressDetail = address.getDetail();
        this.location = address.getLocation();
        this.postcode = address.getPostcode();
        this.addressCode = address.getCode();
        this.addressValue = address.getValue();
        this.storeId = confirmScore.getStoreId();
        this.storeShortname = store.getShortname();
        this.subMchid = store.getSubMchId();
        this.spAppid = spAppid;
        this.spMchid = spMchid;
        this.subAppid = confirmScore.getSubAppId();
        this.outTradeNo = outTradeNo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Score score = (Score) o;
        return id != null && Objects.equals(id, score.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Getter
    @AllArgsConstructor
    public enum Status implements Serializable {
        WAIT_PAY("待支付"),
        WAIT_PACKAGE("待配货"),
        WAIT_DELIVER("待配送"),
        WAIT_SIGN("待签收"),
        REFUND_SCCESS("退款成功"),
        REFUND_CLOSED("退款关闭"),
        REFUND_PROCESSING("退款处理中"),
        REFUND_ABNORMAL("退款异常"),
        FINISH("已完成");
        private final String message;
    }

    @Getter
    @AllArgsConstructor
    public enum Way implements Serializable {
        MD("门店"),
        YZ("驿站"),
        PS("配送");
        private final String message;
    }

    @Data
    @NoArgsConstructor
    public static class Product implements Serializable {
        private Long pid;
        private String name;
        private String picture;
        private Integer num;
        private BigDecimal price;
        private String priceUnit;
        @JsonFormat(pattern = "#.00", shape = JsonFormat.Shape.STRING)
        private BigDecimal total;

        public Product(Long pid, String name, String picture, Integer num, BigDecimal price, String priceUnit, BigDecimal total) {
            this.pid = pid;
            this.name = name;
            this.picture = picture;
            this.num = num;
            this.price = price;
            this.priceUnit = priceUnit;
            this.total = total;
        }
    }
}
