package work.onss.dto;

import com.google.gson.FieldNamingPolicy;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.postgresql.geometric.PGpoint;
import org.postgresql.jdbc.PgArray;
import org.springframework.jdbc.core.RowMapper;
import work.onss.domain.Vip;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StoreDto implements Serializable, RowMapper<StoreDto> {
    private Long id;
    private String shortname;
    private String description;
    private String trademark;
    private Double distance;
    private String username;
    private String phone;
    private String addressName;
    private String addressDetail;
    private String[] pictures;
    private String[] videos;
    private PGpoint location;
    private Vip vip;


    @Override
    public StoreDto mapRow(ResultSet rs, int rowNum) {
        StoreDto storeDto = new StoreDto();
        Field[] fields = storeDto.getClass().getDeclaredFields();
        for (Field field : fields) {
            String name = FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES.translateName(field);
            field.setAccessible(true);
            try {
                Object object = rs.getObject(name);
                if (object instanceof PgArray pgArray) {
                    object = pgArray.getArray();
                }
                field.set(storeDto, object);
            } catch (SQLException | IllegalAccessException ignored) {
            }
        }
        return storeDto;
    }
}
