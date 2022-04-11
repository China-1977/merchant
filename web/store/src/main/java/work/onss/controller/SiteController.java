package work.onss.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import work.onss.domain.Site;
import work.onss.domain.SiteRepository;
import work.onss.exception.ServiceException;

import java.util.List;

@Log4j2
@RestController
public class SiteController {

    @Autowired
    protected SiteRepository siteRepository;

    /**
     * @param sid  商户ID
     * @param site 编辑内容
     * @return 最新站点内容
     */
    @PostMapping(value = {"sitees"})
    public Site saveOrInsert(@RequestHeader(name = "sid") Long sid, @RequestBody @Validated Site site) {
        site.setStoreId(sid);
        if (site.getId() == null) {
            siteRepository.save(site);
        } else {
            Site oldSite = siteRepository.findByIdAndStoreId(site.getId(), sid).orElseThrow(() -> new ServiceException("FAIL", "该数据不存在,请联系客服", site));
            site.setInsertTime(oldSite.getInsertTime());
            siteRepository.save(site);
        }
        return site;
    }

    /**
     * @param sid 商户ID
     * @param id  主键
     */
    @DeleteMapping(value = {"sitees/{id}"})
    public void delete(@RequestHeader(name = "sid") Long sid, @PathVariable Long id) {
        siteRepository.deleteByIdAndStoreId(id, sid);
    }

    /**
     * @param sid 商户ID
     * @param id  主键
     * @return 站点
     */
    @GetMapping(value = {"sitees/{id}"})
    public Site findOne(@RequestHeader(name = "sid") Long sid, @PathVariable Long id) {
        return siteRepository.findByIdAndStoreId(id, sid).orElse(null);
    }

    /**
     * @param sid 商户ID
     * @return 所有站点
     */
    @GetMapping(value = {"sitees"})
    public List<Site> findAll(@RequestHeader(name = "sid") Long sid) {
        return siteRepository.findByStoreIdOrderByUpdateTime(sid);
    }
}
