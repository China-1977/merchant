package work.onss.dto;

import com.google.gson.FieldNamingPolicy;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.postgresql.geometric.PGpoint;
import org.postgresql.jdbc.PgArray;
import org.springframework.jdbc.core.RowMapper;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StoreDto implements Serializable, RowMapper<StoreDto> {
    private Long id;
    private String shortname;
    private String description;
    private String trademark;
    private Boolean status;
    private String[] pictures;
    private String[] videos;
    private String licenseNumber;
    private Time openTime;
    private Time closeTime;
    private Long customerId;
    private Timestamp insertTime;
    private Timestamp updateTime;
    private String businessCode;
    private String subMchId;
    private String applymentId;
    private String username;
    private String phone;
    private String addressName;
    private String addressDetail;
    private PGpoint location;
    private String postcode;
    private String[] addressCode;
    private String[] addressValue;
    private Double distance;

    @Override
    public StoreDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        StoreDto storeDto = new StoreDto();
        Field[] fields = storeDto.getClass().getDeclaredFields();
        for (Field field : fields) {
            String name = FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES.translateName(field);
            Object object = rs.getObject(name);
            if (object instanceof PgArray pgArray) {
                object = pgArray.getArray();
            }
            try {
                field.setAccessible(true);
                field.set(storeDto, object);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return storeDto;
    }
}
