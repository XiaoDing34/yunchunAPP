package edu.tianjinagriculture.yunchunapp.service.impl;


import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.tianjinagriculture.yunchunapp.VO.WarnMessageVO;
import edu.tianjinagriculture.yunchunapp.converter.WarnMessageConverter;
import edu.tianjinagriculture.yunchunapp.dto.WarnMessageDTO;
import edu.tianjinagriculture.yunchunapp.entity.DeviceList;
import edu.tianjinagriculture.yunchunapp.entity.DeviceValue;
import edu.tianjinagriculture.yunchunapp.entity.User;
import edu.tianjinagriculture.yunchunapp.entity.WarnMessage;
import edu.tianjinagriculture.yunchunapp.mapper.WarnMessageMapper;
import edu.tianjinagriculture.yunchunapp.service.DeviceService;
import edu.tianjinagriculture.yunchunapp.service.DeviceValueService;
import edu.tianjinagriculture.yunchunapp.service.UserService;
import edu.tianjinagriculture.yunchunapp.service.WarnMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class WarnMessageServiceImpl extends ServiceImpl<WarnMessageMapper, WarnMessage> implements WarnMessageService {

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
            vo.setMessageTitle(warnMessage.getMessageTitle());
            vo.setMessageContent(warnMessage.getMessageContent());
            vo.setSender(warnMessage.getSender());
            vo.setAdvice(warnMessage.getAdvice());
            vo.setWarnLevel(warnMessage.getWarnLevel());
            vo.setCreateTime(warnMessage.getCreateTime());
            voList.add(vo);
        }
        return voList;
    }
}
