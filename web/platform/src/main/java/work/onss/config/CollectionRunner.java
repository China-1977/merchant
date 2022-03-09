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
import work.onss.domain.Mapping;
import work.onss.domain.MappingMemberRepository;
import work.onss.domain.MappingRepository;
import work.onss.domain.QMapping;

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
    private MappingRepository mappingRepository;
    @Autowired
    private MappingMemberRepository mappingMemberRepository;
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
        List<Mapping> mappings = new ArrayList<>();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> m : handlerMethods.entrySet()) {
            HandlerMethod value = m.getValue();
            GetMapping getMapping = value.getMethodAnnotation(GetMapping.class);
            if (getMapping != null && !getMapping.name().equals("")) {
                QMapping qMapping = QMapping.mapping;
                String path = Arrays.toString(getMapping.value());
                BooleanExpression booleanExpression = qMapping.contextPath.eq(contextPath).and(qMapping.value.eq(path)).and(qMapping.type.eq(RequestMethod.GET.name()));
                Mapping mapping = mappingRepository.findOne(booleanExpression).orElse(new Mapping(path, getMapping.name(), RequestMethod.GET.name(), contextPath, now));
                mapping.setUpdateTime(now);
                mappings.add(mapping);
                continue;
            }
            PostMapping postMapping = value.getMethodAnnotation(PostMapping.class);
            if (postMapping != null && !postMapping.name().equals("")) {
                QMapping qMapping = QMapping.mapping;
                String path = Arrays.toString(postMapping.value());
                BooleanExpression booleanExpression = qMapping.contextPath.eq(contextPath).and(qMapping.value.eq(path)).and(qMapping.type.eq(RequestMethod.POST.name()));
                Mapping mapping = mappingRepository.findOne(booleanExpression).orElse(new Mapping(path, postMapping.name(), RequestMethod.POST.name(), contextPath, now));
                mapping.setUpdateTime(now);
                mappings.add(mapping);
                continue;
            }
            PutMapping putMapping = value.getMethodAnnotation(PutMapping.class);
            if (putMapping != null && !putMapping.name().equals("")) {
                QMapping qMapping = QMapping.mapping;
                String path = Arrays.toString(putMapping.value());
                BooleanExpression booleanExpression = qMapping.contextPath.eq(contextPath).and(qMapping.value.eq(path)).and(qMapping.type.eq(RequestMethod.PUT.name()));
                Mapping mapping = mappingRepository.findOne(booleanExpression).orElse(new Mapping(path, putMapping.name(), RequestMethod.PUT.name(), contextPath, now));
                mapping.setUpdateTime(now);
                mappings.add(mapping);
                continue;
            }
            DeleteMapping deleteMapping = value.getMethodAnnotation(DeleteMapping.class);
            if (deleteMapping != null && !deleteMapping.name().equals("")) {
                QMapping qMapping = QMapping.mapping;
                String path = Arrays.toString(deleteMapping.value());
                BooleanExpression booleanExpression = qMapping.contextPath.eq(contextPath).and(qMapping.value.eq(path)).and(qMapping.type.eq(RequestMethod.DELETE.name()));
                Mapping mapping = mappingRepository.findOne(booleanExpression).orElse(new Mapping(path, deleteMapping.name(), RequestMethod.DELETE.name(), contextPath, now));
                mapping.setUpdateTime(now);
                mappings.add(mapping);
                continue;
            }
            PatchMapping patchMapping = value.getMethodAnnotation(PatchMapping.class);
            if (patchMapping != null && !patchMapping.name().equals("")) {
                QMapping qMapping = QMapping.mapping;
                String path = Arrays.toString(patchMapping.value());
                BooleanExpression booleanExpression = qMapping.contextPath.eq(contextPath).and(qMapping.value.eq(path)).and(qMapping.type.eq(RequestMethod.PATCH.name()));
                Mapping mapping = mappingRepository.findOne(booleanExpression).orElse(new Mapping(path, patchMapping.name(), RequestMethod.PATCH.name(), contextPath, now));
                mapping.setUpdateTime(now);
                mappings.add(mapping);
            }
        }
        mappingRepository.saveAll(mappings);
        QMapping qMapping = QMapping.mapping;
        List<Long> mappingsId = jpaQueryFactory.select(qMapping.id).from(qMapping).where(qMapping.updateTime.before(now), qMapping.contextPath.eq(contextPath)).fetch();
        if (!mappingsId.isEmpty()) {
            mappingRepository.deleteAllById(mappingsId);
            mappingMemberRepository.deleteByMappingIdIn(mappingsId);
        }
    }
}
