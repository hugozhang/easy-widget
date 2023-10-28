package me.about.widget.routing.exception;

/**
 *  extend properties not found
 *
 * @author: hugo.zxh
 * @date: 2023/10/27 17:16
 */
public class DatabaseRulesNotFoundException extends RuntimeException {
    public DatabaseRulesNotFoundException(String message) {
        super(message);
    }
}
