package com.OpenOtkPlatform.persistence;

import com.OpenOtkPlatform.domain.Item;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 商品数据访问对象
 */
public class ItemDAO {
    private DatabaseManager dbManager;
    
    public ItemDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }
    
    public boolean insertItem(Item item) {
        if (item == null) {
            return false;
        }
        
        String sql = "INSERT INTO items (name, description, price, seller_id, stock, available) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, item.getName());
            pstmt.setString(2, item.getDescription());
            pstmt.setDouble(3, item.getPrice());
            pstmt.setLong(4, item.getSellerId());
            pstmt.setInt(5, item.getStock());
            pstmt.setBoolean(6, item.getAvailable());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        item.setId(generatedKeys.getLong(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("插入商品失败: " + e.getMessage());
        }
        return false;
    }
    
    public Item getItemById(Long itemId) {
        if (itemId == null || itemId <= 0) {
            return null;
        }
        
        String sql = "SELECT * FROM items WHERE id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, itemId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToItem(rs);
            }
        } catch (SQLException e) {
            System.err.println("查询商品失败: " + e.getMessage());
        }
        return null;
    }
    
    public List<Item> getAllItems() {
        List<Item> items = new ArrayList<>();
        String sql = "SELECT * FROM items ORDER BY id";
        
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                items.add(mapResultSetToItem(rs));
            }
        } catch (SQLException e) {
            System.err.println("查询所有商品失败: " + e.getMessage());
        }
        return items;
    }
    
    public List<Item> getItemsBySeller(Long sellerId) {
        if (sellerId == null || sellerId <= 0) {
            return new ArrayList<>();
        }
        
        List<Item> items = new ArrayList<>();
        String sql = "SELECT * FROM items WHERE seller_id = ? ORDER BY id";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, sellerId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                items.add(mapResultSetToItem(rs));
            }
        } catch (SQLException e) {
            System.err.println("查询卖家商品失败: " + e.getMessage());
        }
        return items;
    }
    
    public List<Item> searchItems(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllItems();
        }
        
        List<Item> items = new ArrayList<>();
        String sql = "SELECT * FROM items WHERE name LIKE ? OR description LIKE ? ORDER BY id";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String searchTerm = "%" + keyword.trim() + "%";
            pstmt.setString(1, searchTerm);
            pstmt.setString(2, searchTerm);
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                items.add(mapResultSetToItem(rs));
            }
        } catch (SQLException e) {
            System.err.println("搜索商品失败: " + e.getMessage());
        }
        return items;
    }
    
    public boolean updateItem(Item item) {
        if (item == null || item.getId() == null) {
            return false;
        }
        
        String sql = "UPDATE items SET name = ?, description = ?, price = ?, seller_id = ?, stock = ?, available = ? WHERE id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, item.getName());
            pstmt.setString(2, item.getDescription());
            pstmt.setDouble(3, item.getPrice());
            pstmt.setLong(4, item.getSellerId());
            pstmt.setInt(5, item.getStock());
            pstmt.setBoolean(6, item.getAvailable());
            pstmt.setLong(7, item.getId());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("更新商品失败: " + e.getMessage());
        }
        return false;
    }
    
    public boolean deleteItem(Long itemId) {
        if (itemId == null || itemId <= 0) {
            return false;
        }
        
        String sql = "DELETE FROM items WHERE id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, itemId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("删除商品失败: " + e.getMessage());
        }
        return false;
    }
    
    public boolean updateItemStock(Long itemId, Integer newStock) {
        if (itemId == null || itemId <= 0 || newStock == null) {
            return false;
        }
        
        String sql = "UPDATE items SET stock = ?, available = ? WHERE id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, newStock);
            pstmt.setBoolean(2, newStock > 0);
            pstmt.setLong(3, itemId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("更新商品库存失败: " + e.getMessage());
        }
        return false;
    }
    
    public boolean updateItemAvailability(Long itemId, Boolean available) {
        if (itemId == null || itemId <= 0 || available == null) {
            return false;
        }
        
        String sql = "UPDATE items SET available = ? WHERE id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setBoolean(1, available);
            pstmt.setLong(2, itemId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("更新商品可用性失败: " + e.getMessage());
        }
        return false;
    }
    
    private Item mapResultSetToItem(ResultSet rs) throws SQLException {
        Item item = new Item();
        item.setId(rs.getLong("id"));
        item.setName(rs.getString("name"));
        item.setDescription(rs.getString("description"));
        item.setPrice(rs.getDouble("price"));
        item.setSellerId(rs.getLong("seller_id"));
        item.setStock(rs.getInt("stock"));
        item.setAvailable(rs.getBoolean("available"));
        item.setCreateTime(rs.getTimestamp("create_time"));
        item.setUpdateTime(rs.getTimestamp("update_time"));
        return item;
    }
}
