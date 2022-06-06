package work.onss.vo;

import com.github.binarywang.wxpay.bean.applyment.WxPayApplyment4SubCreateRequest;
import com.github.binarywang.wxpay.bean.applyment.enums.BankAccountTypeEnum;
import com.github.binarywang.wxpay.bean.applyment.enums.IdTypeEnum;
import com.github.binarywang.wxpay.bean.applyment.enums.SalesScenesTypeEnum;
import com.github.binarywang.wxpay.bean.applyment.enums.SubjectTypeEnum;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.github.binarywang.wxpay.bean.applyment.enums.IdTypeEnum.IDENTIFICATION_TYPE_IDCARD;

@Log4j2
@Data
@NoArgsConstructor
@ToString
public class Merchant implements Serializable {

    private String businessCode;
    /**
     * 商户审核驳回原因
     */
    private String rejected;

    @NotBlank(message = "服务商小程序APPID不能为空")
    private String miniProgramAppid; // 服务商小程序APPID

    //超级管理员
    @NotBlank(message = "请填写管理员姓名")
    private String contactName;//姓名
    @NotBlank(message = "请填写管理员18位身份证号")
    @Pattern(regexp = "^[1-9]\\d{5}(18|19|([23]\\d))\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$", message = "管理员身份证格式错误")
    private String contactIdNumber;//身份证
    @NotBlank(message = "请填写管理员手机号")
    @Pattern(regexp = "^[1][34578][0-9]{9}$", message = "管理员手机号格式错误")
    private String mobilePhone;//手机号
    @NotBlank(message = "请填写常用邮箱")
    @Email(message = "常用邮箱格式错误")
    private String contactEmail;//常用邮箱

    //营业执照
    @NotNull(message = "请选择主体类型")
    private SubjectTypeEnum subjectType;//主体类型
    @NotBlank(message = "请上传营业执照副本")
    private String licenseCopy;//五证合一
    @NotBlank(message = "请填写营业执照编号")
    @Pattern(regexp = "[^_IOZSVa-z\\W]{2}\\d{6}[^_IOZSVa-z\\W]{10}", message = "营业执照编号格式错误")
    private String licenseNumber;//社会信用代码
    @NotBlank(message = "请填写主体全称")
    private String merchantName;//商户全称
    @NotBlank(message = "请填写经营者姓名")
    private String legalPerson;//经营者姓名

    //法人信息
    private IdTypeEnum idDocType = IDENTIFICATION_TYPE_IDCARD;//法人证件类型
    @NotBlank(message = "请上传法人身份证正面")
    private String idCardCopy;//身份证正面
    @NotBlank(message = "请填写管理员18位身份证号")
    @Pattern(regexp = "^[1-9]\\d{5}(18|19|([23]\\d))\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$", message = "管理员身份证格式错误")
    private String idCardNumber;//身份证号
    @NotBlank(message = "请填写管理员姓名")
    private String idCardName;//姓名
    @NotBlank(message = "请上传法人身份证反面")
    private String idCardNational;//身份证反面
    @NotBlank(message = "请填写法人身份证注册日期")
    private String cardPeriodBegin;//开始时间
    @NotBlank(message = "请填写法人身份证过期日期")
    private String cardPeriodEnd;//结束时间
    @NotNull(message = "法人是否是最终受益人")
    private Boolean owner;//是否是最终受益人

    private IdTypeEnum idType = IDENTIFICATION_TYPE_IDCARD;//证件类型
    private String idCardA;//身份证人像面照片
    private String idCardB;//身份证国徽面照片
    private String idDocCopy;//证件照片
    private String beneficiary;//受益人姓名
    private String idNumber;//证件号码
    private String idPeriodBegin;//证件有效期开始时间
    private String idPeriodEnd;//证件有效期结束时间

    //经营资料
    @NotBlank(message = "请填写商户简称")
    private String merchantShortname;//商户简称
    @NotBlank(message = "请填写客服电话")
    private String servicePhone;//客服电话

    //结算规则
    private String settlementId;//入驻结算规则ID
    @NotNull(message = "请选择所属行业")
    private String qualificationType;//所属行业
    @Size(max = 5, message = "请上传1~5张特殊资质图片")
    private List<String> qualifications = new ArrayList<>(0);//特殊资质图片

    //结算银行账户
    @NotNull(message = "请填写账户类型")
    private BankAccountTypeEnum bankAccountType;//账户类型 个人 公户
    @NotBlank(message = "请填写开户名称")
    private String accountName;//开户名称
    @NotBlank(message = "请选择开户银行")
    private String accountBank;//开户银行
    @NotBlank(message = "请填写银行账号")
    private String accountNumber;//银行账号
    private String bankName;//其他银行，需填写银行全称
    @Valid
    private WXAddress bankAddress;//开户地址

    public WxPayApplyment4SubCreateRequest wxPayApplyment4SubCreateRequest() {
        /* 超级管理员 */
        WxPayApplyment4SubCreateRequest.ContactInfo contactInfo = WxPayApplyment4SubCreateRequest.ContactInfo.builder()
                .contactEmail(this.getContactEmail())
                .contactIdNumber(this.getContactIdNumber())
                .contactName(this.getContactName())
                .mobilePhone(this.getMobilePhone())
                .build();
        /* 营业执照 */
        WxPayApplyment4SubCreateRequest.SubjectInfo.BusinessLicenseInfo businessLicenseInfo = WxPayApplyment4SubCreateRequest.SubjectInfo.BusinessLicenseInfo.builder()
                .legalPerson(this.getLegalPerson())
                .licenseCopy(this.getLicenseCopy())
                .licenseNumber(this.getLicenseNumber())
                .merchantName(this.getMerchantName())
                .build();
        /* 身份证信息 */
        WxPayApplyment4SubCreateRequest.SubjectInfo.IdentityInfo.IdCardInfo idCardInfo = WxPayApplyment4SubCreateRequest.SubjectInfo.IdentityInfo.IdCardInfo.builder()
                .idCardCopy(this.getIdCardCopy())
                .idCardNumber(this.getIdCardNumber())
                .idCardName(this.getIdCardName())
                .idCardNational(this.getIdCardNational())
                .cardPeriodBegin(this.getCardPeriodBegin())
                .cardPeriodEnd(this.getCardPeriodEnd())
                .build();
        /* 经营者/法人身份证件 */
        WxPayApplyment4SubCreateRequest.SubjectInfo.IdentityInfo identityInfo = WxPayApplyment4SubCreateRequest.SubjectInfo.IdentityInfo.builder()
                .idCardInfo(idCardInfo)
                .idDocType(IDENTIFICATION_TYPE_IDCARD)
                .owner(true)
                .build();
        /* 1.主体资料 */
        WxPayApplyment4SubCreateRequest.SubjectInfo subjectInfo = WxPayApplyment4SubCreateRequest.SubjectInfo.builder()
                .businessLicenseInfo(businessLicenseInfo)
                .identityInfo(identityInfo)
                .subjectType(this.getSubjectType())
                .build();
        /* 小程序场景 */
        WxPayApplyment4SubCreateRequest.BusinessInfo.SalesInfo.MiniProgramInfo miniProgramInfo = WxPayApplyment4SubCreateRequest.BusinessInfo.SalesInfo.MiniProgramInfo.builder()
                .miniProgramAppid(this.getMiniProgramAppid())
                .build();
        /* 经营场景 */
        WxPayApplyment4SubCreateRequest.BusinessInfo.SalesInfo salesInfo = WxPayApplyment4SubCreateRequest.BusinessInfo.SalesInfo.builder()
                .salesScenesType(Collections.singletonList(SalesScenesTypeEnum.SALES_SCENES_MINI_PROGRAM))
                .miniProgramInfo(miniProgramInfo)
                .build();
        /* 2.经营资料 */
        WxPayApplyment4SubCreateRequest.BusinessInfo businessInfo = WxPayApplyment4SubCreateRequest.BusinessInfo.builder()
                .merchantShortname(this.getMerchantShortname())
                .servicePhone(this.getServicePhone())
                .salesInfo(salesInfo)
                .build();
        /* 结算规则 */
        WxPayApplyment4SubCreateRequest.SettlementInfo settlementInfo = WxPayApplyment4SubCreateRequest.SettlementInfo.builder()
                .settlementId(this.getSettlementId())
                .qualificationType(this.getQualificationType())
                .qualifications(this.getQualifications())
                .build();
        String[] code = this.getBankAddress().getCode();
        /* 结算银行账户 */
        WxPayApplyment4SubCreateRequest.BankAccountInfo bankAccountInfo = WxPayApplyment4SubCreateRequest.BankAccountInfo.builder()
                .accountBank(this.getAccountBank())
                .accountName(this.getAccountName())
                .accountNumber(this.getAccountNumber())
                .bankAccountType(this.getBankAccountType())
                .bankAddressCode(Arrays.stream(this.getBankAddress().getCode()).skip(code.length - 1).findFirst().orElseThrow(() -> new RuntimeException("银行地址编号错误")))
                .bankName(this.getBankName())
                .build();
        return WxPayApplyment4SubCreateRequest.builder()
                .contactInfo(contactInfo)
                .subjectInfo(subjectInfo)
                .businessInfo(businessInfo)
                .settlementInfo(settlementInfo)
                .bankAccountInfo(bankAccountInfo)
                .businessCode(this.getBusinessCode())
                .build();
    }

    @NoArgsConstructor
    @Data
    public static class Refund {
        private String refundId;
        private String outRefundNo;
        private String transactionId;
        private String outTradeNo;
        private String channel;
        private String userReceivedAccount;
        private String successTime;
        private String createTime;
        private String status;
        private String fundsAccount;
        private Amount amount;
        private List<PromotionDetail> promotionDetail;

        @NoArgsConstructor
        @Data
        public static class Amount {
            private Integer total;
            private Integer refund;
            private List<From> from;
            private Integer payerTotal;
            private Integer payerRefund;
            private Integer settlementRefund;
            private Integer settlementTotal;
            private Integer discountRefund;
            private String currency;

            @NoArgsConstructor
            @Data
            public static class From {
                private String account;
                private Integer amount;
            }
        }

        @NoArgsConstructor
        @Data
        public static class PromotionDetail {
            private String promotionId;
            private String scope;
            private String type;
            private Integer amount;
            private Integer refundAmount;
            private GoodsDetail goodsDetail;

            @NoArgsConstructor
            @Data
            public static class GoodsDetail {
                private String merchantGoodsId;
                private String wechatpayGoodsId;
                private String goodsName;
                private Integer unitPrice;
                private Integer refundAmount;
                private Integer refundQuantity;
            }
        }
    }
}
