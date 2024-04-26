package me.about.widget.spring.mvc.result;


import lombok.Getter;

import java.io.Serializable;

/**
 * 统一返回值
 *
 * @author: hugo.zxh
 * @date: 2022/02/26 18:02
 * @description:
 *
 */
@Getter
public class Result<T> implements Serializable {

    private int code;

    private String type;

    private String message;

    private T data;

    public Result() {}

    public Result(int code,String type,String message) {
        this.code = code;
        this.type = type;
        this.message = message;
    }

    public Result(int code,String type,String message,T data) {
        this.code = code;
        this.type = type;
        this.message = message;
        this.data = data;
    }

    public static <T> Result<T> failed(int code,String message) {
        return new Result<T>(code,"error",message);
    }

    public static <T> Result<T> failed(int code,String message,T data) {
        return new Result<T>(code,"error",message,data);
    }

    public static <T> Result<T> success(T data) {
        return new Result<T>(200,"success","成功",data);
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setData(T data) {
        this.data = data;
    }

    public void setType(String type) {
        this.type = type;
    }
}
