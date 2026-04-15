package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.demo.common.exception.BusinessException;
import com.example.demo.common.context.UserContext;
import com.example.demo.model.converter.UserConverter;
import com.example.demo.model.dto.LoginDTO;
import com.example.demo.model.dto.RegisterDTO;
import com.example.demo.model.entity.User;
import com.example.demo.service.AuthService;
import com.example.demo.service.UserService;
import com.example.demo.utils.JwtUtils;
import com.example.demo.utils.PasswordUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final UserConverter userConverter;
    private final JwtUtils jwtUtils;
    private final PasswordUtils passwordUtils;
    private final RedisTemplate<String, Object> redisTemplate;

    AuthServiceImpl(UserService userService, UserConverter userConverter, JwtUtils jwtUtils,PasswordUtils passwordUtils, RedisTemplate<String, Object> redisTemplate) {
        this.userService = userService;
        this.userConverter = userConverter;
        this.jwtUtils = jwtUtils;
        this.passwordUtils = passwordUtils;
        this.redisTemplate = redisTemplate;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(RegisterDTO registerDto) {

        // 检查用户名是否已存在
        User user = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, registerDto.getUsername()));
        if(user != null) {
            throw new BusinessException(500, "用户已存在");
        }

        // 创建新用户
        user = userConverter.toEntity(registerDto);
        user.setPassword(passwordUtils.encode(registerDto.getPassword()));
        user.setRole("user");

        userService.save(user);
    }

    @Override
    public String login(LoginDTO loginDto) {
        String username = loginDto.getUsername();
        String password = loginDto.getPassword();

        // 检查用户是否存在
        User user = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if(user == null) {
            throw new BusinessException(500, "用户不存在");
        }

        // 验证密码
        if(passwordUtils.matches(password, user.getPassword())) {
            Long id =  user.getId();
            String token = jwtUtils.generateToken(id, user.getUsername());

            // 将 token 存入 redis，有效期 30 min，实现自动续期
            redisTemplate.opsForValue().set("login:token:" + id, token, 30, TimeUnit.MINUTES);

            // 将角色存入 redis (用于权限校验)，有效期和 token 相同
            String role = user.getRole();
            redisTemplate.opsForValue().set("role:" + id, role, 30, TimeUnit.MINUTES);

            return token;
        } else {
            throw new BusinessException(500, "密码错误");
        }
    }

    @Override
    public void logout() {
        Long id = UserContext.getUserId();
        if (id == null) {
            return;
        }

        String redisKey = "login:token:" + id;
        redisTemplate.delete(redisKey);

        redisKey = "role:" + id;
        redisTemplate.delete(redisKey);
    }
}
