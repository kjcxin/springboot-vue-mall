package com.mall.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CaptchaVO {

    /** 验证码 key，登录时回传 */
    private String key;

    /** base64 图片 */
    private String img;
}
