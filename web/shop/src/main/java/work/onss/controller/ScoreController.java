package work.onss.controller;


import com.github.binarywang.wxpay.bean.order.WxPayMpOrderResult;
import com.github.binarywang.wxpay.constant.WxPayConstants;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import work.onss.domain.*;
import work.onss.exception.ServiceException;
import work.onss.service.QuerydslService;
import work.onss.service.ScoreService;
import work.onss.service.WxPay;
import work.onss.vo.WXNotify;
import work.onss.vo.WXRefundResult;
import work.onss.vo.Work;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Log4j2
@RestController
public class ScoreController {

    @Value(value = "${wechat.mp.appId}")
    String spappId;
    @Autowired
    private JPAQueryFactory jpaQueryFactory;
    @Autowired
    private QuerydslService querydslService;
    @Autowired
    private ScoreRepository scoreRepository;
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ScoreService scoreService;
    @Autowired
    private WxPay wxPay;

    /**
     * @param id  主键
     * @param aid 用户ID
     * @return 订单信息
     */
    @GetMapping(value = {"scores/{id}"})
    public Score score(@PathVariable Long id, @RequestHeader(name = "aid") Long aid) throws ServiceException {
        return scoreRepository.findByIdAndAccountId(id, aid).orElseThrow(() -> new ServiceException("FAIL", "该订单不存,请联系平台服务商", MessageFormat.format("订单ID:{0},用户ID:{1}", id, aid)));
    }

    /**
     * @param aid      用户ID
     * @param year     订单创建年份
     * @param pageable 默认创建时间排序并分页
     * @return 订单分页
     */
    @GetMapping(value = {"scores"})
    public List<Score> all(@RequestHeader(name = "aid") Long aid,
                           @RequestParam(name = "year") Integer year,
                           @PageableDefault Pageable pageable) {
        QScore qScore = QScore.score;
        return jpaQueryFactory.select(qScore).from(qScore)
                .where(qScore.insertTime.between(
                                Timestamp.valueOf(LocalDateTime.MIN.withYear(year)),
                                Timestamp.valueOf(LocalDateTime.MAX.withYear(year))),
                        qScore.accountId.eq(aid))
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .orderBy(qScore.id.desc(), qScore.updateTime.desc())
                .fetch();
    }

    /**
     * @param aid          用户ID
     * @param confirmScore 确认订单信息
     * @return 订单信息
     */
    @PostMapping(value = {"scores"})
    public Map<String, Object> score(@RequestHeader(name = "aid") Long aid, @Validated @RequestBody ConfirmScore confirmScore) throws WxPayException, ServiceException {
        Store store = storeRepository.findById(confirmScore.getStoreId()).orElseThrow(() -> new ServiceException("FAIL", "该店铺不存,请联系客服", confirmScore));
        if (!store.getStatus()) {
            throw new RuntimeException("正在准备中,请稍后重试!");
        }
        LocalTime now = LocalTime.now();
        if (now.isAfter(store.getCloseTime()) & now.isBefore(store.getOpenTime())) {
            throw new RuntimeException(MessageFormat.format("营业时间:{0}-{1}", store.getOpenTime(), store.getCloseTime()));
        }

        Map<Long, Integer> cart = new TreeMap<>();
        for (String pid_numStr : confirmScore.getCart()) {
            String[] pid_num = pid_numStr.split(":");
            cart.put(Long.valueOf(pid_num[0]), Integer.valueOf(pid_num[1]));
        }

        CompletableFuture<List<Product>> supplyAsync1 = CompletableFuture.supplyAsync(() -> productRepository.findByIdInAndStoreId(cart.keySet(), confirmScore.getStoreId()));
        CompletableFuture<Optional<Account>> supplyAsync2 = CompletableFuture.supplyAsync(() -> accountRepository.findById(aid));

        CompletableFuture.allOf(supplyAsync1, supplyAsync2).join();
        Account account = supplyAsync2.getNow(null).orElseThrow(() -> new ServiceException("FAIL", "该用户不存在,请联系平台服务商", aid));
        List<Product> products = supplyAsync1.getNow(null);

        Score score = wxPay.jsapi(confirmScore, store, products, cart, account);
        score = scoreService.createScore(score, confirmScore.getStoreId(), cart.keySet());
        WxPayMpOrderResult wxPayMpOrderResult = wxPay.continuePay(score);

        Map<String, Object> data = new HashMap<>();
        data.put("order", wxPayMpOrderResult);
        data.put("score", score);
        return data;
    }

    /**
     * @param score 订单详情
     * @return 小程序支付参数
     */
    @PostMapping(value = {"scores/continuePay"})
    public Map<String, Object> pay(@RequestBody Score score) {
        Map<String, Object> data = new HashMap<>();
        data.put("order", wxPay.continuePay(score));
        return data;
    }

    /**
     * @param wxNotify 微信支付通知请求信息
     * @return 成功 或 失败
     */
    @PostMapping(value = {"scores/notify"})
    public Work<String> firstNotify(@RequestBody WXNotify wxNotify) throws GeneralSecurityException, IOException {
        String decryptToString = wxPay.decryptToString(wxNotify);
        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
        WXNotify.WXTransaction wxTransaction = gson.fromJson(decryptToString, WXNotify.WXTransaction.class);
        String tradeState = wxTransaction.getTradeState();
        if (tradeState.equals(WxPayConstants.WxpayTradeStatus.SUCCESS)) {
            Score score = scoreRepository.findByOutTradeNo(wxTransaction.getOutTradeNo()).orElseThrow(() -> new ServiceException("FAIL", "订单丢失", wxTransaction));
            if (score.getStatus().equals(Score.Status.WAIT_PAY)) {
                querydslService.setScore(score.getId(), score.getStatus(), wxTransaction.getTransactionId(), Score.Status.WAIT_PACKAGE);
                score.setStatus(Score.Status.WAIT_PACKAGE);
            }
            scoreService.send(score, "订单支付成功");
        }
        return Work.success("支付成功");
    }


    @PutMapping(value = {"scores/{id}/refund"})
    public Score refund(@PathVariable Long id, @RequestHeader(name = "aid") Long aid) throws WxPayException {
        Score score = scoreRepository.findByIdAndAccountId(id, aid).orElseThrow(() -> new ServiceException("FAIL", "订单丢失,请联系客服", MessageFormat.format("订单ID:{0},用户ID:{1}", id, aid)));
        if (score.getStatus().equals(Score.Status.FINISH)) {
            throw new RuntimeException("该订单已完成,请联系商家");
        }
        if (score.getStatus().equals(Score.Status.WAIT_SIGN)) {
            throw new RuntimeException("该订单准备配送中,请联系商家");
        }
        WXRefundResult wxRefundResult = wxPay.refund(score);
        WXRefundResult.Status status = wxRefundResult.getStatus();
        if (status.equals(WXRefundResult.Status.SUCCESS)) {
            scoreService.refundScore(score, Score.Status.REFUND_SUCCESS);
            score.setStatus(Score.Status.REFUND_SUCCESS);
        } else if (status.equals(WXRefundResult.Status.PROCESSING)) {
            scoreService.refundScore(score, Score.Status.REFUND_PROCESSING);
            score.setStatus(Score.Status.REFUND_PROCESSING);
        } else if (status.equals(WXRefundResult.Status.CLOSED)) {
            throw new RuntimeException("退款关闭,请联系商家");
        } else {
            throw new ServiceException("FAIL", "退款异常,请联系平台服务商", wxRefundResult);
        }
        return score;
    }

    @PostMapping(value = {"scores/{id}/refund/notify"})
    public Work<String> refundNotify(@PathVariable Long id, @RequestBody WXNotify wxNotify) throws GeneralSecurityException, IOException {
        String decryptToString = wxPay.decryptToString(wxNotify);
        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
        WXNotify.WXRefund wxRefund = gson.fromJson(decryptToString, WXNotify.WXRefund.class);
        WXNotify.WXRefund.Status refundStatus = wxRefund.getRefundStatus();
        Score score = scoreRepository.findById(id).orElseThrow(() -> new ServiceException("FAIL", "订单丢失", wxRefund));
        if (refundStatus.equals(WXNotify.WXRefund.Status.SUCCESS) || score.getStatus() == Score.Status.REFUND_PROCESSING) {
            querydslService.setScore(score.getId(), score.getStatus(), Score.Status.REFUND_SUCCESS);
            score.setStatus(Score.Status.REFUND_SUCCESS);
        } else if (refundStatus.equals(WXNotify.WXRefund.Status.CLOSE)) {
            querydslService.setScore(score.getId(), score.getStatus(), Score.Status.REFUND_CLOSED);
            score.setStatus(Score.Status.REFUND_CLOSED);
        } else {
            querydslService.setScore(score.getId(), score.getStatus(), Score.Status.REFUND_ABNORMAL);
            score.setStatus(Score.Status.REFUND_ABNORMAL);
        }
        scoreService.send(score, "订单退款成功");
        return Work.success("成功");
    }

    @PostMapping(value = {"scores/{id}/finishScore"})
    public Score finishScore(@PathVariable Long id, @RequestHeader(name = "aid") Long aid) {
        Score score = scoreRepository.findByIdAndAccountId(id, aid).orElseThrow(() -> new ServiceException("FAIL", "该订单不存,请联系平台服务商", MessageFormat.format("订单ID:{0},用户ID:{1}", id, aid)));
        Score.Status status = score.getStatus();
        if (status.equals(Score.Status.WAIT_SIGN)) {
            querydslService.setScore(score.getId(), score.getStatus(), Score.Status.FINISH);
            score.setStatus(Score.Status.FINISH);
        } else if (status.equals(Score.Status.FINISH)) {
            return score;
        } else {
            throw new ServiceException("FAIL", "订单异常:".concat(status.getMessage()), score);
        }
        return score;
    }
}
