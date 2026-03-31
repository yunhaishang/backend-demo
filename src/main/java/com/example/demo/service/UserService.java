package com.example.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.common.result.PageResult;
import com.example.demo.model.dto.UserInfoDTO;
import com.example.demo.model.entity.User;
import com.example.demo.model.vo.UserInfoVO;

public interface UserService extends IService<User> {
    PageResult<UserInfoVO> getUsersPage(int pageNum, int pageSize);

    UserInfoVO getUserById(Long id);

    UserInfoVO getUserByUsername(String username);

    void updateUserById(Long id, UserInfoDTO userInfoDto);

    void removeUserById(Long id);
}