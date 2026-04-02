package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.common.exception.BusinessException;
import com.example.demo.common.result.PageResult;
import com.example.demo.common.result.ResultCode;
import com.example.demo.model.converter.UserConverter;
import com.example.demo.model.dto.UserDTO;
import com.example.demo.model.entity.User;
import com.example.demo.mapper.UserMapper;
import com.example.demo.model.vo.UserVO;
import com.example.demo.service.UserService;
import com.example.demo.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final UserMapper userMapper;
    private final UserConverter userConverter;
    private final RedisUtils redisUtils;

    public UserServiceImpl(UserMapper userMapper, UserConverter userConverter,  RedisUtils redisUtils) {
        this.userMapper = userMapper;
        this.userConverter = userConverter;
        this.redisUtils = redisUtils;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<UserVO> getUsersPage(int pageNum, int pageSize) {
        Page<User> pageParam = new Page<>(pageNum, pageSize);
        Page<User> usersPage = userMapper.selectPage(pageParam, null);
        Page<UserVO> userVOPage = userConverter.toVO(usersPage);

        return PageResult.of(userVOPage);
    }

    @Override
    @Transactional(readOnly = true)
    public UserVO getUserById(Long id) {
        User user = this.getById(id);
        if(user == null) {
            throw new BusinessException(ResultCode.USER_NOT_EXIST);
        }

        return userConverter.toVO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserVO getUserByUsername(String username) {
        if(username == null) {
            throw new BusinessException(ResultCode.PARAM_VALIDATE_FAILED);
        }

        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
        lqw.eq(User::getUsername, username);
        User user = this.getOne(lqw);
        if(user == null) {
            throw new BusinessException(ResultCode.USER_NOT_EXIST);
        }

        return userConverter.toVO(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserById(Long id, UserDTO userDto) {
        User user = this.getById(id);
        if(user == null) {
            throw new BusinessException(ResultCode.USER_NOT_EXIST);
        }

        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPhone(userDto.getPhone());

        this.updateById(user);
        // 用户信息更新后清除缓存，保证缓存与数据的一致性
        redisUtils.del("role:" + user.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeUserById(Long id) {
        User user = this.getById(id);
        if(user == null) {
            throw new BusinessException(ResultCode.USER_NOT_EXIST);
        }

        this.removeById(id);
    }

}
