package edu.tianjinagriculture.yunchunapp.controller;

import cn.dev33.satoken.stp.StpUtil;
import edu.tianjinagriculture.yunchunapp.commom.Result;
import edu.tianjinagriculture.yunchunapp.entity.DeviceList;
import edu.tianjinagriculture.yunchunapp.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/device")
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    //根据用户id获取设备
    @GetMapping("/getDeviceById")
    public Result<List<DeviceList>> getDeviceById() {
        Integer userId= StpUtil.getLoginIdAsInt();
        return Result.success(deviceService.getDeviceList(userId));
    }
}
