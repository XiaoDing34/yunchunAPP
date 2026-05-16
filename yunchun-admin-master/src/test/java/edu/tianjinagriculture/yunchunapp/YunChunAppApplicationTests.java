package edu.tianjinagriculture.yunchunapp;

import edu.tianjinagriculture.yunchunapp.mapper.UserMapper;
import edu.tianjinagriculture.yunchunapp.entity.User;   // 修正导入
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import java.util.List;

@SpringBootTest
class YunChunAppApplicationTests {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void testSelect() {
        System.out.println("----- selectAll method test ------");
        List<User> userList = userMapper.selectList(null);
        //Assert.isTrue(userList.size() == 5, "用户数量不为5");
        userList.forEach(System.out::println);
    }
}