package edu.tianjinagriculture.yunchunapp.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
@TableName(value = "app_user")
public class User {
//    @TableId(type = IdType.AUTO)
//    private Integer id;
//    private String name;
//    private String contact;
//    private String company;
//    private String position;
//    private String password;
//    @TableField(value = "create_date",fill = FieldFill.INSERT)
//    @JsonFormat(pattern = "YYYY-MM-dd HH:mm:ss")
//    private Date createDate;
    @TableId(type = IdType.AUTO)
    private Integer userId;

    /**
     * 登录账号（手机号/邮箱），仅账号密码用户必填
     */
    private String username;

    /**
     * 加密密码（BCrypt），仅账号密码用户有值
     */
    private String password;

    /**
     * 微信openid，仅微信登录用户有值
     */
    private String openid;

    /**
     * 微信unionid，用于跨应用用户统一
     */
    private String unionid;

    /**
     * 用户昵称（可修改）
     */
    private String nickname;

    /**
     * 头像URL
     */
    private String avatarUrl;

    /**
     * 手机号（可选，用于绑定或合并账号）
     */
    private String phone;

    /**
     * 微信session_key，用于解密用户信息
     */
    private String sessionKey;

    /**
     * 状态：1-正常，0-禁用，2-未完善资料
     */
    private Integer status;

    /**
     * 最后登录时间
     */
    private Date lastLoginTime;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 用户所属公司
     */
    private String organization;

    /**
     * 用户职位
     */
    private String position;
}
