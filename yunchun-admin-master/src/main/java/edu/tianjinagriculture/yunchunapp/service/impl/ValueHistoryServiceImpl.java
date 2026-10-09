package edu.tianjinagriculture.yunchunapp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.tianjinagriculture.yunchunapp.dto.ValueHistoryDTO;
import edu.tianjinagriculture.yunchunapp.entity.ValueHistory;
import edu.tianjinagriculture.yunchunapp.mapper.ValueHistoryMapper;
import edu.tianjinagriculture.yunchunapp.service.ValueHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ValueHistoryServiceImpl extends ServiceImpl<ValueHistoryMapper, ValueHistory> implements ValueHistoryService {

    @Autowired
    private ValueHistoryMapper valueHistoryMapper;
    @Override
    public List<ValueHistory> getValueByValueIdAndTime(ValueHistoryDTO valueHistoryDTO) {
        LocalDateTime endTime;
        LocalDateTime startTime;
        if(valueHistoryDTO.getTimeRange().equals("12小时")){
             endTime = LocalDateTime.now();
             startTime = endTime.minusHours(12);
        } else if (valueHistoryDTO.getTimeRange().equals("24小时")) {
             endTime = LocalDateTime.now();
             startTime = endTime.minusHours(24);
        }else {
             endTime = LocalDateTime.now();
             startTime = endTime.minusDays(7);
        }
        List<ValueHistory> list=valueHistoryMapper.getValueByValueIdAndTime(startTime,endTime, valueHistoryDTO.getValueId());
        return list;
    }
}
