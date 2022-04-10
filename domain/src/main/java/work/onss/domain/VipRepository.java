package work.onss.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public interface VipRepository extends JpaRepository<Vip, Long>, JpaSpecificationExecutor<Vip>, QuerydslPredicateExecutor<Vip>, QuerydslBinderCustomizer<QVip> {

    default void customize(QuerydslBindings bindings, QVip qVip) {
        bindings.bind(qVip.id).withDefaultBinding();
        bindings.bind(qVip.insertTime).all(((path, value) -> {
            Iterator<? extends Timestamp> iterator = value.iterator();
            if (value.size() == 1) {
                return Optional.ofNullable(path.eq(iterator.next()));
            }
            return Optional.ofNullable(path.between(iterator.next(), iterator.next()));
        }));
        bindings.bind(qVip.updateTime).all(((path, value) -> {
            Iterator<? extends Timestamp> iterator = value.iterator();
            if (value.size() == 1) {
                return Optional.ofNullable(path.eq(iterator.next()));
            }
            return Optional.ofNullable(path.between(iterator.next(), iterator.next()));
        }));
    }

    Optional<Vip> findByIdAndAccountId(Long id, Long accountId);
    Optional<Vip> findByIdAndStoreId(Long id, Long storeId);

    @Modifying
    @Transactional
    void deleteByIdAndAccountId(Long id, Long accountId);

    List<Vip> findByAccountIdOrderByUpdateTime(Long accountId);
}
