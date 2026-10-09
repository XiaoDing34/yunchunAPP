package edu.tianjinagriculture.yunchunapp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.tianjinagriculture.yunchunapp.entity.ValueHistory;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

public interface ValueHistoryMapper extends BaseMapper<ValueHistory> {

    @Select("SELECT * FROM value_history " +
            "WHERE update_time BETWEEN #{startTime} AND #{endTime} " +
            "AND EXTRACT(MINUTE FROM update_time) = 0 " +  // PostgreSQL 语法
            "AND value_id = #{valueId} " +
            "ORDER BY update_time ASC")
    List<ValueHistory> getValueByValueIdAndTime(@Param("startTime") LocalDateTime startTime,
                                     @Param("endTime") LocalDateTime endTime,
                                     @Param("valueId") Integer valueId);
}
