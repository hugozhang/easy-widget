package me.about.widget.excel.spring.support.reader;

import org.springframework.web.multipart.MultipartFile;

public interface MultiPartHandler {

    default void doHandle(MultipartFile multipartFile) throws Exception {

    }

}
