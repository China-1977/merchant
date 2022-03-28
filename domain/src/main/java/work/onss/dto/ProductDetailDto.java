package work.onss.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vladmihalcea.hibernate.type.array.StringArrayType;
import lombok.Data;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import java.math.BigDecimal;
import java.math.BigInteger;

@TypeDefs({
        @TypeDef(name = "string-array", typeClass = StringArrayType.class),
})
@Data
public class ProductDetailDto {
    private Long id;
    private Long cartId;
    private String name;
    private String description;
    @JsonFormat(pattern = "#.00", shape = JsonFormat.Shape.STRING)
    private BigDecimal price;
    private String priceUnit;
    @JsonFormat(pattern = "#.00", shape = JsonFormat.Shape.STRING)
    private BigDecimal average;
    private String averageUnit;
    private Long storeId;
    @Type(type = "string-array")
    private String[] pictures;
    private BigInteger num;
    private Boolean checked = false;
    @JsonFormat(pattern = "#.00", shape = JsonFormat.Shape.STRING)
    private BigDecimal total;

    public ProductDetailDto(Long id, Long cartId, String name, String description,
                            BigDecimal price, String priceUnit, BigDecimal average,
                            String averageUnit, Long storeId, String[] pictures, BigInteger num,
                            Boolean checked, BigDecimal total) {
        this.id = id;
        this.cartId = cartId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.priceUnit = priceUnit;
        this.average = average;
        this.averageUnit = averageUnit;
        this.storeId = storeId;
        this.pictures = pictures;
        this.num = num;
        this.checked = checked;
        this.total = total;
    }

    public ProductDetailDto(Long id, String name, String description, BigDecimal price, String priceUnit, BigDecimal average, String averageUnit, Long storeId, String[] pictures) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.priceUnit = priceUnit;
        this.average = average;
        this.averageUnit = averageUnit;
        this.storeId = storeId;
        this.pictures = pictures;
    }
}
