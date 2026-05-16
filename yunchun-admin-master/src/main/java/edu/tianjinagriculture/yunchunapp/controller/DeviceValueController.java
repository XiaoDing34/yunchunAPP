package edu.tianjinagriculture.yunchunapp.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import edu.tianjinagriculture.yunchunapp.commom.Result;
import edu.tianjinagriculture.yunchunapp.entity.DeviceValue;
import edu.tianjinagriculture.yunchunapp.service.DeviceValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/deviceValue")
public class DeviceValueController {

    @Autowired
    DeviceValueService deviceValueService;
    //根据用户id获取到所需要的设备数据
    @GetMapping("/getDeviceValueById")
    public Result<List<DeviceValue>> getDeviceValueById(){
        int userId= StpUtil.getLoginIdAsInt();
        return Result.success(deviceValueService.getDeviceValueById(userId));
    }

    @GetMapping("/getDeviceValueByDeviceId")
    public Result<List<DeviceValue>> getDeviceValueByDeviceId(@RequestParam Integer deviceId){
        return Result.success(deviceValueService.getDeviceValueByDeviceId(deviceId));
    }
}
