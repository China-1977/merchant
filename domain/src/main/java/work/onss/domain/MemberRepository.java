package work.onss.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;

import java.sql.Timestamp;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, QuerydslPredicateExecutor<Member>, QuerydslBinderCustomizer<QMember> {

    default void customize(QuerydslBindings bindings, QMember qMember) {
        bindings.bind(qMember.phone).first((path, s) -> Objects.requireNonNull(path).contains(s));
        bindings.bind(qMember.insertTime).all(((path, value) -> {
            Iterator<? extends Timestamp> iterator = value.iterator();
            if (value.size() == 1) {
                return Optional.ofNullable(path.eq(iterator.next()));
            }
            return Optional.ofNullable(path.between(iterator.next(), iterator.next()));
        }));
    }
}
