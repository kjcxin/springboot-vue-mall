package com.mall.controller;

import com.mall.common.Result;
import com.mall.dto.ChangePasswordRequest;
import com.mall.dto.UserUpdateRequest;
import com.mall.entity.User;
import com.mall.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public Result<User> profile() {
        return Result.success(userService.profile());
    }

    @PutMapping("/profile")
    public Result<Void> updateProfile(@RequestBody UserUpdateRequest req) {
        userService.updateProfile(req);
        return Result.success();
    }

    @PutMapping("/password")
    public Result<Void> changePassword(@Valid @RequestBody ChangePasswordRequest req) {
        userService.changePassword(req);
        return Result.success();
    }
}
