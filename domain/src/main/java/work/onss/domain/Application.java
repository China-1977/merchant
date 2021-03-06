package work.onss.domain;

import lombok.*;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Hibernate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;

/**
 * 平台资源
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
public class Application implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String value;
    private String name;
    private String type;
    private String contextPath;
    private Timestamp insertTime;
    private Timestamp updateTime;

    public Application(String value, String name, String type, String contextPath, Timestamp insertTime) {
        this.value = value;
        this.name = name;
        this.type = type;
        this.contextPath = contextPath;
        this.insertTime = insertTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Application application = (Application) o;
        return id != null && Objects.equals(id, application.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
