package work.onss.controller;

import com.querydsl.core.types.Predicate;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import work.onss.domain.Mapping;
import work.onss.domain.MappingRepository;


/**
 * 资源管理
 *
 * @author wangchanghao
 */
@Log4j2
@RestController
public class MappingController {
    @Autowired
    private MappingRepository mappingRepository;


    /**
     * @param predicate 查询条件
     * @param pageable  分页参数
     * @return 资源分页
     */
    @GetMapping(value = {"mappings"}, name = "资源列表")
    public Page<Mapping> mappings(@QuerydslPredicate(bindings = MappingRepository.class) Predicate predicate, @PageableDefault Pageable pageable) {
        return mappingRepository.findAll(predicate, pageable);
    }

}

