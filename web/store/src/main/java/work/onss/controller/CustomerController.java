package work.onss.controller;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import work.onss.domain.*;
import work.onss.exception.ServiceException;
import work.onss.service.QuerydslService;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * 商户管理
 *
 * @author wangchanghao
 */
@Log4j2
@RestController
public class CustomerController {
    @Autowired
    private JPAQueryFactory jpaQueryFactory;
    @Autowired
    private QuerydslService querydslService;
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private StoreCustomerRepository storeCustomerRepository;
    @Autowired
    private ResourceCustomerRepository resourceCustomerRepository;


    /**
     * @param sid 商户ID
     * @return 营业员列表
     */
    @GetMapping(value = {"customers"}, name = "营业员列表")
    public List<Customer> detail(@RequestHeader(name = "sid") Long sid) {
        return querydslService.getCustomers(sid);
    }

    /**
     * @param cid      营业员Id
     * @param sid      商户ID
     * @param customer 邀请营业员
     * @return 商户与营业员关联信息
     */
    @PostMapping(value = {"customers/inviteCustomer"}, name = "营业成员邀请")
    public StoreCustomer inviteCustomer(@RequestHeader(name = "cid") Long cid, @RequestHeader(name = "sid") Long sid, @RequestBody Customer customer) {
        QCustomer qCustomer = QCustomer.customer;
        QStoreCustomer qStoreCustomer = QStoreCustomer.storeCustomer;

        Tuple tuple = jpaQueryFactory.select(qCustomer.id, qStoreCustomer.id, qStoreCustomer).from(qCustomer)
                .leftJoin(qStoreCustomer).on(qStoreCustomer.customerId.eq(qCustomer.id), qStoreCustomer.storeId.eq(sid))
                .where(qCustomer.phone.eq(customer.getPhone()))
                .fetchOne();

        if (tuple == null) {
            throw new RuntimeException("手机号未注册");
        }
        Long customerId = tuple.get(qCustomer.id);
        if (cid.equals(customerId)) {
            throw new RuntimeException("无法邀请自己");
        }
        Long storeCustomerId = tuple.get(qStoreCustomer.id);

        StoreCustomer storeCustomer;
        if (storeCustomerId == null) {
            storeCustomer = new StoreCustomer(sid, customerId);
            storeCustomerRepository.save(storeCustomer);
        } else {
            storeCustomer = tuple.get(qStoreCustomer);
        }
        return storeCustomer;

    }

    /**
     * @param id  待删除营业成员ID
     * @param sid 商户ID
     * @param cid 当前登录营业成员ID
     */
    @DeleteMapping(value = {"customers/{id}"}, name = "营业成员删除")
    public void deleteCustomer(@RequestHeader(name = "sid") Long sid, @RequestHeader(name = "cid") Long cid, @PathVariable Long id) throws ServiceException {
        Store store = storeRepository.findById(sid).orElseThrow(() -> new ServiceException("FAIL", "商户不存在，请联系客服", MessageFormat.format("商户ID:{0}", id)));
        if (id.equals(store.getCustomerId())) {
            throw new RuntimeException("无法删除超级管理员!");
        }
        if (id.equals(cid)) {
            throw new RuntimeException("无法删除自己!");
        }
        storeCustomerRepository.deleteByStoreIdAndCustomerId(sid, id);
    }

    /**
     * @param id          营业员ID
     * @param sid         商户ID
     * @param resourceIds 待授权资源ID
     */
    @PostMapping(value = {"customers/{id}/authorize"}, name = "营业成员权限设置")
    public void updateResources(@PathVariable Long id, @RequestHeader(name = "sid") Long sid, @Validated @RequestBody List<Long> resourceIds) {
        resourceCustomerRepository.deleteByResourceIdNotInAndCustomerIdAndStoreId(resourceIds, id, sid);
        List<ResourceCustomer> resourceCustomers = new ArrayList<>();
        for (Long resourceId : resourceIds) {
            ResourceCustomer resourceCustomer = resourceCustomerRepository.findByResourceIdAndCustomerIdAndStoreId(resourceId, id, sid).orElse(new ResourceCustomer(sid, resourceId, id));
            if (null == resourceCustomer.getId()) {
                resourceCustomers.add(resourceCustomer);
            }
        }
        if (!resourceCustomers.isEmpty()) {
            resourceCustomerRepository.saveAll(resourceCustomers);
        }
    }

    /**
     * @param id  营业员ID
     * @param sid 商户ID
     * @return 资源与营业员关系列表
     */
    @GetMapping(value = {"customers/{id}/resources"}, name = "营业成员资源列表")
    public List<ResourceDoto> resources(@PathVariable Long id, @RequestHeader(name = "sid") Long sid) {
        QResource qResource = QResource.resource;
        QResourceCustomer qResourceCustomer = QResourceCustomer.resourceCustomer;
        return jpaQueryFactory.select(
                        Projections.bean(ResourceDoto.class, qResource, qResourceCustomer))
                .from(qResource)
                .leftJoin(qResourceCustomer).on(
                        qResourceCustomer.resourceId.eq(qResource.id),
                        qResourceCustomer.storeId.eq(sid),
                        qResourceCustomer.customerId.eq(id)
                ).fetch();
    }

}

