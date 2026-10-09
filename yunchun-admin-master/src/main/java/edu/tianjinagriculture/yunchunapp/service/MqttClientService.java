package edu.tianjinagriculture.yunchunapp.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.tianjinagriculture.yunchunapp.VO.YouRenYunDataVO;
import edu.tianjinagriculture.yunchunapp.config.MqttConfig;
import edu.tianjinagriculture.yunchunapp.entity.DeviceList;
import edu.tianjinagriculture.yunchunapp.entity.DeviceValue;
import edu.tianjinagriculture.yunchunapp.entity.ProjectList;
import edu.tianjinagriculture.yunchunapp.tool.YouRenYunDataSimple;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
public class MqttClientService {

    private static final Logger logger = LoggerFactory.getLogger(MqttClientService.class);

    @Autowired
    private MqttConfig mqttConfig;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ApplicationContext applicationContext;

    private DeviceValueService deviceValueService;

    private MqttClient mqttClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void init() {
        this.deviceValueService = applicationContext.getBean(DeviceValueService.class);
        connect();
    }

    @PreDestroy
    public void destroy() {
        disconnect();
    }

    public void connect() {
        try {
            String clientId = mqttConfig.getClientId() + "_" + System.currentTimeMillis();
            
            mqttClient = new MqttClient(mqttConfig.getBrokerUrl(), clientId, new MemoryPersistence());

            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName(mqttConfig.getUsername());
            options.setPassword(mqttConfig.getPassword().toCharArray());
            options.setConnectionTimeout(mqttConfig.getConnectionTimeout());
            options.setKeepAliveInterval(mqttConfig.getKeepAliveInterval());
            options.setCleanSession(mqttConfig.isCleanSession());
            options.setAutomaticReconnect(true);

            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    logger.error("MQTT连接丢失: {}", cause.getMessage());
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    String payload = new String(message.getPayload());
                    handleMessage(topic, payload);
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    logger.debug("消息发送完成");
                }
            });

            mqttClient.connect(options);
            logger.info("MQTT连接成功 - Broker: {}", mqttConfig.getBrokerUrl());

            subscribeAllProjectTopics();

        } catch (MqttException e) {
            logger.error("MQTT连接失败: {}", e.getMessage(), e);
        }
    }

    public void disconnect() {
        if (mqttClient != null && mqttClient.isConnected()) {
            try {
                mqttClient.disconnect();
                logger.info("MQTT连接已断开");
            } catch (MqttException e) {
                logger.error("MQTT断开连接失败: {}", e.getMessage());
            }
        }
    }

    public void subscribe(String topic, int qos) {
        if (mqttClient != null && mqttClient.isConnected()) {
            try {
                mqttClient.subscribe(topic, qos);
                logger.info("订阅主题成功: {}", topic);
            } catch (MqttException e) {
                logger.error("订阅主题失败: {}", e.getMessage());
            }
        } else {
            logger.warn("MQTT客户端未连接，无法订阅主题");
        }
    }

    public void unsubscribe(String topic) {
        if (mqttClient != null && mqttClient.isConnected()) {
            try {
                mqttClient.unsubscribe(topic);
                logger.info("取消订阅主题: {}", topic);
            } catch (MqttException e) {
                logger.error("取消订阅失败: {}", e.getMessage());
            }
        }
    }

    public void subscribeAllProjectTopics() {
        if (mqttClient != null && mqttClient.isConnected()) {
            try {
                List<ProjectList> projects = projectService.list();
                for (ProjectList project : projects) {
                    String projectName = project.getName();
                    
                    String dataTopic = "/usr/plcnet/" + projectName + "/edge/u";
                    String heartbeatTopic = "/usr/plcnet/" + projectName + "/heart/u";
                    String downlinkTopic = "/usr/plcnet/" + projectName + "/edge/d";
                    
                    mqttClient.subscribe(dataTopic, 1);
                    logger.info("订阅上行主题成功: {}", dataTopic);
                    
                    mqttClient.subscribe(heartbeatTopic, 1);
                    logger.info("订阅心跳主题成功: {}", heartbeatTopic);
                    
                    mqttClient.subscribe(downlinkTopic, 1);
                    logger.info("订阅下行主题成功: {}", downlinkTopic);
                }
            } catch (MqttException e) {
                logger.error("订阅项目主题失败: {}", e.getMessage());
            }
        }
    }

    public void subscribeProjectTopic(String projectName) {
        if (mqttClient != null && mqttClient.isConnected()) {
            try {
                String dataTopic = "/usr/plcnet/" + projectName + "/edge/u";
                String heartbeatTopic = "/usr/plcnet/" + projectName + "/heart/u";
                String downlinkTopic = "/usr/plcnet/" + projectName + "/edge/d";
                
                mqttClient.subscribe(dataTopic, 1);
                logger.info("订阅上行主题成功: {}", dataTopic);
                
                mqttClient.subscribe(heartbeatTopic, 1);
                logger.info("订阅心跳主题成功: {}", heartbeatTopic);
                
                mqttClient.subscribe(downlinkTopic, 1);
                logger.info("订阅下行主题成功: {}", downlinkTopic);
            } catch (MqttException e) {
                logger.error("订阅项目主题失败: {}", e.getMessage());
            }
        }
    }

    public void publish(String topic, String message, int qos, boolean retained) {
        if (mqttClient != null && mqttClient.isConnected()) {
            try {
                MqttMessage mqttMessage = new MqttMessage(message.getBytes());
                mqttMessage.setQos(qos);
                mqttMessage.setRetained(retained);
                mqttClient.publish(topic, mqttMessage);
                logger.info("发送MQTT消息 - 主题: {}, 消息: {}", topic, message);
            } catch (MqttException e) {
                logger.error("发送MQTT消息失败: {}", e.getMessage());
            }
        } else {
            logger.warn("MQTT客户端未连接，无法发送消息");
        }
    }

    public void publish(String topic, String message) {
        publish(topic, message, 0, false);
    }

    private void handleMessage(String topic, String payload) {
        if (topic.contains("/edge/u")) {
            handleEdgeUpload(topic, payload);
        } else if (topic.contains("/heart/u")) {
            handleHeartbeat(topic);
        } else if (topic.contains("/edge/d")) {
            handleDownlinkResponse(topic, payload);
        } else {
            logger.info("未处理的主题: {}", topic);
        }
    }

    private void handleDownlinkResponse(String topic, String payload) {
        logger.info("接收到下行主题响应 - 主题: {}, 内容: {}", topic, payload);
        // 解析设备对控制指令的响应
        try {
            String[] topicParts = topic.split("/");
            if (topicParts.length >= 4) {
                String projectName = topicParts[3];
                logger.info("项目 {} 收到下行响应", projectName);
            }
            // TODO: 根据实际响应格式解析并处理
        } catch (Exception e) {
            logger.error("解析下行响应失败", e);
        }
    }

    private void handleEdgeUpload(String topic, String payload) {
        try {
            YouRenYunDataVO dataVO = objectMapper.readValue(payload, new TypeReference<YouRenYunDataVO>() {});
            //logger.info("接收到的数据: {}", dataVO);
            if (dataVO.getD() == null || dataVO.getD().isEmpty()) {
                logger.warn("消息数据为空");
                return;
            }

            String[] topicParts = topic.split("/");
            if (topicParts.length < 4) {
                logger.error("主题格式不正确: {}", topic);
                return;
            }
            String projectName = topicParts[3];

            for (YouRenYunDataSimple data : dataVO.getD()) {
                updateDeviceValue(projectName, data.getSid(), data.getPid(), data.getV());
            }
        } catch (Exception e) {
            logger.error("解析设备上传数据失败", e);
        }
    }

    private void updateDeviceValue(String projectName, String deviceName, String variableName, String value) {
        try {
            List<DeviceList> devices = deviceService.lambdaQuery()
                    .eq(DeviceList::getName, deviceName)
                    .list();

            DeviceList device;
            if (devices.isEmpty()) {
                logger.info("设备不存在，创建新设备: {}", deviceName);
                
                List<ProjectList> projects = projectService.lambdaQuery()
                        .eq(ProjectList::getName, projectName)
                        .list();
                
                if (projects.isEmpty()) {
                    logger.warn("项目不存在，无法创建设备: {}", projectName);
                    return;
                }
                
                ProjectList project = projects.get(0);
                
                device = new DeviceList();
                device.setName(deviceName);
                device.setProject(project.getId());
                device.setState("在线");
                device.setNotes(deviceName);
                device.setDataNumber(0);
                deviceService.save(device);
            } else {
                device = devices.get(0);
            }

            List<DeviceValue> values = deviceValueService.lambdaQuery()
                    .eq(DeviceValue::getDeviceId, device.getId())
                    .eq(DeviceValue::getName, variableName)
                    .list();

            if (values.isEmpty()) {
                DeviceValue newDeviceValue = new DeviceValue();
                newDeviceValue.setDeviceId(device.getId());
               newDeviceValue.setName(variableName);
                newDeviceValue.setValue(new BigDecimal(value));
               newDeviceValue.setUpdateTime(new Date(System.currentTimeMillis()));
                deviceValueService.save(newDeviceValue);
                logger.info("创建新变量: {} - {}", deviceName, variableName);
                return;
            }

           DeviceValue deviceValue = values.get(0);
            deviceValue.setValue(new BigDecimal(value));
           deviceValue.setUpdateTime(new Date(System.currentTimeMillis()));
            deviceValueService.updateById(deviceValue);
        } catch (Exception e) {
            logger.error("更新设备变量值失败", e);
        }
   }

   private void handleHeartbeat(String topic) {
        try {
            String[] topicParts = topic.split("/");
            if (topicParts.length < 4) {
                logger.error("主题格式不正确: {}", topic);
                return;
            }
            String projectName = topicParts[3];

            List<ProjectList> projects = projectService.lambdaQuery()
                    .eq(ProjectList::getName, projectName)
                    .list();

            if (projects.isEmpty()) {
                logger.warn("项目不存在: {}", projectName);
                return;
            }

            ProjectList project = projects.get(0);
            project.setUpdateTime(new Date(System.currentTimeMillis()));
            projectService.updateById(project);

            List<DeviceList> devices = deviceService.lambdaQuery()
                    .eq(DeviceList::getProject, project.getId())
                    .list();

            for (DeviceList device : devices) {
                device.setState("在线");
                deviceService.updateById(device);
            }
        } catch (Exception e) {
            logger.error("处理心跳消息失败", e);
        }
    }

    public boolean isConnected() {
        return mqttClient != null && mqttClient.isConnected();
    }
}
