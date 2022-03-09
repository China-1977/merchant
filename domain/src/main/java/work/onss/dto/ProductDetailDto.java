package work.onss.dto;

import lombok.Data;
import work.onss.domain.Cart;
import work.onss.domain.Prefer;
import work.onss.domain.Product;

import java.math.BigDecimal;

@Data
public class ProductDetailDto {
    private Product product;
    private Cart cart;
    private Prefer prefer;
    private BigDecimal total;
}
