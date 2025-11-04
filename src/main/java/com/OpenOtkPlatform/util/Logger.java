package com.OpenOtkPlatform.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 日志记录器 - 观察者模式
 */
public class Logger {
    private static Logger instance;
    private List<LogObserver> observers;
    
    private Logger() {} // TODO - finish me
    
    public static Logger getInstance() { return null; } // TODO - finish me
    
    public void addObserver(LogObserver observer) {} // TODO - finish me
    
    public void removeObserver(LogObserver observer) {} // TODO - finish me
    
    public void notifyObservers(String message) {} // TODO - finish me
    
    public void info(String message) {} // TODO - finish me
    
    public void warn(String message) {} // TODO - finish me
    
    public void error(String message) {} // TODO - finish me
    
    public void debug(String message) {} // TODO - finish me
}

/**
 * 日志观察者接口
 */
interface LogObserver {
    void update(String message);
}
