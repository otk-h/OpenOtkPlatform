package com.OpenOtkPlatform.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.OpenOtkPlatform.domain.Order;
import com.OpenOtkPlatform.service.OrderService;
import com.OpenOtkPlatform.service.LogService;
import com.OpenOtkPlatform.service.UserService;
import com.OpenOtkPlatform.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private LogService logService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private ItemService itemService;
    
    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody CreateOrderRequest request) {
        Long itemId = request.getItemId();
        Long buyerId = request.getBuyerId();
        Long sellerId = request.getSellerId();
        Double totalPrice = request.getTotalPrice();
        
        if (itemId == null || itemId <= 0
            || buyerId == null || buyerId <= 0
            || sellerId == null || sellerId <= 0
            || totalPrice == null || totalPrice <= 0
        ) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "参数无效"));
        }
        
        // 验证买家余额是否足够
        Double buyerBalance = userService.getUserById(buyerId).getBalance();
        if (buyerBalance < totalPrice) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "余额不足"));
        }
        
        Order order = orderService.createOrder(itemId, buyerId, sellerId, totalPrice);
        if (order != null) {
            // 扣减买家余额
            userService.deductBalance(buyerId, totalPrice);
            
            logService.logOrderCreate(buyerId, order.getId());
            
            return ResponseEntity.ok(order);
        }
        return ResponseEntity.badRequest().body(new ApiResponse(false, "订单创建失败"));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "订单ID无效"));
        }
        
        Order order = orderService.getOrderById(id);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(order);
    }
    
    @GetMapping("/buyer/{buyerId}")
    public ResponseEntity<?> getOrdersByBuyer(@PathVariable Long buyerId) {
        if (buyerId == null || buyerId <= 0) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "买家ID无效"));
        }
        
        List<Order> orders = orderService.getOrdersByBuyer(buyerId);
        return ResponseEntity.ok(orders);
    }
    
    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<?> getOrdersBySeller(@PathVariable Long sellerId) {
        if (sellerId == null || sellerId <= 0) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "卖家ID无效"));
        }
        
        List<Order> orders = orderService.getOrdersBySeller(sellerId);
        return ResponseEntity.ok(orders);
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long id, @RequestParam String status) {
        if (id == null || id <= 0 || status == null || status.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "参数无效"));
        }
        
        boolean success = orderService.updateOrderStatus(id, status);
        if (success) {
            Order order = orderService.getOrderById(id);
            if (order != null) {
                logService.logOrderUpdate(order.getBuyerId(), id, status);
            }
            return ResponseEntity.ok(new ApiResponse(true, "订单状态更新成功"));
        }
        return ResponseEntity.badRequest().body(new ApiResponse(false, "订单状态更新失败"));
    }
    
    @PostMapping("/{id}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "订单ID无效"));
        }
        
        Order order = orderService.getOrderById(id);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }
        
        boolean success = orderService.cancelOrder(id);
        if (success) {
            // 恢复买家余额
            userService.rechargeBalance(order.getBuyerId(), order.getTotalPrice());
            
            logService.logOrderCancel(order.getBuyerId(), id);
            return ResponseEntity.ok(new ApiResponse(true, "订单取消成功"));
        }
        return ResponseEntity.badRequest().body(new ApiResponse(false, "订单取消失败"));
    }
    
    @PostMapping("/{id}/complete")
    public ResponseEntity<?> completeOrder(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "订单ID无效"));
        }
        
        Order order = orderService.getOrderById(id);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }
        
        boolean success = orderService.completeOrder(id);
        if (success) {
            // 将款项转给卖家
            userService.rechargeBalance(order.getSellerId(), order.getTotalPrice());
            
            logService.logOrderComplete(order.getBuyerId(), id);
            return ResponseEntity.ok(new ApiResponse(true, "订单完成成功"));
        }
        return ResponseEntity.badRequest().body(new ApiResponse(false, "订单完成失败"));
    }
    
    @GetMapping("/{id}/contact")
    public ResponseEntity<?> exchangeContactInfo(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "订单ID无效"));
        }
        
        String contactInfo = orderService.exchangeContactInfo(id);
        if (contactInfo != null) {
            Order order = orderService.getOrderById(id);
            if (order != null) {
                logService.logExchangeInfo(order.getBuyerId(), order.getSellerId());
            }
            return ResponseEntity.ok(new ApiResponse(true, contactInfo));
        }
        return ResponseEntity.badRequest().body(new ApiResponse(false, "无法获取联系方式"));
    }
    
    @GetMapping("/validate")
    public ResponseEntity<?> validateOrder(@RequestParam Long itemId, @RequestParam Long buyerId) {
        if (itemId == null || itemId <= 0 || buyerId == null || buyerId <= 0) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "参数无效"));
        }
        
        // 验证商品是否存在且有库存
        if (!itemService.isItemAvailable(itemId)) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "商品不可用"));
        }
        
        // 验证买家是否存在
        if (userService.getUserById(buyerId) == null) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "买家不存在"));
        }
        
        // 验证买家余额是否足够
        Double itemPrice = itemService.getItemById(itemId).getPrice();
        Double buyerBalance = userService.getUserById(buyerId).getBalance();
        
        if (buyerBalance >= itemPrice) {
            return ResponseEntity.ok(new ApiResponse(true, "订单验证通过"));
        } else {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "余额不足"));
        }
    }
    
    @GetMapping
    public ResponseEntity<?> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }
    
    // 请求DTO类
    public static class CreateOrderRequest {
        private Long itemId;
        private Long buyerId;
        private Long sellerId;
        private Double totalPrice;
        
        // getters and setters
        public Long getItemId() { return itemId; }
        public void setItemId(Long itemId) { this.itemId = itemId; }
        public Long getBuyerId() { return buyerId; }
        public void setBuyerId(Long buyerId) { this.buyerId = buyerId; }
        public Long getSellerId() { return sellerId; }
        public void setSellerId(Long sellerId) { this.sellerId = sellerId; }
        public Double getTotalPrice() { return totalPrice; }
        public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }
    }

    public static class ApiResponse {
        private boolean success;
        private String message;
        
        public ApiResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        
        // getters and setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}
