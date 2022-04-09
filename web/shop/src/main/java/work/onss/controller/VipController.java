package work.onss.controller;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import work.onss.domain.QStore;
import work.onss.domain.QVip;
import work.onss.domain.Vip;
import work.onss.domain.VipRepository;
import work.onss.dto.VipDto;
import work.onss.exception.ServiceException;

import java.math.BigDecimal;
import java.util.List;

@Log4j2
@RestController
public class VipController {

    @Autowired
    protected JPAQueryFactory jpaQueryFactory;
    @Autowired
    protected VipRepository vipRepository;

    /**
     * @param aid 用户ID
     * @param vip 编辑内容
     */
    @PostMapping(value = {"vips"})
    public void saveOrInsert(@RequestHeader(name = "aid") Long aid, @RequestBody @Validated Vip vip) {
        QVip qVip = QVip.vip;
        jpaQueryFactory.insert(qVip).columns(qVip.accountId, qVip.storeId).values(aid, vip.getStoreId()).execute();
    }

    /**
     * @param aid 用户ID
     * @param id  主键
     */
    @DeleteMapping(value = {"vips/{id}"})
    public void delete(@RequestHeader(name = "aid") Long aid, @PathVariable Long id) {
        Vip oldVip = vipRepository.findByIdAndAccountId(id, aid).orElseThrow(() -> new ServiceException("FAIL", "该数据不存在,请联系客服", id));
        if (oldVip.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            String message = String.format("您的会员卡余额为:%s,无法删除", oldVip.getBalance().toPlainString());
            throw new ServiceException("FAIL", message);
        }
        vipRepository.deleteByIdAndAccountId(id, aid);
    }

    /**
     * @param aid 用户ID
     * @param id  主键
     * @return 会员卡
     */
    @GetMapping(value = {"vips/{id}"})
    public VipDto findOne(@RequestHeader(name = "aid") Long aid, @PathVariable Long id) {
        QVip qVip = QVip.vip;
        QStore qStore = QStore.store;
        return jpaQueryFactory.select(Projections.fields(VipDto.class,
                qVip.id,
                qVip.accountId,
                qVip.storeId,
                qVip.balance,
                qStore.shortname,
                qStore.trademark,
                qStore.username,
                qStore.phone,
                qStore.location,
                qStore.addressName,
                qStore.addressDetail,
                qStore.openTime,
                qStore.closeTime,
                qVip.insertTime,
                qVip.updateTime
        )).where(qVip.accountId.eq(aid).and(qVip.id.eq(id))).fetchFirst();
    }

    /**
     * @param aid 用户ID
     * @return 所有会员卡
     */
    @GetMapping(value = {"vips"})
    public List<VipDto> findAll(@RequestHeader(name = "aid") Long aid) {
        QVip qVip = QVip.vip;
        QStore qStore = QStore.store;
        return jpaQueryFactory.select(Projections.fields(VipDto.class,
                qVip.id,
                qVip.accountId,
                qVip.storeId,
                qVip.balance,
                qStore.shortname,
                qStore.trademark,
                qStore.openTime,
                qStore.closeTime,
                qVip.insertTime,
                qVip.updateTime
        )).where(qVip.accountId.eq(aid)).fetch();
    }
}
