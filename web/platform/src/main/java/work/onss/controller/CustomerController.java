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
import work.onss.domain.Customer;
import work.onss.domain.CustomerRepository;

/**
 * 营业员管理
 *
 * @author wangchanghao
 */
@Log4j2
@RestController
public class CustomerController {
    @Autowired
    private CustomerRepository customerRepository;

    /**
     * @param predicate 查询条件
     * @param pageable  分页参数
     * @return 资源分页
     */
    @GetMapping(value = {"customers"}, name = "营业员列表")
    public Page<Customer> mappings(@QuerydslPredicate(bindings = CustomerRepository.class) Predicate predicate, @PageableDefault(sort = {"id", "updateTime"}, direction = Sort.Direction.DESC) Pageable pageable) {
        return customerRepository.findAll(predicate, pageable);
    }
}

