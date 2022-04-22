package work.onss.exception;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author wangchanghao
 */
@Builder
@EqualsAndHashCode(callSuper = true)
@ResponseStatus(code = HttpStatus.NOT_EXTENDED)
@Data
public class ServiceException extends RuntimeException {
    private String code;
    private String message;
    private Object data;

    public ServiceException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public ServiceException(String code, String message, Object data) {
        super(message);
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public ServiceException(String code, String message, Object data, Throwable e) {
        super(message, e);
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public ServiceException(Throwable e) {
        super(e);
    }

}
