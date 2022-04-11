package work.onss.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;

import java.sql.Timestamp;
import java.util.Iterator;
import java.util.Optional;

public interface SiteRepository extends JpaRepository<Site, Long>, JpaSpecificationExecutor<Site>, QuerydslPredicateExecutor<Site>, QuerydslBinderCustomizer<QSite> {

    default void customize(QuerydslBindings bindings, QSite qSite) {
        bindings.bind(qSite.id).withDefaultBinding();
        bindings.bind(qSite.insertTime).all(((path, value) -> {
            Iterator<? extends Timestamp> iterator = value.iterator();
            if (value.size() == 1) {
                return Optional.ofNullable(path.eq(iterator.next()));
            }
            return Optional.ofNullable(path.between(iterator.next(), iterator.next()));
        }));
        bindings.bind(qSite.updateTime).all(((path, value) -> {
            Iterator<? extends Timestamp> iterator = value.iterator();
            if (value.size() == 1) {
                return Optional.ofNullable(path.eq(iterator.next()));
            }
            return Optional.ofNullable(path.between(iterator.next(), iterator.next()));
        }));
    }

}
