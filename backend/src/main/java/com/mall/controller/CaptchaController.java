package com.mall.controller;

import com.mall.common.Result;
import com.mall.dto.CaptchaVO;
import com.mall.security.CaptchaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/captcha")
public class CaptchaController {

    @Autowired
    private CaptchaService captchaService;

    @GetMapping
    public Result<CaptchaVO> captcha() {
        return Result.success(captchaService.generate());
    }
}
