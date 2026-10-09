package edu.tianjinagriculture.yunchunapp.service.impl;


import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import edu.tianjinagriculture.yunchunapp.VO.WarnMessageVO;
import edu.tianjinagriculture.yunchunapp.converter.WarnMessageConverter;
import edu.tianjinagriculture.yunchunapp.dto.WarnMessageDTO;
import edu.tianjinagriculture.yunchunapp.entity.DeviceList;
import edu.tianjinagriculture.yunchunapp.entity.DeviceValue;
import edu.tianjinagriculture.yunchunapp.entity.User;
import edu.tianjinagriculture.yunchunapp.entity.WarnMessage;
import edu.tianjinagriculture.yunchunapp.mapper.WarnMessageMapper;
import edu.tianjinagriculture.yunchunapp.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class WarnMessageServiceImpl extends ServiceImpl<WarnMessageMapper, WarnMessage> implements WarnMessageService {

    private static final Logger logger = LoggerFactory.getLogger(WarnMessageServiceImpl.class);

    @Autowired
    private WarnMessageMapper warnMessageMapper;
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private DeviceValueService deviceValueService ;
    @Autowired
    private WarnMessageConverter warnMessageConverter;
    @Autowired
    private UserService userService;
    @Autowired
    private TencentTtsService tencentTtsService;
    @Autowired
    private COSClient cosClient;
    @Override
    public String addWarnMessage(WarnMessageDTO warnMessageDTO) {
        WarnMessage warnMessage = new WarnMessage();
        warnMessage=warnMessageConverter.toEntity(warnMessageDTO);
        QueryWrapper<DeviceList> queryWrapper = new QueryWrapper<DeviceList>();
        queryWrapper.eq("id", warnMessageDTO.getDeviceId());
        DeviceList d=deviceService.getOne(queryWrapper);
        String deviceName=d.getName();
        QueryWrapper<DeviceValue> queryWrapper1=new QueryWrapper<>();
        queryWrapper1.eq("id", warnMessageDTO.getValueId());
        DeviceValue deviceValue=deviceValueService.getOne(queryWrapper1);
        String deviceValueName=deviceValue.getNotes();
        String messageContent=deviceName+"的"+deviceValueName+"发生了报警！报警原因："+warnMessageDTO.getMessageTitle();
        warnMessage.setMessageContent(messageContent);
        Integer userId=StpUtil.getLoginIdAsInt();
        QueryWrapper<User> queryWrapper2=new QueryWrapper<User>();
        queryWrapper2.eq("user_id", userId);
        User user=userService.getOne(queryWrapper2);
        String sender=user.getUsername();
        String warnOrganization=user.getOrganization();
        if (sender==null) {
            warnMessage.setSender(user.getNickname());
        }else {
            warnMessage.setSender(sender);
        }
        warnMessage.setWarnOrganization(warnOrganization);
        int r1= warnMessageMapper.insert(warnMessage);

        // 生成报警语音并上传COS，存入数据库
        try {
            byte[] audioData = tencentTtsService.synthesize(messageContent);
            String audioUrl = uploadAudio(audioData, String.valueOf(warnMessage.getId()));
            UpdateWrapper<WarnMessage> audioUpdate = new UpdateWrapper<>();
            audioUpdate.eq("id", warnMessage.getId()).set("audio_url", audioUrl);
            warnMessageMapper.update(null, audioUpdate);
        } catch (Exception e) {
            logger.error("报警语音生成失败，warnId={}", warnMessage.getId(), e);
        }

        //下面这部分的代码是将最高值和最低值插入到device_value表中
        UpdateWrapper<DeviceValue> updateWrapper=new UpdateWrapper<DeviceValue>();
        updateWrapper.eq("id", warnMessageDTO.getValueId()).set("high_limit", warnMessageDTO.getHighLimit()).set("low_limit", warnMessageDTO.getLowLimit());
        boolean r2=deviceValueService.update(updateWrapper);
        if (r1==1&&r2){
            return "报警成功";
        }
        return "报警失败";
    }

    @Override
    public List<WarnMessageVO> getAllMessage() {
        Integer userId=StpUtil.getLoginIdAsInt();
        QueryWrapper<User> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        User u= userService.getUserById(userId);
        QueryWrapper<WarnMessage> queryWrapper1=new QueryWrapper<>();
        queryWrapper1.eq("warn_organization", u.getOrganization());
        List<WarnMessage> list=warnMessageMapper.selectList(queryWrapper1);
        List<WarnMessageVO> voList=new ArrayList<WarnMessageVO>();
        for (WarnMessage warnMessage:list) {
            WarnMessageVO vo=new WarnMessageVO();
            vo.setId(warnMessage.getId());
            vo.setMessageTitle(warnMessage.getMessageTitle());
            vo.setMessageContent(warnMessage.getMessageContent());
            vo.setAudioUrl(warnMessage.getAudioUrl());
            vo.setSender(warnMessage.getSender());
            vo.setAdvice(warnMessage.getAdvice());
            vo.setWarnLevel(warnMessage.getWarnLevel());
            vo.setCreateTime(warnMessage.getCreateTime());
            vo.setFeedbackStatus(warnMessage.getFeedbackStatus() == null ? 0 : warnMessage.getFeedbackStatus());
            vo.setHandleStatus(warnMessage.getHandleStatus() == null ? 0 : warnMessage.getHandleStatus());
            // 根据 handler_id 查询 app_user 表获取处理人名称
            if (warnMessage.getHandlerId() != null) {
                User handler = userService.getUserById(warnMessage.getHandlerId());
                if (handler != null) {
                    vo.setHandlerName(handler.getUsername() != null ? handler.getUsername() : handler.getNickname());
                }
            }
            voList.add(vo);
        }
        return voList;
    }

    @Override
    public String delWarnMessage(Integer id) {
       int r=warnMessageMapper.deleteById(id);
        System.out.println(r);
        return  r==1?"删除成功！":"删除失败！";
    }

    @Override
    public String feedback(Integer id, Integer feedbackStatus, Integer handleStatus, Integer handlerId) {
        UpdateWrapper<WarnMessage> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id);
        if (feedbackStatus != null) {
            updateWrapper.set("feedback_status", feedbackStatus);
        }
        if (handleStatus != null) {
            updateWrapper.set("handle_status", handleStatus);
            // 标记已处理时，记录处理人ID
            if (handleStatus == 1 && handlerId != null) {
                updateWrapper.set("handler_id", handlerId);
            }
        }
        boolean success = warnMessageMapper.update(null, updateWrapper) > 0;
        return success ? "反馈成功" : "反馈失败";
    }

    @Value("${tencent.cos.bucket-name}")
    private String bucketName;
    @Override
    public String uploadAudio(byte[] audioData, String alertId) {
        String key = "audio/" + alertId + "_" + System.currentTimeMillis() + ".mp3";

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(audioData.length);
        metadata.setContentType("audio/mpeg"); // 关键：设置正确的 MIME 类型

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(audioData)) {
            PutObjectRequest request = new PutObjectRequest(bucketName, key, inputStream, metadata);
            cosClient.putObject(request);
        } catch (Exception e) {
            throw new RuntimeException("音频上传 COS 失败", e);
        }

        return "https://img.yunchunremote.cn/" + key;
    }
}
