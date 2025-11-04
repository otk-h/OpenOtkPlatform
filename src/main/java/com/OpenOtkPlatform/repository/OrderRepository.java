package com.OpenOtkPlatform.repository;

import com.OpenOtkPlatform.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    List<Order> findByBuyerId(Long buyerId);
    
    List<Order> findBySellerId(Long sellerId);
    
    List<Order> findByStatus(String status);
    
    @Query("SELECT o FROM Order o WHERE o.buyerId = :userId OR o.sellerId = :userId")
    List<Order> findByUserId(@Param("userId") Long userId);
    
    @Query("SELECT o FROM Order o WHERE o.status = :status AND (o.buyerId = :userId OR o.sellerId = :userId)")
    List<Order> findByStatusAndUserId(@Param("status") String status, @Param("userId") Long userId);
    
    List<Order> findByItemId(Long itemId);
}
