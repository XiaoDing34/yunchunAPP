package edu.tianjinagriculture.yunchunapp.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.bouncycastle.crypto.signers.ISOTrailers;

import java.util.Date;

@Data
@TableName(value = "feedback")
public class Feedback {
    @TableField(value = "f_user")
    private String username;
    private String content;
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
}
