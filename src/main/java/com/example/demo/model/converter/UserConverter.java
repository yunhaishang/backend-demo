package com.example.demo.model.converter;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.model.dto.RegisterDTO;
import com.example.demo.model.entity.User;
import com.example.demo.model.vo.UserVO;
import org.mapstruct.Mapper;

// componentModel = "spring" 表示生成的实现类会加上 @Component 注解，可以被 Spring 直接注入
@Mapper(componentModel = "spring")
public interface UserConverter {

    User toEntity(RegisterDTO dto);

    UserVO toVO(User entity);

    Page<UserVO> toVO(Page<User> entityList);
}
