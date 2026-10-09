package edu.tianjinagriculture.yunchunapp.dto;

import lombok.Data;

@Data
public class ChangePasswordDTO {
    //原密码
    private String oldPassword;
    //新密码
    private String newPassword;
    //确认新密码
    private String confirmPassword;
}
