package work.onss.vo;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author wangchanghao
 */
@Builder
@Data
public class Work<T> implements Serializable {

    private String code;
    private String message;
    private T data;

    public Work(String code, String message, T t) {
        this.code = code;
        this.message = message;
        this.data = t;
    }

    public static <T> Work<T> success(T t) {
        return new Work<>("SUCCESS", "操作成功", t);
    }

    public static <T> Work<T> fail(String message) {
        return new Work<>("FAIL", message, null);
    }

}
