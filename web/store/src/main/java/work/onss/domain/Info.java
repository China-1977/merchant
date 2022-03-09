package work.onss.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;

import java.util.Date;

/**
 * 商户登录信息
 *
 * @author wangchanghao
 */
@Log4j2
@Data
@NoArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Info {
    /**
     * 营业员ID
     */
    private Long cid;
    /**
     * 商户ID
     */
    private Long sid;
    /**
     * 营业员登录时间
     */
    private Date lastTime;

    public Info(Long cid, Date lastTime) {
        this.cid = cid;
        this.lastTime = lastTime;
    }

    public Info(Long cid, Long sid, Date lastTime) {
        this.cid = cid;
        this.sid = sid;
        this.lastTime = lastTime;
    }
}
