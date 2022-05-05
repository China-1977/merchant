package work.onss.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.*;

public interface ProductRepository extends JpaRepository<Product, Long>, QuerydslPredicateExecutor<Product>, QuerydslBinderCustomizer<QProduct> {

    default void customize(QuerydslBindings bindings, QProduct qProduct) {
        bindings.bind(qProduct.name).first((path, s) -> Objects.requireNonNull(path).contains(s));
        bindings.bind(qProduct.insertTime).all(((path, value) -> {
            Iterator<? extends Timestamp> iterator = value.iterator();
            if (value.size() == 1) {
                return Optional.ofNullable(path.eq(iterator.next()));
            }
            return Optional.ofNullable(path.between(iterator.next(), iterator.next()));
        }));
    }

    List<Product> findByIdInAndStoreId(Collection<Long> ids, Long sid);

    List<Product> findByStoreId(Long sid);
    Page<Product> findByStoreId(Long sid, Pageable pageable);

    Optional<Product> findByIdAndStoreId(Long id, Long sid);

    @Modifying
    @Transactional
    void deleteByIdAndStoreId(Long id, Long sid);

    @Modifying
    @Transactional
    void deleteByIdInAndStoreId(Collection<Long> ids, Long sid);

    @Modifying
    @Transactional
    void deleteByStoreId(Long storeId);
}
