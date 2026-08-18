package com.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("orders")
public class Order {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String orderNo;

    private Long userId;

    private BigDecimal totalAmount;

    /** 0 待支付 1 已支付 2 已取消 */
    private Integer status;

    private String receiverName;

    private String receiverPhone;

    private String receiverAddress;

    private LocalDateTime createTime;

    /** 订单明细（非表字段） */
    @TableField(exist = false)
    private List<OrderItem> items;
}
