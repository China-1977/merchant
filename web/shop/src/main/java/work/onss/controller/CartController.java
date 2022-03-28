package work.onss.controller;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import work.onss.domain.*;
import work.onss.dto.ProductDetailDto;
import work.onss.dto.StoreDto;
import work.onss.exception.ServiceException;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

@Log4j2
@RestController
public class CartController {
    @Autowired
    protected JPAQueryFactory jpaQueryFactory;
    @Autowired
    protected CartRepository cartRepository;
    @Autowired
    protected ProductRepository productRepository;
    @Autowired
    protected StoreRepository storeRepository;

    /**
     * @param aid 用户ID
     * @param id  主键
     */
    @DeleteMapping(value = {"carts/{id}"})
    public void delete(@PathVariable Long id, @RequestHeader(name = "aid") Long aid) {
        cartRepository.deleteByIdAndAccountId(id, aid);
    }

    /**
     * @param aid  用户ID
     * @param cart 购物车
     * @return 更新购车商品数量
     */
    @PostMapping(value = {"carts"})
    public Cart updateNum(@RequestHeader(name = "aid") Long aid, @Validated @RequestBody Cart cart) {
        if (cart.getNum().intValue() == 0) {
            throw new RuntimeException("请到购物车移除");
        }
        Product product = productRepository.findById(cart.getProductId()).orElseThrow(() -> new RuntimeException("该商品已下架"));
        if (cart.getNum().compareTo(product.getMax()) > 0) {
            throw new ServiceException("FAIL", MessageFormat.format("每次仅限购买{0}至{1}", product.getMin(), product.getMax()), product);
        } else if (cart.getNum().compareTo(product.getStock()) > 0) {
            throw new ServiceException("FAIL", MessageFormat.format("库存不足", product));
        } else if (!product.getStatus()) {
            throw new RuntimeException("该商品已下架");
        }

        BigDecimal total = product.getAverage().multiply(new BigDecimal(cart.getNum()));
        Cart oldCart = cartRepository.findByAccountIdAndProductId(aid, cart.getProductId()).orElse(null);
        if (oldCart == null) {
            cart.setStoreId(product.getStoreId());
            cart.setAccountId(aid);
            cart.setTotal(total);
            cartRepository.save(cart);
            return cart;
        } else {
            oldCart.setNum(cart.getNum());
            oldCart.setTotal(total);
            cartRepository.save(oldCart);
            return oldCart;
        }
    }

    /**
     * @param aid 用户ID
     * @return 购物车商户
     */
    @GetMapping(value = {"carts/getStores"})
    public List<StoreDto> getStores(@RequestHeader(name = "aid") Long aid) {
        QCart qCart = QCart.cart;
        QStore qStore = QStore.store;
        return jpaQueryFactory.select(Projections.fields(StoreDto.class, qStore.id, qStore.shortname, qStore.description, qStore.trademark)).from(qStore)
                .innerJoin(qCart).on(qCart.accountId.eq(aid), qStore.id.eq(qCart.storeId))
                .groupBy(qStore.id)
                .fetch();
    }

    /**
     * @param storeId 商户ID
     * @return 购物车
     */
    @GetMapping(value = {"carts"})
    public Map<String, Object> getCarts(@RequestParam(name = "storeId") Long storeId, @RequestHeader(name = "aid") Long aid) {
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
        QCart qCart = QCart.cart;
        QProduct qProduct = QProduct.product;
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
                .from(qCart)
                .innerJoin(qProduct).on(qProduct.id.eq(qCart.productId), qCart.storeId.eq(storeId), qCart.accountId.eq(aid))
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
        return Map.of("store",store,"products", productDetailDtos, "checkAll", checkAll, "sum", sum.toPlainString());
    }

    @Transactional
    @PostMapping(value = {"carts/{id}/setChecked"})
    public Long setChecked(@PathVariable Long id, @RequestHeader(name = "aid") Long aid, @RequestParam(name = "storeId") Long storeId, @RequestParam(name = "checked") Boolean checked) {
        Cart cart = cartRepository.findByAccountIdAndId(aid, id).orElseThrow(() -> new RuntimeException("请重新加入购物车"));
        cart.setChecked(!checked);
        cartRepository.save(cart);
        return cartRepository.countByAccountIdAndStoreIdAndChecked(aid, storeId, false);
    }

    @Transactional
    @PostMapping(value = {"carts/setCheckAll"})
    public String setCheckAll(@RequestParam Boolean checkAll, @RequestHeader(name = "aid") Long aid, @RequestParam(name = "storeId") Long storeId) {
        List<Cart> carts = cartRepository.findByAccountIdAndStoreId(aid, storeId);
        for (Cart cart : carts) {
            cart.setChecked(checkAll);
        }
        cartRepository.saveAll(carts);
        BigDecimal sum = new BigDecimal("0.00");
        if (checkAll) {
            for (Cart cart : carts) {
                sum = sum.add(cart.getTotal());
            }
        }
        return sum.toPlainString();
    }
}
