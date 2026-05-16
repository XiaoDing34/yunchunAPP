package edu.tianjinagriculture.yunchunapp.VO;

import lombok.Data;

@Data
public class LoginVO {
    /**
     * 登录token
     */
    private String token;

    /**
     * 是否需要完善资料
     */
    private Boolean needCompleteProfile;

    /**
     * 是否需要审核
     */
    private Boolean needAudit;

    /**
     * 用户信息
     */
    private UserInfoVO userInfo;
}
