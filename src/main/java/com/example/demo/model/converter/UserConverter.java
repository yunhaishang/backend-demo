package com.example.demo.model.converter;

import com.example.demo.model.dto.RegisterDTO;
import com.example.demo.model.dto.UserInfoDTO;
import com.example.demo.model.entity.User;
import com.example.demo.model.vo.UserInfoVO;
import org.mapstruct.Mapper;

import java.util.List;

// componentModel = "spring" 表示生成的实现类会加上 @Component 注解，可以被 Spring 直接注入
@Mapper(componentModel = "spring")
public interface UserConverter {

    User toEntity(RegisterDTO dto);

    User toEntity(UserInfoDTO dto);

    UserInfoVO toVO(User entity);

    List<UserInfoVO> toVO(List<User> entityList);
}
