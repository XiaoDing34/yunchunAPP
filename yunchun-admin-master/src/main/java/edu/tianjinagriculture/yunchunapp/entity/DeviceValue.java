package edu.tianjinagriculture.yunchunapp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;


@Data
@TableName(value = "device_value")
public class DeviceValue {
    @TableId(type = IdType.AUTO)
    //主键
    private Integer id;
    private String name;
    private Date updateTime;
    private BigDecimal value;
    private String unitName;
    private Integer deviceId;
    private BigDecimal highLimit;
    private BigDecimal lowLimit;
    private String alarmTag;
    private String notes;
    private Integer collectFrequency;
    private String highAdvice;
    private String lowAdvice;
}
