package me.about.widget.oss;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * OSS 服务类
 *
 * @author: hugo.zxh
 * @date: 2023/04/04 14:31
 */
public interface OssService {


    /**
     * 上传文件
     * @param objectName
     * @param file
     * @return
     */
    void putObject(String objectName, File file);

    /**
     * 上传文件
     * @param objectName
     * @param inputStream
     * @throws IOException
     */
    void putObject(String objectName, InputStream inputStream) throws IOException;

    /**
     * 获取文件
     * @param objectName
     * @return
     */
    InputStream getObject(String objectName);

    /**
     * 获取http地址
     * @param objectName
     * @return
     */
    String getObjectURL(String objectName);

    /**
     * 删除文件
     * @param objectName
     */
    void deleteObject(String objectName);

}
