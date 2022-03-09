package work.onss.aop;

import cn.binarywang.wx.miniapp.json.WxMaGsonBuilder;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.gson.Gson;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import work.onss.config.SystemConfig;
import work.onss.domain.Info;
import work.onss.exception.ServiceException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.MessageFormat;

@Log4j2
@Component
public class AuthorizationInterceptor implements HandlerInterceptor {
    @Autowired
    private SystemConfig systemConfig;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws ServiceException {
        if (handler instanceof HandlerMethod) {
            String authorization = request.getHeader("authorization");
            if (StringUtils.hasLength(authorization)) {
                Algorithm algorithm = Algorithm.HMAC256(systemConfig.getSecret());
                JWTVerifier jwtVerifier = JWT.require(algorithm).build();
                jwtVerifier.verify(authorization);
                DecodedJWT decode = JWT.decode(authorization);
                String subject = decode.getSubject();
                String aid = request.getHeader("aid");
                Gson gson = WxMaGsonBuilder.create();
                Info info = gson.fromJson(subject, Info.class);
                if (StringUtils.hasLength(aid) && !aid.equals(String.valueOf(info.getAid()))) {
                    throw new ServiceException("SESSION_EXPIRE", "请重新登录", MessageFormat.format("恶意攻击,info:{0},用户ID:{1}", subject, aid));
                }
            }
        }
        return true;
    }
}
