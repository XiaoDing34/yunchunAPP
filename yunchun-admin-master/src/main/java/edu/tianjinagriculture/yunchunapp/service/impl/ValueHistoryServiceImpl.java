package edu.tianjinagriculture.yunchunapp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.tianjinagriculture.yunchunapp.entity.ValueHistory;
import edu.tianjinagriculture.yunchunapp.mapper.ValueHistoryMapper;
import edu.tianjinagriculture.yunchunapp.service.ValueHistoryService;
import org.springframework.stereotype.Service;

@Service
public class ValueHistoryServiceImpl extends ServiceImpl<ValueHistoryMapper, ValueHistory> implements ValueHistoryService {
}
