package work.onss.aop;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Resource
    private AuthorizationInterceptor authorizationInterceptor;
    @Resource
    private RequestMappingInterceptor requestMappingInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authorizationInterceptor).addPathPatterns("/**")
                .excludePathPatterns("/login", "/register");
        registry.addInterceptor(requestMappingInterceptor).addPathPatterns("/**")
                .excludePathPatterns("/login", "/register","/members/*/setPassword");
    }
}
