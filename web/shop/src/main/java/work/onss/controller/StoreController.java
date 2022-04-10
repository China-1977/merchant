package work.onss.controller;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.log4j.Log4j2;
import org.postgresql.geometric.PGcircle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import work.onss.domain.QStore;
import work.onss.domain.Vip;
import work.onss.domain.VipRepository;
import work.onss.dto.StoreDto;
import work.onss.service.QuerydslService;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Log4j2
@RestController
public class StoreController {
    @Autowired
    protected JPAQueryFactory jpaQueryFactory;

    @Autowired
    private QuerydslService querydslService;
    @Autowired
    private VipRepository vipRepository;

    /**
     * @param id 主键
     * @return 店铺信息
     */
    @GetMapping(value = {"stores/{id}"})
    public StoreDto store(@PathVariable Long id) {
        QStore qStore = QStore.store;
        return jpaQueryFactory.select(Projections.fields(StoreDto.class,
                        qStore.id,
                        qStore.shortname,
                        qStore.description,
                        qStore.trademark,
                        qStore.username,
                        qStore.phone,
                        qStore.videos,
                        qStore.pictures,
                        qStore.location,
                        qStore.addressName
                )).from(qStore)
                .where(qStore.id.eq(id))
                .fetchOne();
    }

    /**
     * @param x        经度
     * @param y        纬度
     * @param keyword  关键字
     * @param pageable 分页参数
     * @return 店铺分页
     */
    @GetMapping(path = "stores/{x}-{y}/near")
    public List<StoreDto> store(@PathVariable(name = "x") Double x,
                                @PathVariable(name = "y") Double y,
                                @RequestParam(name = "r", defaultValue = "100000") Double r,
                                @RequestParam(required = false) String keyword,
                                @PageableDefault Pageable pageable) {
        PGcircle pGcircle = new PGcircle(x, y, r);
        return querydslService.stores(pGcircle, keyword, pageable);
    }

    /**
     * @param aid 用户ID
     * @param id  商户ID
     */
    @Transactional
    @PostMapping(value = {"stores/{id}/insertVip"})
    public Vip insertVip(@RequestHeader(name = "aid") Long aid, @PathVariable Long id) {
        Timestamp now = Timestamp.from(Instant.now());
        return vipRepository.save(new Vip(aid, id, BigDecimal.ZERO));
    }
}
