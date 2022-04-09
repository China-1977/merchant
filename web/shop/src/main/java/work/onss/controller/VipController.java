package work.onss.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import work.onss.domain.Vip;
import work.onss.domain.VipRepository;
import work.onss.exception.ServiceException;

import java.util.List;

@Log4j2
@RestController
public class VipController {

    @Autowired
    protected VipRepository vipRepository;

    /**
     * @param aid 用户ID
     * @param vip 编辑内容
     * @return 最新会员卡内容
     */
    @PostMapping(value = {"vips"})
    public Vip saveOrInsert(@RequestHeader(name = "aid") Long aid, @RequestBody @Validated Vip vip) {
        vip.setAccountId(aid);
        if (vip.getId() == null) {
            vipRepository.save(vip);
        } else {
            Vip oldVip = vipRepository.findByIdAndAccountId(vip.getId(), aid).orElseThrow(() -> new ServiceException("FAIL", "该数据不存在,请联系客服", vip));
            vip.setInsertTime(oldVip.getInsertTime());
            vipRepository.save(vip);
        }
        return vip;
    }

    /**
     * @param aid 用户ID
     * @param id  主键
     */
    @DeleteMapping(value = {"vips/{id}"})
    public void delete(@RequestHeader(name = "aid") Long aid, @PathVariable Long id) {
        vipRepository.deleteByIdAndAccountId(id, aid);
    }

    /**
     * @param aid 用户ID
     * @param id  主键
     * @return 会员卡
     */
    @GetMapping(value = {"vips/{id}"})
    public Vip findOne(@RequestHeader(name = "aid") Long aid, @PathVariable Long id) {
        return vipRepository.findByIdAndAccountId(id, aid).orElse(null);
    }

    /**
     * @param aid 用户ID
     * @return 所有会员卡
     */
    @GetMapping(value = {"vips"})
    public List<Vip> findAll(@RequestHeader(name = "aid") Long aid) {
        return vipRepository.findByAccountIdOrderByUpdateTime(aid);
    }
}
