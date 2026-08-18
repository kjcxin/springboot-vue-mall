package com.mall.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CartItemVO {

    private Long productId;

    private String name;

    private String image;

    private BigDecimal price;

    private Integer quantity;

    private BigDecimal totalPrice;
}
