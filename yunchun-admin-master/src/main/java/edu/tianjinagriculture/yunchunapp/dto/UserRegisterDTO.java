package edu.tianjinagriculture.yunchunapp.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class UserRegisterDTO {
    private String username;
    private String phone;
    private String code;
    private String organization;
    private String position;
    private String password;
    private String confirmPassword;
}
