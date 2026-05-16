package edu.tianjinagriculture.yunchunapp.VO;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.util.Date;

@Data
public class UserInfoVO {
    private String username;
    private String nickname;
    private String avatar;
    private Date createTime;
    private String organization;
    private String position;
    private String phone;
}
