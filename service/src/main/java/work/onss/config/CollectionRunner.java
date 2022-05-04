package work.onss.config;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import work.onss.domain.Application;
import work.onss.domain.ApplicationRepository;
import work.onss.domain.QApplication;

import javax.servlet.ServletContext;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Log4j2
@Component
@Order(value = 2)
public class CollectionRunner implements CommandLineRunner {

    @Autowired
    private JPAQueryFactory jpaQueryFactory;
    @Autowired
    private ApplicationRepository applicationRepository;
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Transactional
    @Override
    public void run(String... args) {

        ServletContext servletContext = webApplicationContext.getServletContext();
        assert servletContext != null;
        String contextPath = servletContext.getContextPath();

        Timestamp now = Timestamp.from(Instant.now());
        RequestMappingHandlerMapping requestMappingHandlerMapping = webApplicationContext.getBean(RequestMappingHandlerMapping.class);
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = requestMappingHandlerMapping.getHandlerMethods();
        List<Application> applications = new ArrayList<>();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> m : handlerMethods.entrySet()) {
            HandlerMethod value = m.getValue();
            GetMapping getMapping = value.getMethodAnnotation(GetMapping.class);
            if (getMapping != null && !getMapping.name().equals("")) {
                QApplication qApplication = QApplication.application;
                String path = Arrays.toString(getMapping.value());
                BooleanExpression booleanExpression = qApplication.contextPath.eq(contextPath).and(qApplication.value.eq(path)).and(qApplication.type.eq(RequestMethod.GET.name()));
                Application application = applicationRepository.findOne(booleanExpression).orElse(new Application(path, getMapping.name(), RequestMethod.GET.name(), contextPath, now));
                application.setUpdateTime(now);
                applications.add(application);
                continue;
            }
            PostMapping postMapping = value.getMethodAnnotation(PostMapping.class);
            if (postMapping != null && !postMapping.name().equals("")) {
                QApplication qApplication = QApplication.application;
                String path = Arrays.toString(postMapping.value());
                BooleanExpression booleanExpression = qApplication.contextPath.eq(contextPath).and(qApplication.value.eq(path)).and(qApplication.type.eq(RequestMethod.POST.name()));
                Application application = applicationRepository.findOne(booleanExpression).orElse(new Application(path, postMapping.name(), RequestMethod.POST.name(), contextPath, now));
                application.setUpdateTime(now);
                applications.add(application);
                continue;
            }
            PutMapping putMapping = value.getMethodAnnotation(PutMapping.class);
            if (putMapping != null && !putMapping.name().equals("")) {
                QApplication qApplication = QApplication.application;
                String path = Arrays.toString(putMapping.value());
                BooleanExpression booleanExpression = qApplication.contextPath.eq(contextPath).and(qApplication.value.eq(path)).and(qApplication.type.eq(RequestMethod.PUT.name()));
                Application application = applicationRepository.findOne(booleanExpression).orElse(new Application(path, putMapping.name(), RequestMethod.PUT.name(), contextPath, now));
                application.setUpdateTime(now);
                applications.add(application);
                continue;
            }
            DeleteMapping deleteMapping = value.getMethodAnnotation(DeleteMapping.class);
            if (deleteMapping != null && !deleteMapping.name().equals("")) {
                QApplication qApplication = QApplication.application;
                String path = Arrays.toString(deleteMapping.value());
                BooleanExpression booleanExpression = qApplication.contextPath.eq(contextPath).and(qApplication.value.eq(path)).and(qApplication.type.eq(RequestMethod.DELETE.name()));
                Application application = applicationRepository.findOne(booleanExpression).orElse(new Application(path, deleteMapping.name(), RequestMethod.DELETE.name(), contextPath, now));
                application.setUpdateTime(now);
                applications.add(application);
                continue;
            }
            PatchMapping patchMapping = value.getMethodAnnotation(PatchMapping.class);
            if (patchMapping != null && !patchMapping.name().equals("")) {
                QApplication qApplication = QApplication.application;
                String path = Arrays.toString(patchMapping.value());
                BooleanExpression booleanExpression = qApplication.contextPath.eq(contextPath).and(qApplication.value.eq(path)).and(qApplication.type.eq(RequestMethod.PATCH.name()));
                Application application = applicationRepository.findOne(booleanExpression).orElse(new Application(path, patchMapping.name(), RequestMethod.PATCH.name(), contextPath, now));
                application.setUpdateTime(now);
                applications.add(application);
            }
        }
        applicationRepository.saveAllAndFlush(applications);
        QApplication qApplication = QApplication.application;
        List<Long> applicationsId = jpaQueryFactory.select(qApplication.id).from(qApplication).where(qApplication.updateTime.before(now), qApplication.contextPath.eq(contextPath)).fetch();
        if (!applicationsId.isEmpty()) {
            applicationRepository.deleteAllById(applicationsId);
        }
    }
}
