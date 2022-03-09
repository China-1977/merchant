package work.onss.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

public interface ResourceRepository extends JpaRepository<Resource, Long>, QuerydslPredicateExecutor<Resource>, QuerydslBinderCustomizer<QResource> {

    default void customize(QuerydslBindings bindings, QResource qResource) {
        bindings.bind(qResource.name).first((path, s) -> Objects.requireNonNull(path).contains(s));
    }

    @Modifying
    @Transactional
    void deleteByContextPath(String s);
}
