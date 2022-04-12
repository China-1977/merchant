package work.onss.domain;

import com.google.gson.FieldNamingPolicy;
import com.vladmihalcea.hibernate.type.array.IntArrayType;
import com.vladmihalcea.hibernate.type.array.StringArrayType;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.postgresql.geometric.PGpoint;
import org.postgresql.jdbc.PgArray;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.jdbc.core.RowMapper;
import work.onss.config.PointType;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Objects;

/**
 * 地址
 *
 * @author wangchanghao
 */
@Builder
@Log4j2
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@TypeDefs({
        @TypeDef(name = "string-array", typeClass = StringArrayType.class),
        @TypeDef(name = "int-array", typeClass = IntArrayType.class),
        @TypeDef(name = "point", typeClass = PointType.class),
})
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Site implements Serializable, RowMapper<Site> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "请填写地址名称")
    private String name;
    @NotBlank(message = "请填写地址详情")
    @Size(min = 3, max = 50, message = "请尽可能填写详细地址")
    private String detail;
    @Type(type = "point")
    @NotNull(message = "请重新定位收货地址")
    private PGpoint location;
    @NotBlank(message = "请选择地区")
    private String postcode;
    @Type(type = "string-array")
    @NotEmpty(message = "请选择地区")
    @Size(min = 3, max = 3, message = "请选择地区")
    private String[] code;
    @Type(type = "string-array")
    @NotEmpty(message = "请选择地区")
    @Size(min = 3, max = 3, message = "请选择地区")
    private String[] value;
    @CreatedDate
    private Timestamp insertTime;
    @LastModifiedDate
    private Timestamp updateTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Site site = (Site) o;
        return id != null && Objects.equals(id, site.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }


    @Override
    public Site mapRow(ResultSet rs, int rowNum) {
        Site site = new Site();
        Field[] fields = site.getClass().getDeclaredFields();
        for (Field field : fields) {
            String name = FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES.translateName(field);
            field.setAccessible(true);
            try {
                Object object = rs.getObject(name);
                if (object instanceof PgArray pgArray) {
                    object = pgArray.getArray();
                }
                field.set(site, object);
            } catch (SQLException | IllegalAccessException ignored) {
            }
        }
        return site;
    }
}
