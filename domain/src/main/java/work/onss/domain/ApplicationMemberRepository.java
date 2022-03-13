package work.onss.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ApplicationMemberRepository extends JpaRepository<ApplicationMember, Long>, QuerydslPredicateExecutor<ApplicationMember>, QuerydslBinderCustomizer<QApplicationMember> {

    default void customize(QuerydslBindings bindings, QApplicationMember qApplicationMember) {
        bindings.bind(qApplicationMember.id).withDefaultBinding();
    }

    @Modifying
    @Transactional
    void deleteByMemberId(Long memberId);

    @Modifying
    @Transactional
    void deleteByApplicationIdIn(List<Long> applicationsId);

    @Modifying
    @Transactional
    void deleteByApplicationIdNotInAndMemberId(List<Long> applicationsId, Long memberId);

    Optional<ApplicationMember> findByApplicationIdAndMemberId(Long applicationId, Long memberId);
}
