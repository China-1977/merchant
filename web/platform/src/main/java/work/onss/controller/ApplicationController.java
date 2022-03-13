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
import work.onss.domain.Application;
import work.onss.domain.ApplicationRepository;


/**
 * 资源管理
 *
 * @author wangchanghao
 */
@Log4j2
@RestController
public class ApplicationController {
    @Autowired
    private ApplicationRepository applicationRepository;


    /**
     * @param predicate 查询条件
     * @param pageable  分页参数
     * @return 资源分页
     */
    @GetMapping(value = {"applications"}, name = "资源列表")
    public Page<Application> applications(@QuerydslPredicate(bindings = ApplicationRepository.class) Predicate predicate, @PageableDefault Pageable pageable) {
        return applicationRepository.findAll(predicate, pageable);
    }

}

