package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.common.exception.BusinessException;
import com.example.demo.common.result.ResultCode;
import com.example.demo.common.context.UserContext;
import com.example.demo.mapper.UserMapper;
import com.example.demo.model.converter.UserConverter;
import com.example.demo.model.dto.LoginDTO;
import com.example.demo.model.dto.RegisterDTO;
import com.example.demo.model.entity.User;
import com.example.demo.service.AuthService;
import com.example.demo.service.UserService;
import com.example.demo.utils.JwtUtils;
import com.example.demo.utils.PasswordUtils;
import com.example.demo.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class AuthServiceImpl extends ServiceImpl<UserMapper, User> implements AuthService {

    @Autowired
    private UserService userService;

    @Autowired
    private UserConverter userConverter;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PasswordUtils passwordUtils;

    @Autowired
    private RedisUtils redisUtils;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(RegisterDTO registerDto) {

        // 检查用户名是否已存在
        User user = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, registerDto.getUsername()));
        if(user != null) {
            throw new BusinessException(ResultCode.USER_ALREADY_EXIST);
        }

        // 创建新用户
        user = userConverter.toEntity(registerDto);
        user.setPassword(passwordUtils.encode(registerDto.getPassword()));

        this.save(user);
    }

    @Override
    public String login(LoginDTO loginDto) {
        String username = loginDto.getUsername();
        String password = loginDto.getPassword();

        // 检查用户是否存在
        User user = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if(user == null) {
            throw new BusinessException(ResultCode.USER_NOT_EXIST);
        }

        // 验证密码
        if(passwordUtils.matches(password, user.getPassword())) {
            Long id =  user.getId();
            String token = jwtUtils.generateToken(id, user.getUsername());

            // 将 token 存入 redis，有效期 30 min，实现自动续期
            redisUtils.set("login:token:" + id, token, 60 * 30);

            // 还需要补充将权限存入 redis (用于权限校验)
            // List<String> perms = permissionService.getPermsByUserId(id);
            // redisUtils.set("auth:perms:" + id, perms, 60 * 30);

            return token;
        } else {
            throw new BusinessException(ResultCode.USER_PASSWORD_ERROR);
        }
    }

    @Override
    public void logout() {
        Long id = UserContext.getUserId();
        if (id == null) {
            return;
        }

        String redisKey = "login:token:" + id;
        redisUtils.del(redisKey);

        /*
        redisKey = "auth:perms:" + id;
        redisUtils.del(redisKey);
        */
    }
}
