package edu.tianjinagriculture.yunchunapp.dto;

import lombok.Data;

@Data
public class WxLoginInfoDTO {
    private String phone;
    private String code;
    private String organization;
    private String position;
}
