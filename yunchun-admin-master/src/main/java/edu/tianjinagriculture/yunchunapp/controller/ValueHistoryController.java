package edu.tianjinagriculture.yunchunapp.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import edu.tianjinagriculture.yunchunapp.commom.Result;
import edu.tianjinagriculture.yunchunapp.entity.ValueHistory;
import edu.tianjinagriculture.yunchunapp.service.ValueHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/history")
public class ValueHistoryController {

    @Autowired
    private ValueHistoryService valueHistoryService;


    @GetMapping("/getValueByValueId")
    public Result<List<ValueHistory>> getValueByValueId(@RequestParam Integer valueId){
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("value_id", valueId);
        queryWrapper.orderByDesc("update_time");
        queryWrapper.last("limit 24");
        List<ValueHistory> list = valueHistoryService.list(queryWrapper);
        Collections.reverse(list);
        return Result.success(list);
    }
}

