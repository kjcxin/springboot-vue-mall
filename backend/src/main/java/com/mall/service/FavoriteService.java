package com.mall.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mall.entity.Favorite;
import com.mall.entity.Product;
import com.mall.mapper.FavoriteMapper;
import com.mall.mapper.ProductMapper;
import com.mall.security.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FavoriteService {

    @Autowired
    private FavoriteMapper favoriteMapper;

    @Autowired
    private ProductMapper productMapper;

    public void add(Long productId) {
        Long userId = UserContext.getUserId();
        Long cnt = favoriteMapper.selectCount(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId).eq(Favorite::getProductId, productId));
        if (cnt == null || cnt == 0) {
            Favorite f = new Favorite();
            f.setUserId(userId);
            f.setProductId(productId);
            favoriteMapper.insert(f);
        }
    }

    public void remove(Long productId) {
        Long userId = UserContext.getUserId();
        favoriteMapper.delete(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId).eq(Favorite::getProductId, productId));
    }

    public boolean isFavorite(Long productId) {
        Long userId = UserContext.getUserId();
        Long cnt = favoriteMapper.selectCount(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId).eq(Favorite::getProductId, productId));
        return cnt != null && cnt > 0;
    }

    public List<Favorite> list() {
        Long userId = UserContext.getUserId();
        List<Favorite> list = favoriteMapper.selectList(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId).orderByDesc(Favorite::getCreateTime));
        if (!list.isEmpty()) {
            List<Long> ids = list.stream().map(Favorite::getProductId).toList();
            Map<Long, Product> map = productMapper.selectBatchIds(ids).stream()
                    .collect(Collectors.toMap(Product::getId, p -> p));
            list.forEach(f -> f.setProduct(map.get(f.getProductId())));
        }
        return list;
    }
}
