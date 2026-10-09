package edu.tianjinagriculture.yunchunapp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.tianjinagriculture.yunchunapp.entity.DeviceValue;

import java.util.List;

public interface DeviceValueService extends IService<DeviceValue> {
    List<DeviceValue> getDeviceValueById(int userId);

    List<DeviceValue> getDeviceValueByDeviceId(Integer deviceId);

    List<DeviceValue> getFaultDeviceValue(int userId);

    boolean setDeviceValue(Integer deviceId, Integer valueId, Float value);

    boolean queryDeviceValue(Integer deviceId, Integer valueId);

    boolean toggleDeviceValue(Integer deviceId, Integer valueId);

    boolean toggleDeviceValueByName(Integer deviceId, String valueName);
}
