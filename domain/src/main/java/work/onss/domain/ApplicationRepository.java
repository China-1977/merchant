package work.onss.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;

import java.util.Objects;

public interface ApplicationRepository extends JpaRepository<Application, Long>, QuerydslPredicateExecutor<Application>, QuerydslBinderCustomizer<QApplication> {

    default void customize(QuerydslBindings bindings, QApplication qApplication) {
        bindings.bind(qApplication.name).first((path, s) -> Objects.requireNonNull(path).contains(s));
    }
}
