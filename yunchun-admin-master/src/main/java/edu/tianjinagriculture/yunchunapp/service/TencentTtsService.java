package edu.tianjinagriculture.yunchunapp.service;

import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.tts.v20190823.TtsClient;
import com.tencentcloudapi.tts.v20190823.models.TextToVoiceRequest;
import com.tencentcloudapi.tts.v20190823.models.TextToVoiceResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.UUID;

@Service
public class TencentTtsService {

    @Value("${tencent.tts.secret-id}")
    private String secretId;
    @Value("${tencent.tts.secret-key}")
    private String secretKey;
    @Value("${tencent.tts.region}")
    private String region;
    @Value("${tencent.tts.app-id}")
    private Long appId;

    /**
     * 将文本合成为音频字节数组（MP3格式）
     */
    public byte[] synthesize(String text) {
        try {
            Credential cred = new Credential(secretId, secretKey);
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("tts.tencentcloudapi.com");
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);

            TtsClient client = new TtsClient(cred, region, clientProfile);

            TextToVoiceRequest req = new TextToVoiceRequest();
            req.setText(text);
            req.setSessionId(UUID.randomUUID().toString());
            req.setVolume(5f);      // 音量 0-10
            req.setSpeed(0f);       // 语速 -2 到 6，0为正常
            req.setProjectId(0L);
            req.setModelType(1L);   // 1: 基础模型
            req.setVoiceType(101001L); // 音色ID，101001 为"智瑜"女声

            TextToVoiceResponse resp = client.TextToVoice(req);

            // 腾讯云 TTS 返回的是 Base64 编码的音频数据
            String base64Audio = resp.getAudio();
            return Base64.getDecoder().decode(base64Audio);

        } catch (Exception e) {
            throw new RuntimeException("语音合成失败", e);
        }
    }
}
