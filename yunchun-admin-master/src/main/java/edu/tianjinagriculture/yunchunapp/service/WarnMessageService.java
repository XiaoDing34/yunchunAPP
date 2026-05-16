package edu.tianjinagriculture.yunchunapp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.tianjinagriculture.yunchunapp.VO.WarnMessageVO;
import edu.tianjinagriculture.yunchunapp.dto.WarnMessageDTO;
import edu.tianjinagriculture.yunchunapp.entity.WarnMessage;
import org.springframework.stereotype.Service;

import java.util.List;


public interface WarnMessageService extends IService<WarnMessage> {

     String addWarnMessage(WarnMessageDTO warnMessageDTO);

     List<WarnMessageVO> getAllMessage();
}
