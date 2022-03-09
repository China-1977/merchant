package work.onss.exception;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.github.binarywang.wxpay.exception.WxPayException;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import work.onss.vo.Work;

import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author wangchanghao
 */
@Log4j2
@ControllerAdvice
@ResponseBody
public class CommonException {

    @ResponseStatus(code = HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(RuntimeException.class)
    public Work<Object> exception(RuntimeException e) {
        return Work.fail(e.getMessage());
    }

    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(ServiceException.class)
    public Work<Object> serviceException(ServiceException e) {
        log.error(e);
        return Work.builder().code(e.getCode()).message(e.getMessage()).build();
    }

    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public Work<Object> exception(Exception e) {
        return Work.fail("系统异常");
    }

    @ResponseStatus(code = HttpStatus.NOT_EXTENDED)
    @ExceptionHandler(WxPayException.class)
    public Work<Object> xxPayException(WxPayException e) {
        log.error("支付失败", e);
        return Work.fail(e.getCustomErrorMsg());
    }

    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class})
    public Work<Object> methodArgumentNotValidException(Exception e) {
        StringJoiner message = new StringJoiner(",", "[", "]");
        if (e instanceof MethodArgumentNotValidException methodArgumentNotValidException) {
            methodArgumentNotValidException.getBindingResult().getAllErrors().forEach(item -> message.add(item.getDefaultMessage()));
        }
        if (e instanceof ConstraintViolationException constraintViolationException) {
            constraintViolationException.getConstraintViolations().forEach(constraintViolation -> message.add(constraintViolation.getMessage()));
        }
        return Work.fail(message.toString());
    }

    @ResponseStatus(code = HttpStatus.REQUEST_HEADER_FIELDS_TOO_LARGE)
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public Work<Object> maxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        return Work.fail("文件大小上限为1M");
    }

    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(JWTVerificationException.class)
    public Work<Object> exception(JWTVerificationException e) {
        return Work.builder().code("SESSION_EXPIRE").message("请重新登录").build();
    }

    @ResponseStatus(code = HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(MissingRequestHeaderException.class)
    public Work<Object> missingServletRequestParameterException(MissingRequestHeaderException e) {
        String parameterName = e.getHeaderName();
        Work.WorkBuilder<Object> workBuilder = Work.builder();
        switch (parameterName) {
            case "sid", "areaCode" -> workBuilder.code("MERCHANT_NOT_BIND").message("请绑定商户账户");
            case "cid", "aid", "mid" -> workBuilder.code("SESSION_EXPIRE").message("请重新登录");
            default -> {
            }
        }
        return workBuilder.build();
    }

    private static final Map<String, String> constraintCodeMap = new HashMap<>() {
        {
            put("uk_account_phone", "手机号已被注册");
            put("uk_member_phone", "手机号已被注册");
            put("uk_member_id_card", "身份证已被注册");
            put("uk_customer_phone", "手机号已被注册");
            put("uk_store_license_number", "统一社会信用代码已被注册");
            put("uk_score_out_trade_no", "订单编号重复");
        }
    };

    @ResponseStatus(code = HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler({DuplicateKeyException.class, DataIntegrityViolationException.class})
    public Work<Object> missingServletRequestParameterException(Exception e) {
        AtomicReference<String> message = new AtomicReference<>(e.getMessage());
        constraintCodeMap.forEach((k, v) -> {
            if (message.get().contains(k)) {
                message.set(v);
            }
        });
        return Work.fail(message.get());
    }
}
