package me.about.widget.oss.spring;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import me.about.widget.oss.S3OssServiceImpl;
import me.about.widget.oss.OssService;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Value;

/**
 * OSS FactoryBean
 *
 * @author: hugo.zxh
 * @date: 2023/04/04 17:09
 */
public class OssServiceFactoryBean implements FactoryBean<OssService> {

    @Value("${oss.endpoint:https://oss-cn-north-2-gov-1.aliyuncs.com}")
    private String endpoint;

    @Value("${oss.access-key-id:}")
    private String accessKeyId;

    @Value("${oss.access-key-secret:}")
    private String accessKeySecret;

    @Value("${oss.bucket-name:hmap-web}")
    private String bucketName;

    @Value("${oss.region:north-2-gov-1}")
    private String region;

    @Override
    public OssService getObject() throws Exception {

        ClientConfiguration clientConfiguration = new ClientConfiguration();
        clientConfiguration.setMaxConnections(100);

        AwsClientBuilder.EndpointConfiguration endpointConfiguration = new AwsClientBuilder.EndpointConfiguration(endpoint,region);
        AWSCredentials awsCredentials = new BasicAWSCredentials(accessKeyId,accessKeySecret);
        AWSCredentialsProvider awsCredentialsProvider = new AWSStaticCredentialsProvider(awsCredentials);

        AmazonS3 amazonS3 = AmazonS3Client.builder()
                .withEndpointConfiguration(endpointConfiguration)
                .withClientConfiguration(clientConfiguration)
                .withCredentials(awsCredentialsProvider)
                .disableChunkedEncoding()
                .build();

        OssService ossService = new S3OssServiceImpl(amazonS3,bucketName);
        return ossService;
    }

    @Override
    public Class<?> getObjectType() {
        return OssService.class;
    }
}
