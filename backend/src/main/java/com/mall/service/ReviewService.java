package com.mall.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mall.common.BusinessException;
import com.mall.dto.ReviewRequest;
import com.mall.entity.Order;
import com.mall.entity.OrderItem;
import com.mall.entity.Review;
import com.mall.entity.User;
import com.mall.mapper.OrderItemMapper;
import com.mall.mapper.OrderMapper;
import com.mall.mapper.ReviewMapper;
import com.mall.mapper.UserMapper;
import com.mall.security.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    @Autowired
    private ReviewMapper reviewMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    /** 评价（需有已完成订单且含该商品） */
    public void create(ReviewRequest req) {
        Long userId = UserContext.getUserId();
        List<Order> completed = orderMapper.selectList(new LambdaQueryWrapper<Order>()
                .eq(Order::getUserId, userId).eq(Order::getStatus, 3));
        List<Long> orderIds = completed.stream().map(Order::getId).toList();
        if (orderIds.isEmpty()) {
            throw new BusinessException("完成订单后才能评价");
        }
        Long cnt = orderItemMapper.selectCount(new LambdaQueryWrapper<OrderItem>()
                .eq(OrderItem::getProductId, req.getProductId())
                .in(OrderItem::getOrderId, orderIds));
        if (cnt == null || cnt == 0) {
            throw new BusinessException("完成订单后才能评价");
        }

        Review review = new Review();
        review.setProductId(req.getProductId());
        review.setUserId(userId);
        review.setOrderId(req.getOrderId());
        review.setRating(req.getRating());
        review.setContent(req.getContent());
        reviewMapper.insert(review);
    }

    /** 商品评价列表 */
    public List<Review> listByProduct(Long productId) {
        List<Review> reviews = reviewMapper.selectList(new LambdaQueryWrapper<Review>()
                .eq(Review::getProductId, productId).orderByDesc(Review::getCreateTime));
        if (!reviews.isEmpty()) {
            List<Long> userIds = reviews.stream().map(Review::getUserId).distinct().toList();
            Map<Long, User> users = userMapper.selectBatchIds(userIds).stream()
                    .collect(Collectors.toMap(User::getId, u -> u));
            reviews.forEach(r -> {
                User u = users.get(r.getUserId());
                r.setUsername(u != null ? (u.getNickname() != null ? u.getNickname() : u.getUsername()) : "用户");
            });
        }
        return reviews;
    }
}
