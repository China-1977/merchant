package work.onss.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ResourceCustomerRepository extends JpaRepository<ResourceCustomer, Long>,
        QuerydslPredicateExecutor<ResourceCustomer>, QuerydslBinderCustomizer<QResourceCustomer> {

    default void customize(QuerydslBindings bindings, QResourceCustomer qResourceCustomer) {
        bindings.bind(qResourceCustomer.id).withDefaultBinding();
    }

    Optional<ResourceCustomer> findByResourceIdAndCustomerIdAndStoreId(Long rid, Long cid, Long sid);

    @Modifying
    @Transactional
    void deleteByResourceIdIn(List<Long> resourcesId);

    @Modifying
    @Transactional
    void deleteByResourceIdNotInAndCustomerIdAndStoreId(List<Long> rids, Long cid, Long sid);

    @Modifying
    @Transactional
    void deleteByStoreId(Long storeId);
}
