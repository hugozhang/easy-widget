package me.about.widget.oss;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * s3 oss
 *
 * @author: hugo.zxh
 * @date: 2023/04/04 15:09
 */
@Slf4j
public class S3OssServiceImpl implements OssService {

    private AmazonS3 ossClient;

    private String bucketName;

    public S3OssServiceImpl(AmazonS3 ossClient, String bucketName) {
        this.ossClient = ossClient;
        this.bucketName = bucketName;
    }

    @Override
    public void putObject(String objectName, File file) {
        ossClient.putObject(bucketName, objectName, file);
    }

    @Override
    public void putObject(String objectName, InputStream inputStream) throws IOException {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(inputStream.available());
        objectMetadata.setContentType("application/octet-stream");
        ossClient.putObject(bucketName, objectName, inputStream,objectMetadata);
    }

    @Override
    public InputStream getObject(String objectName) {
        S3Object s3Object = ossClient.getObject(bucketName, objectName);
        if (s3Object != null) {
            return s3Object.getObjectContent();
        }
        return null;
    }

    @Override
    public String getObjectURL(String objectName) {
        Date date = new Date();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        URL url = ossClient.generatePresignedUrl(bucketName, objectName, calendar.getTime());
        return url.toString();
    }

    @Override
    public void deleteObject(String objectName) {
        ossClient.deleteObject(bucketName,objectName);
    }
}
