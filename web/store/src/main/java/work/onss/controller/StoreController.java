package work.onss.controller;

import com.github.binarywang.wxpay.bean.applyment.ApplymentStateQueryResult;
import com.github.binarywang.wxpay.bean.applyment.WxPayApplymentCreateResult;
import com.github.binarywang.wxpay.bean.media.ImageUploadResult;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.Applyment4SubService;
import com.github.binarywang.wxpay.service.MerchantMediaService;
import com.github.binarywang.wxpay.service.WxPayService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import work.onss.config.SystemConfig;
import work.onss.domain.Store;
import work.onss.domain.StoreRepository;
import work.onss.exception.ServiceException;
import work.onss.service.QuerydslService;
import work.onss.utils.Utils;
import work.onss.vo.Merchant;

import java.text.MessageFormat;
import java.util.Objects;


/**
 * 商户管理
 *
 * @author wangchanghao
 */
@Log4j2
@RestController
public class StoreController {
    @Autowired
    private QuerydslService querydslService;
    @Autowired
    private Applyment4SubService applyment4SubService;
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private SystemConfig systemConfig;
    @Autowired
    private WxPayService wxPayService;

    /**
     * @param id  商户ID
     * @param cid 营业员ID
     * @return 商户详情
     */
    @GetMapping(value = {"stores/{id}"}, name = "商户详情")
    public Store detail(@PathVariable Long id, @RequestHeader(name = "cid") Long cid) {
        return querydslService.get(id, cid);
    }

    /**
     * @param id     商户ID
     * @param cid    营业员ID
     * @param status 更新商户状态
     */
    @PutMapping(value = {"stores/{id}/updateStatus"}, name = "商户状态")
    public void updateStatus(@PathVariable(name = "id") Long id, @RequestHeader(name = "cid") Long cid, @RequestHeader(name = "status") Boolean status) throws ServiceException, WxPayException {
        Store store = querydslService.get(id, cid);
        if (store == null) {
            throw new ServiceException("FAIL", "商户不存在，请联系客服", MessageFormat.format("商户ID:{0},状态:{1}", id, status));
        }
        if (null == store.getApplymentId()) {
            throw new ServiceException("MERCHANT_NOT_REGISTER", "请完善商户资质");
        }
        ApplymentStateQueryResult applymentStateQueryResult = applyment4SubService.queryApplyStatusByApplymentId(store.getApplymentId());
        switch (applymentStateQueryResult.getApplymentState()) {
            case APPLYMENT_STATE_FINISHED -> {
                Long count = querydslService.setStore(id, status, applymentStateQueryResult.getBusinessCode(), applymentStateQueryResult.getSubMchid());
            }
            case APPLYMENT_STATE_EDITTING, APPLYMENT_STATE_CANCELED -> throw new ServiceException("MERCHANT_NOT_REGISTER", "请完善商户资质");
            case APPLYMENT_STATE_REJECTED -> {
                String message = String.join("\n", applymentStateQueryResult.getAuditDetail().stream().map(ApplymentStateQueryResult.AuditDetail::getRejectReason).toList());
                throw new ServiceException("MERCHANT_NOT_REGISTER", message);
            }
            case APPLYMENT_STATE_AUDITING -> throw new RuntimeException("正在审核中,请耐心等待");
            case APPLYMENT_STATE_TO_BE_CONFIRMED -> throw new RuntimeException("请及时验证账户");
            case APPLYMENT_STATE_TO_BE_SIGNED -> throw new RuntimeException("请及时签约特约商户");
            case APPLYMENT_STATE_SIGNING -> throw new RuntimeException("开通权限中,请耐心等待");
            default -> throw new ServiceException("FAIL", "系统异常,请立刻联系客服", store);
        }
    }

    /**
     * @param id    商户ID
     * @param cid   营业员ID
     * @param store 更新商户详情
     */
    @PutMapping(value = {"stores/{id}"}, name = "商户更新")
    public void update(@PathVariable(name = "id") Long id, @RequestHeader(name = "cid") Long cid, @Validated @RequestBody Store store) {
        Store oldStore = querydslService.get(id, cid);
        if (oldStore == null) {
            throw new ServiceException("FAIL", "商户不存在，请联系客服", store);
        }
        Long count = querydslService.setStore(id, store);
    }

    /**
     * @param id       商户ID
     * @param cid      营业员ID
     * @param merchant 商户资质
     * @return 商户详情
     */
    @PostMapping(value = {"stores/{id}/setMerchant"}, name = "商户资质")
    public Store setMerchant(@RequestHeader(name = "cid") Long cid, @PathVariable Long id, @Validated @RequestBody Merchant merchant) throws ServiceException, WxPayException {
        Store store = storeRepository.findById(id).orElseThrow(() -> new ServiceException("FAIL", "商户不存在，请联系客服", MessageFormat.format("商户ID:{0}", id)));
        if (cid.equals(store.getCustomerId())) {
            merchant.setBusinessCode(store.getBusinessCode());
            merchant.setLicenseNumber(store.getLicenseNumber());
            WxPayApplymentCreateResult wxPayApplymentCreateResult = applyment4SubService.createApply(merchant.wxPayApplyment4SubCreateRequest());
            Long count = querydslService.setStore(id, wxPayApplymentCreateResult.getApplymentId());
        } else {
            throw new RuntimeException("权限不足,请联系超级管理");
        }
        return store;
    }

    /**
     * @param file 文件
     * @param sid  商户ID
     * @return 文件存储路径
     * @throws Exception 文件上传失败异常
     */
    @PostMapping(value = "stores/uploadPicture", name = "商户图片")
    public String uploadPicture(@RequestHeader(name = "sid") Long sid, @RequestParam(value = "file") MultipartFile file) throws Exception {

        String subtype = MediaType.valueOf(Objects.requireNonNull(file.getContentType())).getSubtype();
        String fileName = DigestUtils.sha256Hex(file.getBytes()).concat(".").concat(subtype);
        String filePath = Utils.getFilePath(systemConfig.getFilePath(), fileName, String.valueOf(sid));
        Utils.uploadFile(file, filePath);

        return filePath;
    }

    /**
     * @param file 文件
     * @param sid  商户ID
     * @return 文件存储路径
     * @throws Exception 文件上传失败异常
     */
    @PostMapping(value = "stores/imageUploadV3", name = "商户图片V3")
    public String imageUploadV3(@RequestHeader(name = "sid") Long sid, @RequestParam(value = "fileName") String fileName, @RequestParam(value = "file") MultipartFile file) throws Exception {
        MerchantMediaService merchantMediaService = wxPayService.getMerchantMediaService();
        ImageUploadResult imageUploadResult = merchantMediaService.imageUploadV3(file.getInputStream(), file.getOriginalFilename());

//        String subtype = MediaType.valueOf(Objects.requireNonNull(file.getContentType())).getSubtype();
        String filePath = Utils.getFilePath(systemConfig.getFilePath(), fileName.concat(".").concat("png"), String.valueOf(sid));
        Utils.uploadFile(file, filePath);

        return imageUploadResult.getMediaId();
    }
}

