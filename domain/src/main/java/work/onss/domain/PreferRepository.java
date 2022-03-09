package work.onss.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface PreferRepository extends JpaRepository<Prefer, Long>, QuerydslPredicateExecutor<Prefer>, QuerydslBinderCustomizer<QPrefer> {

    default void customize(QuerydslBindings bindings, QPrefer qPrefer) {
        bindings.bind(qPrefer.id).withDefaultBinding();
    }

    @Modifying
    @Transactional
    void deleteByIdAndAccountId(Long id, Long aid);

    List<Prefer> findByAccountId(Long aid);

    Optional<Prefer> findByProductIdAndAccountId(Long pid, Long aid);

    @Modifying
    @Transactional
    void deleteByStoreId(Long storeId);
}
