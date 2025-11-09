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
        Long quantity = request.getQuantity();
        Double totalPrice = request.getTotalPrice();
        
        if (itemId == null || itemId <= 0
            || buyerId == null || buyerId <= 0
            || sellerId == null || sellerId <= 0
            || totalPrice == null || totalPrice <= 0
        ) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "inValid arg"));
        }
        
        // 验证买家余额是否足够
        Double buyerBalance = userService.getUserById(buyerId).getBalance();
        if (buyerBalance < totalPrice) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "No Sufficient Balance"));
        }
        
        Order order = orderService.createOrder(itemId, buyerId, sellerId, quantity, totalPrice);
        if (order != null) {
            // 扣减买家余额并增加卖家余额
            userService.deductBalance(buyerId, totalPrice);
            userService.rechargeBalance(sellerId, totalPrice);
            
            logService.logOrderCreate(buyerId, order.getId());
            
            return ResponseEntity.ok(new ApiResponse(true, "Order created successfully", order));
        }
        return ResponseEntity.badRequest().body(new ApiResponse(false, "Order Create Fail"));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "inValid orderId"));
        }
        
        Order order = orderService.getOrderById(id);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new ApiResponse(true, "Success", order));
    }
    
    @GetMapping("/buyer/{buyerId}")
    public ResponseEntity<?> getOrdersByBuyer(@PathVariable Long buyerId) {
        if (buyerId == null || buyerId <= 0) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "inValid buyerId"));
        }
        
        List<Order> orders = orderService.getOrdersByBuyer(buyerId);
        return ResponseEntity.ok(new ApiResponse(true, "Success", orders));
    }
    
    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<?> getOrdersBySeller(@PathVariable Long sellerId) {
        if (sellerId == null || sellerId <= 0) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "inValid sellerId"));
        }
        
        List<Order> orders = orderService.getOrdersBySeller(sellerId);
        return ResponseEntity.ok(new ApiResponse(true, "Success", orders));
    }
    
    @PostMapping("/{id}/confirm")
    public ResponseEntity<?> confirmOrder(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "订单ID无效"));
        }
        
        Order order = orderService.getOrderById(id);
        if (order == null) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "订单不存在"));
        }
        
        boolean success = orderService.confirmOrder(id);
        if (success) {
            logService.logOrderConfirm(order.getSellerId(), id);
            return ResponseEntity.ok(new ApiResponse(true, "订单确认成功"));
        }
        return ResponseEntity.badRequest().body(new ApiResponse(false, "订单确认失败"));
    }
    
    @PostMapping("/{id}/complete")
    public ResponseEntity<?> completeOrder(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "订单ID无效"));
        }
        
        Order order = orderService.getOrderById(id);
        if (order == null) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "订单不存在"));
        }
        
        boolean success = orderService.completeOrder(id);
        if (success) {
            logService.logOrderComplete(order.getBuyerId(), id);
            return ResponseEntity.ok(new ApiResponse(true, "订单完成成功"));
        }
        return ResponseEntity.badRequest().body(new ApiResponse(false, "订单完成失败"));
    }
    
    @PostMapping("/{id}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "订单ID无效"));
        }
        
        Order order = orderService.getOrderById(id);
        if (order == null) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "订单不存在"));
        }
        
        boolean success = orderService.cancelOrder(id);
        if (success) {
            // 恢复买家余额并扣减卖家余额
            userService.rechargeBalance(order.getBuyerId(), order.getTotalPrice());
            userService.deductBalance(order.getSellerId(), order.getTotalPrice());
            
            logService.logOrderCancel(order.getBuyerId(), id);
            return ResponseEntity.ok(new ApiResponse(true, "订单取消成功"));
        }
        return ResponseEntity.badRequest().body(new ApiResponse(false, "订单取消失败"));
    }
    
    @GetMapping("/validate")
    public ResponseEntity<?> validateOrder(@RequestParam Long itemId, @RequestParam Long buyerId) {
        if (itemId == null || itemId <= 0 || buyerId == null || buyerId <= 0) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "inValid arg"));
        }
        
        // 验证商品是否存在且有库存
        if (!itemService.isItemAvailable(itemId)) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "inValid Item"));
        }
        
        // 验证买家是否存在
        if (userService.getUserById(buyerId) == null) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Buyer Not Exist"));
        }
        
        // 验证买家余额是否足够
        Double itemPrice = itemService.getItemById(itemId).getPrice();
        Double buyerBalance = userService.getUserById(buyerId).getBalance();
        
        if (buyerBalance >= itemPrice) {
            return ResponseEntity.ok(new ApiResponse(true, "Order Success"));
        } else {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "No Sufficient Balance"));
        }
    }
    
    @GetMapping
    public ResponseEntity<?> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return ResponseEntity.ok(new ApiResponse(true, "Success", orders));
    }
    
    // 请求DTO类
    public static class CreateOrderRequest {
        private Long itemId;
        private Long buyerId;
        private Long sellerId;
        private Long quantity;
        private Double totalPrice;
        
        // getters and setters
        public Long getItemId() { return itemId; }
        public void setItemId(Long itemId) { this.itemId = itemId; }
        public Long getBuyerId() { return buyerId; }
        public void setBuyerId(Long buyerId) { this.buyerId = buyerId; }
        public Long getSellerId() { return sellerId; }
        public void setSellerId(Long sellerId) { this.sellerId = sellerId; }
        public Long getQuantity() { return quantity; }
        public void setQuantity(Long quantity) { this.quantity = quantity; }
        public Double getTotalPrice() { return totalPrice; }
        public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }
    }

    public static class ApiResponse {
        private boolean success;
        private String message;
        private Object data;
        
        public ApiResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        
        public ApiResponse(boolean success, String message, Object data) {
            this.success = success;
            this.message = message;
            this.data = data;
        }
        
        // getters and setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public Object getData() { return data; }
        public void setData(Object data) { this.data = data; }
    }
}
