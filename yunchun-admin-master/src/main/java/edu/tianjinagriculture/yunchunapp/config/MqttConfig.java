package edu.tianjinagriculture.yunchunapp.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "mqtt")
public class MqttConfig {

    private String brokerUrl = "tcp://150.158.107.172:1883";
    private String username = "liu";
    private String password = "Yunchunerp!1";
    private String clientId = "spring-boot-mqtt-client";
    private int connectionTimeout = 10;
    private int keepAliveInterval = 60;
    private boolean cleanSession = true;
}
