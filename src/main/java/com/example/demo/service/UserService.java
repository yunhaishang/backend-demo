package com.example.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.model.dto.UserInfoDTO;
import com.example.demo.model.entity.User;
import com.example.demo.model.vo.UserInfoVO;

import java.util.List;

public interface UserService extends IService<User> {
    List<UserInfoVO> getAllUsers();
    UserInfoVO getUserById(Long id);
    UserInfoVO getUserByUsername(String username);
    void insertUser(User user);
    void updateUserById(Long id, UserInfoDTO userInfoDto);
    void removeUserById(Long id);
}