package edu.tianjinagriculture.yunchunapp;

import cn.dev33.satoken.SaManager;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@RequestMapping("/index")
@MapperScan("edu.tianjinagriculture.yunchunapp.mapper")

public class YunChunAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(YunChunAppApplication.class, args);
        System.out.println("启动成功，Sa-Token 配置如下：" + SaManager.getConfig());
    }

    @GetMapping("/hello")
    public String hello(){
        return "hello,spingboot!";
    }
}
