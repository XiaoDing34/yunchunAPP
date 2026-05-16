package edu.tianjinagriculture.yunchunapp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.tianjinagriculture.yunchunapp.entity.DeviceList;
import edu.tianjinagriculture.yunchunapp.entity.DeviceValue;
import edu.tianjinagriculture.yunchunapp.entity.ProjectList;
import edu.tianjinagriculture.yunchunapp.entity.User;
import edu.tianjinagriculture.yunchunapp.mapper.DeviceMapper;
import edu.tianjinagriculture.yunchunapp.mapper.DeviceValueMapper;
import edu.tianjinagriculture.yunchunapp.mapper.ProjectMapper;
import edu.tianjinagriculture.yunchunapp.service.DeviceService;
import edu.tianjinagriculture.yunchunapp.service.DeviceValueService;
import edu.tianjinagriculture.yunchunapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DeviceValueServiceImpl extends ServiceImpl<DeviceValueMapper,DeviceValue> implements DeviceValueService {
    @Autowired
    UserService userService;

    @Autowired
    DeviceService deviceService;

    @Autowired
    DeviceValueMapper deviceValueMapper;

    //根据用户ID获取设备数据值
    @Override
    public List<DeviceValue> getDeviceValueById(int userId) {
        List<DeviceValue> deviceValueList=new ArrayList<>();
        String[] whiteList = {"后烟箱温度", "膨胀水箱液位", "出水温度", "回水温度", "炉膛负压",
                "炉膛温度", "补水压力", "布袋进口压力", "布袋出口温度", "脱硝加热棒温度"};
        Set<String> whiteSet = Set.of(whiteList);
        List<DeviceList> lists=deviceService.getDeviceList(userId);
        for(DeviceList list:lists){
            QueryWrapper<DeviceValue> queryWrapper=new QueryWrapper<>();
            queryWrapper.eq("device_id",list.getId());
            List<DeviceValue> noFiltered= deviceValueMapper.selectList(queryWrapper);
            for(DeviceValue deviceValue:noFiltered){
                if(whiteSet.contains(deviceValue.getNotes())){
                    deviceValueList.add(deviceValue);
                }
            }
        }
        //System.out.println(deviceValueList);
        return deviceValueList;
    }

    @Override
    public List<DeviceValue> getDeviceValueByDeviceId(Integer deviceId) {
        List<DeviceValue> deviceValueList=new ArrayList<>();
        String[] whiteList = {"后烟箱温度", "膨胀水箱液位", "出水温度", "回水温度", "炉膛负压",
                "炉膛温度", "补水压力", "布袋进口压力", "布袋出口温度", "脱硝加热棒温度"};
        Set<String> whiteSet = Set.of(whiteList);
        QueryWrapper<DeviceValue> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("device_id",deviceId);
        List<DeviceValue> list=deviceValueMapper.selectList(queryWrapper);
        for(DeviceValue deviceValue:list){
            if(whiteSet.contains(deviceValue.getNotes())){
                deviceValueList.add(deviceValue);
            }
        }
        return deviceValueList;
    }
}
