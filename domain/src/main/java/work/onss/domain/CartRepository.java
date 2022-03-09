package work.onss.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long>, QuerydslPredicateExecutor<Cart>, QuerydslBinderCustomizer<QCart> {

    default void customize(QuerydslBindings bindings, QCart qCart) {
        bindings.bind(qCart.id).withDefaultBinding();
    }

    Optional<Cart> findByAccountIdAndId(Long aid, Long id);

    @Modifying
    @Transactional
    void deleteByIdAndAccountId(Long id, Long aid);

    List<Cart> findByAccountIdAndStoreId(Long aid, Long sid);

    long countByAccountIdAndStoreIdAndChecked(Long aid, Long sid, Boolean checked);

    Optional<Cart> findByAccountIdAndProductId(Long aid, Long pid);

    List<Cart> findByAccountId(Long aid);

    @Modifying
    void deleteByAccountIdAndStoreIdAndProductIdIn(Long aid, Long sid, Collection<Long> pid);

    @Modifying
    @Transactional
    void deleteByStoreId(Long storeId);
}
