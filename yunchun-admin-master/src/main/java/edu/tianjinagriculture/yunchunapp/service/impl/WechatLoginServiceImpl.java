package edu.tianjinagriculture.yunchunapp.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WechatLoginServiceImpl {

    @Autowired
    private WxMaService wxMaService;

    public WxMaJscode2SessionResult code2Session(String code) {
        try {
            // 一行代码搞定，换取用户信息
            //System.out.println(wxMaService.getUserService().getSessionInfo(code));
            return wxMaService.getUserService().getSessionInfo(code);
        } catch (WxErrorException e) {
            throw new RuntimeException("微信登录失败: " + e.getMessage());
        }
    }
}
