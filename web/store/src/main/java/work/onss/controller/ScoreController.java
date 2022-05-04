package work.onss.controller;


import com.github.binarywang.wxpay.exception.WxPayException;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import work.onss.domain.*;
import work.onss.exception.ServiceException;
import work.onss.service.QuerydslService;
import work.onss.service.WxMa;
import work.onss.service.WxPay;
import work.onss.utils.Utils;
import work.onss.vo.WXRefundResult;

import java.sql.Timestamp;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 订单管理
 *
 * @author wangchanghao
 */
@Log4j2
@RestController
public class ScoreController {

    @Value(value = "${wechat.mp.appId}")
    String spappId;
    @Autowired
    private JPAQueryFactory jpaQueryFactory;
    @Autowired
    private ScoreRepository scoreRepository;
    @Autowired
    private QuerydslService querydslService;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private WxMa wxMa;
    @Autowired
    private WxPay wxPay;

    /**
     * @param id  订单ID
     * @param sid 商户ID
     * @return 订单详情
     */
    @GetMapping(value = {"scores/{id}"}, name = "订单详情")
    public Score score(@PathVariable Long id, @RequestHeader(name = "sid") Long sid) {
        return scoreRepository.findByIdAndStoreId(id, sid).orElse(null);
    }

    /**
     * @param sid    商户ID
     * @param status 订单状态集合
     * @return 订单列表
     */
    @GetMapping(value = {"scores"}, name = "订单列表")
    public List<Score> scores(@RequestHeader(name = "sid") Long sid,
                              @RequestParam(name = "status") List<Score.Status> status,
                              @RequestParam(name = "year") Integer year,
                              @RequestParam(name = "keyword", required = false) String keyword,
                              @PageableDefault Pageable pageable) {
        QScore qScore = QScore.score;

        BooleanExpression booleanExpression1 = qScore.storeId.eq(sid).and(qScore.status.in(status)).and(qScore.insertTime.between(
                Timestamp.valueOf(LocalDateTime.MIN.withYear(year)),
                Timestamp.valueOf(LocalDateTime.MAX.withYear(year))));
        BooleanExpression booleanExpression2 = null;
        if (StringUtils.hasLength(keyword)) {
            booleanExpression2 = qScore.username.eq(keyword).or(qScore.phone.eq(keyword)).or(qScore.outTradeNo.eq(keyword));
        }

        return jpaQueryFactory.select(Projections.fields(Score.class,
                        qScore.id,
                        qScore.username,
                        qScore.phone,
                        qScore.addressDetail,
                        qScore.insertTime,
                        qScore.status,
                        qScore.total,
                        qScore.products
                )).from(qScore)
                .where(ExpressionUtils.and(booleanExpression1, booleanExpression2))
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .orderBy(qScore.id.desc(), qScore.updateTime.desc())
                .fetch();
    }

    /**
     * @param id  订单ID
     * @param sid 商户ID
     */
    @PostMapping(value = {"scores/{id}-{outTradeNo}/refund"}, name = "确定退货")
    public Score refund(@PathVariable Long id, @PathVariable String outTradeNo, @RequestHeader(name = "sid") Long sid) throws WxPayException {
        Score oldScore = scoreRepository.findByIdAndStoreId(id, sid).orElseThrow(() -> new ServiceException("FAIL", "订单不存在，请联系客服", MessageFormat.format("订单ID:{0},状态:{1}", id, Score.Status.REFUND_SUCCESS)));
        if (!oldScore.getOutTradeNo().equals(outTradeNo)) {
            throw new RuntimeException("请确认订单编号是否正确");
        }
        if (oldScore.getStatus().equals(Score.Status.WAIT_PACKAGE) || oldScore.getStatus().equals(Score.Status.WAIT_DELIVER) || oldScore.getStatus().equals(Score.Status.WAIT_SIGN) || oldScore.getStatus().equals(Score.Status.FINISH)) {
            WXRefundResult wxRefundResult = wxPay.refund(oldScore);
            WXRefundResult.Status status = wxRefundResult.getStatus();
            if (status.equals(WXRefundResult.Status.SUCCESS)) {
                querydslService.setScore(id, oldScore.getStatus(), Score.Status.REFUND_SUCCESS);
                oldScore.setStatus(Score.Status.REFUND_SUCCESS);
            } else if (status.equals(WXRefundResult.Status.PROCESSING)) {
                querydslService.setScore(id, oldScore.getStatus(), Score.Status.REFUND_PROCESSING);
                oldScore.setStatus(Score.Status.REFUND_PROCESSING);
            } else if (status.equals(WXRefundResult.Status.CLOSED)) {
                querydslService.setScore(id, oldScore.getStatus(), Score.Status.REFUND_CLOSED);
                oldScore.setStatus(Score.Status.REFUND_CLOSED);
            } else {
                querydslService.setScore(id, oldScore.getStatus(), Score.Status.REFUND_ABNORMAL);
                oldScore.setStatus(Score.Status.REFUND_ABNORMAL);
            }
        } else {
            String message = oldScore.getStatus().getMessage();
            throw new RuntimeException("当前订单状态:".concat(message));
        }
        return oldScore;
    }

    /**
     * @param id  订单ID
     * @param sid 商户ID
     */
    @PostMapping(value = {"scores/{id}/waitPackage"}, name = "确定配货")
    public Score waitPackage(@PathVariable Long id, @RequestHeader(name = "sid") Long sid) {
        Score oldScore = scoreRepository.findByIdAndStoreId(id, sid).orElseThrow(() -> new ServiceException("FAIL", "订单不存在，请联系客服", MessageFormat.format("订单ID:{0},状态:{1}", id, Score.Status.WAIT_PACKAGE)));
        if (!oldScore.getStatus().equals(Score.Status.WAIT_PACKAGE)) {
            String message = oldScore.getStatus().getMessage();
            throw new RuntimeException("当前订单状态:".concat(message));
        }
        Map<String, String> data;
        String keyword3 = String.join("\n", oldScore.getProducts().stream().map(Score.Product::getName).toList());
        String remark = String.join("\n", oldScore.getUsername(), oldScore.getPhone(), oldScore.getAddressDetail());

        if (oldScore.getWay().equals(Score.Way.MD)) {
            // 门店自取
            querydslService.setScore(id, oldScore.getStatus(), Score.Status.WAIT_SIGN);
            oldScore.setStatus(Score.Status.WAIT_SIGN);
            data = Utils.OPENTM207940503("您的订单已配货完成,请尽快到门店取货,谢谢配合", oldScore.getOutTradeNo(), oldScore.getStatus().getMessage(), keyword3, remark);
        } else {
            // 配送到家 配送到驿站
            querydslService.setScore(id, oldScore.getStatus(), Score.Status.WAIT_DELIVER);
            oldScore.setStatus(Score.Status.WAIT_DELIVER);
            data = Utils.OPENTM207940503("您的订单准备配送中,请保持电话畅通,谢谢配合", oldScore.getOutTradeNo(), oldScore.getStatus().getMessage(), keyword3, remark);
        }
        Optional<Account> accountOptional = accountRepository.findById(oldScore.getAccountId());
        accountOptional.ifPresent(account -> wxMa.sendUniformMsg(spappId, account.getSubAppid(), account.getSubOpenid(), "boyfaz_o7NEzgEsOPOZMSvcqvrfWgHbA0k8NuEMV-b8", data, "pages/score/detail?id=".concat(oldScore.getId().toString())));
        return oldScore;
    }

    /**
     * @param id  订单ID
     * @param sid 商户ID
     */
    @PostMapping(value = {"scores/{id}/waitDeliver"}, name = "确定配送")
    public Score waitDeliver(@PathVariable Long id, @RequestHeader(name = "sid") Long sid) {
        Score oldScore = scoreRepository.findByIdAndStoreId(id, sid).orElseThrow(() -> new ServiceException("FAIL", "订单不存在，请联系客服", MessageFormat.format("订单ID:{0},状态:{1}", id, Score.Status.WAIT_DELIVER)));
        if (!oldScore.getStatus().equals(Score.Status.WAIT_DELIVER)) {
            String message = oldScore.getStatus().getMessage();
            throw new RuntimeException("当前订单状态:".concat(message));
        }
        querydslService.setScore(id, oldScore.getStatus(), Score.Status.WAIT_SIGN);
        oldScore.setStatus(Score.Status.WAIT_SIGN);
        String keyword3 = String.join("\n", oldScore.getProducts().stream().map(Score.Product::getName).toList());
        String remark = String.join("\n", oldScore.getUsername(), oldScore.getPhone(), oldScore.getAddressDetail());
        Map<String, String> data = Utils.OPENTM207940503("您的订单已配送,谢谢您的支持", oldScore.getOutTradeNo(), oldScore.getStatus().getMessage(), keyword3, remark);
        Optional<Account> accountOptional = accountRepository.findById(oldScore.getAccountId());
        accountOptional.ifPresent(account -> wxMa.sendUniformMsg(spappId, account.getSubAppid(), account.getSubOpenid(), "boyfaz_o7NEzgEsOPOZMSvcqvrfWgHbA0k8NuEMV-b8", data, "pages/score/detail?id=".concat(oldScore.getId().toString())));
        return oldScore;
    }

}
