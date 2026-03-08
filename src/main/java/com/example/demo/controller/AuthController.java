package com.example.demo.controller;

import com.example.demo.common.result.Result;
import com.example.demo.model.dto.LoginDTO;
import com.example.demo.model.dto.RegisterDTO;
import com.example.demo.service.AuthService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/auth/register")
    public Result<Void> register(@Valid @RequestBody RegisterDTO registerDto) {
        authService.register(registerDto);
        return Result.success();
    }

    @PostMapping("/auth/login")
    public Result<String> login(@Valid @RequestBody LoginDTO loginDto) {
        String token = authService.login(loginDto);
        return Result.success(token);
    }

    @PostMapping("/auth/logout")
    public Result<Void> logout() {
        authService.logout();
        return Result.success();
    }
}
