package work.onss.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ApplicationCustomerRepository extends JpaRepository<ApplicationCustomer, Long>,
        QuerydslPredicateExecutor<ApplicationCustomer>, QuerydslBinderCustomizer<QApplicationCustomer> {

    default void customize(QuerydslBindings bindings, QApplicationCustomer qApplicationCustomer) {
        bindings.bind(qApplicationCustomer.id).withDefaultBinding();
    }

    Optional<ApplicationCustomer> findByApplicationIdAndCustomerIdAndStoreId(Long rid, Long cid, Long sid);

    @Modifying
    @Transactional
    void deleteByApplicationIdIn(List<Long> applicationsId);

    @Modifying
    @Transactional
    void deleteByApplicationIdNotInAndCustomerIdAndStoreId(List<Long> rids, Long cid, Long sid);

    @Modifying
    @Transactional
    void deleteByStoreId(Long storeId);
}
