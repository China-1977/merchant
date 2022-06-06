package work.onss.controller;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import work.onss.config.SystemConfig;
import work.onss.domain.QProduct;
import work.onss.service.QuerydslService;
import work.onss.utils.Utils;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Objects;

/**
 * 商品管理
 *
 * @author wangchanghao
 */
@Log4j2
@RestController
public class ProductController {
    @Autowired
    private JPAQueryFactory jpaQueryFactory;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private QuerydslService querydslService;
    @Autowired
    private SystemConfig systemConfig;

    /**
     * @param id  商品ID
     * @param sid 商户ID
     * @return 商品详情
     */
    @GetMapping(value = {"products/{id}"}, name = "商品详情")
    public Product product(@PathVariable Long id, @RequestHeader(name = "sid") Long sid) {
        return productRepository.findByIdAndStoreId(id, sid).orElse(null);
    }

    /**
     * @param sid 商户ID
     * @return 商品列表
     */
    @GetMapping(value = {"products"}, name = "商品列表")
    public Page<Product> products(@RequestHeader(name = "sid") Long sid, @PageableDefault Pageable pageable) {
        return productRepository.findByStoreId(sid, pageable);
    }


    /**
     * @param sid     商户ID
     * @param product 待新增商品
     * @return 新商品详情
     */
    @PostMapping(value = {"products"}, name = "商品创建")
    public Product insert(@RequestHeader(name = "sid") Long sid, @Validated @RequestBody Product product) {
        product.setStoreId(sid);
        productRepository.save(product);
        return product;
    }

    /**
     * @param id      商品ID
     * @param sid     商户ID
     * @param product 待编辑商品
     * @return 商品详情
     */
    @PutMapping(value = {"products/{id}"}, name = "商品编辑")
    public Product update(@PathVariable Long id, @RequestHeader(name = "sid") Long sid, @Validated @RequestBody Product product) {
        Long count = querydslService.setProduct(id, sid, product);
        if (count == 0) {
            throw new RuntimeException("商品不存在");
        }
        return product;
    }

    /**
     * @param id     商品ID
     * @param sid    商户ID
     * @param status 更新商品状态
     */
    @Transactional
    @PutMapping(value = {"products/{id}/updateStatus"}, name = "商品状态设置")
    public void updateStatus(@PathVariable Long id, @RequestHeader(name = "sid") Long sid, @RequestParam(name = "status") Boolean status) {
        QProduct qProduct = QProduct.product;
        jpaQueryFactory.update(qProduct)
                .set(qProduct.status, status)
                .where(qProduct.id.eq(id), qProduct.storeId.eq(sid))
                .execute();
    }

    /**
     * @param sid    商户ID
     * @param ids    商品ID集合
     * @param status 更新商品状态
     */
    @Transactional
    @PutMapping(value = {"products"}, name = "商品状态批量设置")
    public void updateStatus(@RequestHeader(name = "sid") Long sid, @RequestBody Collection<Long> ids, @RequestParam(name = "status") Boolean status) {
        QProduct qProduct = QProduct.product;
        jpaQueryFactory.update(qProduct)
                .set(qProduct.status, status)
                .where(qProduct.id.in(ids), qProduct.storeId.eq(sid))
                .execute();
    }

    /**
     * @param sid 商户ID
     * @param id  商品ID
     */
    @DeleteMapping(value = {"products/{id}"}, name = "商品删除")
    public void delete(@RequestHeader(name = "sid") Long sid, @PathVariable Long id) {
        productRepository.deleteByIdAndStoreId(id, sid);
    }

    /**
     * @param sid 商户ID
     * @param ids 商品ID集合
     */
    @DeleteMapping(value = {"products"}, name = "商品删除批量")
    public void delete(@RequestHeader(name = "sid") Long sid, @RequestBody Collection<Long> ids) {
        productRepository.deleteByIdInAndStoreId(ids, sid);
    }

    /**
     * @param file 文件
     * @param sid  商户ID
     * @return 文件存储路径
     * @throws Exception 文件上传失败异常
     */
    @PostMapping(value = "products/uploadPicture", name = "商品图片")
    public String uploadPicture(@RequestHeader(name = "sid") Long sid, @RequestParam(value = "file") MultipartFile file) throws Exception {
        String subtype = MediaType.valueOf(Objects.requireNonNull(file.getContentType())).getSubtype();
        String fileName = DigestUtils.sha256Hex(file.getBytes()).concat(".").concat(subtype);
        Path path = Utils.uploadFile(file, systemConfig.getFilePath(), "store", String.valueOf(sid), "products", fileName);
        return StringUtils.cleanPath(path.subpath(1, path.getNameCount()).toString());
    }
}
