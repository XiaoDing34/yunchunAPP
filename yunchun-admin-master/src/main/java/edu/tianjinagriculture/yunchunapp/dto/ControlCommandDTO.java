package edu.tianjinagriculture.yunchunapp.dto;

import lombok.Data;

@Data
public class ControlCommandDTO {
    private Integer deviceId;
    private Integer valueId;
    private String valueName;
    private Float value;
}