package edu.tianjinagriculture.yunchunapp.controller;

import edu.tianjinagriculture.yunchunapp.VO.WarnMessageVO;
import edu.tianjinagriculture.yunchunapp.commom.Result;
import edu.tianjinagriculture.yunchunapp.dto.WarnMessageDTO;
import edu.tianjinagriculture.yunchunapp.entity.WarnMessage;
import edu.tianjinagriculture.yunchunapp.service.WarnMessageService;
import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/warnMessage")
public class WarnMessageController {

    @Autowired
    private WarnMessageService warnMessageService;

    @PostMapping("/addWarnMessage")
    public Result<String> addWarnMessage(@RequestBody WarnMessageDTO warnMessageDTO) {
        //System.out.println(warnMessageDTO);
        return Result.success(warnMessageService.addWarnMessage(warnMessageDTO));
    }

    @GetMapping("/getAllMessage")
    public Result<List<WarnMessageVO>> getAllMessage() {
        return Result.success(warnMessageService.getAllMessage());
    }
}
