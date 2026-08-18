package com.mall.service;

import com.mall.common.BusinessException;
import com.mall.dto.AddCartRequest;
import com.mall.dto.CartItemVO;
import com.mall.entity.Product;
import com.mall.mapper.ProductMapper;
import com.mall.security.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 购物车：基于 Redis Hash 存储（key = mall:cart:{userId}, field = productId, value = quantity）
 */
@Service
public class CartService {

    private static final String CART_KEY_PREFIX = "mall:cart:";

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ProductMapper productMapper;

    private String cartKey(Long userId) {
        return CART_KEY_PREFIX + userId;
    }

    public void add(AddCartRequest req) {
        Long userId = UserContext.getUserId();
        Product product = productMapper.selectById(req.getProductId());
        if (product == null || product.getStatus() == null || product.getStatus() != 1) {
            throw new BusinessException("商品不存在或已下架");
        }
        String key = cartKey(userId);
        Long incr = redisTemplate.opsForHash().increment(key, String.valueOf(req.getProductId()), req.getQuantity());
        if (incr != null && incr > product.getStock()) {
            // 回滚本次增加，避免超过库存
            redisTemplate.opsForHash().increment(key, String.valueOf(req.getProductId()), -req.getQuantity());
            throw new BusinessException("库存不足");
        }
    }

    public List<CartItemVO> list() {
        Long userId = UserContext.getUserId();
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(cartKey(userId));
        List<CartItemVO> items = new ArrayList<>();
        for (Map.Entry<Object, Object> e : entries.entrySet()) {
            Long productId = Long.valueOf(e.getKey().toString());
            Integer quantity = Integer.valueOf(e.getValue().toString());
            Product product = productMapper.selectById(productId);
            if (product == null) {
                continue;
            }
            items.add(CartItemVO.builder()
                    .productId(productId)
                    .name(product.getName())
                    .image(product.getImage())
                    .price(product.getPrice())
                    .quantity(quantity)
                    .totalPrice(product.getPrice().multiply(BigDecimal.valueOf(quantity)))
                    .build());
        }
        return items;
    }

    public void updateQuantity(Long productId, Integer quantity) {
        Long userId = UserContext.getUserId();
        String key = cartKey(userId);
        if (quantity == null || quantity <= 0) {
            redisTemplate.opsForHash().delete(key, String.valueOf(productId));
            return;
        }
        redisTemplate.opsForHash().put(key, String.valueOf(productId), String.valueOf(quantity));
    }

    public void remove(Long productId) {
        Long userId = UserContext.getUserId();
        redisTemplate.opsForHash().delete(cartKey(userId), String.valueOf(productId));
    }

    public void clear() {
        Long userId = UserContext.getUserId();
        redisTemplate.delete(cartKey(userId));
    }
}
