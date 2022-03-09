package work.onss.service;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import work.onss.domain.*;
import work.onss.utils.Utils;

import java.util.*;

@Service
public class ScoreService {

    @Autowired
    private ScoreRepository scoreRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private JPAQueryFactory jpaQueryFactory;
    @Autowired
    private QuerydslService querydslService;

    /**
     * @param score 订单表单
     * @param sid   商户ID
     * @param pids  商品ID
     * @return 最新订单
     */
    @Transactional
    public Score createScore(Score score, Long sid, Set<Long> pids) {
        QProduct qProduct = QProduct.product;
        for (Score.Product product : score.getProducts()) {
            long execute = jpaQueryFactory.update(qProduct)
                    .set(qProduct.stock, qProduct.stock.subtract(product.getNum()))
                    .where(qProduct.id.eq(product.getPid()), qProduct.stock.goe(product.getNum()))
                    .execute();
            if (execute == 0) {
                throw new RuntimeException(product.getName().concat("库存不足"));
            }
        }
        cartRepository.deleteByAccountIdAndStoreIdAndProductIdIn(score.getAccountId(), sid, pids);
        scoreRepository.save(score);
        return score;
    }

    /**
     * @param score  当前订单
     * @param status 设置订单状态
     */
    @Transactional
    public void refundScore(Score score, Score.Status status) {
        QProduct qProduct = QProduct.product;
        for (Score.Product product : score.getProducts()) {
            jpaQueryFactory.update(qProduct)
                    .set(qProduct.stock, qProduct.stock.add(product.getNum()))
                    .where(qProduct.id.eq(product.getPid()))
                    .execute();
        }
        QScore qScore = QScore.score;
        jpaQueryFactory.update(qScore)
                .set(qScore.status, status)
                .where(qScore.id.eq(score.getId()), qScore.status.eq(score.getStatus()))
                .execute();
    }

    @Autowired
    private WxMa wxMa;
    @Value(value = "${wechat.mp.appId}")
    String spappId;

    public void send(Score score, String first) {
        List<String> resources = Arrays.asList(
                "/storePOST[scores/{id}/waitDeliver]",
                "/storePOST[scores/{id}/waitPackage]",
                "/storePOST[scores/{id}-{outTradeNo}/refund]"
        );
        List<Customer> customers = querydslService.get(score.getStoreId(), resources);
        String keyword3 = String.join("\n", score.getProducts().stream().map(Score.Product::getName).toList());
        String remark = String.join("\n", score.getUsername(), score.getPhone(), score.getAddressDetail());
        Map<String, String> data = Utils.OPENTM207940503(first, score.getOutTradeNo(), score.getStatus().getMessage(), keyword3, remark);
        for (Customer customer : customers) {
            wxMa.sendUniformMsg(spappId, customer.getSubAppid(), customer.getSubOpenid(), "boyfaz_o7NEzgEsOPOZMSvcqvrfWgHbA0k8NuEMV-b8", data, "pages/score/detail?id=".concat(score.getId().toString()));
        }
        Optional<Account> accountOptional = accountRepository.findById(score.getAccountId());
        accountOptional.ifPresent(account -> wxMa.sendUniformMsg(spappId, account.getSubAppid(), account.getSubOpenid(), "boyfaz_o7NEzgEsOPOZMSvcqvrfWgHbA0k8NuEMV-b8", data, "pages/score/detail?id=".concat(score.getId().toString())));
    }
}
