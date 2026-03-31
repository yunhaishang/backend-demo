package com.example.demo.controller;

import com.example.demo.common.result.PageResult;
import com.example.demo.common.result.Result;
import com.example.demo.model.dto.UserInfoDTO;
import com.example.demo.model.vo.UserInfoVO;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public Result<PageResult<UserInfoVO>> getUsers(@RequestParam(defaultValue = "1") int pageNum,
                                                   @RequestParam(defaultValue = "10") int pageSize) {
        PageResult<UserInfoVO> userInfos = userService.getUsersPage(pageNum, pageSize);
        return Result.success(userInfos);
    }

    @GetMapping("/users/{id}")
    public Result<UserInfoVO> getUserById(@PathVariable Long id) {
        UserInfoVO userInfo = userService.getUserById(id);
        return Result.success(userInfo);
    }

    @GetMapping("/users/search")
    public Result<UserInfoVO> getUserByUsername(@RequestParam String username) {
        UserInfoVO userInfo = userService.getUserByUsername(username);
        return Result.success(userInfo);
    }

    @PutMapping("/users/{id}")
    public Result<Void> updateUserById(@PathVariable Long id,
                                       @Valid @RequestBody UserInfoDTO userInfoDto) {
        userService.updateUserById(id, userInfoDto);
        return Result.success();
    }

    @DeleteMapping("/users/{id}")
    public Result<Void> removeUserById(@PathVariable Long id) {
        userService.removeUserById(id);
        return Result.success();
    }
}
