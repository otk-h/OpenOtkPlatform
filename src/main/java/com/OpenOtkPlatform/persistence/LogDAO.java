package com.OpenOtkPlatform.persistence;

import com.OpenOtkPlatform.domain.SystemLog;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 日志数据访问对象
 */
public class LogDAO {
    private DatabaseManager dbManager;
    
    public LogDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }
    
    public boolean insertLog(SystemLog log) {
        if (log == null) {
            return false;
        }
        
        String sql = "INSERT INTO system_logs (operation_type, user_id, description) VALUES (?, ?, ?)";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, log.getOperationType());
            if (log.getUserId() != null) {
                pstmt.setLong(2, log.getUserId());
            } else {
                pstmt.setNull(2, Types.BIGINT);
            }
            pstmt.setString(3, log.getDescription());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        log.setId(generatedKeys.getLong(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("插入日志失败: " + e.getMessage());
        }
        return false;
    }
    
    public SystemLog getLogById(Long logId) {
        if (logId == null || logId <= 0) {
            return null;
        }
        
        String sql = "SELECT * FROM system_logs WHERE id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, logId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToLog(rs);
            }
        } catch (SQLException e) {
            System.err.println("查询日志失败: " + e.getMessage());
        }
        return null;
    }
    
    public List<SystemLog> getLogsByUser(Long userId) {
        if (userId == null || userId <= 0) {
            return new ArrayList<>();
        }
        
        List<SystemLog> logs = new ArrayList<>();
        String sql = "SELECT * FROM system_logs WHERE user_id = ? ORDER BY create_time DESC";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                logs.add(mapResultSetToLog(rs));
            }
        } catch (SQLException e) {
            System.err.println("查询用户日志失败: " + e.getMessage());
        }
        return logs;
    }
    
    public List<SystemLog> getLogsByOperationType(String operationType) {
        if (operationType == null || operationType.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        List<SystemLog> logs = new ArrayList<>();
        String sql = "SELECT * FROM system_logs WHERE operation_type = ? ORDER BY create_time DESC";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, operationType.trim());
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                logs.add(mapResultSetToLog(rs));
            }
        } catch (SQLException e) {
            System.err.println("查询操作类型日志失败: " + e.getMessage());
        }
        return logs;
    }
    
    public List<SystemLog> getAllLogs() {
        List<SystemLog> logs = new ArrayList<>();
        String sql = "SELECT * FROM system_logs ORDER BY create_time DESC";
        
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                logs.add(mapResultSetToLog(rs));
            }
        } catch (SQLException e) {
            System.err.println("查询所有日志失败: " + e.getMessage());
        }
        return logs;
    }
    
    public boolean deleteLog(Long logId) {
        if (logId == null || logId <= 0) {
            return false;
        }
        
        String sql = "DELETE FROM system_logs WHERE id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, logId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("删除日志失败: " + e.getMessage());
        }
        return false;
    }
    
    public boolean deleteLogsByUser(Long userId) {
        if (userId == null || userId <= 0) {
            return false;
        }
        
        String sql = "DELETE FROM system_logs WHERE user_id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, userId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("删除用户日志失败: " + e.getMessage());
        }
        return false;
    }
    
    public boolean deleteLogsByDateRange(java.util.Date startDate, java.util.Date endDate) {
        if (startDate == null || endDate == null) {
            return false;
        }
        
        String sql = "DELETE FROM system_logs WHERE create_time BETWEEN ? AND ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setTimestamp(1, new Timestamp(startDate.getTime()));
            pstmt.setTimestamp(2, new Timestamp(endDate.getTime()));
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("删除日期范围日志失败: " + e.getMessage());
        }
        return false;
    }
    
    private SystemLog mapResultSetToLog(ResultSet rs) throws SQLException {
        SystemLog log = new SystemLog();
        log.setId(rs.getLong("id"));
        log.setOperationType(rs.getString("operation_type"));
        
        long userId = rs.getLong("user_id");
        if (!rs.wasNull()) {
            log.setUserId(userId);
        }
        
        log.setDescription(rs.getString("description"));
        log.setCreateTime(rs.getTimestamp("create_time"));
        return log;
    }
}
