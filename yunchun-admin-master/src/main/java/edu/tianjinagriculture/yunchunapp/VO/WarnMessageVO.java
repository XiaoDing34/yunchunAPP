package edu.tianjinagriculture.yunchunapp.VO;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.util.Date;

@Data
public class WarnMessageVO {
    private String messageTitle;
    private String messageContent;
    private String sender;
    private Date createTime;
    private String advice;
    private String warnLevel;
}
