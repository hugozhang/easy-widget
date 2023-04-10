package me.about.widget.spring.mvc.result;

import java.io.Serializable;

/**
 * spring valid
 *
 * @author: hugo.zxh
 * @date: 2022/02/26 18:04
 * @description:
 */

public class ValidFieldError implements Serializable {

    private String field;

    private String message;

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
