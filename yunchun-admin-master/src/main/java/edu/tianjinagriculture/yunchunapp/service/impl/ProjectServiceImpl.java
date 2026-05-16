package edu.tianjinagriculture.yunchunapp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.tianjinagriculture.yunchunapp.entity.ProjectList;
import edu.tianjinagriculture.yunchunapp.mapper.ProjectMapper;
import edu.tianjinagriculture.yunchunapp.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Service
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, ProjectList> implements ProjectService {

    @Autowired
    ProjectMapper projectMapper;
    //根据id拿项目信息

    @Override
    public List<ProjectList> getProjectById(String organization) {
        QueryWrapper<ProjectList> queryWrapper = new QueryWrapper<ProjectList>();
        queryWrapper.eq("organization", organization);
        return projectMapper.selectList(queryWrapper);
    }
}
