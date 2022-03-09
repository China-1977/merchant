package work.onss.service;


import me.chanjar.weixin.common.error.WxErrorException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import work.onss.ShopApplication;
import work.onss.domain.Account;
import work.onss.domain.AccountRepository;
import work.onss.domain.Score;
import work.onss.domain.ScoreRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@SpringBootTest
public class WxMaTest extends ShopApplication {

    @Autowired
    private ScoreRepository scoreRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private WxMa wxMa;
    @Value(value = "${spring.profiles.active}")
    String active;

    @Value(value = "${wechat.mp.appId}")
    String appId;

    @Test
    public void testSend() {
        Map<String, String> data = new HashMap<>();
        data.put("character_string1", "20220306");
        data.put("date2", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        data.put("number3", "123458");
        data.put("thing4", "星美城市广场二期");
        data.put("phone_number8", "15063517241");
        try {
            wxMa.send("wxe78290c2a5313de3", "o0l844y9jeAom9PEQRQBj7urCpTg", "4ENigiRroZ6Hrao9Wwg0VrBn3_4mIXo0uuS-bzH4DBA", data, "pages/score/detail?id=2151615", active);
        } catch (WxErrorException e) {
            e.printStackTrace();
        }
    }

    @Test
    void sendUniformMsg() {
        Map<String, String> data = new HashMap<>();
        data.put("first", "first");
        data.put("keyword1", "订单号");
        data.put("keyword2", "订单状态");
        data.put("keyword3", "商品名称");
        data.put("remark", "remark");
        wxMa.sendUniformMsg(appId, "wxe78290c2a5313de3", "o0l844y9jeAom9PEQRQBj7urCpTg", "boyfaz_o7NEzgEsOPOZMSvcqvrfWgHbA0k8NuEMV-b8", data, "pages/score/detail?id=2151615");
    }

    @Test
    void sendUniformMsg1() {
        Score oldScore = scoreRepository.findById(708694L).orElse(null);
        assert oldScore != null;
        Optional<Account> accountOptional = accountRepository.findById(oldScore.getAccountId());
        if (accountOptional.isPresent()) {
            Map<String, String> data = new HashMap<>();
            data.put("first", "您的订单准备配送中,请保持电话畅通,谢谢配合");
            data.put("keyword1", oldScore.getOutTradeNo());
            data.put("keyword2", oldScore.getStatus().getMessage());
            data.put("keyword3", String.join("\n", oldScore.getProducts().stream().map(Score.Product::getName).toList()));
            data.put("remark", String.join("\n", oldScore.getStoreShortname(), oldScore.getStoreUsername(), oldScore.getStorePhone(), oldScore.getStoreAddressDetail()));
            wxMa.sendUniformMsg(appId, accountOptional.get().getSubAppid(), accountOptional.get().getSubOpenid(), "boyfaz_o7NEzgEsOPOZMSvcqvrfWgHbA0k8NuEMV-b8", data, "pages/score/detail?id=2151615");
        }
    }
}
