package work.onss.service;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.log4j.Log4j2;
import org.postgresql.geometric.PGcircle;
import org.postgresql.geometric.PGpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import work.onss.domain.*;
import work.onss.dto.StoreDto;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@Service
public class QuerydslService {

    @Autowired
    private JPAQueryFactory jpaQueryFactory;
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    /**
     * @param id            订单ID
     * @param currentStatus 当前订单状态
     * @param transactionId 设置微信订单ID
     * @param status        设置订单状态
     */
    @Transactional
    public void setScore(Long id, Score.Status currentStatus, String transactionId, Score.Status status) {
        QScore qScore = QScore.score;
        jpaQueryFactory.update(qScore)
                .set(qScore.status, status)
                .set(qScore.transactionId, transactionId)
                .where(qScore.id.eq(id), qScore.status.eq(currentStatus))
                .execute();
    }

    /**
     * @param id            订单ID
     * @param currentStatus 当前订单状态
     * @param status        设置订单状态
     */
    @Transactional
    public void setScore(Long id, Score.Status currentStatus, Score.Status status) {
        QScore qScore = QScore.score;
        jpaQueryFactory.update(qScore)
                .set(qScore.status, status)
                .where(qScore.id.eq(id), qScore.status.eq(currentStatus))
                .execute();
    }

    /**
     * @param pGcircle 区域
     * @param keyword  描述全文检索
     * @param pageable 分页参数
     * @return 区域内商户
     */
    public List<StoreDto> stores(PGcircle pGcircle, String keyword, Pageable pageable) {
        Map<String, Object> value = new HashMap<>();
        PGpoint center = pGcircle.center;
        if (center == null) {
            throw new RuntimeException("请选择位置信息");
        }
        value.put("location", center.getValue());
        value.put("circle", pGcircle.getValue());
        value.put("limit", pageable.getPageSize());
        value.put("offset", pageable.getOffset());
        String sql;
        if (StringUtils.hasLength(keyword)) {
            sql = "select s.id,s.shortname,s.description,s.trademark,s.location <-> :location\\:\\:point as distance from store s where s.status = 'true' and s.location <@ :circle\\:\\:circle and to_tsvector(s.description) @@ to_tsquery(:keyword) order by distance limit :limit offset :offset ";
            value.put("keyword", keyword);
        } else {
            sql = "select s.id,s.shortname,s.description,s.trademark,s.location <-> :location\\:\\:point as distance from store s where s.status = 'true' and s.location <@ :circle\\:\\:circle order by distance limit :limit offset :offset ";
        }
        return namedParameterJdbcTemplate.query(sql, value, new StoreDto());
    }

    /**
     * @param id  商户ID
     * @param cid 营业员ID
     * @return 商户详情
     */
    public Store get(Long id, Long cid) {
        QStoreCustomer qStoreCustomer = QStoreCustomer.storeCustomer;
        QStore qStore = QStore.store;
        return jpaQueryFactory.select(qStore).from(qStore)
                .innerJoin(qStoreCustomer).on(qStoreCustomer.storeId.eq(qStore.id))
                .where(qStore.id.eq(id), qStoreCustomer.customerId.eq(cid))
                .fetchOne();
    }

    /**
     * @param cid    营业员ID
     * @param limit  数量
     * @param offset 偏移
     * @return 商户详情
     */
    public List<Store> get(Long cid, Integer limit, Long offset) {
        QStoreCustomer qStoreCustomer = QStoreCustomer.storeCustomer;
        QStore qStore = QStore.store;
        return jpaQueryFactory.select(qStore).from(qStore)
                .innerJoin(qStoreCustomer).on(qStore.id.eq(qStoreCustomer.storeId))
                .where(qStoreCustomer.customerId.eq(cid))
                .limit(limit)
                .offset(offset)
                .fetch();
    }

    /**
     * @param cid    营业员ID
     * @param limit  数量
     * @param offset 偏移
     * @return 商户详情
     */
    public List<Store> get(Long cid, Integer limit, Long offset,QStore qStore,Expression<?>... exprs) {
        QStoreCustomer qStoreCustomer = QStoreCustomer.storeCustomer;
        return jpaQueryFactory.select(Projections.fields(Store.class,exprs)).from(qStore)
                .innerJoin(qStoreCustomer).on(qStore.id.eq(qStoreCustomer.storeId))
                .where(qStoreCustomer.customerId.eq(cid))
                .limit(limit)
                .offset(offset)
                .fetch();
    }

    /**
     * @param sid 商户ID
     * @return 营业员列表
     */
    public List<Customer> getCustomers(Long sid) {
        QCustomer qCustomer = QCustomer.customer;
        QStoreCustomer qStoreCustomer = QStoreCustomer.storeCustomer;
        return jpaQueryFactory.select(qCustomer).from(qCustomer)
                .innerJoin(qStoreCustomer).on(qStoreCustomer.customerId.eq(qCustomer.id))
                .where(qStoreCustomer.storeId.eq(sid))
                .fetch();
    }

    public List<Customer> get(Long storeId, List<String> applications) {
        QCustomer qCustomer = QCustomer.customer;
        QApplicationCustomer qApplicationCustomer = QApplicationCustomer.applicationCustomer;
        QApplication qApplication = QApplication.application;
        return jpaQueryFactory.select(qCustomer).from(qApplicationCustomer)
                .innerJoin(qCustomer).on(qCustomer.id.eq(qApplicationCustomer.customerId))
                .innerJoin(qApplication).on(qCustomer.id.eq(qApplicationCustomer.applicationId))
                .where(qApplicationCustomer.storeId.eq(storeId), qApplication.contextPath.concat(qApplication.type).concat(qApplication.value).in(applications))
                .groupBy(qCustomer.id)
                .fetch();
    }

    /**
     * 商品编辑
     *
     * @param id      商品ID
     * @param sid     商户ID
     * @param product 商品
     * @return 更新数量
     */
    @Transactional
    public Long setProduct(Long id, Long sid, Product product) {
        QProduct qProduct = QProduct.product;
        return jpaQueryFactory.update(qProduct)
                .set(qProduct.name, product.getName())
                .set(qProduct.label, product.getLabel())
                .set(qProduct.price, product.getPrice())
                .set(qProduct.priceUnit, product.getPriceUnit())
                .set(qProduct.average, product.getAverage())
                .set(qProduct.averageUnit, product.getAverageUnit())
                .set(qProduct.stock, product.getStock())
                .set(qProduct.min, product.getMin())
                .set(qProduct.max, product.getMax())
                .set(qProduct.vid, product.getVid())
                .set(qProduct.status, product.getStatus())
                .set(qProduct.description, product.getDescription())
                .set(qProduct.pictures, product.getPictures())
                .where(qProduct.id.eq(id), qProduct.storeId.eq(sid))
                .execute();
    }

    /**
     * @param id    商户ID
     * @param store 商户
     * @return 更新数量
     */
    @Transactional
    public Long setStore(Long id, Store store) {
        QStore qStore = QStore.store;
        return jpaQueryFactory.update(qStore)
                .set(qStore.shortname, store.getShortname())
                .set(qStore.description, store.getDescription())
                .set(qStore.trademark, store.getTrademark())
                .set(qStore.pictures, store.getPictures())
                .set(qStore.videos, store.getVideos())
                .set(qStore.openTime, store.getOpenTime())
                .set(qStore.closeTime, store.getCloseTime())
                .set(qStore.addressValue, store.getAddressValue())
                .set(qStore.addressCode, store.getAddressCode())
                .set(qStore.addressName, store.getAddressName())
                .set(qStore.addressDetail, store.getAddressDetail())
                .set(qStore.username, store.getUsername())
                .set(qStore.phone, store.getPhone())
                .set(qStore.updateTime, Timestamp.from(Instant.now()))
                .where(qStore.id.eq(id))
                .execute();
    }

    /**
     * @param id          商户ID
     * @param applymentId 特约商户申请ID
     * @return 更新数量
     */
    @Transactional
    public Long setStore(Long id, String applymentId) {
        QStore qStore = QStore.store;
        return jpaQueryFactory.update(qStore)
                .set(qStore.applymentId, applymentId)
                .where(qStore.id.eq(id))
                .execute();
    }

    /**
     * @param id           商户ID
     * @param status       是否营业
     * @param businessCode 特约商户申请业务编号
     * @param subMchId     特约商户号
     * @return 更新数量
     */
    @Transactional
    public Long setStore(Long id, Boolean status, String businessCode, String subMchId) {
        QStore qStore = QStore.store;
        return jpaQueryFactory.update(qStore)
                .set(qStore.status, status)
                .set(qStore.businessCode, businessCode)
                .set(qStore.subMchId, subMchId)
                .where(qStore.id.eq(id))
                .execute();
    }

    public Store getStore(Long id, Expression<?>... exprs) {
        QStore qStore = QStore.store;
        return jpaQueryFactory.select(Projections.fields(Store.class, qStore.id,
                qStore.shortname,
                qStore.description,
                qStore.trademark,
                qStore.status,
                qStore.pictures,
                qStore.videos,
                qStore.openTime,
                qStore.closeTime,
                qStore.username,
                qStore.phone,
                qStore.location,
                qStore.postcode,
                qStore.addressName,
                qStore.addressDetail,
                qStore.addressCode,
                qStore.addressValue)).from(qStore).where(qStore.id.eq(id)).fetchOne();
    }
}
