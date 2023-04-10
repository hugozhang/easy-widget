package me.about.widget.spring.mvc.result;


import java.io.Serializable;

/**
 * 统一返回值
 *
 * @author: hugo.zxh
 * @date: 2022/02/26 18:02
 * @description:
 *
 */
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

    public static Result failed(int code,String message) {
        return new Result(code,"error",message);
    }

    public static <T> Result failed(int code,String message,T data) {
        return new Result(code,"error",message,data);
    }

    public static <T> Result success(T data) {
        return new Result(200,"success","成功",data);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
