package work.onss.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.gson.FieldNamingPolicy;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.postgresql.geometric.PGpoint;
import org.postgresql.jdbc.PgArray;
import org.postgresql.util.PGmoney;
import org.springframework.jdbc.core.RowMapper;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VipDto implements Serializable, RowMapper<VipDto> {
    private Long id;
    private Long accountId;
    private Long storeId;
    private BigDecimal balance;
    private Integer discount;
    private String shortname;
    private String trademark;
    private String username;
    private String phone;
    private PGpoint location;
    private String addressName;
    private String addressDetail;
    private String[] pictures;
    private String[] videos;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime openTime;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime closeTime;
    private Timestamp insertTime;
    private Timestamp updateTime;

    @Override
    public VipDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        VipDto vipDto = new VipDto();
        Field[] fields = vipDto.getClass().getDeclaredFields();
        for (Field field : fields) {
            String name = FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES.translateName(field);
            Object object = rs.getObject(name);
            if (object instanceof PgArray pgArray) {
                object = pgArray.getArray();
            } else if (object instanceof PGmoney pGmoney)
                object = BigDecimal.valueOf(pGmoney.val);
            try {
                field.setAccessible(true);
                field.set(vipDto, object);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return vipDto;
    }
}
