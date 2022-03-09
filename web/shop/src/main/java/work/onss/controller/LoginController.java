package work.onss.controller;

import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import com.google.gson.Gson;
import lombok.extern.log4j.Log4j2;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import work.onss.config.SystemConfig;
import work.onss.domain.Account;
import work.onss.domain.AccountRepository;
import work.onss.domain.Info;
import work.onss.domain.QAccount;
import work.onss.exception.ServiceException;
import work.onss.service.WxMa;
import work.onss.utils.Utils;
import work.onss.vo.PhoneEncryptedData;
import work.onss.vo.WXLogin;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Log4j2
@RestController
public class LoginController {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private SystemConfig systemConfig;
    @Autowired
    private WxMa wxMa;

    /**
     * @param wxLogin 微信登陆信息
     * @return 密钥
     */
    @PostMapping(value = {"wxLogin"})
    public Map<String, Object> wxLogin(@RequestBody WXLogin wxLogin) throws WxErrorException {
        WxMaJscode2SessionResult wxMaJscode2SessionResult = wxMa.wxLogin(wxLogin);
        PhoneEncryptedData phoneEncryptedData = wxMa.decrypt(wxMaJscode2SessionResult, wxLogin);

        QAccount qAccount = QAccount.account;
        Instant now = Instant.now();
        Account account = accountRepository.findOne(qAccount.phone.eq(phoneEncryptedData.getPhoneNumber())).orElse(new Account(
                phoneEncryptedData.getPhoneNumber(),
                wxMaJscode2SessionResult.getOpenid(),
                wxLogin.getSubAppId(),
                wxMaJscode2SessionResult.getSessionKey(),
                Timestamp.from(now)
        ));
        if (!account.getSubOpenid().equals(wxMaJscode2SessionResult.getOpenid())) {
            throw new ServiceException("FAIL", "您的手机号已被注册,请联系客服", account);
        }
        account.setUpdateTime(Timestamp.from(now));
        accountRepository.save(account);

        Info info = new Info(account.getId(), Date.from(now));
        String subject = new Gson().toJson(info);
        String authorization = Utils.authorization(systemConfig.getSecret(), "1977", now, subject, account.getId().toString(), "WeChat");

        Map<String, Object> result = new HashMap<>();
        result.put("authorization", authorization);
        result.put("info", info);
        return result;
    }
}

