package edu.tianjinagriculture.yunchunapp.converter;

import edu.tianjinagriculture.yunchunapp.dto.UserRegisterDTO;
import edu.tianjinagriculture.yunchunapp.dto.WarnMessageDTO;
import edu.tianjinagriculture.yunchunapp.entity.User;
import edu.tianjinagriculture.yunchunapp.entity.WarnMessage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WarnMessageConverter {
    @Mapping(target = "messageTitle")
    @Mapping(target = "deviceId")
    @Mapping(target = "valueId")
    @Mapping(target="advice")
    @Mapping(target = "warnLevel")
    //@Mapping(target = "password", source = "password") // 如果字段名一致可省略
    WarnMessage toEntity(WarnMessageDTO dto);
}
