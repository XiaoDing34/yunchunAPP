package edu.tianjinagriculture.yunchunapp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import edu.tianjinagriculture.yunchunapp.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    String selectRoleById(int loginId);

    List<String> selectPermissionById(int loginId);
}
