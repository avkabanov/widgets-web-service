package com.kabanov.widgets.utils;

/**
 * @author Kabanov Alexey
 */
public class QueryUtils {
    
    public static String update(String table) {
        return "UPDATE " + table + " ";
    }

    public static String set(String column, String newValue) {
        return "SET " + column + " = " + newValue + " ";
    }

    public static String where(String column, String condition, String value) {
        return "WHERE " + column + " " + condition + " " + value + " ";
    }

    public static String and(String column, String condition, String value) {
        return "AND " + column + " " + condition + " " + value + " ";
    }
    
    public static String from(String left, String joinOperation, String right) {
        return "FROM " + left + " " + joinOperation + " " + right + " ";
    }

    public static String on(String left, String operation, String right) {
        return "FROM " + left + " " + operation + " " + right + " ";
    }
    
    public static String orderBy(String field) {
        return "ORDER BY " + field + " ";
    }
    
    public static String select (String field) {
        return "SELECT " + field + " ";
    }
    
       
}
