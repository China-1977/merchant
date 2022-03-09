package work.onss.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface MappingMemberRepository extends JpaRepository<MappingMember, Long>, QuerydslPredicateExecutor<MappingMember>, QuerydslBinderCustomizer<QMappingMember> {

    default void customize(QuerydslBindings bindings, QMappingMember qMappingMember) {
        bindings.bind(qMappingMember.id).withDefaultBinding();
    }

    @Modifying
    @Transactional
    void deleteByMemberId(Long memberId);

    @Modifying
    @Transactional
    void deleteByMappingIdIn(List<Long> mappingsId);

    @Modifying
    @Transactional
    void deleteByMappingIdNotInAndMemberId(List<Long> mappingsId, Long memberId);

    Optional<MappingMember> findByMappingIdAndMemberId(Long mappingId, Long memberId);
}
