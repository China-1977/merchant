package work.onss.controller;

import lombok.extern.log4j.Log4j2;
import org.postgresql.geometric.PGcircle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import work.onss.domain.Store;
import work.onss.domain.StoreRepository;
import work.onss.dto.StoreDto;
import work.onss.service.QuerydslService;

import java.util.List;

@Log4j2
@RestController
public class StoreController {


    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private QuerydslService querydslService;
    /**
     * @param id 主键
     * @return 店铺信息
     */
    @GetMapping(value = {"stores/{id}"})
    public Store store(@PathVariable Long id) {
        return storeRepository.findById(id).orElse(null);
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
}
