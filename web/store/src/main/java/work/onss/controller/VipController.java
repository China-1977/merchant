package work.onss.controller;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import work.onss.domain.QAccount;
import work.onss.domain.QVip;
import work.onss.domain.VipRepository;
import work.onss.dto.VipAccountDto;

import java.util.List;

@Log4j2
@RestController
public class VipController {

    @Autowired
    protected JPAQueryFactory jpaQueryFactory;
    @Autowired
    protected VipRepository vipRepository;


    /**
     * @param sid 商户ID
     * @return 所有会员卡
     */
    @GetMapping(value = {"vips"}, name = "会员卡列表")
    public List<VipAccountDto> findAll(@RequestHeader(name = "sid") Long sid, @RequestParam(required = false) String phone, @PageableDefault Pageable pageable) {
        QVip qVip = QVip.vip;
        QAccount qAccount = QAccount.account;

        BooleanExpression booleanExpression = qAccount.id.eq(qVip.accountId);
        if (StringUtils.hasLength(phone)) {
            booleanExpression = booleanExpression.and(qAccount.phone.eq(phone));
        }
        return jpaQueryFactory.select(Projections.fields(VipAccountDto.class,
                        qVip.id,
                        qVip.accountId,
                        qVip.balance,
                        qVip.discount,
                        qAccount.phone,
                        qVip.insertTime,
                        qVip.updateTime
                )).from(qVip)
                .innerJoin(qAccount).on(booleanExpression)
                .where(qVip.storeId.eq(sid))
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch();
    }
}
