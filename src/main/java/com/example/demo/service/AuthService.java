package com.example.demo.service;

import com.example.demo.model.dto.LoginDTO;
import com.example.demo.model.dto.RegisterDTO;

public interface AuthService {
    void register(RegisterDTO registerDto);

    String login(LoginDTO loginDto);

    void logout();
}
