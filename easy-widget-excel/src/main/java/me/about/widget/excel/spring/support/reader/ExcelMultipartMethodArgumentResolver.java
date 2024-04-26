package me.about.widget.excel.spring.support.reader;

import me.about.widget.excel.Creator;
import me.about.widget.excel.reader.XlsxReader;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ExcelMultipartMethodArgumentResolver implements HandlerMethodArgumentResolver {

    private static final Map<Class<?>,MultiPartHandler> HANDLER_CACHE = new ConcurrentHashMap<>();
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        ExcelMultipart excelMultipart = parameter.getParameterAnnotation(ExcelMultipart.class);
        return excelMultipart != null && List.class.isAssignableFrom(parameter.getParameterType());
    }


    private static boolean isMultipartContent(HttpServletRequest request) {
        String contentType = request.getContentType();
        return (contentType != null && contentType.toLowerCase().startsWith("multipart/"));
    }
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        ExcelMultipart excelMultipart = parameter.getParameterAnnotation(ExcelMultipart.class);
        HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        if (!isMultipartContent(servletRequest)) {
            throw new MultipartException("Content-Type is error");
        }
        MultipartRequest multipartRequest = new StandardMultipartHttpServletRequest(servletRequest);
        
        List<MultipartFile> multipartFiles = multipartRequest
                .getFiles(excelMultipart.name());

        for(MultipartFile partFile : multipartFiles) {
            if (excelMultipart.handler() != Void.class) {
                MultiPartHandler multiPartHandlerFromCache = HANDLER_CACHE.get(excelMultipart.handler());
                if (multiPartHandlerFromCache != null) {
                    multiPartHandlerFromCache.doHandle(partFile);
                } else {
                    Object handler = Creator.of(excelMultipart.handler());
                    if (handler instanceof MultiPartHandler) {
                        MultiPartHandler multiPartHandler = (MultiPartHandler) handler;
                        HANDLER_CACHE.put(excelMultipart.handler(), multiPartHandler);
                        multiPartHandler.doHandle(partFile);
                    }
                }
            }
        }
        return multipartFiles.stream().map(multipartFile -> {
            try {
                return XlsxReader
                        .build(excelMultipart.outputClass())
                        .open(multipartFile.getInputStream()).sheetsParser();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
    }
}
