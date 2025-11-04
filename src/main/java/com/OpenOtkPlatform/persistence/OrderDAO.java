package com.OpenOtkPlatform.persistence;

import com.OpenOtkPlatform.domain.Order;
import java.util.List;

/**
 * 订单数据访问对象
 */
public class OrderDAO {
    private DatabaseManager dbManager;
    
    public OrderDAO() {} // TODO - finish me
    
    public boolean insertOrder(Order order) { return false; } // TODO - finish me
    
    public Order getOrderById(Long orderId) { return null; } // TODO - finish me
    
    public List<Order> getOrdersByBuyer(Long buyerId) { return null; } // TODO - finish me
    
    public List<Order> getOrdersBySeller(Long sellerId) { return null; } // TODO - finish me
    
    public List<Order> getAllOrders() { return null; } // TODO - finish me
    
    public boolean updateOrder(Order order) { return false; } // TODO - finish me
    
    public boolean updateOrderStatus(Long orderId, String status) { return false; } // TODO - finish me
    
    public boolean deleteOrder(Long orderId) { return false; } // TODO - finish me
}
