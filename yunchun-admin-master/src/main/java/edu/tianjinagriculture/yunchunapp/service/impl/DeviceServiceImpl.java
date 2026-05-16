package edu.tianjinagriculture.yunchunapp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.tianjinagriculture.yunchunapp.entity.DeviceList;
import edu.tianjinagriculture.yunchunapp.entity.ProjectList;
import edu.tianjinagriculture.yunchunapp.entity.User;
import edu.tianjinagriculture.yunchunapp.mapper.DeviceMapper;
import edu.tianjinagriculture.yunchunapp.mapper.ProjectMapper;
import edu.tianjinagriculture.yunchunapp.service.DeviceService;
import edu.tianjinagriculture.yunchunapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DeviceServiceImpl extends ServiceImpl<DeviceMapper,DeviceList> implements DeviceService {

    @Autowired
    UserService userService;

    @Autowired
    ProjectMapper projectMapper;

    @Autowired
    DeviceMapper deviceMapper;

    @Override
    public List<DeviceList> getDeviceList(Integer userId) {

        List<DeviceList> list=new ArrayList<DeviceList>();
        User user=userService.getUserById(userId);
        String organization=user.getOrganization();
        System.out.println(user.getOrganization());
        QueryWrapper<ProjectList> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("organization",organization);
        List<ProjectList> projectLists=projectMapper.selectList(queryWrapper);
        //System.out.println(projectLists);
        for(ProjectList projectList:projectLists){
            QueryWrapper<DeviceList> queryWrapper2 = new QueryWrapper<>();
            queryWrapper2.eq("project", projectList.getId());
            List<DeviceList> lists= deviceMapper.selectList(queryWrapper2);
            list.addAll(lists);
        }
        return list;
    }
}
