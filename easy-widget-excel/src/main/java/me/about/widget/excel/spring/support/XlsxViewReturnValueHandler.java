package me.about.widget.excel.spring.support;

import me.about.widget.excel.writer.XlsxWriter;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * spring view support
 * 参考链接：
 * http://zeng233.github.io/2016/11/02/6.7spring%20MVC%E5%A4%84%E7%90%86Excel%E8%A7%86%E5%9B%BE%E7%9A%84%E4%B8%89%E7%A7%8D%E6%96%B9%E5%BC%8F/
 *
 * @author: hugo.zxh
 * @date: 2020/11/01 23:48
 * @description:
 */

public class XlsxViewReturnValueHandler implements HandlerMethodReturnValueHandler {

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return (AnnotationUtils.findAnnotation(returnType.getContainingClass(), XlsxView.class) != null
                || returnType.getMethodAnnotation(XlsxView.class) != null);
    }

    @Override
    public void handleReturnValue(Object returnValue,
                                  MethodParameter methodParameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest) throws Exception {
        mavContainer.setRequestHandled(true);
        HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
        XlsxView xlsxView = methodParameter.getMethodAnnotation(XlsxView.class);

        String fileName = URLEncoder.encode(xlsxView.fileName() + suffix() + ".xlsx","UTF-8");

        response.setCharacterEncoding("utf-8");
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment;filename*=UTF-8''" + fileName);

        if (returnValue instanceof List) {
            XlsxWriter.build(xlsxView.inputClass()).toOutputStream((List)returnValue,response.getOutputStream());
        }
        response.getOutputStream().flush();
    }

    private String suffix() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate currentDate = LocalDate.now();
        String formattedDate = currentDate.format(formatter);
        return formattedDate + System.currentTimeMillis();
    }
}
