package edu.tianjinagriculture.yunchunapp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.tianjinagriculture.yunchunapp.commom.Result;
import edu.tianjinagriculture.yunchunapp.entity.DeviceList;
import edu.tianjinagriculture.yunchunapp.entity.DeviceValue;

import java.util.List;

public interface DeviceService extends IService<DeviceList> {
      List<DeviceList> getDeviceList(Integer projectId);
}
