package com.mall.controller;

import com.mall.common.Result;
import com.mall.dto.AddCartRequest;
import com.mall.dto.CartItemVO;
import com.mall.service.CartService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping
    public Result<List<CartItemVO>> list() {
        return Result.success(cartService.list());
    }

    @PostMapping
    public Result<Void> add(@Valid @RequestBody AddCartRequest req) {
        cartService.add(req);
        return Result.success();
    }

    @PutMapping("/{productId}")
    public Result<Void> update(@PathVariable Long productId, @RequestBody Map<String, Integer> body) {
        cartService.updateQuantity(productId, body.get("quantity"));
        return Result.success();
    }

    @DeleteMapping("/{productId}")
    public Result<Void> remove(@PathVariable Long productId) {
        cartService.remove(productId);
        return Result.success();
    }
}
