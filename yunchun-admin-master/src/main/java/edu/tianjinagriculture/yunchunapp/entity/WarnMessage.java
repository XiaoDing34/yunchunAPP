package edu.tianjinagriculture.yunchunapp.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

@Data
@TableName(value = "warn_message")
public class WarnMessage {
    @TableId(type = IdType.AUTO)
    //主键
    private Integer id;
    private String messageTitle;
    private String messageContent;
    private Integer deviceId;
    private Integer valueId;
    private String sender;
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    private String advice;
    private String warnLevel;
    private String warnOrganization;
}
