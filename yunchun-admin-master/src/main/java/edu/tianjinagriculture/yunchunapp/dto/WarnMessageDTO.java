package edu.tianjinagriculture.yunchunapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class WarnMessageDTO {
    @JsonProperty("warnReason")
    private String messageTitle;
    @JsonProperty("maxValue")
    private BigDecimal highLimit;
    @JsonProperty("minValue")
    private BigDecimal lowLimit;
    private Integer deviceId;
    private Integer valueId;
    private String advice;
    private String warnLevel;
}
