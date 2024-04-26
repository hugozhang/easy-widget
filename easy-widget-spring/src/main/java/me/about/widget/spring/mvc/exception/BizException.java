package me.about.widget.spring.mvc.exception;

/**
 * 业务异常
 *
 * @author: hugo.zxh
 * @date: 2022/02/26 18:02
 * @description:
 */

public class BizException extends RuntimeException {

    private int code;

    public BizException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
