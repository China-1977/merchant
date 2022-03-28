package work.onss.controller;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import work.onss.domain.*;
import work.onss.dto.ProductDetailDto;
import work.onss.dto.StoreDto;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@RestController
public class ProductController {

    @Autowired
    private JPAQueryFactory jpaQueryFactory;
    @Autowired
    private StoreRepository storeRepository;

    /**
     * @param id 主键
     * @return 商品信息
     */
    @GetMapping(value = {"products/{id}"})
    public Map<String, Object> product(@PathVariable Long id, @RequestHeader(required = false, defaultValue = "0") Long aid) {
        QProduct qProduct = QProduct.product;
        QStore qStore = QStore.store;
        QPrefer qPrefer = QPrefer.prefer;
        QCart qCart = QCart.cart;
        Tuple tuple = jpaQueryFactory.select(qProduct, qStore, qPrefer, qCart)
                .from(qProduct)
                .leftJoin(qStore).on(qStore.id.eq(qProduct.storeId))
                .leftJoin(qPrefer).on(qPrefer.productId.eq(qProduct.id), qPrefer.accountId.eq(aid))
                .leftJoin(qCart).on(qCart.productId.eq(qProduct.id), qCart.accountId.eq(aid))
                .where(qProduct.id.eq(id))
                .fetchOne();

        if (tuple == null) {
            return null;
        }
        Product product = tuple.get(qProduct);
        Prefer prefer = tuple.get(qPrefer);
        Cart cart = tuple.get(qCart);

        Map<String,Object> data = new HashMap<>();
        data.put("product", product);
        data.put("cart", cart);
        data.put("prefer", prefer);
        return data;
    }

    /**
     * @param storeId 商户ID
     * @param aid 用户ID
     * @return 商品列表
     */
    @GetMapping(value = {"products"})
    public Map<String, Object> products(@RequestParam Long storeId, @RequestHeader(required = false) Long aid) {
        QStore qStore = QStore.store;
        StoreDto store = jpaQueryFactory.select(Projections.fields(
                        StoreDto.class,
                        qStore.id,
                        qStore.shortname,
                        qStore.username,
                        qStore.phone,
                        qStore.addressName,
                        qStore.addressDetail
                ))
                .from(qStore).where(qStore.id.eq(storeId)).fetchFirst();
        QProduct qProduct = QProduct.product;
        if (null == aid) {
            List<ProductDetailDto> products = jpaQueryFactory
                    .select(Projections.constructor(
                            ProductDetailDto.class,
                            qProduct.id,
                            qProduct.name,
                            qProduct.description,
                            qProduct.price,
                            qProduct.priceUnit,
                            qProduct.average,
                            qProduct.averageUnit,
                            qProduct.storeId,
                            qProduct.pictures
                    ))
                    .from(qProduct)
                    .where(qProduct.storeId.eq(storeId))
                    .fetch();
            return Map.of("store", store, "products", products);
        } else {
            QCart qCart = QCart.cart;
            List<ProductDetailDto> productDetailDtos = jpaQueryFactory
                    .select(Projections.constructor(
                            ProductDetailDto.class,
                            qProduct.id,
                            qCart.id,
                            qProduct.name,
                            qProduct.description,
                            qProduct.price,
                            qProduct.priceUnit,
                            qProduct.average,
                            qProduct.averageUnit,
                            qProduct.storeId,
                            qProduct.pictures,
                            qCart.num,
                            qCart.checked,
                            (qProduct.average.multiply(qCart.num)).as(qCart.total)
                    ))
                    .from(qProduct)
                    .leftJoin(qCart).on(qCart.productId.eq(qProduct.id), qCart.accountId.eq(aid))
                    .where(qProduct.storeId.eq(storeId))
                    .fetch();
            BigDecimal sum = BigDecimal.ZERO;
            boolean checkAll = true;
            for (ProductDetailDto productDetailDto : productDetailDtos) {
                if (null != productDetailDto.getCartId() && productDetailDto.getChecked()) {
                    sum = sum.add(productDetailDto.getTotal());
                } else {
                    checkAll = false;
                }
            }
            return Map.of("store", store, "products", productDetailDtos, "checkAll", checkAll, "sum", sum.toPlainString());
        }
    }
}
