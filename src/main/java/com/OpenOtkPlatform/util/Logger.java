package com.OpenOtkPlatform.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

/**
 * 日志记录器 - 观察者模式
 */
public class Logger {
    private static Logger instance;
    private List<LogObserver> observers;
    
    private Logger() {
        this.observers = new ArrayList<>();
    }
    
    public static Logger getInstance() {
        if (instance == null) {
            instance = new Logger();
        }
        return instance;
    }
    
    public void addObserver(LogObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }
    
    public void removeObserver(LogObserver observer) {
        if (observer != null) {
            observers.remove(observer);
        }
    }
    
    public void notifyObservers(String message) {
        for (LogObserver observer : observers) {
            observer.update(message);
        }
    }
    
    public void info(String message) {
        String logMessage = formatLogMessage("INFO", message);
        System.out.println(logMessage);
        notifyObservers(logMessage);
    }
    
    public void warn(String message) {
        String logMessage = formatLogMessage("WARN", message);
        System.out.println(logMessage);
        notifyObservers(logMessage);
    }
    
    public void error(String message) {
        String logMessage = formatLogMessage("ERROR", message);
        System.err.println(logMessage);
        notifyObservers(logMessage);
    }
    
    public void debug(String message) {
        String logMessage = formatLogMessage("DEBUG", message);
        System.out.println(logMessage);
        notifyObservers(logMessage);
    }
    
    private String formatLogMessage(String level, String message) {
        return String.format("[%s] %s - %s", 
            new Date().toString(), level, message);
    }
}

/**
 * 日志观察者接口
 */
interface LogObserver {
    void update(String message);
}

/**
 * 控制台日志观察者实现
 */
class ConsoleLogObserver implements LogObserver {
    @Override
    public void update(String message) {
        System.out.println("Observer: " + message);
    }
}

/**
 * 文件日志观察者实现
 */
class FileLogObserver implements LogObserver {
    @Override
    public void update(String message) {
        // 这里可以实现将日志写入文件的功能
        // 为了简化，这里只打印到控制台
        System.out.println("File Observer: " + message);
    }
}
