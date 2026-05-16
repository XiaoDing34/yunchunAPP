package edu.tianjinagriculture.yunchunapp.service.impl;

import cn.dev33.satoken.stp.StpInterface;
import edu.tianjinagriculture.yunchunapp.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class StpInterfaceImpl implements StpInterface {

    @Autowired
    private UserMapper userMapper;
    /**
     * 返回一个账号所拥有的权限码集合
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {

        // 本 list 仅做模拟，实际项目中要根据具体业务逻辑来查询权限
        System.out.println(userMapper.selectPermissionById((Integer) loginId));
        List<String> list = userMapper.selectPermissionById((Integer) loginId);;
//        list.add("101");
//        list.add("user.add");
//        list.add("user.update");
//        list.add("user.get");
//        list.add("user.list");
//        // list.add("user.delete");
//        list.add("art.*");
        System.out.println("list:" + list);
        return list;
    }

    /**
     * 返回一个账号所拥有的角色标识集合 (权限与角色可分开校验)
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {

        loginId = Integer.parseInt((String) loginId);
        String role=userMapper.selectRoleById((Integer) loginId);
        System.out.println("role:" + role);
        // 本 list 仅做模拟，实际项目中要根据具体业务逻辑来查询角色
        List<String> list = new ArrayList<String>();
        list.add(role);
        //list.add("admin");
        //list.add("super-admin");
        return list;
    }
}
