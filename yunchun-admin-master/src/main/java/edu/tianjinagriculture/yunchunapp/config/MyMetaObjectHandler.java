package edu.tianjinagriculture.yunchunapp.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;

// java example
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("开始插入填充...");
        //this.strictInsertFill(metaObject, "createUserId", Long.class, 123456L)
        this.strictInsertFill(metaObject, "createTime", Date.class, new Date());
    }


    //暂时未用到，后续用到的话可以尝试使用该注解
    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("开始更新填充...");
        //this.strictInsertFill(metaObject, "updateUserId", Long.class, 123456L)
        this.fillStrategy(metaObject, "updateTime", new Date());
    }
}
