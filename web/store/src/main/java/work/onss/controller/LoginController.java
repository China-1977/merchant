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
import work.onss.domain.Customer;
import work.onss.domain.CustomerRepository;
import work.onss.domain.Info;
import work.onss.domain.QCustomer;
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
    private CustomerRepository customerRepository;
    @Autowired
    private SystemConfig systemConfig;
    @Autowired
    private WxMa wxMa;

    /**
     * @param wxLogin 微信登陆信息
     * @return 密钥及营业员信息
     */
    @PostMapping(value = {"wxLogin"})
    public Map<String, Object> wxLogin(@RequestBody WXLogin wxLogin) throws WxErrorException {
        WxMaJscode2SessionResult wxMaJscode2SessionResult = wxMa.wxLogin(wxLogin);
        PhoneEncryptedData phoneEncryptedData = wxMa.decrypt(wxMaJscode2SessionResult, wxLogin);

        QCustomer qCustomer = QCustomer.customer;
        Instant now = Instant.now();
        Customer customer = customerRepository.findOne(qCustomer.phone.eq(phoneEncryptedData.getPhoneNumber())).orElse(new Customer(
                phoneEncryptedData.getPhoneNumber(),
                wxMaJscode2SessionResult.getOpenid(),
                wxLogin.getSubAppId(),
                wxMaJscode2SessionResult.getSessionKey(),
                Timestamp.from(now)
        ));
        if (!customer.getSubOpenid().equals(wxMaJscode2SessionResult.getOpenid())) {
            throw new ServiceException("FAIL", "您的手机号已被注册,请联系客服", customer);
        }
        customer.setUpdateTime(Timestamp.from(now));
        customerRepository.save(customer);

        Info info = new Info(customer.getId(), Date.from(now));
        String subject = new Gson().toJson(info);
        String authorization = Utils.authorization(systemConfig.getSecret(), "1977", now, subject, customer.getId().toString(), "WeChat");

        Map<String, Object> result = new HashMap<>();
        result.put("authorization", authorization);
        result.put("info", info);
        return result;
    }
}

