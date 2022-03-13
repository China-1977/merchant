package work.onss.controller;

import com.querydsl.core.types.Predicate;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import work.onss.domain.Store;
import work.onss.domain.StoreRepository;


/**
 * 商户管理
 *
 * @author wangchanghao
 */
@Log4j2
@RestController
public class StoreController {
    @Autowired
    private StoreRepository storeRepository;

    /**
     * @param predicate 查询条件
     * @param pageable  分页参数
     * @return 资源分页
     */
    @GetMapping(value = {"stores"}, name = "商户列表")
    public Page<Store> stores(@QuerydslPredicate(bindings = StoreRepository.class) Predicate predicate, @PageableDefault(sort = {"id"},direction = Sort.Direction.DESC) Pageable pageable) {
        return storeRepository.findAll(predicate, pageable);
    }

}

