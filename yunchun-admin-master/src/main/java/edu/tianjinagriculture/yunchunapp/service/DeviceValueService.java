package edu.tianjinagriculture.yunchunapp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.tianjinagriculture.yunchunapp.entity.DeviceValue;

import java.util.List;

public interface DeviceValueService extends IService<DeviceValue> {
    List<DeviceValue> getDeviceValueById(int userId);

    List<DeviceValue> getDeviceValueByDeviceId(Integer deviceId);
}
