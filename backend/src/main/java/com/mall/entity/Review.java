package com.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("review")
public class Review {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long orderId;

    private Long productId;

    private Long userId;

    /** 评分 1-5 */
    private Integer rating;

    private String content;

    private LocalDateTime createTime;

    /** 评价人昵称（非表字段） */
    @TableField(exist = false)
    private String username;
}
