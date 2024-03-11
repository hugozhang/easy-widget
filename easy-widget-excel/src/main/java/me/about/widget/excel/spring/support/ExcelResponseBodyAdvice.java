package me.about.widget.excel.spring.support;

import lombok.extern.slf4j.Slf4j;
import me.about.widget.excel.writer.XlsxWriter;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.List;

@Slf4j
public class ExcelResponseBodyAdvice implements ResponseBodyAdvice<List> {


    public ExcelResponseBodyAdvice() {
        System.out.println("ExcelResponseBodyAdvice");
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
            HttpServletResponse httpServletResponse = (HttpServletResponse)response;

            httpServletResponse.setCharacterEncoding("utf-8");
            httpServletResponse.setContentType("application/octet-stream");
            httpServletResponse.setHeader("Content-Disposition", "attachment;filename*=UTF-8''" + URLEncoder.encode(excelResponseBody.fileName() + ".xlsx","UTF-8"));

            XlsxWriter.build(excelResponseBody.outputClass()).toOutputStream(body,httpServletResponse.getOutputStream());
        } catch (Exception e) {
            log.error("ExcelResponseBodyAdvice error",e);
            throw new RuntimeException("ExcelResponseBodyAdvice error",e);
        }
        return null;
    }
}
