package me.about.widget.spring.mvc.advice;

import com.alibaba.fastjson.JSON;
import me.about.widget.spring.mvc.result.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 统一返回值
 *
 * @author: hugo.zxh
 * @date: 2022/02/26 18:02
 * @description:
 */

@ControllerAdvice
public class ResponseBodyHandler implements ResponseBodyAdvice<Object> {

    @Value("${me.about.widget.spring.match-url:/hmap}")
    private String matchUrl;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter returnType,
                                  MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {
        if (!request.getURI().getPath().startsWith(matchUrl)
                || body instanceof Result) {
            return body;
        }

        if (body instanceof String) {
            return JSON.toJSONString(Result.success(body));
        }

        Result result = new Result<>();
        result.setCode(0);
        result.setType("success");
        result.setMessage("成功");
        result.setData(body);
        return result;
    }
}
