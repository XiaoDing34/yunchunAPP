package edu.tianjinagriculture.yunchunapp.entity;

import lombok.Data;

@Data
public class Sms {
    private String phone;
    private String code;
    private int min;
}
