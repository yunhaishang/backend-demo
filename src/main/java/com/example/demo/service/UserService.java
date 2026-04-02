package com.example.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.common.result.PageResult;
import com.example.demo.model.dto.UserDTO;
import com.example.demo.model.entity.User;
import com.example.demo.model.vo.UserVO;

public interface UserService extends IService<User> {
    PageResult<UserVO> getUsersPage(int pageNum, int pageSize);

    UserVO getUserById(Long id);

    UserVO getUserByUsername(String username);

    void updateUserById(Long id, UserDTO userDto);

    void removeUserById(Long id);
}