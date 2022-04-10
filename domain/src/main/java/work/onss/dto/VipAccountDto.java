package work.onss.dto;

import com.google.gson.FieldNamingPolicy;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.postgresql.jdbc.PgArray;
import org.postgresql.util.PGmoney;
import org.springframework.jdbc.core.RowMapper;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VipAccountDto implements Serializable, RowMapper<VipAccountDto> {
    private Long id;
    private Long accountId;
    private BigDecimal balance;
    private Integer discount;
    private String phone;
    private Timestamp insertTime;
    private Timestamp updateTime;

    @Override
    public VipAccountDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        VipAccountDto vipDto = new VipAccountDto();
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
