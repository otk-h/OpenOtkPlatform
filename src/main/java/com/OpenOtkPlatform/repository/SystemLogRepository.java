package com.OpenOtkPlatform.repository;

import com.OpenOtkPlatform.domain.SystemLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface SystemLogRepository extends JpaRepository<SystemLog, Long> {
    
    List<SystemLog> findByUserId(Long userId);
    
    List<SystemLog> findByOperationType(String operationType);
    
    List<SystemLog> findByCreateTimeBetween(Date startTime, Date endTime);
    
    @Query("SELECT l FROM SystemLog l WHERE l.userId = :userId AND l.operationType = :operationType")
    List<SystemLog> findByUserIdAndOperationType(@Param("userId") Long userId, @Param("operationType") String operationType);
    
    @Query("SELECT l FROM SystemLog l WHERE l.createTime >= :startTime")
    List<SystemLog> findRecentLogs(@Param("startTime") Date startTime);
    
    List<SystemLog> findByDescriptionContainingIgnoreCase(String keyword);
}
