package com.OpenOtkPlatform.api;

import com.OpenOtkPlatform.domain.Item;
import com.OpenOtkPlatform.service.ItemService;
import com.OpenOtkPlatform.service.LogService;
import com.OpenOtkPlatform.service.UserService;
import com.OpenOtkPlatform.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
public class ItemController {
    
    @Autowired
    private ItemService itemService;
    
    @Autowired
    private LogService logService;
    
    @Autowired
    private UserService userService;
    
    @PostMapping
    public ResponseEntity<?> publishItem(@RequestBody PublishItemRequest request) {
        String name = request.getName();
        String description = request.getDescription();
        Double price = request.getPrice();
        Long sellerId = request.getSellerId();
        Integer stock = request.getStock();
        
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
            return ResponseEntity.badRequest().body(new ApiResponse(false, "参数无效"));
        }
        
        // 验证卖家是否存在
        if (userService.getUserById(sellerId) == null) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "卖家不存在"));
        }
        
        boolean success = itemService.publishItem(name, description, price, sellerId, stock);
        if (success) {
            // 获取最新发布的商品ID（这里简化处理，实际应该返回商品对象）
            List<Item> sellerItems = itemService.getItemsBySeller(sellerId);
            if (!sellerItems.isEmpty()) {
                Item latestItem = sellerItems.get(sellerItems.size() - 1);
                logService.logItemPublish(sellerId, latestItem.getId());
            }
            return ResponseEntity.ok(new ApiResponse(true, "商品发布成功"));
        }
        return ResponseEntity.badRequest().body(new ApiResponse(false, "商品发布失败"));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getItemById(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "商品ID无效"));
        }
        
        Item item = itemService.getItemById(id);
        if (item == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(item);
    }
    
    @GetMapping
    public ResponseEntity<?> getAllItems() {
        List<Item> items = itemService.getAllItems();
        return ResponseEntity.ok(items);
    }
    
    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<?> getItemsBySeller(@PathVariable Long sellerId) {
        if (sellerId == null || sellerId <= 0) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "卖家ID无效"));
        }
        
        List<Item> items = itemService.getItemsBySeller(sellerId);
        return ResponseEntity.ok(items);
    }
    
    @GetMapping("/search")
    public ResponseEntity<?> searchItems(@RequestParam String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllItems();
        }
        
        List<Item> items = itemService.searchItems(keyword);
        return ResponseEntity.ok(items);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateItem(@PathVariable Long id, @RequestBody UpdateItemRequest request) {
        String name = request.getName();
        String description = request.getDescription();
        Double price = request.getPrice();
        Integer stock = request.getStock();
        
        if (id == null || id <= 0
            || name == null || name.trim().isEmpty()
            || description == null || description.trim().isEmpty()
            || price == null || price <= 0
            || stock == null || stock < 0
            || !ValidationUtil.isValidItemName(name)
            || !ValidationUtil.isValidItemDescription(description)
            || !ValidationUtil.isValidPrice(price)
            || !ValidationUtil.isValidStock(stock)
        ) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "参数无效"));
        }
        
        Item item = itemService.getItemById(id);
        if (item == null) {
            return ResponseEntity.notFound().build();
        }
        
        item.setName(name);
        item.setDescription(description);
        item.setPrice(price);
        item.setStock(stock);
        
        boolean success = itemService.updateItem(item);
        if (success) {
            logService.logUserOperation("UPDATE_ITEM", item.getSellerId(), 
                String.format("更新商品信息，商品ID: %d", id));
            return ResponseEntity.ok(new ApiResponse(true, "商品更新成功"));
        }
        return ResponseEntity.badRequest().body(new ApiResponse(false, "商品更新失败"));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteItem(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "商品ID无效"));
        }
        
        Item item = itemService.getItemById(id);
        if (item == null) {
            return ResponseEntity.notFound().build();
        }
        
        boolean success = itemService.deleteItem(id);
        if (success) {
            logService.logUserOperation("DELETE_ITEM", item.getSellerId(), 
                String.format("删除商品，商品ID: %d", id));
            return ResponseEntity.ok(new ApiResponse(true, "商品删除成功"));
        }
        return ResponseEntity.badRequest().body(new ApiResponse(false, "商品删除失败"));
    }
    
    @PostMapping("/{id}/reduce-stock")
    public ResponseEntity<?> reduceStock(@PathVariable Long id, @RequestParam Integer quantity) {
        if (id == null || id <= 0 || quantity == null || quantity <= 0) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "参数无效"));
        }
        
        boolean success = itemService.reduceStock(id, quantity);
        if (success) {
            Item item = itemService.getItemById(id);
            if (item != null) {
                logService.logUserOperation("REDUCE_STOCK", item.getSellerId(), 
                    String.format("减少商品库存，商品ID: %d, 数量: %d", id, quantity));
            }
            return ResponseEntity.ok(new ApiResponse(true, "库存减少成功"));
        }
        return ResponseEntity.badRequest().body(new ApiResponse(false, "库存减少失败"));
    }
    
    // 请求DTO类
    public static class PublishItemRequest {
        private String name;
        private String description;
        private Double price;
        private Long sellerId;
        private Integer stock;
        
        // getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Double getPrice() { return price; }
        public void setPrice(Double price) { this.price = price; }
        public Long getSellerId() { return sellerId; }
        public void setSellerId(Long sellerId) { this.sellerId = sellerId; }
        public Integer getStock() { return stock; }
        public void setStock(Integer stock) { this.stock = stock; }
    }

    public static class UpdateItemRequest {
        private String name;
        private String description;
        private Double price;
        private Integer stock;
        
        // getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Double getPrice() { return price; }
        public void setPrice(Double price) { this.price = price; }
        public Integer getStock() { return stock; }
        public void setStock(Integer stock) { this.stock = stock; }
    }

    public static class ApiResponse {
        private boolean success;
        private String message;
        
        public ApiResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        
        // getters and setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}
