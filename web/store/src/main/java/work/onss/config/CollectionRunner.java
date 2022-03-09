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
import work.onss.domain.QResource;
import work.onss.domain.Resource;
import work.onss.domain.ResourceCustomerRepository;
import work.onss.domain.ResourceRepository;

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
    private ResourceRepository resourceRepository;
    @Autowired
    private ResourceCustomerRepository resourceCustomerRepository;
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
        List<Resource> resources = new ArrayList<>();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> m : handlerMethods.entrySet()) {
            HandlerMethod value = m.getValue();
            GetMapping getMapping = value.getMethodAnnotation(GetMapping.class);
            if (getMapping != null && !getMapping.name().equals("")) {
                QResource qResource = QResource.resource;
                String path = Arrays.toString(getMapping.value());
                BooleanExpression booleanExpression = qResource.contextPath.eq(contextPath).and(qResource.value.eq(path)).and(qResource.type.eq(RequestMethod.GET.name()));
                Resource resource = resourceRepository.findOne(booleanExpression).orElse(new Resource(path, getMapping.name(), RequestMethod.GET.name(), contextPath, now));
                resource.setUpdateTime(now);
                resources.add(resource);
                continue;
            }
            PostMapping postMapping = value.getMethodAnnotation(PostMapping.class);
            if (postMapping != null && !postMapping.name().equals("")) {
                QResource qResource = QResource.resource;
                String path = Arrays.toString(postMapping.value());
                BooleanExpression booleanExpression = qResource.contextPath.eq(contextPath).and(qResource.value.eq(path)).and(qResource.type.eq(RequestMethod.POST.name()));
                Resource resource = resourceRepository.findOne(booleanExpression).orElse(new Resource(path, postMapping.name(), RequestMethod.POST.name(), contextPath, now));
                resource.setUpdateTime(now);
                resources.add(resource);
                continue;
            }
            PutMapping putMapping = value.getMethodAnnotation(PutMapping.class);
            if (putMapping != null && !putMapping.name().equals("")) {
                QResource qResource = QResource.resource;
                String path = Arrays.toString(putMapping.value());
                BooleanExpression booleanExpression = qResource.contextPath.eq(contextPath).and(qResource.value.eq(path)).and(qResource.type.eq(RequestMethod.PUT.name()));
                Resource resource = resourceRepository.findOne(booleanExpression).orElse(new Resource(path, putMapping.name(), RequestMethod.PUT.name(), contextPath, now));
                resource.setUpdateTime(now);
                resources.add(resource);
                continue;
            }
            DeleteMapping deleteMapping = value.getMethodAnnotation(DeleteMapping.class);
            if (deleteMapping != null && !deleteMapping.name().equals("")) {
                QResource qResource = QResource.resource;
                String path = Arrays.toString(deleteMapping.value());
                BooleanExpression booleanExpression = qResource.contextPath.eq(contextPath).and(qResource.value.eq(path)).and(qResource.type.eq(RequestMethod.DELETE.name()));
                Resource resource = resourceRepository.findOne(booleanExpression).orElse(new Resource(path, deleteMapping.name(), RequestMethod.DELETE.name(), contextPath, now));
                resource.setUpdateTime(now);
                resources.add(resource);
                continue;
            }
            PatchMapping patchMapping = value.getMethodAnnotation(PatchMapping.class);
            if (patchMapping != null && !patchMapping.name().equals("")) {
                QResource qResource = QResource.resource;
                String path = Arrays.toString(patchMapping.value());
                BooleanExpression booleanExpression = qResource.contextPath.eq(contextPath).and(qResource.value.eq(path)).and(qResource.type.eq(RequestMethod.PATCH.name()));
                Resource resource = resourceRepository.findOne(booleanExpression).orElse(new Resource(path, patchMapping.name(), RequestMethod.PATCH.name(), contextPath, now));
                resource.setUpdateTime(now);
                resources.add(resource);
            }
        }
        resourceRepository.saveAllAndFlush(resources);
        QResource qResource = QResource.resource;
        List<Long> resourcesId = jpaQueryFactory.select(qResource.id).from(qResource).where(qResource.updateTime.before(now), qResource.contextPath.eq(contextPath)).fetch();
        if (!resourcesId.isEmpty()) {
            resourceRepository.deleteAllById(resourcesId);
            resourceCustomerRepository.deleteByResourceIdIn(resourcesId);
        }
    }
}
