package work.onss.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, Long>, QuerydslPredicateExecutor<Store>, QuerydslBinderCustomizer<QStore> {

    default void customize(QuerydslBindings bindings, QStore qStore) {
        bindings.bind(qStore.shortname).first((path, s) -> Objects.requireNonNull(path).contains(s));
        bindings.bind(qStore.insertTime).all(((path, value) -> {
            Iterator<? extends Timestamp> iterator = value.iterator();
            if (value.size() == 1) {
                return Optional.ofNullable(path.eq(iterator.next()));
            }
            return Optional.ofNullable(path.between(iterator.next(), iterator.next()));
        }));
    }

    @Modifying
    @Transactional
    void deleteByIdAndCustomerId(Long id, Long cid);

    @Query(value = "select s.* as store, s.location <-> :location\\:\\:point as distance from store as s where s.location <@ :pGcircle\\:\\:circle order by distance desc ", nativeQuery = true)
    Page<Store> queryAll(@Param("location") String location, @Param("pGcircle") String pGcircle, Pageable pageable);
}
