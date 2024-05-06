package me.about.widget.spring.mvc.exception;

import lombok.Getter;

/**
 * 业务异常
 *
 * @author: hugo.zxh
 * @date: 2022/02/26 18:02
 * @description:
 */

@Getter
public class BizException extends RuntimeException {

    private final int code;

    public BizException(int code, String message) {
        super(message);
        this.code = code;
    }

}
