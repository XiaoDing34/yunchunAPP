package edu.tianjinagriculture.yunchunapp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName(value = "project_list")
public class ProjectList {
    @TableId(type = IdType.AUTO)
    //项目id
    private Integer id;
    //项目名称
    private String name;
    //项目具体信息（也就是备注）
    private String detailInformation;
    //项目所属组织
    private String organization;
    //项目所在地区行政区划编码
    private Integer locationCode;
    //项目所在地区具体地址
    private String locationVar;
    //项目Sn编号（没任何意义，只要不重复就可以，连接mqtt服务器时候当账号用的）
    private String deviceSn;
    //创建项目的用户的userCode，也就是手机号
    private String createUser;
    //项目创建时间
    private Date updateTime;
}
