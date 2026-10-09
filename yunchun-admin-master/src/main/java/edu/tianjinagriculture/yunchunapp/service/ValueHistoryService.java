package edu.tianjinagriculture.yunchunapp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.tianjinagriculture.yunchunapp.dto.ValueHistoryDTO;
import edu.tianjinagriculture.yunchunapp.entity.ValueHistory;
import org.springframework.stereotype.Service;

import java.util.List;


public interface ValueHistoryService extends IService<ValueHistory> {
    List<ValueHistory> getValueByValueIdAndTime(ValueHistoryDTO valueHistoryDTO);
}
