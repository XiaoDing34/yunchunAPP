package edu.tianjinagriculture.yunchunapp.controller;

import cn.dev33.satoken.stp.StpUtil;
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
        System.out.println(warnMessageDTO);
        //System.out.println(warnMessageDTO);
        return Result.success(warnMessageService.addWarnMessage(warnMessageDTO));
    }

    @GetMapping("/getAllMessage")
    public Result<List<WarnMessageVO>> getAllMessage() {
        return Result.success(warnMessageService.getAllMessage());
    }

    @DeleteMapping("/delWarnMessage")
    public Result<String> delMessage(Integer id){
        System.out.println(id);
        String r=warnMessageService.delWarnMessage(id);
        System.out.println(r);
        if (r.equals("删除成功！")){
            return Result.success(r);
        }
        return Result.error(r);
    }

    @PutMapping("/feedback")
    public Result<String> feedback(Integer id, Integer feedbackStatus, Integer handleStatus) {
        Integer handlerId = StpUtil.getLoginIdAsInt();
        String r = warnMessageService.feedback(id, feedbackStatus, handleStatus, handlerId);
        if (r.equals("反馈成功")) {
            return Result.success(r);
        }
        return Result.error(r);
    }
}
