package me.about.widget.excel.spring.support.writer;

import lombok.extern.slf4j.Slf4j;
import me.about.widget.excel.writer.XlsxWriter;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

        ExcelResponseBody excelResponseBody = methodParameter.getMethodAnnotation(ExcelResponseBody.class);
        try {
            String fileName = URLEncoder.encode(excelResponseBody.fileName() + suffix() + ".xlsx","UTF-8");

            HttpHeaders headers = response.getHeaders();
            headers.add("Content-Type", "application/octet-stream");
            headers.add("Content-Disposition","attachment;filename*=UTF-8''" + fileName);
            XlsxWriter.build(excelResponseBody.inputClass()).toOutputStream(body,response.getBody());
        } catch (Exception e) {
            log.error("ExcelResponseBodyAdvice error",e);
            throw new RuntimeException("ExcelResponseBodyAdvice error",e);
        }
        return null;
    }

    private String suffix() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate currentDate = LocalDate.now();
        String formattedDate = currentDate.format(formatter);
        return formattedDate + System.currentTimeMillis();
    }
}
