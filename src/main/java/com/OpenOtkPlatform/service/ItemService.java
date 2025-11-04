package com.OpenOtkPlatform.service;

import com.OpenOtkPlatform.domain.Item;
import com.OpenOtkPlatform.repository.ItemRepository;
import com.OpenOtkPlatform.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ItemService {
    
    @Autowired
    private ItemRepository itemRepository;
    
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
        try {
            itemRepository.save(newItem);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public Item getItemById(Long itemId) {
        if (itemId == null || itemId <= 0) {
            return null;
        }
        return itemRepository.findById(itemId).orElse(null);
    }
    
    public List<Item> getAllItems() {
        return itemRepository.findByAvailableTrue();
    }
    
    public List<Item> getItemsBySeller(Long sellerId) {
        if (sellerId == null || sellerId <= 0) {
            return null;
        }
        return itemRepository.findBySellerId(sellerId);
    }
    
    public List<Item> searchItems(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllItems();
        }
        return itemRepository.searchItems(keyword);
    }
    
    public boolean updateItem(Item item) {
        if (item == null || item.getId() == null) {
            return false;
        }
        try {
            itemRepository.save(item);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean deleteItem(Long itemId) {
        if (itemId == null || itemId <= 0) {
            return false;
        }
        try {
            itemRepository.deleteById(itemId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean reduceStock(Long itemId, Integer quantity) {
        if (itemId == null || itemId <= 0 || quantity == null || quantity <= 0) {
            return false;
        }
        
        Optional<Item> itemOpt = itemRepository.findById(itemId);
        if (!itemOpt.isPresent() || !itemOpt.get().isInStock() || itemOpt.get().getStock() < quantity) {
            return false;
        }
        
        Item item = itemOpt.get();
        if (item.reduceStock(quantity)) {
            try {
                itemRepository.save(item);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }
    
    public boolean increaseStock(Long itemId, Integer quantity) {
        if (itemId == null || itemId <= 0 || quantity == null || quantity <= 0) {
            return false;
        }
        
        Optional<Item> itemOpt = itemRepository.findById(itemId);
        if (!itemOpt.isPresent()) {
            return false;
        }
        
        Item item = itemOpt.get();
        item.increaseStock(quantity);
        try {
            itemRepository.save(item);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean isItemAvailable(Long itemId) {
        if (itemId == null || itemId <= 0) {
            return false;
        }
        
        Optional<Item> itemOpt = itemRepository.findById(itemId);
        return itemOpt.isPresent() && itemOpt.get().isInStock();
    }
}
