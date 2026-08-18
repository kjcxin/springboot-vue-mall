package com.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("category")
public class Category {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 父分类ID，0 为顶级 */
    private Long parentId;

    private String name;

    private Integer sort;

    private Integer status;

    private LocalDateTime createTime;

    /** 子分类（非表字段） */
    @TableField(exist = false)
    private List<Category> children;
}
