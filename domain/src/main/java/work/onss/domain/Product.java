package work.onss.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vladmihalcea.hibernate.type.array.StringArrayType;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Objects;

/**
 * 商品
 *
 * @author wangchanghao
 */
@Log4j2
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@TypeDefs({
        @TypeDef(name = "string-array", typeClass = StringArrayType.class),
})
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Product implements Serializable {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    private Long storeId;
    private String vid;
    @NotBlank(message = "商品名称不能为空")
    private String name;
    @NotBlank(message = "商品描述不能为空")
    private String description;
    @NotNull(message = "单价不能为空")
    @DecimalMin(value = "0.00", message = "商品单价不能小于{value}元")
    @Digits(fraction = 2, integer = 10, message = "单价小数位不能大于{fraction},整数不能大于{integer}")
    @JsonFormat(pattern = "#.00", shape = JsonFormat.Shape.STRING)
    private BigDecimal price;
    @NotBlank(message = "单位不能为空")
    private String priceUnit;
    @NotNull(message = "销售价不能为空")
    @DecimalMin(value = "0.00", message = "销售价不能小于{value}元")
    @Digits(fraction = 2, integer = 10, message = "销售价限制{integer}位整数和{fraction}位小数")
    @JsonFormat(pattern = "#.00", shape = JsonFormat.Shape.STRING)
    private BigDecimal average;
    @NotBlank(message = "单位不能为空")
    private String averageUnit;
    @NotNull(message = "请填写库存数量")
    @Min(value = 0, message = "库存不能小于{value}")
    private Integer stock;
    @NotNull(message = "请填写最小购买数量")
    @Min(value = 1, message = "最小购买数量不能小于{value}")
    private Integer min = 1;
    @NotNull(message = "请填写最大购买数量")
    @Min(value = 1, message = "最大购买数量不能小于{value}")
    private Integer max = 1;
    @NotBlank(message = "商品标签不能为空")
    private String label;
    private Boolean status = false;
    @Type(type = "string-array")
    @NotEmpty(message = "请上传商品图片")
    @Size(min = 1, max = 9, message = "仅限上传{min}-{max}张图片")
    private String[] pictures;
    @CreatedDate
    private Timestamp insertTime;
    @LastModifiedDate
    private Timestamp updateTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Product product = (Product) o;
        return id != null && Objects.equals(id, product.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
