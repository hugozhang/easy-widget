package me.about.widget.spring.mvc.exception;

/**
 * 业务异常
 *
 * @Author: hugo.zxh
 * @Date: 2022/02/26 18:02
 * @Description:
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
