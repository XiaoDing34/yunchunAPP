package edu.tianjinagriculture.yunchunapp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.tianjinagriculture.yunchunapp.entity.ProjectList;

import java.util.List;

public interface ProjectService extends IService<ProjectList> {
    List<ProjectList> getProjectById(String organization);
}
