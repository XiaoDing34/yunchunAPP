package edu.tianjinagriculture.yunchunapp.VO;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.util.Date;

@Data
public class WarnMessageVO {
    private Integer id;
    private String messageTitle;
    private String messageContent;
    private String sender;
    private Date createTime;
    private String advice;
    private String warnLevel;
    private String audioUrl;
    private Integer feedbackStatus;
    private Integer handleStatus;
    private String handlerName;
}
