package work.onss.domain;

import lombok.*;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Hibernate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;

/**
 * 管理员
 *
 * @author wangchanghao
 */
@Log4j2
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Member implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Pattern(regexp = "^[1][34578][0-9]{9}$", message = "手机号格式错误")
    @NotBlank(message = "手机号不能为空")
    private String phone;
    @NotBlank(message = "姓名不能为空")
    private String name;
    @Pattern(regexp = "^[1-9]\\d{5}(18|19|([23]\\d))\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$", message = "身份证格式错误")
    @NotBlank(message = "身份证不能为空")
    private String idCard;
    @NotBlank(message = "密码不能为空")
    private String password;
    @CreatedDate
    private Timestamp insertTime;
    @LastModifiedDate
    private Timestamp updateTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Member member = (Member) o;
        return id != null && Objects.equals(id, member.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
