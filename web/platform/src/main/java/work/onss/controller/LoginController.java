package work.onss.controller;

import com.google.gson.Gson;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import work.onss.config.SystemConfig;
import work.onss.domain.Info;
import work.onss.domain.Member;
import work.onss.domain.MemberRepository;
import work.onss.domain.QMember;
import work.onss.utils.Utils;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Log4j2
@RestController
public class LoginController {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private SystemConfig systemConfig;

    /**
     * @param member 管理员
     * @return 密钥
     */
    @PostMapping(value = {"login"})
    public Map<String, Object> login(@RequestBody Member member) {
        QMember qMember = QMember.member;
        Member oldMember = memberRepository.findOne(qMember.phone.eq(member.getPhone())).orElseThrow(() -> new RuntimeException("账号不存在或者密码错误"));
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        boolean matches = bCryptPasswordEncoder.matches(member.getPassword(), oldMember.getPassword());
        if (matches) {
            Instant now = Instant.now();
            Info info = new Info(oldMember.getId(), Timestamp.from(now));
            String subject = new Gson().toJson(info);
            String authorization = Utils.authorization(systemConfig.getSecret(), "1977", now, subject, oldMember.getId().toString(), "Platform");
            Map<String, Object> result = new HashMap<>();
            result.put("authorization", authorization);
            result.put("info", info);
            return result;
        } else {
            throw new RuntimeException("账号不存在或者密码错误");
        }
    }

    /**
     * @param member 待注册管理员
     * @return 注册管理员
     */
    @PostMapping(value = {"register"})
    public Member register(@Validated @RequestBody Member member) {
        int length = member.getPassword().length();
        if (6 > length || 12 < length) {
            throw new RuntimeException("请输入6~12位密码");
        }
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        member.setPassword(bCryptPasswordEncoder.encode(member.getPassword()));
        return memberRepository.save(member);
    }
}

