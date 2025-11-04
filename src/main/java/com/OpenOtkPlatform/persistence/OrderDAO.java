package com.OpenOtkPlatform.persistence;

import com.OpenOtkPlatform.domain.Order;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 订单数据访问对象
 */
public class OrderDAO {
    private DatabaseManager dbManager;
    
    public OrderDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }
    
    public boolean insertOrder(Order order) {
        if (order == null) {
            return false;
        }
        
        String sql = "INSERT INTO orders (item_id, buyer_id, seller_id, total_price, status) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setLong(1, order.getItemId());
            pstmt.setLong(2, order.getBuyerId());
            pstmt.setLong(3, order.getSellerId());
            pstmt.setDouble(4, order.getTotalPrice());
            pstmt.setString(5, order.getStatus());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        order.setId(generatedKeys.getLong(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("插入订单失败: " + e.getMessage());
        }
        return false;
    }
    
    public Order getOrderById(Long orderId) {
        if (orderId == null || orderId <= 0) {
            return null;
        }
        
        String sql = "SELECT * FROM orders WHERE id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, orderId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToOrder(rs);
            }
        } catch (SQLException e) {
            System.err.println("查询订单失败: " + e.getMessage());
        }
        return null;
    }
    
    public List<Order> getOrdersByBuyer(Long buyerId) {
        if (buyerId == null || buyerId <= 0) {
            return new ArrayList<>();
        }
        
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE buyer_id = ? ORDER BY create_time DESC";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, buyerId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                orders.add(mapResultSetToOrder(rs));
            }
        } catch (SQLException e) {
            System.err.println("查询买家订单失败: " + e.getMessage());
        }
        return orders;
    }
    
    public List<Order> getOrdersBySeller(Long sellerId) {
        if (sellerId == null || sellerId <= 0) {
            return new ArrayList<>();
        }
        
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE seller_id = ? ORDER BY create_time DESC";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, sellerId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                orders.add(mapResultSetToOrder(rs));
            }
        } catch (SQLException e) {
            System.err.println("查询卖家订单失败: " + e.getMessage());
        }
        return orders;
    }
    
    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders ORDER BY create_time DESC";
        
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                orders.add(mapResultSetToOrder(rs));
            }
        } catch (SQLException e) {
            System.err.println("查询所有订单失败: " + e.getMessage());
        }
        return orders;
    }
    
    public boolean updateOrder(Order order) {
        if (order == null || order.getId() == null) {
            return false;
        }
        
        String sql = "UPDATE orders SET item_id = ?, buyer_id = ?, seller_id = ?, total_price = ?, status = ? WHERE id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, order.getItemId());
            pstmt.setLong(2, order.getBuyerId());
            pstmt.setLong(3, order.getSellerId());
            pstmt.setDouble(4, order.getTotalPrice());
            pstmt.setString(5, order.getStatus());
            pstmt.setLong(6, order.getId());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("更新订单失败: " + e.getMessage());
        }
        return false;
    }
    
    public boolean updateOrderStatus(Long orderId, String status) {
        if (orderId == null || orderId <= 0 || status == null || status.trim().isEmpty()) {
            return false;
        }
        
        String sql = "UPDATE orders SET status = ? WHERE id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status.trim());
            pstmt.setLong(2, orderId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("更新订单状态失败: " + e.getMessage());
        }
        return false;
    }
    
    public boolean deleteOrder(Long orderId) {
        if (orderId == null || orderId <= 0) {
            return false;
        }
        
        String sql = "DELETE FROM orders WHERE id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, orderId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("删除订单失败: " + e.getMessage());
        }
        return false;
    }
    
    private Order mapResultSetToOrder(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setId(rs.getLong("id"));
        order.setItemId(rs.getLong("item_id"));
        order.setBuyerId(rs.getLong("buyer_id"));
        order.setSellerId(rs.getLong("seller_id"));
        order.setTotalPrice(rs.getDouble("total_price"));
        order.setStatus(rs.getString("status"));
        order.setCreateTime(rs.getTimestamp("create_time"));
        order.setUpdateTime(rs.getTimestamp("update_time"));
        return order;
    }
}
