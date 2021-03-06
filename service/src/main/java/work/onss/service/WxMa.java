package work.onss.service;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.WxMaUserService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaSubscribeMessage;
import cn.binarywang.wx.miniapp.bean.WxMaTemplateData;
import cn.binarywang.wx.miniapp.bean.WxMaUniformMessage;
import cn.binarywang.wx.miniapp.json.WxMaGsonBuilder;
import cn.binarywang.wx.miniapp.util.crypt.WxMaCryptUtils;
import com.google.gson.Gson;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import work.onss.vo.PhoneEncryptedData;
import work.onss.vo.WXLogin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class WxMa {

    @Autowired
    private WxMaService wxMaService;

    public WxMaJscode2SessionResult wxLogin(WXLogin wxLogin) throws WxErrorException {
        wxMaService = wxMaService.switchoverTo(wxLogin.getSubAppId());
        WxMaUserService userService = wxMaService.getUserService();
        return userService.getSessionInfo(wxLogin.getCode());
    }

    public PhoneEncryptedData decrypt(WxMaJscode2SessionResult wxMaJscode2SessionResult, WXLogin wxLogin) {
        String encryptedData = WxMaCryptUtils.decrypt(wxMaJscode2SessionResult.getSessionKey(), wxLogin.getEncryptedData(), wxLogin.getIv());
        Gson gson = WxMaGsonBuilder.create();
        return gson.fromJson(encryptedData, PhoneEncryptedData.class);
    }

    public void send(String subAppid, String toUser, String templateId, Map<String, String> data, String page, String miniProgramState) throws WxErrorException {
        wxMaService = wxMaService.switchoverTo(subAppid);
        List<WxMaSubscribeMessage.MsgData> msgDataList = new ArrayList<>();

        data.forEach((key, value) -> {
            WxMaSubscribeMessage.MsgData msgData = new WxMaSubscribeMessage.MsgData();
            msgData.setName(key);
            msgData.setValue(value);
            msgDataList.add(msgData);
        });

        WxMaSubscribeMessage wxMaSubscribeMessage = WxMaSubscribeMessage.builder()
                .templateId(templateId)
                .toUser(toUser)
                .data(msgDataList)
                .page(page)
                .miniprogramState(miniProgramState)
                .build();
        wxMaService.getMsgService().sendSubscribeMsg(wxMaSubscribeMessage);
    }


    /**
     * @param spappid    ?????????appid
     * @param subAppId   ?????????appid
     * @param toUser     ?????????openid ?????? ?????????openid
     * @param templateId ?????????????????????ID
     * @param data       ??????????????????
     * @param page       ???????????????
     */
    @Async
    public void sendUniformMsg(String spappid, String subAppId, String toUser, String templateId, Map<String, String> data, String page) {
        wxMaService = wxMaService.switchoverTo(subAppId);
        List<WxMaTemplateData> wxMaTemplateDataList = new ArrayList<>();
        data.forEach((key, value) -> {
            WxMaTemplateData wxMaTemplateData = new WxMaTemplateData();
            wxMaTemplateData.setName(key);
            wxMaTemplateData.setValue(value);
            wxMaTemplateDataList.add(wxMaTemplateData);
        });

        WxMaUniformMessage.MiniProgram miniProgram = new WxMaUniformMessage.MiniProgram(subAppId, page, false, false);
        WxMaUniformMessage wxMaUniformMessage = WxMaUniformMessage.builder()
                .data(wxMaTemplateDataList)
                .toUser(toUser)
                .templateId(templateId)
                .isMpTemplateMsg(true)
                .miniProgram(miniProgram)
                .appid(spappid)
                .build();

        try {
            wxMaService.getMsgService().sendUniformMsg(wxMaUniformMessage);
        } catch (WxErrorException e) {
            e.printStackTrace();
        }
    }
}
