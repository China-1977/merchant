package work.onss.domain;

import lombok.*;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Hibernate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

/**
 * 营业员
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
public class Customer implements Serializable {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    private String phone;
    private String spAppid;
    private String spOpenid;
    private String subAppid;
    private String subOpenid;
    private String sessionKey;
    @CreatedDate
    private Timestamp insertTime;
    @LastModifiedDate
    private Timestamp updateTime;
    @Transient
    private List<Application> applications;

    public Customer(String phone, String subOpenid, String subAppid, String sessionKey, Timestamp insertTime) {
        this.phone = phone;
        this.subOpenid = subOpenid;
        this.subAppid = subAppid;
        this.sessionKey = sessionKey;
        this.insertTime = insertTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Customer customer = (Customer) o;
        return id != null && Objects.equals(id, customer.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
