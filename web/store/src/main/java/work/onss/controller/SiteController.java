package work.onss.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import work.onss.domain.Site;
import work.onss.domain.SiteRepository;
import work.onss.exception.ServiceException;

@Log4j2
@RestController
public class SiteController {

    @Autowired
    protected SiteRepository siteRepository;

    /**
     * @param site 编辑内容
     * @return 最新站点内容
     */
    @PostMapping(value = {"sites"}, name = "站点编辑")
    public Site saveOrInsert(@RequestBody @Validated Site site) {
        if (site.getId() == null) {
            siteRepository.save(site);
        } else {
            Site oldSite = siteRepository.findById(site.getId()).orElseThrow(() -> new ServiceException("FAIL", "该数据不存在,请联系客服", site));
            site.setInsertTime(oldSite.getInsertTime());
            siteRepository.save(site);
        }
        return site;
    }

    /**
     * @param id 主键
     */
    @DeleteMapping(value = {"sites/{id}"}, name = "站点删除")
    public void delete(@PathVariable Long id) {
        siteRepository.deleteById(id);
    }

    /**
     * @param id 主键
     * @return 站点
     */
    @GetMapping(value = {"sites/{id}"}, name = "站点详情")
    public Site findOne(@PathVariable Long id) {
        return siteRepository.findById(id).orElse(null);
    }

    /**
     * @param pageable 分页参数
     * @return 所有站点
     */
    @GetMapping(value = {"sites"}, name = "站点列表")
    public Page<Site> findAll(@PageableDefault Pageable pageable) {
        return siteRepository.findAll(pageable);
    }
}
