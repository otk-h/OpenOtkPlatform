package com.OpenOtkPlatform.service;

import com.OpenOtkPlatform.domain.Item;
import com.OpenOtkPlatform.persistence.ItemDAO;
import com.OpenOtkPlatform.util.ValidationUtil;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商品服务类 - 单例模式
 */
public class ItemService {
    private static ItemService instance;
    private ItemDAO itemDAO;
    
    private ItemService() {
        this.itemDAO = new ItemDAO();
    }
    
    public static ItemService getInstance() {
        if (instance == null) {
            instance = new ItemService();
        }
        return instance;
    }
    
    public boolean publishItem(String name, String description, Double price, Long sellerId, Integer stock) {
        if (name == null || name.trim().isEmpty()
            || description == null || description.trim().isEmpty()
            || price == null || price <= 0
            || sellerId == null || sellerId <= 0
            || stock == null || stock < 0
            || !ValidationUtil.isValidItemName(name)
            || !ValidationUtil.isValidItemDescription(description)
            || !ValidationUtil.isValidPrice(price)
            || !ValidationUtil.isValidStock(stock)
        ) {
            return false;
        }
        
        Item newItem = new Item(name, description, price, sellerId, stock);
        return itemDAO.insertItem(newItem);
    }
    
    public Item getItemById(Long itemId) {
        if (itemId == null || itemId <= 0) {
            return null;
        }
        return itemDAO.getItemById(itemId);
    }
    
    public List<Item> getAllItems() {
        return itemDAO.getAllItems();
    }
    
    public List<Item> getItemsBySeller(Long sellerId) {
        if (sellerId == null || sellerId <= 0) {
            return null;
        }
        return itemDAO.getItemsBySeller(sellerId);
    }
    
    public List<Item> searchItems(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllItems();
        }
        
        String searchTerm = keyword.toLowerCase().trim();
        List<Item> allItems = getAllItems();
        
        return allItems.stream()
                .filter(item -> 
                    item.getName().toLowerCase().contains(searchTerm) ||
                    item.getDescription().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());
    }
    
    public boolean updateItem(Item item) {
        if (item == null || item.getId() == null) {
            return false;
        }
        return itemDAO.updateItem(item);
    }
    
    public boolean deleteItem(Long itemId) {
        if (itemId == null || itemId <= 0) {
            return false;
        }
        return itemDAO.deleteItem(itemId);
    }
    
    public boolean reduceStock(Long itemId, Integer quantity) {
        if (itemId == null || itemId <= 0 || quantity == null || quantity <= 0) {
            return false;
        }
        
        Item item = itemDAO.getItemById(itemId);
        if (item == null || !item.isInStock() || item.getStock() < quantity) {
            return false;
        }
        
        if (item.reduceStock(quantity)) {
            return itemDAO.updateItemStock(itemId, item.getStock());
        }
        return false;
    }
    
    public boolean increaseStock(Long itemId, Integer quantity) {
        if (itemId == null || itemId <= 0 || quantity == null || quantity <= 0) {
            return false;
        }
        
        Item item = itemDAO.getItemById(itemId);
        if (item == null) {
            return false;
        }
        
        item.increaseStock(quantity);
        return itemDAO.updateItemStock(itemId, item.getStock());
    }
    
    public boolean isItemAvailable(Long itemId) {
        if (itemId == null || itemId <= 0) {
            return false;
        }
        
        Item item = itemDAO.getItemById(itemId);
        return item != null && item.isInStock();
    }
}
