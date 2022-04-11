package work.onss.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import work.onss.domain.Site;
import work.onss.domain.SiteRepository;

import java.util.List;

@Log4j2
@RestController
public class SiteController {

    @Autowired
    protected SiteRepository siteRepository;

    /**
     * @param storeId 商户ID
     * @return 所有站点
     */
    @GetMapping(value = {"sites"})
    public List<Site> findAll(@RequestParam(name = "storeId") Long storeId) {
        return siteRepository.findByStoreIdOrderByUpdateTime(storeId);
    }
}
