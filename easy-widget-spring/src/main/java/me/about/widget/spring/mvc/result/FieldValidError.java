package me.about.widget.spring.mvc.result;

import lombok.Getter;

import java.io.Serializable;

/**
 * spring valid
 *
 * @author: hugo.zxh
 * @date: 2022/02/26 18:04
 * @description:
 */

@Getter
public class FieldValidError implements Serializable {

    private String field;

    private String message;

    public void setField(String field) {
        this.field = field;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
