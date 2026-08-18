package com.mall.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class OrderCreateRequest {

    @NotNull(message = "请选择收货地址")
    private Long addressId;

    /** 勾选的商品ID */
    @NotEmpty(message = "请选择要结算的商品")
    private List<Long> productIds;
}
