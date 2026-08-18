package com.mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mall.entity.Product;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface ProductMapper extends BaseMapper<Product> {

    /** 扣减库存（带库存充足校验，避免超卖） */
    @Update("UPDATE product SET stock = stock - #{quantity} WHERE id = #{id} AND stock >= #{quantity}")
    int deductStock(@Param("id") Long id, @Param("quantity") Integer quantity);

    /** 回补库存（取消订单时） */
    @Update("UPDATE product SET stock = stock + #{quantity} WHERE id = #{id}")
    int restoreStock(@Param("id") Long id, @Param("quantity") Integer quantity);

    /** 增加销量（支付成功时） */
    @Update("UPDATE product SET sales = sales + #{quantity} WHERE id = #{id}")
    int incrementSales(@Param("id") Long id, @Param("quantity") Integer quantity);
}
