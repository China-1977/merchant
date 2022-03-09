package work.onss.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Timestamp;

@Log4j2
@Data
@NoArgsConstructor
@ToString
public class Info {

    private Long mid;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Timestamp lastTime;

    public Info(Long mid, Timestamp lastTime) {
        this.mid = mid;
        this.lastTime = lastTime;
    }
}
