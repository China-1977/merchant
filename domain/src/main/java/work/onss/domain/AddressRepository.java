package work.onss.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long>, QuerydslPredicateExecutor<Address>, QuerydslBinderCustomizer<QAddress> {

    default void customize(QuerydslBindings bindings, QAddress address) {
        bindings.bind(address.name).first((path, s) -> Objects.requireNonNull(path).contains(s));
        bindings.bind(address.insertTime).all(((path, value) -> {
            Iterator<? extends Timestamp> iterator = value.iterator();
            if (value.size() == 1) {
                return Optional.ofNullable(path.eq(iterator.next()));
            }
            return Optional.ofNullable(path.between(iterator.next(), iterator.next()));
        }));
    }

    Optional<Address> findByIdAndAccountId(Long id, Long accountId);

    @Modifying
    @Transactional
    void deleteByIdAndAccountId(Long id, Long accountId);

    List<Address> findByAccountIdOrderByUpdateTime(Long accountId);
}
