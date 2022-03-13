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
import work.onss.domain.Product;
import work.onss.domain.ProductRepository;

/**
 * 商品管理
 *
 * @author wangchanghao
 */
@Log4j2
@RestController
public class ProductController {
    @Autowired
    private ProductRepository productRepository;

    /**
     * @param predicate 查询条件
     * @param pageable  分页参数
     * @return 资源分页
     */
    @GetMapping(value = {"products"}, name = "商品列表")
    public Page<Product> products(@QuerydslPredicate(bindings = ProductRepository.class) Predicate predicate, @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable) {
        return productRepository.findAll(predicate, pageable);
    }

}

