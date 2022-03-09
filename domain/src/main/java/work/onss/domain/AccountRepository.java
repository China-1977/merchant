package work.onss.domain;

import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.repository.CrudRepository;

import java.sql.Timestamp;
import java.util.Iterator;
import java.util.Optional;

public interface AccountRepository extends CrudRepository<Account, Long>, QuerydslPredicateExecutor<Account>, QuerydslBinderCustomizer<QAccount> {

    default void customize(QuerydslBindings bindings, QAccount qAccount) {
        bindings.bind(qAccount.id).withDefaultBinding();
        bindings.bind(qAccount.phone).first((path, value) -> {
            if (value.length() == 11) {
                return path.eq(value);
            } else {
                return path.contains(value);
            }
        });
        bindings.bind(qAccount.insertTime).all(((path, value) -> {
            Iterator<? extends Timestamp> iterator = value.iterator();
            if (value.size() == 1) {
                return Optional.ofNullable(path.eq(iterator.next()));
            }
            return Optional.ofNullable(path.between(iterator.next(), iterator.next()));
        }));
        bindings.bind(qAccount.updateTime).all(((path, value) -> {
            Iterator<? extends Timestamp> iterator = value.iterator();
            if (value.size() == 1) {
                return Optional.ofNullable(path.eq(iterator.next()));
            }
            return Optional.ofNullable(path.between(iterator.next(), iterator.next()));
        }));
    }

    Optional<Account> findBySubOpenid(String subOpenid);
}
