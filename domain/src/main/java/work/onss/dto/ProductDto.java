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
public class ProductDto implements Serializable, RowMapper<ProductDto> {
    private Long id;
    private Long sid;
    private String vid;
    private String name;
    private String description;
    private BigDecimal price;
    private String priceUnit;
    private BigDecimal average;
    private String averageUnit;
    private Integer stock;
    private Integer min = 1;
    private Integer max = 1;
    private String label;
    private Boolean status = false;
    private String[] pictures;
    private Timestamp insertTime;
    private Timestamp updateTime;

    private Long cartId;
    private Boolean checked;
    private BigDecimal num;
    private BigDecimal total;

    @Override
    public ProductDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        ProductDto productDto = new ProductDto();
        Field[] fields = productDto.getClass().getDeclaredFields();
        for (Field field : fields) {
            String name = FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES.translateName(field);
            Object object = rs.getObject(name);
            if (object instanceof PgArray pgArray) {
                object = pgArray.getArray();
            } else if (object instanceof PGmoney pGmoney)
                object = BigDecimal.valueOf(pGmoney.val);
            try {
                field.setAccessible(true);
                field.set(productDto, object);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return productDto;
    }
}
