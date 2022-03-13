package work.onss.aop;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import work.onss.domain.QApplication;
import work.onss.domain.QApplicationMember;
import work.onss.exception.ServiceException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Arrays;

@Log4j2
@Component
@Order(value = 2)
public class RequestMappingInterceptor implements AsyncHandlerInterceptor {

    @Autowired
    private JPAQueryFactory jpaueryFactory;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws ServiceException {
        if (handler instanceof HandlerMethod) {
            Method method = ((HandlerMethod) handler).getMethod();
            Long mid = Long.valueOf(request.getHeader("mid"));
            if (StringUtils.hasLength(String.valueOf(mid)) && mid != 1L) {
                String value = null;
                String type = null;
                String name = "";
                GetMapping getMapping = method.getDeclaredAnnotation(GetMapping.class);
                if (getMapping != null) {
                    value = Arrays.toString(getMapping.value());
                    type = RequestMethod.GET.name();
                    name = getMapping.name();
                }
                PostMapping postMapping = method.getDeclaredAnnotation(PostMapping.class);
                if (value == null && postMapping != null) {
                    value = Arrays.toString(postMapping.value());
                    type = RequestMethod.POST.name();
                    name = postMapping.name();
                }
                PutMapping putMapping = method.getDeclaredAnnotation(PutMapping.class);
                if (value == null && putMapping != null) {
                    value = Arrays.toString(putMapping.value());
                    type = RequestMethod.PUT.name();
                    name = putMapping.name();
                }
                DeleteMapping deleteMapping = method.getDeclaredAnnotation(DeleteMapping.class);
                if (value == null && deleteMapping != null) {
                    value = Arrays.toString(deleteMapping.value());
                    type = RequestMethod.DELETE.name();
                    name = deleteMapping.name();
                }
                PatchMapping patchMapping = method.getDeclaredAnnotation(PatchMapping.class);
                if (value == null && patchMapping != null) {
                    value = Arrays.toString(patchMapping.value());
                    type = RequestMethod.PATCH.name();
                    name = patchMapping.name();
                }
                String contextPath = request.getContextPath();
                QApplication qApplication = QApplication.application;
                QApplicationMember qApplicationMember = QApplicationMember.applicationMember;
                Long id = jpaueryFactory.select(qApplication.id).from(qApplication)
                        .innerJoin(qApplicationMember).on(qApplicationMember.applicationId.eq(qApplication.id))
                        .where(
                                qApplicationMember.memberId.eq(mid),
                                qApplication.value.eq(value),
                                qApplication.type.eq(type),
                                qApplication.contextPath.eq(contextPath)
                        ).fetchOne();
                if (id == null) {
                    throw new ServiceException("FAIL", name.concat("权限不足"));
                }
            }
        }
        return true;
    }
}
