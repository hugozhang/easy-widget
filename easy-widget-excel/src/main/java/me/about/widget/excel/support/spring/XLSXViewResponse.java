package me.about.widget.excel.support.spring;

import me.about.widget.excel.writer.XLSXWriter;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: hugo.zxh
 * @Date: 2020/11/01 23:48
 * @Description:
 */
public class XLSXViewResponse implements HandlerMethodReturnValueHandler {
    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return (AnnotationUtils.findAnnotation(returnType.getContainingClass(), XLSXView.class) != null
                || returnType.getMethodAnnotation(XLSXView.class) != null);
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter methodParameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        mavContainer.setRequestHandled(true);
        HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);

        response.setContentType("application/form-data");
        response.setHeader("Content-Disposition", "attachment;fileName=download.xlsx");

        if (returnValue instanceof List) {
            XLSXWriter.builder().toStream((List)returnValue,response.getOutputStream());
        }
    }
}
