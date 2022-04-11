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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import work.onss.domain.*;
import work.onss.vo.PasswordVo;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


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
    private ApplicationMemberRepository applicationMemberRepository;

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
    }

    /**
     * @param id             管理员ID
     * @param applicationsId 待授权平台资源ID
     */
    @PostMapping(value = {"members/{id}/authorize"}, name = "管理员权限设置")
    public void authorize(@PathVariable Long id, @RequestBody List<Long> applicationsId) {
        applicationMemberRepository.deleteByApplicationIdNotInAndMemberId(applicationsId, id);
        List<ApplicationMember> applicationMembers = new ArrayList<>();
        for (Long applicationId : applicationsId) {
            ApplicationMember applicationMember = applicationMemberRepository.findByApplicationIdAndMemberId(applicationId, id).orElse(new ApplicationMember(id, applicationId));
            if (null == applicationMember.getId()) {
                applicationMembers.add(applicationMember);
            }
        }
        if (!applicationMembers.isEmpty()) {
            applicationMemberRepository.saveAll(applicationMembers);
        }
    }

    /**
     * @param id 管理员ID
     * @return 平台资源与管理员关系列表
     */
    @GetMapping(value = {"members/{id}/applications"}, name = "管理员平台资源列表")
    public List<ApplicationDto> applications(@PathVariable Long id) {
        QApplication qApplication = QApplication.application;
        QApplicationMember qApplicationMember = QApplicationMember.applicationMember;
        return jpaQueryFactory.select(
                        Projections.bean(ApplicationDto.class, qApplication, qApplicationMember))
                .from(qApplication)
                .leftJoin(qApplicationMember).on(
                        qApplicationMember.applicationId.eq(qApplication.id),
                        qApplicationMember.memberId.eq(id)
                ).fetch();
    }

    /**
     * @param id         管理员ID
     * @param passwordVo 密码
     */
    @PostMapping(value = {"members/{id}/setPassword"})
    public void setPassword(@PathVariable Long id, @RequestBody PasswordVo passwordVo) {
        Set<String> password = passwordVo.getPassword();
        if (password.size() == 1) {
            Member oldMember = memberRepository.findById(id).orElseThrow(() -> new RuntimeException("账号不存在"));
            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            boolean matches = bCryptPasswordEncoder.matches(passwordVo.getOldPassword(), oldMember.getPassword());
            if (matches) {
                QMember qMember = QMember.member;
                jpaQueryFactory.update(qMember)
                        .set(qMember.password, password.iterator().next())
                        .where(qMember.id.eq(id))
                        .execute();
            } else {
                throw new RuntimeException("原密码错误");
            }

        } else {
            throw new RuntimeException("输入的密码不一致");
        }
    }

}

