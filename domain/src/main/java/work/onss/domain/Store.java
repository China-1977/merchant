package work.onss.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vladmihalcea.hibernate.type.array.StringArrayType;
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

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalTime;
import java.util.Objects;

/**
 * 商户
 *
 * @author wangchanghao
 */
@Log4j2
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@TypeDefs({
        @TypeDef(name = "string-array", typeClass = StringArrayType.class),
        @TypeDef(name = "point", typeClass = PointType.class),
})
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Store implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "商户简称不能为空")
    private String shortname;
    private String description;
    private String trademark = "picture/logo.png";
    private Boolean status = false;
    @Type(type = "string-array")
    private String[] pictures;
    @Type(type = "string-array")
    private String[] videos;
    @NotBlank(message = "统一社会信用代码不能为空")
    private String licenseNumber;
    @NotNull(message = "营业开始时间不能为空")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime openTime;
    @NotNull(message = "营业结束时间不能为空")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime closeTime;
    private Long customerId;
    @CreatedDate
    private Timestamp insertTime;
    @LastModifiedDate
    private Timestamp updateTime;
    private String businessCode;
    private String subMchId;
    private String applymentId;

    @NotBlank(message = "请填写姓名")
    private String username;
    @NotBlank(message = "请填写手机号")
    @Pattern(regexp = "^[1][34578][0-9]{9}$", message = "手机号格式错误")
    private String phone;
    @Type(type = "point")
    @NotNull(message = "请重新定位收货地址")
    private PGpoint location;
    @NotBlank(message = "请选择地区")
    private String postcode;
    @NotBlank(message = "请填写地址名称")
    private String addressName;
    @NotBlank(message = "请填写地址详情")
    @Size(min = 3, max = 50, message = "请尽可能填写详细地址")
    private String addressDetail;
    @Type(type = "string-array")
    @NotEmpty(message = "请选择地区")
    @Size(min = 3, max = 3, message = "请选择地区")
    private String[] addressCode;
    @Type(type = "string-array")
    @NotEmpty(message = "请选择地区")
    @Size(min = 3, max = 3, message = "请选择地区")
    private String[] addressValue;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Store store = (Store) o;
        return id != null && Objects.equals(id, store.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
