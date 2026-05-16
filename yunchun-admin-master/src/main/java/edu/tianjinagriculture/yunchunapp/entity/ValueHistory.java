package edu.tianjinagriculture.yunchunapp.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName(value = "value_history")
public class ValueHistory {
    @TableId(type = IdType.AUTO)
    //主键
    private Integer id;
    private Date updateTime;
    private BigDecimal value;
    private Integer valueId;
}