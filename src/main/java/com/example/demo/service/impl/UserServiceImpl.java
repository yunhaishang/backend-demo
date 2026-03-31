package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.common.exception.BusinessException;
import com.example.demo.common.result.PageResult;
import com.example.demo.common.result.ResultCode;
import com.example.demo.model.converter.UserConverter;
import com.example.demo.model.dto.UserInfoDTO;
import com.example.demo.model.entity.User;
import com.example.demo.mapper.UserMapper;
import com.example.demo.model.vo.UserInfoVO;
import com.example.demo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final UserMapper userMapper;
    private final UserConverter userConverter;

    public UserServiceImpl(UserMapper userMapper, UserConverter userConverter) {
        this.userMapper = userMapper;
        this.userConverter = userConverter;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<UserInfoVO> getUsersPage(int pageNum, int pageSize) {
        Page<User> pageParam = new Page<>(pageNum, pageSize);
        Page<User> usersPage = userMapper.selectPage(pageParam, null);
        Page<UserInfoVO> userVOPage = userConverter.toVO(usersPage);

        return PageResult.of(userVOPage);
    }

    @Override
    @Transactional(readOnly = true)
    public UserInfoVO getUserById(Long id) {
        User user = this.getById(id);
        if(user == null) {
            throw new BusinessException(ResultCode.USER_NOT_EXIST);
        }

        return userConverter.toVO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserInfoVO getUserByUsername(String username) {
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
    public void updateUserById(Long id, UserInfoDTO userInfoDto) {
        User user = this.getById(id);
        if(user == null) {
            throw new BusinessException(ResultCode.USER_NOT_EXIST);
        }

        user = userConverter.toEntity(userInfoDto);
        user.setId(id);

        this.updateById(user);
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
