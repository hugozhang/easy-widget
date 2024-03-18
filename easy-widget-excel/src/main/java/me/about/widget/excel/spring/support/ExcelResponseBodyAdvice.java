package me.about.widget.excel.spring.support;

import lombok.extern.slf4j.Slf4j;
import me.about.widget.excel.writer.XlsxWriter;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.net.URLEncoder;
import java.util.List;

@Slf4j
@ControllerAdvice
public class ExcelResponseBodyAdvice implements ResponseBodyAdvice<List> {


    public ExcelResponseBodyAdvice() {
    }

    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> converterType) {
        return methodParameter.getMethodAnnotation(ExcelResponseBody.class) != null;
    }

    @Override
    public List beforeBodyWrite(List body, MethodParameter methodParameter, MediaType selectedContentType,
                                Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                ServerHttpRequest request,
                                ServerHttpResponse response) {
        if(CollectionUtils.isEmpty(body)) {
            return null;
        }

        ExcelResponseBody excelResponseBody = methodParameter.getMethodAnnotation(ExcelResponseBody.class);

        try {
            HttpHeaders headers = response.getHeaders();
            headers.add("Content-Type", "application/octet-stream");
            headers.add("Content-Disposition","attachment;filename*=UTF-8''" + URLEncoder.encode(excelResponseBody.fileName() + ".xlsx","UTF-8"));
            XlsxWriter.build(excelResponseBody.inputClass()).toOutputStream(body,response.getBody());
        } catch (Exception e) {
            log.error("ExcelResponseBodyAdvice error",e);
            throw new RuntimeException("ExcelResponseBodyAdvice error",e);
        }
        return null;
    }
}
