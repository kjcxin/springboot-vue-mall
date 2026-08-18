package com.mall.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.common.BusinessException;
import com.mall.common.PageResult;
import com.mall.dto.CartItemVO;
import com.mall.dto.OrderCreateRequest;
import com.mall.entity.Address;
import com.mall.entity.Order;
import com.mall.entity.OrderItem;
import com.mall.entity.Product;
import com.mall.mapper.AddressMapper;
import com.mall.mapper.OrderItemMapper;
import com.mall.mapper.OrderMapper;
import com.mall.mapper.ProductMapper;
import com.mall.security.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class OrderService {

    public static final int STATUS_PENDING_PAY = 0;
    public static final int STATUS_PENDING_SHIP = 1;
    public static final int STATUS_PENDING_RECEIVE = 2;
    public static final int STATUS_COMPLETED = 3;
    public static final int STATUS_CANCELLED = 4;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private AddressMapper addressMapper;

    @Autowired
    private CartService cartService;

    /** 从购物车勾选项下单 */
    @Transactional(rollbackFor = Exception.class)
    public Order create(OrderCreateRequest req) {
        Long userId = UserContext.getUserId();
        Address address = addressMapper.selectOne(new LambdaQueryWrapper<Address>()
                .eq(Address::getId, req.getAddressId()).eq(Address::getUserId, userId));
        if (address == null) {
            throw new BusinessException("收货地址不存在");
        }

        List<CartItemVO> cartItems = cartService.list();
        if (cartItems.isEmpty()) {
            throw new BusinessException("购物车为空");
        }
        Set<Long> selected = new HashSet<>(req.getProductIds());
        List<CartItemVO> chosen = cartItems.stream()
                .filter(i -> selected.contains(i.getProductId())).toList();
        if (chosen.isEmpty()) {
            throw new BusinessException("请选择要结算的商品");
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItemVO item : chosen) {
            Product product = productMapper.selectById(item.getProductId());
            if (product == null || product.getStatus() == null || product.getStatus() != 1) {
                throw new BusinessException("商品「" + item.getName() + "」已下架");
            }
            int updated = productMapper.deductStock(product.getId(), item.getQuantity());
            if (updated == 0) {
                throw new BusinessException("商品「" + item.getName() + "」库存不足");
            }
            BigDecimal totalPrice = product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            totalAmount = totalAmount.add(totalPrice);

            OrderItem oi = new OrderItem();
            oi.setProductId(product.getId());
            oi.setProductName(product.getName());
            oi.setProductImage(product.getImage());
            oi.setProductPrice(product.getPrice());
            oi.setQuantity(item.getQuantity());
            oi.setTotalPrice(totalPrice);
            orderItems.add(oi);
        }

        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setStatus(STATUS_PENDING_PAY);
        order.setReceiverName(address.getReceiverName());
        order.setReceiverPhone(address.getReceiverPhone());
        order.setReceiverAddress(address.getReceiverAddress());
        order.setCreateTime(LocalDateTime.now());
        orderMapper.insert(order);

        for (OrderItem oi : orderItems) {
            oi.setOrderId(order.getId());
            orderItemMapper.insert(oi);
        }

        // 从购物车移除已下单商品
        for (CartItemVO item : chosen) {
            cartService.remove(item.getProductId());
        }

        order.setItems(orderItems);
        return order;
    }

    /** 模拟支付 */
    @Transactional(rollbackFor = Exception.class)
    public void pay(Long orderId) {
        Order order = getOwnOrder(orderId);
        if (order.getStatus() != STATUS_PENDING_PAY) {
            throw new BusinessException("当前状态不可支付");
        }
        order.setStatus(STATUS_PENDING_SHIP);
        order.setPayTime(LocalDateTime.now());
        orderMapper.updateById(order);
        for (OrderItem oi : getItems(orderId)) {
            productMapper.incrementSales(oi.getProductId(), oi.getQuantity());
        }
    }

    /** 取消订单（待支付可取消，恢复库存） */
    @Transactional(rollbackFor = Exception.class)
    public void cancel(Long orderId) {
        Order order = getOwnOrder(orderId);
        if (order.getStatus() != STATUS_PENDING_PAY) {
            throw new BusinessException("当前状态不可取消");
        }
        order.setStatus(STATUS_CANCELLED);
        orderMapper.updateById(order);
        for (OrderItem oi : getItems(orderId)) {
            productMapper.restoreStock(oi.getProductId(), oi.getQuantity());
        }
    }

    /** 确认收货 */
    public void confirm(Long orderId) {
        Order order = getOwnOrder(orderId);
        if (order.getStatus() != STATUS_PENDING_RECEIVE) {
            throw new BusinessException("当前状态不可确认收货");
        }
        order.setStatus(STATUS_COMPLETED);
        order.setCompleteTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    /** 发货（管理员） */
    public void ship(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (order.getStatus() != STATUS_PENDING_SHIP) {
            throw new BusinessException("当前状态不可发货");
        }
        order.setStatus(STATUS_PENDING_RECEIVE);
        order.setShipTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    /** 关闭订单（管理员，对待支付/待发货订单） */
    public void close(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (order.getStatus() == STATUS_PENDING_PAY || order.getStatus() == STATUS_PENDING_SHIP) {
            order.setStatus(STATUS_CANCELLED);
            orderMapper.updateById(order);
            for (OrderItem oi : getItems(orderId)) {
                productMapper.restoreStock(oi.getProductId(), oi.getQuantity());
            }
        } else {
            throw new BusinessException("当前状态不可关闭");
        }
    }

    public List<Order> listMyOrders(Integer status) {
        Long userId = UserContext.getUserId();
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<Order>()
                .eq(Order::getUserId, userId).orderByDesc(Order::getCreateTime);
        if (status != null) {
            wrapper.eq(Order::getStatus, status);
        }
        List<Order> orders = orderMapper.selectList(wrapper);
        orders.forEach(o -> o.setItems(getItems(o.getId())));
        return orders;
    }

    public Order detail(Long orderId) {
        Order order = getOwnOrder(orderId);
        order.setItems(getItems(orderId));
        return order;
    }

    /** 管理端订单分页 */
    public PageResult<Order> adminPage(int page, int size, Integer status, String keyword) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(Order::getStatus, status);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Order::getOrderNo, keyword).or().like(Order::getReceiverName, keyword));
        }
        wrapper.orderByDesc(Order::getCreateTime);
        Page<Order> result = orderMapper.selectPage(new Page<>(page, size), wrapper);
        result.getRecords().forEach(o -> o.setItems(getItems(o.getId())));
        return PageResult.of(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    private Order getOwnOrder(Long orderId) {
        Long userId = UserContext.getUserId();
        Order order = orderMapper.selectOne(new LambdaQueryWrapper<Order>()
                .eq(Order::getId, orderId).eq(Order::getUserId, userId));
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        return order;
    }

    private List<OrderItem> getItems(Long orderId) {
        return orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, orderId));
    }

    private String generateOrderNo() {
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        return "M" + time + random;
    }
}
