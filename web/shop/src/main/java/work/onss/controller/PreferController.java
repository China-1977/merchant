package work.onss.controller;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import work.onss.domain.*;
import work.onss.dto.ProductDetailDto;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@RestController
public class PreferController {

    @Autowired
    private JPAQueryFactory jpaQueryFactory;
    @Autowired
    private PreferRepository preferRepository;
    @Autowired
    private ProductRepository productRepository;

    /**
     * @param aid       用户ID
     * @param productId 商品ID
     * @return 新增收藏
     */
    @PostMapping(value = {"prefers"})
    public Prefer saveOrInsert(@RequestHeader(name = "aid") Long aid, @RequestParam Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("该商品已下架"));
        Prefer prefer = new Prefer(product.getStoreId(), product.getId(), aid);
        preferRepository.save(prefer);
        return prefer;
    }

    /**
     * @param aid 用户ID
     * @param id  主键
     */
    @DeleteMapping(value = {"prefers/{id}"})
    public void delete(@RequestHeader(name = "aid") Long aid, @PathVariable Long id) {
        preferRepository.deleteByIdAndAccountId(id, aid);
    }

    /**
     * @param aid 用户ID
     * @return 所有收藏
     */
    @GetMapping(value = {"prefers"})
    public Page<ProductDetailDto> findAll(@RequestHeader(name = "aid") Long aid, @PageableDefault Pageable pageable) {
        QProduct qProduct = QProduct.product;
        QPrefer qPrefer = QPrefer.prefer;
        QCart qCart = QCart.cart;

        long count = preferRepository.count(qPrefer.accountId.eq(aid));
        List<ProductDetailDto> productDetailDtos = new ArrayList<>((int) count);
        if (0 < count) {
            productDetailDtos = jpaQueryFactory
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
                    .from(qPrefer)
                    .innerJoin(qProduct).on(qPrefer.accountId.eq(aid), qProduct.id.eq(qPrefer.productId))
                    .leftJoin(qCart).on(qCart.accountId.eq(aid), qCart.productId.eq(qPrefer.productId))
                    .limit(pageable.getPageSize())
                    .offset(pageable.getOffset())
                    .fetch();
        }
        return new PageImpl<>(productDetailDtos, pageable, count);
    }
}
