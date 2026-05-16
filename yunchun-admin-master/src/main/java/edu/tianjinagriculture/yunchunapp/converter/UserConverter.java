package edu.tianjinagriculture.yunchunapp.converter;

import edu.tianjinagriculture.yunchunapp.dto.UserRegisterDTO;
import edu.tianjinagriculture.yunchunapp.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
//@Mapper
public interface UserConverter {
    // DTO 转 Entity（注册场景
//@Mapping(target = "id", ignore = true) // 忽略 ID，数据库自增
    @Mapping(target = "username")
    @Mapping(target="phone")
    @Mapping(target="organization")
    @Mapping(target="position")
    @Mapping(target = "password", source = "password") // 如果字段名一致可省略
    User toEntity(UserRegisterDTO dto);
}
