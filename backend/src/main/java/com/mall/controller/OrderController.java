package com.mall.controller;

import com.mall.common.Result;
import com.mall.dto.OrderCreateRequest;
import com.mall.entity.Order;
import com.mall.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public Result<Order> create(@Valid @RequestBody OrderCreateRequest req) {
        return Result.success(orderService.create(req));
    }

    @GetMapping
    public Result<List<Order>> list() {
        return Result.success(orderService.listMyOrders());
    }

    @GetMapping("/{id}")
    public Result<Order> detail(@PathVariable Long id) {
        return Result.success(orderService.detail(id));
    }
}
