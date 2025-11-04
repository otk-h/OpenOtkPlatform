package com.OpenOtkPlatform.service;

import com.OpenOtkPlatform.domain.Order;
import com.OpenOtkPlatform.persistence.OrderDAO;
import com.OpenOtkPlatform.service.ItemService;
import com.OpenOtkPlatform.service.UserService;
import java.util.List;

/**
 * 订单服务类 - 单例模式
 */
public class OrderService {
    private static OrderService instance;
    private OrderDAO orderDAO;
    private ItemService itemService;
    private UserService userService;
    
    private OrderService() {
        this.orderDAO = new OrderDAO();
        this.itemService = ItemService.getInstance();
        this.userService = UserService.getInstance();
    }
    
    public static OrderService getInstance() {
        if (instance == null) {
            instance = new OrderService();
        }
        return instance;
    }
    
    public Order createOrder(Long itemId, Long buyerId, Long sellerId, Double totalPrice) {
        if (!validateOrderCreation(itemId, buyerId, sellerId)) {
            return null;
        }
        
        Order newOrder = new Order(itemId, buyerId, sellerId, totalPrice);
        if (orderDAO.insertOrder(newOrder)) {
            itemService.reduceStock(itemId, 1);
            return newOrder;
        }
        return null;
    }
    
    public Order getOrderById(Long orderId) {
        if (orderId == null || orderId <= 0) {
            return null;
        }
        return orderDAO.getOrderById(orderId);
    }
    
    public List<Order> getOrdersByBuyer(Long buyerId) {
        if (buyerId == null || buyerId <= 0) {
            return null;
        }
        return orderDAO.getOrdersByBuyer(buyerId);
    }
    
    public List<Order> getOrdersBySeller(Long sellerId) {
        if (sellerId == null || sellerId <= 0) {
            return null;
        }
        return orderDAO.getOrdersBySeller(sellerId);
    }
    
    public boolean updateOrderStatus(Long orderId, String status) {
        if (orderId == null || orderId <= 0 || status == null || status.trim().isEmpty()) {
            return false;
        }
        
        Order order = orderDAO.getOrderById(orderId);
        if (order == null) {
            return false;
        }
        
        order.setStatus(status);
        return orderDAO.updateOrder(order);
    }
    
    public boolean cancelOrder(Long orderId) {
        if (orderId == null || orderId <= 0) {
            return false;
        }
        
        Order order = orderDAO.getOrderById(orderId);
        if (order == null || !order.cancelOrder()) {
            return false;
        }
        
        itemService.increaseStock(order.getItemId(), 1);
        return orderDAO.updateOrder(order);
    }
    
    public boolean completeOrder(Long orderId) {
        if (orderId == null || orderId <= 0) {
            return false;
        }
        
        Order order = orderDAO.getOrderById(orderId);
        if (order == null || !order.completeOrder()) {
            return false;
        }
        
        return orderDAO.updateOrder(order);
    }
    
    public String exchangeContactInfo(Long orderId) {
        if (orderId == null || orderId <= 0) {
            return null;
        }
        
        Order order = orderDAO.getOrderById(orderId);
        if (order == null || !order.isConfirmed()) {
            return null;
        }
        
        String buyerContact = userService.getUserById(order.getBuyerId()).getPhone();
        String sellerContact = userService.getUserById(order.getSellerId()).getPhone();
        
        return String.format("买家联系方式: %s, 卖家联系方式: %s", buyerContact, sellerContact);
    }
    
    public boolean validateOrderCreation(Long itemId, Long buyerId, Long sellerId) {
        if (itemId == null || itemId <= 0 || 
            buyerId == null || buyerId <= 0 || 
            sellerId == null || sellerId <= 0) {
            return false;
        }
        
        if (!itemService.isItemAvailable(itemId)) {
            return false;
        }
    
        if (userService.getUserById(buyerId) == null || 
            userService.getUserById(sellerId) == null) {
            return false;
        }
        
        return !buyerId.equals(sellerId);
    }
}
