package com.mall.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mall.entity.Order;
import com.mall.mapper.CategoryMapper;
import com.mall.mapper.OrderMapper;
import com.mall.mapper.ProductMapper;
import com.mall.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    public Map<String, Object> stats() {
        Map<String, Object> map = new HashMap<>();
        map.put("productCount", productMapper.selectCount(null));
        map.put("userCount", userMapper.selectCount(null));
        map.put("orderCount", orderMapper.selectCount(null));
        map.put("categoryCount", categoryMapper.selectCount(null));

        // 销售额（已支付及之后状态）
        List<Order> paid = orderMapper.selectList(new LambdaQueryWrapper<Order>().in(Order::getStatus, 1, 2, 3));
        map.put("salesAmount", paid.stream().map(Order::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add));

        // 今日订单
        map.put("todayOrders", orderMapper.selectCount(new LambdaQueryWrapper<Order>()
                .ge(Order::getCreateTime, LocalDate.now().atStartOfDay())));

        // 待发货
        map.put("pendingShip", orderMapper.selectCount(new LambdaQueryWrapper<Order>().eq(Order::getStatus, 1)));

        // 最近订单
        map.put("recentOrders", orderMapper.selectList(new LambdaQueryWrapper<Order>()
                .orderByDesc(Order::getCreateTime).last("LIMIT 5")));
        return map;
    }
}
