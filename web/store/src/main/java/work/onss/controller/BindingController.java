package work.onss.controller;

import com.google.gson.Gson;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import work.onss.config.SystemConfig;
import work.onss.config.WechatMpProperties;
import work.onss.domain.*;
import work.onss.exception.ServiceException;
import work.onss.service.QuerydslService;
import work.onss.utils.Utils;

import java.text.MessageFormat;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 商户授权
 *
 * @author wangchanghao
 */
@Log4j2
@RestController
public class BindingController {
    @Autowired
    private JPAQueryFactory jpaQueryFactory;
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private StoreCustomerRepository storeCustomerRepository;
    @Autowired
    private SystemConfig systemConfig;
    @Autowired
    private WechatMpProperties wechatMpProperties;
    @Autowired
    private QuerydslService querydslService;

    /**
     * @param cid 营业员ID
     * @return 商户分页
     */
    @GetMapping(value = {"bindings/getStores"})
    public List<Store> stores(@RequestHeader(name = "cid") Long cid, @PageableDefault Pageable pageable) {
        QStore qStore = QStore.store;
        return querydslService.get(cid, pageable.getPageSize(), pageable.getOffset(), qStore, qStore.id, qStore.shortname, qStore.trademark, qStore.status);
    }

    /**
     * @param id  商户ID
     * @param cid 营业员ID
     * @return 密钥及商户信息
     */
    @PostMapping(value = {"bindings/{id}"})
    public Map<String, Object> bind(@PathVariable Long id, @RequestHeader(name = "cid") Long cid) {
        Customer customer = customerRepository.findById(cid).orElseThrow(() -> new ServiceException("FAIL", "账号不存在，请联系客服", MessageFormat.format("营业员ID:{0}", cid)));
        QStoreCustomer qStoreCustomer = QStoreCustomer.storeCustomer;
        QStore qStore = QStore.store;
        Store store = jpaQueryFactory.select(qStore).from(qStore)
                .innerJoin(qStoreCustomer).on(qStore.id.eq(qStoreCustomer.storeId))
                .where(qStore.id.eq(id), qStoreCustomer.customerId.eq(cid))
                .fetchOne();
        if (store == null) {
            throw new ServiceException("FAIL", "商户不存在，请联系客服!", MessageFormat.format("商户ID:{0}", id));
        }

        Map<String, Object> result = new HashMap<>();
        Instant now = Instant.now();
        Info info = new Info(customer.getId(), store.getId(), Date.from(now));
        String subject = new Gson().toJson(info);
        String authorization = Utils.authorization(systemConfig.getSecret(), "1977", now, subject, customer.getId().toString(), "WeChat");

        result.put("authorization", authorization);
        result.put("info", info);
        return result;
    }

    /**
     * @param cid   营业员ID
     * @param store 新增商户详情
     * @return 新商户详情
     */
    @Transactional
    @PostMapping(value = {"bindings/insertStore"})
    public Store insert(@RequestHeader(name = "cid") Long cid, @Validated @RequestBody Store store) {
        store.setCustomerId(cid);
        String businessCode = String.format("%s_%s",wechatMpProperties.getMchId(), store.getLicenseNumber());
        store.setBusinessCode(businessCode);
        store.setSubMchId(businessCode);
        storeRepository.save(store);
        StoreCustomer storeCustomer = new StoreCustomer(store.getId(), cid);
        storeCustomerRepository.save(storeCustomer);
        return store;
    }

    /**
     * @param id  商户ID
     * @param cid 营业员ID
     */
    @Transactional
    @DeleteMapping(value = {"bindings/{id}/deleteStore"})
    public void deleteStore(@PathVariable Long id, @RequestHeader(name = "cid") Long cid) {
        Store store = storeRepository.findById(id).orElseThrow(() -> new RuntimeException("商户不存在，请联系平台客服"));
        if (store.getStatus()) {
            throw new RuntimeException("请停止营业");
        }
        if (null != store.getApplymentId()) {
            throw new RuntimeException("您已申请特约商户,请联系平台客服");
        }
        if (store.getCustomerId().equals(cid)) {
            throw new RuntimeException("权限不足");
        }
        storeRepository.deleteById(id);
    }
}

