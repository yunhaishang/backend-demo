package com.example.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.model.dto.LoginDTO;
import com.example.demo.model.dto.RegisterDTO;
import com.example.demo.model.entity.User;

public interface AuthService extends IService<User> {
    void register(RegisterDTO registerDto);

    String login(LoginDTO loginDto);

    void logout();
}
