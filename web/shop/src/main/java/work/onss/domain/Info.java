package work.onss.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Log4j2
@Data
@NoArgsConstructor
@ToString
public class Info {

    private Long aid;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date lastTime;

    public Info(Long aid, Date lastTime) {
        this.aid = aid;
        this.lastTime = lastTime;
    }
}
