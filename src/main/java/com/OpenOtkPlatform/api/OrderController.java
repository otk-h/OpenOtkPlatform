package com.OpenOtkPlatform.api;

import com.OpenOtkPlatform.domain.Order;
import com.OpenOtkPlatform.service.OrderService;
import com.OpenOtkPlatform.service.LogService;
import com.OpenOtkPlatform.service.UserService;
import com.OpenOtkPlatform.service.ItemService;
import java.util.List;

/**
 * 订单控制器类
 */
public class OrderController {
    private OrderService orderService;
    private LogService logService;
    private UserService userService;
    private ItemService itemService;
    
    public OrderController() {
        this.orderService = OrderService.getInstance();
        this.logService = LogService.getInstance();
        this.userService = UserService.getInstance();
        this.itemService = ItemService.getInstance();
    }
    
    public Order createOrder(Long itemId, Long buyerId, Long sellerId, Double totalPrice) {
        if (itemId == null || itemId <= 0
            || buyerId == null || buyerId <= 0
            || sellerId == null || sellerId <= 0
            || totalPrice == null || totalPrice <= 0
        ) {
            return null;
        }
        
        // 验证买家余额是否足够
        Double buyerBalance = userService.getUserById(buyerId).getBalance();
        if (buyerBalance < totalPrice) {
            return null;
        }
        
        Order order = orderService.createOrder(itemId, buyerId, sellerId, totalPrice);
        if (order != null) {
            // 扣减买家余额
            userService.deductBalance(buyerId, totalPrice);
            
            // 记录订单创建日志
            logService.logOrderCreate(buyerId, order.getId());
        }
        return order;
    }
    
    public Order getOrderById(Long orderId) {
        if (orderId == null || orderId <= 0) {
            return null;
        }
        return orderService.getOrderById(orderId);
    }
    
    public List<Order> getOrdersByBuyer(Long buyerId) {
        if (buyerId == null || buyerId <= 0) {
            return null;
        }
        return orderService.getOrdersByBuyer(buyerId);
    }
    
    public List<Order> getOrdersBySeller(Long sellerId) {
        if (sellerId == null || sellerId <= 0) {
            return null;
        }
        return orderService.getOrdersBySeller(sellerId);
    }
    
    public boolean updateOrderStatus(Long orderId, String status) {
        if (orderId == null || orderId <= 0 || status == null || status.trim().isEmpty()) {
            return false;
        }
        
        boolean success = orderService.updateOrderStatus(orderId, status);
        if (success) {
            Order order = orderService.getOrderById(orderId);
            if (order != null) {
                logService.logUserOperation("UPDATE_ORDER_STATUS", order.getBuyerId(), 
                    String.format("更新订单状态，订单ID: %d, 状态: %s", orderId, status));
            }
        }
        return success;
    }
    
    public boolean cancelOrder(Long orderId) {
        if (orderId == null || orderId <= 0) {
            return false;
        }
        
        Order order = orderService.getOrderById(orderId);
        if (order == null) {
            return false;
        }
        
        boolean success = orderService.cancelOrder(orderId);
        if (success) {
            // 恢复买家余额
            userService.rechargeBalance(order.getBuyerId(), order.getTotalPrice());
            
            logService.logUserOperation("CANCEL_ORDER", order.getBuyerId(), 
                String.format("取消订单，订单ID: %d", orderId));
        }
        return success;
    }
    
    public boolean completeOrder(Long orderId) {
        if (orderId == null || orderId <= 0) {
            return false;
        }
        
        Order order = orderService.getOrderById(orderId);
        if (order == null) {
            return false;
        }
        
        boolean success = orderService.completeOrder(orderId);
        if (success) {
            // 将款项转给卖家
            userService.rechargeBalance(order.getSellerId(), order.getTotalPrice());
            
            logService.logUserOperation("COMPLETE_ORDER", order.getBuyerId(), 
                String.format("完成订单，订单ID: %d", orderId));
        }
        return success;
    }
    
    public String exchangeContactInfo(Long orderId) {
        if (orderId == null || orderId <= 0) {
            return null;
        }
        
        String contactInfo = orderService.exchangeContactInfo(orderId);
        if (contactInfo != null) {
            Order order = orderService.getOrderById(orderId);
            if (order != null) {
                logService.logUserOperation("EXCHANGE_CONTACT", order.getBuyerId(), 
                    String.format("交换联系方式，订单ID: %d", orderId));
            }
        }
        return contactInfo;
    }
    
    public boolean validateOrder(Long itemId, Long buyerId) {
        if (itemId == null || itemId <= 0 || buyerId == null || buyerId <= 0) {
            return false;
        }
        
        // 验证商品是否存在且有库存
        if (!itemService.isItemAvailable(itemId)) {
            return false;
        }
        
        // 验证买家是否存在
        if (userService.getUserById(buyerId) == null) {
            return false;
        }
        
        // 验证买家余额是否足够
        Double itemPrice = itemService.getItemById(itemId).getPrice();
        Double buyerBalance = userService.getUserById(buyerId).getBalance();
        
        return buyerBalance >= itemPrice;
    }
}
