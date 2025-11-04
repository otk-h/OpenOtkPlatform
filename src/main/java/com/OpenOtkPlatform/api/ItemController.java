package com.OpenOtkPlatform.api;

import com.OpenOtkPlatform.domain.Item;
import com.OpenOtkPlatform.service.ItemService;
import com.OpenOtkPlatform.service.LogService;
import com.OpenOtkPlatform.service.UserService;
import com.OpenOtkPlatform.util.ValidationUtil;
import java.util.List;

/**
 * 商品控制器类
 */
public class ItemController {
    private ItemService itemService;
    private LogService logService;
    private UserService userService;
    
    public ItemController() {
        this.itemService = ItemService.getInstance();
        this.logService = LogService.getInstance();
        this.userService = UserService.getInstance();
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
        
        // 验证卖家是否存在
        if (userService.getUserById(sellerId) == null) {
            return false;
        }
        
        boolean success = itemService.publishItem(name, description, price, sellerId, stock);
        if (success) {
            // 获取最新发布的商品ID（这里简化处理，实际应该返回商品对象）
            List<Item> sellerItems = itemService.getItemsBySeller(sellerId);
            if (!sellerItems.isEmpty()) {
                Item latestItem = sellerItems.get(sellerItems.size() - 1);
                logService.logItemPublish(sellerId, latestItem.getId());
            }
        }
        return success;
    }
    
    public Item getItemById(Long itemId) {
        if (itemId == null || itemId <= 0) {
            return null;
        }
        return itemService.getItemById(itemId);
    }
    
    public List<Item> getAllItems() {
        return itemService.getAllItems();
    }
    
    public List<Item> getItemsBySeller(Long sellerId) {
        if (sellerId == null || sellerId <= 0) {
            return null;
        }
        return itemService.getItemsBySeller(sellerId);
    }
    
    public List<Item> searchItems(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllItems();
        }
        return itemService.searchItems(keyword);
    }
    
    public boolean updateItem(Long itemId, String name, String description, Double price, Integer stock) {
        if (itemId == null || itemId <= 0
            || name == null || name.trim().isEmpty()
            || description == null || description.trim().isEmpty()
            || price == null || price <= 0
            || stock == null || stock < 0
            || !ValidationUtil.isValidItemName(name)
            || !ValidationUtil.isValidItemDescription(description)
            || !ValidationUtil.isValidPrice(price)
            || !ValidationUtil.isValidStock(stock)
        ) {
            return false;
        }
        
        Item item = itemService.getItemById(itemId);
        if (item == null) {
            return false;
        }
        
        item.setName(name);
        item.setDescription(description);
        item.setPrice(price);
        item.setStock(stock);
        
        boolean success = itemService.updateItem(item);
        if (success) {
            logService.logUserOperation("UPDATE_ITEM", item.getSellerId(), 
                String.format("更新商品信息，商品ID: %d", itemId));
        }
        return success;
    }
    
    public boolean deleteItem(Long itemId) {
        if (itemId == null || itemId <= 0) {
            return false;
        }
        
        Item item = itemService.getItemById(itemId);
        if (item == null) {
            return false;
        }
        
        boolean success = itemService.deleteItem(itemId);
        if (success) {
            logService.logUserOperation("DELETE_ITEM", item.getSellerId(), 
                String.format("删除商品，商品ID: %d", itemId));
        }
        return success;
    }
    
    public boolean reduceStock(Long itemId, Integer quantity) {
        if (itemId == null || itemId <= 0 || quantity == null || quantity <= 0) {
            return false;
        }
        
        boolean success = itemService.reduceStock(itemId, quantity);
        if (success) {
            Item item = itemService.getItemById(itemId);
            if (item != null) {
                logService.logUserOperation("REDUCE_STOCK", item.getSellerId(), 
                    String.format("减少商品库存，商品ID: %d, 数量: %d", itemId, quantity));
            }
        }
        return success;
    }
}
