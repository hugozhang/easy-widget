package me.about.widget.routing.exception;

/**
 *  database not found
 *
 * @author: hugo.zxh
 * @date: 2023/10/27 17:16
 */
public class DatabaseNotFoundException extends RuntimeException {
    public DatabaseNotFoundException(String message) {
        super(message);
    }
}
