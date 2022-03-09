package work.onss.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;

import javax.validation.constraints.NotBlank;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;

public interface ScoreRepository extends JpaRepository<Score, Long>, QuerydslPredicateExecutor<Score>, QuerydslBinderCustomizer<QScore> {

    default void customize(QuerydslBindings bindings, QScore qScore) {
        bindings.bind(qScore.storeShortname).first((path, s) -> Objects.requireNonNull(path).contains(s));
        bindings.bind(qScore.insertTime).all(((path, value) -> {
            Iterator<? extends Timestamp> iterator = value.iterator();
            if (value.size() == 1) {
                return Optional.ofNullable(path.eq(iterator.next()));
            }
            return Optional.ofNullable(path.between(iterator.next(), iterator.next()));
        }));
    }

    Optional<Score> findByIdAndAccountId(Long id, Long aid);

    Optional<Score> findByIdAndStoreId(Long id, @NotBlank(message = "缺少商户参数") Long sid);

    Page<Score> findByStoreIdAndStatusIn(@NotBlank(message = "缺少商户参数") Long sid, Collection<Score.Status> status, Pageable pageable);

    Optional<Score> findByOutTradeNo(String outTradeNo);
}
