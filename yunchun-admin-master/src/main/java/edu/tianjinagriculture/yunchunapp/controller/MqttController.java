package edu.tianjinagriculture.yunchunapp.controller;

import edu.tianjinagriculture.yunchunapp.commom.Result;
import edu.tianjinagriculture.yunchunapp.service.MqttClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mqtt")
public class MqttController {

    @Autowired
    private MqttClientService mqttClientService;

    /**
     * 检查MQTT连接状态
     */
    @GetMapping("/status")
    public Result<String> getStatus() {
        boolean connected = mqttClientService.isConnected();
        if (connected) {
            return Result.success("MQTT客户端已连接");
        } else {
            return Result.error("MQTT客户端未连接");
        }
    }

    /**
     * 发布MQTT消息
     * @param topic 主题
     * @param message 消息内容
     */
    @PostMapping("/publish")
    public Result<String> publish(
            @RequestParam String topic,
            @RequestParam String message,
            @RequestParam(defaultValue = "0") int qos,
            @RequestParam(defaultValue = "false") boolean retained) {
        
        mqttClientService.publish(topic, message, qos, retained);
        return Result.success("消息发布成功");
    }

    /**
     * 订阅MQTT主题
     * @param topic 主题
     * @param qos 服务质量等级
     */
    @PostMapping("/subscribe")
    public Result<String> subscribe(
            @RequestParam String topic,
            @RequestParam(defaultValue = "0") int qos) {
        
        mqttClientService.subscribe(topic, qos);
        return Result.success("订阅成功");
    }

    /**
     * 取消订阅MQTT主题
     * @param topic 主题
     */
    @PostMapping("/unsubscribe")
    public Result<String> unsubscribe(@RequestParam String topic) {
        mqttClientService.unsubscribe(topic);
        return Result.success("取消订阅成功");
    }

    /**
     * 重新连接MQTT
     */
    @PostMapping("/reconnect")
    public Result<String> reconnect() {
        mqttClientService.disconnect();
        mqttClientService.connect();
        return Result.success("正在重新连接MQTT...");
    }
}
