package edu.tianjinagriculture.yunchunapp.controller;

import cn.dev33.satoken.stp.StpUtil;
import edu.tianjinagriculture.yunchunapp.commom.Result;
import edu.tianjinagriculture.yunchunapp.dto.ControlCommandDTO;
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

    @GetMapping("/getDeviceValueById")
    public Result<List<DeviceValue>> getDeviceValueById(){
        int userId= StpUtil.getLoginIdAsInt();
        return Result.success(deviceValueService.getDeviceValueById(userId));
    }

    @GetMapping("/getDeviceValueByDeviceId")
    public Result<List<DeviceValue>> getDeviceValueByDeviceId(@RequestParam Integer deviceId){
        System.out.println(deviceValueService.getDeviceValueByDeviceId(deviceId));
        return Result.success(deviceValueService.getDeviceValueByDeviceId(deviceId));
    }

    @GetMapping("/getFaultDeviceValue")
    public Result<List<DeviceValue>> getFaultDeviceValue() {
        int userId = StpUtil.getLoginIdAsInt();
        return Result.success(deviceValueService.getFaultDeviceValue(userId));
    }

    @PostMapping("/setValue")
    public Result<String> setValue(@RequestBody ControlCommandDTO command){
        boolean success = deviceValueService.setDeviceValue(command.getDeviceId(), command.getValueId(), command.getValue());
        if(success){
            return Result.success("控制指令下发成功");
        }else{
            return Result.error("控制指令下发失败");
        }
    }

    @PostMapping("/queryValue")
    public Result<String> queryValue(@RequestBody ControlCommandDTO command){
        boolean success = deviceValueService.queryDeviceValue(command.getDeviceId(), command.getValueId());
        if(success){
            return Result.success("查询指令下发成功");
        }else{
            return Result.error("查询指令下发失败");
        }
    }

    @PostMapping("/toggle")
    public Result<String> toggle(@RequestBody ControlCommandDTO command){
        boolean success = deviceValueService.toggleDeviceValue(command.getDeviceId(), command.getValueId());
        if(success){
            return Result.success("开关控制指令下发成功");
        }else{
            return Result.error("开关控制指令下发失败");
        }
    }

    @PostMapping("/toggleByName")
    public Result<String> toggleByName(@RequestBody ControlCommandDTO command){
        boolean success = deviceValueService.toggleDeviceValueByName(command.getDeviceId(), command.getValueName());
        if(success){
            return Result.success("开关控制指令下发成功");
        }else{
            return Result.error("开关控制指令下发失败");
        }
    }
}
