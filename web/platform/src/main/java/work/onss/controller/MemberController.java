package work.onss.controller;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.web.PageableDefault;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import work.onss.domain.*;

import java.util.ArrayList;
import java.util.List;


/**
 * 管理员管理
 *
 * @author wangchanghao
 */
@Log4j2
@RestController
public class MemberController {
    @Autowired
    private JPAQueryFactory jpaQueryFactory;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MappingMemberRepository mappingMemberRepository;

    /**
     * @param predicate 查询条件
     * @param pageable  分页参数
     * @return 管理员分页
     */
    @GetMapping(value = {"members"}, name = "管理员列表")
    public Page<Member> members(@QuerydslPredicate(bindings = MemberRepository.class) Predicate predicate, @PageableDefault Pageable pageable) {
        return memberRepository.findAll(predicate, pageable);
    }

    /**
     * @param id 待删除管理员ID
     */
    @Transactional
    @DeleteMapping(value = {"members/{id}"}, name = "管理员删除")
    public void deleteMember(@RequestHeader(name = "mid") Long mid, @PathVariable Long id) {
        if (mid.equals(id)) {
            throw new RuntimeException("无法删除自己");
        }
        memberRepository.deleteById(id);
        mappingMemberRepository.deleteByMemberId(id);
    }

    /**
     * @param id         管理员ID
     * @param mappingsId 待授权平台资源ID
     */
    @PostMapping(value = {"members/{id}/authorize"}, name = "管理员权限设置")
    public void authorize(@PathVariable Long id, @RequestBody List<Long> mappingsId) {
        mappingMemberRepository.deleteByMappingIdNotInAndMemberId(mappingsId, id);
        List<MappingMember> mappingMembers = new ArrayList<>();
        for (Long mappingId : mappingsId) {
            MappingMember mappingMember = mappingMemberRepository.findByMappingIdAndMemberId(mappingId, id).orElse(new MappingMember(id, mappingId));
            if (null == mappingMember.getId()) {
                mappingMembers.add(mappingMember);
            }
        }
        if (!mappingMembers.isEmpty()) {
            mappingMemberRepository.saveAll(mappingMembers);
        }
    }

    /**
     * @param id 管理员ID
     * @return 平台资源与管理员关系列表
     */
    @GetMapping(value = {"members/{id}/mappings"}, name = "管理员平台资源列表")
    public List<MappingDto> mappings(@PathVariable Long id) {
        QMapping qMapping = QMapping.mapping;
        QMappingMember qMappingMember = QMappingMember.mappingMember;
        return jpaQueryFactory.select(
                        Projections.bean(MappingDto.class, qMapping, qMappingMember))
                .from(qMapping)
                .leftJoin(qMappingMember).on(
                        qMappingMember.mappingId.eq(qMapping.id),
                        qMappingMember.memberId.eq(id)
                ).fetch();
    }

}

