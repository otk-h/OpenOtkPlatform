package com.OpenOtkPlatform.repository;

import com.OpenOtkPlatform.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    
    List<Item> findBySellerId(Long sellerId);
    
    List<Item> findByAvailableTrue();
    
    List<Item> findByNameContainingIgnoreCase(String name);
    
    List<Item> findByDescriptionContainingIgnoreCase(String description);
    
    @Query("SELECT i FROM Item i WHERE i.name LIKE %:keyword% OR i.description LIKE %:keyword%")
    List<Item> searchItems(@Param("keyword") String keyword);
    
    @Query("SELECT i FROM Item i WHERE i.price BETWEEN :minPrice AND :maxPrice AND i.available = true")
    List<Item> findByPriceRange(@Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice);
    
    List<Item> findByStockGreaterThan(Integer stock);
}
