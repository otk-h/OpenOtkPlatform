package com.OpenOtkPlatform.service;

import com.OpenOtkPlatform.domain.Order;
import com.OpenOtkPlatform.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private ItemService itemService;
    
    @Autowired
    private UserService userService;
    
    public Order createOrder(Long itemId, Long buyerId, Long sellerId, Long quantity, Double totalPrice) {
        if (!validateOrderCreation(itemId, buyerId, sellerId, quantity)) {
            return null;
        }
        
        Order newOrder = new Order(itemId, buyerId, sellerId, quantity, totalPrice);
        try {
            Order savedOrder = orderRepository.save(newOrder);
            itemService.reduceStock(itemId, quantity.intValue());
            return savedOrder;
        } catch (Exception e) {
            return null;
        }
    }
    
    public Order getOrderById(Long orderId) {
        if (orderId == null || orderId <= 0) {
            return null;
        }
        return orderRepository.findById(orderId).orElse(null);
    }
    
    public List<Order> getOrdersByBuyer(Long buyerId) {
        if (buyerId == null || buyerId <= 0) {
            return null;
        }
        return orderRepository.findByBuyerId(buyerId);
    }
    
    public List<Order> getOrdersBySeller(Long sellerId) {
        if (sellerId == null || sellerId <= 0) {
            return null;
        }
        return orderRepository.findBySellerId(sellerId);
    }
    
    public boolean updateOrderStatus(Long orderId, String status) {
        if (orderId == null || orderId <= 0 || status == null || status.trim().isEmpty()) {
            return false;
        }
        
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (!orderOpt.isPresent()) {
            return false;
        }
        
        Order order = orderOpt.get();
        order.setStatus(status);
        try {
            orderRepository.save(order);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean cancelOrder(Long orderId) {
        if (orderId == null || orderId <= 0) {
            return false;
        }
        
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (!orderOpt.isPresent() || !orderOpt.get().cancelOrder()) {
            return false;
        }
        
        Order order = orderOpt.get();
        itemService.increaseStock(order.getItemId(), order.getQuantity().intValue());
        try {
            orderRepository.save(order);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean completeOrder(Long orderId) {
        if (orderId == null || orderId <= 0) {
            return false;
        }
        
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (!orderOpt.isPresent() || !orderOpt.get().completeOrder()) {
            return false;
        }
        
        Order order = orderOpt.get();
        try {
            orderRepository.save(order);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public String exchangeContactInfo(Long orderId) {
        if (orderId == null || orderId <= 0) {
            return null;
        }
        
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (!orderOpt.isPresent() || !orderOpt.get().isConfirmed()) {
            return null;
        }
        
        Order order = orderOpt.get();
        String buyerContact = userService.getUserById(order.getBuyerId()).getPhone();
        String sellerContact = userService.getUserById(order.getSellerId()).getPhone();
        
        return String.format("买家联系方式: %s, 卖家联系方式: %s", buyerContact, sellerContact);
    }
    
    public boolean validateOrderCreation(Long itemId, Long buyerId, Long sellerId, Long quantity) {
        if (itemId == null || itemId <= 0
            || buyerId == null || buyerId <= 0
            || sellerId == null || sellerId <= 0
            || quantity == null || quantity <= 0
        ) {
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
    
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
}
