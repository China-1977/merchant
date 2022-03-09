package work.onss.controller;


import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import work.onss.domain.Resource;
import work.onss.domain.ResourceRepository;

/**
 * 订单管理
 *
 * @author wangchanghao
 */
@Log4j2
@RestController
public class ResourceController {

    @Autowired
    private ResourceRepository resourceRepository;

    /**
     * @param id 资源ID
     * @return 资源详情
     */
    @GetMapping(value = {"resources/{id}"}, name = "资源详情")
    public Resource score(@PathVariable Long id) {
        return resourceRepository.findById(id).orElse(null);
    }
}
