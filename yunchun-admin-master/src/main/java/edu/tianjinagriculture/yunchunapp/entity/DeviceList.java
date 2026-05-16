package edu.tianjinagriculture.yunchunapp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "device_list")
public class DeviceList {
    @TableId(type = IdType.AUTO)
    //设备id
    private Integer id;
    //设备名称
    private String name;
    //设备所属项目id
    private Integer project;
    //设备状态（离线/在线）
    private String state;
    //设备备注名称，数据大屏项目大部分地方用的是这个备注名称而不是真正的设备名称
    private String notes;
    //设备所在地区行政区划编码（六位代码，身份证前六位就是，比如130827，代表河北省承德市宽城县）
    private Integer locationCode;
    //设备所属具体位置（如祥泰小区12号楼2单元5楼205室）
    private String locationVar;
    //用于统计当日该设备共接收多少条数据
    private Integer dataNumber;
    //unity模型id
    private Integer moduleId;
}
