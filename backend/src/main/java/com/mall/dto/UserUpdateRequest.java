package com.mall.dto;

import lombok.Data;

@Data
public class UserUpdateRequest {

    private String nickname;

    private String phone;

    private String avatar;
}
