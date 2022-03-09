package work.onss.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;

import java.util.Objects;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long>,
        QuerydslPredicateExecutor<Customer>, QuerydslBinderCustomizer<QCustomer> {

    default void customize(QuerydslBindings bindings, QCustomer qCustomer) {
        bindings.bind(qCustomer.phone).first((path, s) -> Objects.requireNonNull(path).contains(s));
    }

    Optional<Customer> findBySubOpenid(String subOpenid);
}
