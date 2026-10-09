package edu.tianjinagriculture.yunchunapp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.tianjinagriculture.yunchunapp.entity.*;
import edu.tianjinagriculture.yunchunapp.mapper.DeviceValueMapper;
import edu.tianjinagriculture.yunchunapp.mapper.WarnMessageMapper;
import edu.tianjinagriculture.yunchunapp.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DeviceValueServiceImpl extends ServiceImpl<DeviceValueMapper,DeviceValue> implements DeviceValueService {
    private static final Logger logger = LoggerFactory.getLogger(DeviceValueServiceImpl.class);

    @Autowired
    DeviceService deviceService;

    @Autowired
    ProjectService projectService;

    @Autowired
    UserService userService;

    @Autowired
    @Lazy
    MqttClientService mqttClientService;

    @Autowired
    DeviceValueMapper deviceValueMapper;

    @Autowired
    WarnMessageMapper warnMessageMapper;

    //根据用户ID获取设备数据值
    @Override
    public List<DeviceValue> getDeviceValueById(int userId) {
        List<DeviceValue> deviceValueList=new ArrayList<>();
        String[] whiteList = {"后烟箱温度", "膨胀水箱液位", "出水温度", "回水温度", "炉膛负压",
                "炉膛温度", "补水压力", "布袋进口压力", "布袋出口温度", "脱硝加热棒温度"};
        Set<String> whiteSet = new HashSet<>(Arrays.asList(whiteList));
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
                "炉膛温度", "补水压力", "布袋进口压力", "布袋出口温度",
                "脱硝加热棒温度","用电量","用料量","蒸汽累计流量","磁翻板水位","省煤器入口烟温","省煤出口烟温"
                ,"烟尘入口压力","烟尘入口温度","烟气含氧量","蒸汽流量","蒸汽压力","蒸汽温度","引风机压力",
                "鼓风机压力","引风机频率","鼓风机频率","料斗温度","省煤出口水温","省煤器入口水温","软化水水箱液位","进料总量","给水总量","用电总量"};
        Set<String> whiteSet = new HashSet<>(Arrays.asList(whiteList));
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



    @Override
    public List<DeviceValue> getFaultDeviceValue(int userId) {
        List<DeviceValue> result = new ArrayList<>();

        try {
            // 1. 根据用户ID查询用户信息
            QueryWrapper<User> queryWrapper=new QueryWrapper<>();
            queryWrapper.eq("user_id",userId);
            User user=userService.getOne(queryWrapper);
            String organization=user.getOrganization();

            // 2. 根据组织查询项目列表
            QueryWrapper<ProjectList> projectWrapper = new QueryWrapper<>();
            projectWrapper.eq("organization", organization);
            List<ProjectList> projects = projectService.list(projectWrapper);

            if (projects.isEmpty()) {
                logger.warn("未找到匹配的项目，organization: {}", organization);
                return result;
            }

            // 3. 根据项目ID查询设备列表
            List<Integer> projectIds = projects.stream().map(ProjectList::getId).collect(Collectors.toList());
            QueryWrapper<DeviceList> deviceWrapper = new QueryWrapper<>();
            deviceWrapper.in("project", projectIds);
            List<DeviceList> devices = deviceService.list(deviceWrapper);

            if (devices.isEmpty()) {
                logger.warn("未找到匹配的设备，projectIds: {}", projectIds);
                return result;
            }

            // 4. 根据设备ID查询变量并过滤"故障"数据，且value值为1
            List<Integer> deviceIds = devices.stream().map(DeviceList::getId).collect(Collectors.toList());
            QueryWrapper<DeviceValue> valueWrapper = new QueryWrapper<>();
            valueWrapper.in("device_id", deviceIds);
            valueWrapper.like("notes", "故障");
            valueWrapper.eq("value", BigDecimal.ONE);  // ✅ 新增：只返回value为1的数据
            result = deviceValueMapper.selectList(valueWrapper);
            System.out.println("返回的数据"+result);

            for (DeviceValue deviceValue : result) {
                WarnMessage warnMessage=new WarnMessage();
                warnMessage.setMessageTitle("现场故障告警！");
                QueryWrapper<DeviceList> deviceListWrapper = new QueryWrapper<>();
                deviceListWrapper.eq("id", deviceValue.getDeviceId());
                DeviceList deviceList=deviceService.getOne(deviceListWrapper);
                String name=deviceList.getName();
                warnMessage.setMessageContent(organization+"的"+name+"发生"+deviceValue.getNotes()+"告警");
                warnMessage.setDeviceId(deviceValue.getDeviceId());
                warnMessage.setValueId(deviceValue.getId());
                warnMessage.setSender("现场");
                warnMessage.setAdvice("请尽快联系现场工人处理！");
                warnMessage.setWarnLevel("紧急");
                warnMessage.setWarnOrganization(organization);
                System.out.println("故障数据"+warnMessage);
                warnMessageMapper.insert(warnMessage);
            }


            logger.info("查询故障数据完成，用户ID: {}, 组织: {}, 结果数量: {}", userId, organization, result.size());

        } catch (Exception e) {
            logger.error("查询故障设备数据异常，userId: {}", userId, e);
        }

        return result;
    }

    @Override
    public boolean setDeviceValue(Integer deviceId, Integer valueId, Float value) {
        try {
            DeviceList dl = deviceService.getById(deviceId);
            if (dl == null) {
                logger.error("设备不存在，deviceId: {}", deviceId);
                return false;
            }

            DeviceValue dv = getById(valueId);
            if (dv == null) {
                logger.error("变量不存在，valueId: {}", valueId);
                return false;
            }

            ProjectList pl = projectService.getById(dl.getProject());
            if (pl == null) {
                logger.error("项目不存在，projectId: {}", dl.getProject());
                return false;
            }

            String topic = "/usr/plcnet/" + pl.getName() + "/edge/d";

            String setMessage = "{\"f\": \"s\", \"d\": [{\"sid\": \"" + dl.getName() + "\", \"pid\": \"" + dv.getName() + "\", \"v\": \"" + Float.toString(value) + "\" }]}";
            logger.info("发送设置指令 - 主题: {}, 消息: {}", topic, setMessage);
            mqttClientService.publish(topic, setMessage, 2, false);

            String queryMessage = "{\"f\": \"q\", \"d\": [{\"sid\": \"" + dl.getName() + "\", \"pid\": \"" + dv.getName() + "\"}]}";
            logger.info("发送查询指令 - 主题: {}, 消息: {}", topic, queryMessage);
            mqttClientService.publish(topic, queryMessage, 2, false);

            return true;
        } catch (Exception e) {
            logger.error("设置设备值失败", e);
            return false;
        }
    }

    @Override
    public boolean queryDeviceValue(Integer deviceId, Integer valueId) {
        try {
            DeviceList dl = deviceService.getById(deviceId);
            if (dl == null) {
                logger.error("设备不存在，deviceId: {}", deviceId);
                return false;
            }

            DeviceValue dv = getById(valueId);
            if (dv == null) {
                logger.error("变量不存在，valueId: {}", valueId);
                return false;
            }

            ProjectList pl = projectService.getById(dl.getProject());
            if (pl == null) {
                logger.error("项目不存在，projectId: {}", dl.getProject());
                return false;
            }

            String topic = "/usr/plcnet/" + pl.getName() + "/edge/d";
            String queryMessage = "{\"f\": \"q\", \"d\": [{\"sid\": \"" + dl.getName() + "\", \"pid\": \"" + dv.getName() + "\"}]}";
            logger.info("发送查询指令 - 主题: {}, 消息: {}", topic, queryMessage);
            mqttClientService.publish(topic, queryMessage, 2, false);

            return true;
        } catch (Exception e) {
            logger.error("查询设备值失败", e);
            return false;
        }
    }

    @Override
    public boolean toggleDeviceValue(Integer deviceId, Integer valueId) {
        try {
            DeviceList dl = deviceService.getById(deviceId);
            if (dl == null) {
                logger.error("设备不存在，deviceId: {}", deviceId);
                return false;
            }

            DeviceValue dv = getById(valueId);
            if (dv == null) {
                logger.error("变量不存在，valueId: {}", valueId);
                return false;
            }

            ProjectList pl = projectService.getById(dl.getProject());
            if (pl == null) {
                logger.error("项目不存在，projectId: {}", dl.getProject());
                return false;
            }

            int newValue = dv.getValue().compareTo(BigDecimal.ZERO) == 0 ? 4 : 0;

            String topic = "/usr/plcnet/" + pl.getName() + "/edge/d";

            String setMessage = "{\"f\": \"s\", \"d\": [{\"sid\": \"" + dl.getName() + "\", \"pid\": \"" + dv.getName() + "\", \"v\": \"" + Integer.toString(newValue) + "\" }]}";
            logger.info("发送开关控制指令 - 主题: {}, 消息: {}", topic, setMessage);
            mqttClientService.publish(topic, setMessage, 2, false);

            String queryMessage = "{\"f\": \"q\", \"d\": [{\"sid\": \"" + dl.getName() + "\", \"pid\": \"" + dv.getName() + "\"}]}";
            logger.info("发送查询指令 - 主题: {}, 消息: {}", topic, queryMessage);
            mqttClientService.publish(topic, queryMessage, 2, false);

            return true;
        } catch (Exception e) {
            logger.error("开关控制失败", e);
            return false;
        }
    }

    @Override
    public boolean toggleDeviceValueByName(Integer deviceId, String valueName) {
        try {
            QueryWrapper<DeviceValue> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("device_id", deviceId);
            queryWrapper.like("notes", valueName);
            List<DeviceValue> deviceValues = list(queryWrapper);

            if (deviceValues.isEmpty()) {
                logger.error("未找到变量，deviceId: {}, valueName: {}", deviceId, valueName);
                return false;
            }

            DeviceValue dv = deviceValues.get(0);
            logger.info("找到变量 - name: {}, deviceId: {}, value: {}", dv.getName(), dv.getDeviceId(), dv.getValue());

            DeviceList dl = deviceService.getById(deviceId);
            if (dl == null) {
                logger.error("设备不存在，deviceId: {}", deviceId);
                return false;
            }

            ProjectList pl = projectService.getById(dl.getProject());
            if (pl == null) {
                logger.error("项目不存在，projectId: {}", dl.getProject());
                return false;
            }

            int newValue = dv.getValue().compareTo(BigDecimal.ZERO) == 0 ? 4 : 0;

            String topic = "/usr/plcnet/" + pl.getName() + "/edge/d";

            String setMessage = "{\"f\": \"s\", \"d\": [{\"sid\": \"" + dl.getName() + "\", \"pid\": \"" + dv.getName() + "\", \"v\": \"" + Integer.toString(newValue) + "\" }]}";
            logger.info("发送开关控制指令 - 主题: {}, 消息: {}", topic, setMessage);
            mqttClientService.publish(topic, setMessage, 2, false);

            String queryMessage = "{\"f\": \"q\", \"d\": [{\"sid\": \"" + dl.getName() + "\", \"pid\": \"" + dv.getName() + "\"}]}";
            logger.info("发送查询指令 - 主题: {}, 消息: {}", topic, queryMessage);
            mqttClientService.publish(topic, queryMessage, 2, false);

            return true;
        } catch (Exception e) {
            logger.error("按名称开关控制失败", e);
            return false;
        }
    }
}
