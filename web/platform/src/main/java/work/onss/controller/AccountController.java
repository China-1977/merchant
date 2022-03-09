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
import work.onss.domain.Account;
import work.onss.domain.AccountRepository;

/**
 * 用户管理
 *
 * @author wangchanghao
 */
@Log4j2
@RestController
public class AccountController {
    @Autowired
    private AccountRepository accountRepository;

    /**
     * @param predicate 查询条件
     * @param pageable  分页参数
     * @return 资源分页
     */
    @GetMapping(value = {"accounts"}, name = "用户列表")
    public Page<Account> mappings(@QuerydslPredicate(bindings = AccountRepository.class) Predicate predicate, @PageableDefault(sort = {"id","updateTime"}, direction = Sort.Direction.DESC) Pageable pageable) {
        return accountRepository.findAll(predicate, pageable);
    }

}

