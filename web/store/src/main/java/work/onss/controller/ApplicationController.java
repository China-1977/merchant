package work.onss.controller;


import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import work.onss.domain.Application;
import work.onss.domain.ApplicationRepository;

/**
 * 订单管理
 *
 * @author wangchanghao
 */
@Log4j2
@RestController
public class ApplicationController {

    @Autowired
    private ApplicationRepository applicationRepository;

    /**
     * @param id 资源ID
     * @return 资源详情
     */
    @GetMapping(value = {"applications/{id}"}, name = "资源详情")
    public Application score(@PathVariable Long id) {
        return applicationRepository.findById(id).orElse(null);
    }
}
