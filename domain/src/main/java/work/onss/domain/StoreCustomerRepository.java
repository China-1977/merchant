package work.onss.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.transaction.annotation.Transactional;

public interface StoreCustomerRepository extends JpaRepository<StoreCustomer, Long>, QuerydslPredicateExecutor<StoreCustomer> {

    @Modifying
    @Transactional
    void deleteByStoreIdAndCustomerId(Long sid, Long cid);

    @Modifying
    @Transactional
    void deleteByStoreId(Long sid);
}
