package work.onss.aop;

import cn.binarywang.wx.miniapp.json.WxMaGsonBuilder;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.gson.Gson;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import work.onss.config.SystemConfig;
import work.onss.domain.Info;
import work.onss.exception.ServiceException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.MessageFormat;

@Log4j2
@Component
@Order(value = 2)
public class AuthorizationInterceptor implements AsyncHandlerInterceptor {
    @Autowired
    private SystemConfig systemConfig;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws ServiceException {
        if (handler instanceof HandlerMethod) {
            String authorization = request.getHeader("authorization");
            if (StringUtils.hasLength(authorization)) {
                Algorithm algorithm = Algorithm.HMAC256(systemConfig.getSecret());
                JWTVerifier jwtVerifier = JWT.require(algorithm).build();
                jwtVerifier.verify(authorization);
                DecodedJWT decode = JWT.decode(authorization);
                String subject = decode.getSubject();
                Gson gson = WxMaGsonBuilder.create();
                Info info = gson.fromJson(subject, Info.class);
                String cid = request.getHeader("cid");
                String sid = request.getHeader("sid");
                if (StringUtils.hasLength(sid) && !sid.equals(String.valueOf(info.getSid()))) {
                    throw new ServiceException("SESSION_EXPIRE", "请重新登录", MessageFormat.format("恶意攻击,info:{0},账号ID:{1},商户ID:{2}", subject, cid, sid));
                }
            }
        }
        return true;
    }
}
