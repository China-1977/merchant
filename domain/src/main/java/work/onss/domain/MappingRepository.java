package work.onss.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Objects;

public interface MappingRepository extends JpaRepository<Mapping, Long>, QuerydslPredicateExecutor<Mapping>, QuerydslBinderCustomizer<QMapping> {

    default void customize(QuerydslBindings bindings, QMapping qMapping) {
        bindings.bind(qMapping.name).first((path, s) -> Objects.requireNonNull(path).contains(s));
    }
}
