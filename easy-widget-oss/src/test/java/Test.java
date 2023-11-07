import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import me.about.widget.oss.OssService;
import me.about.widget.oss.S3OssServiceImpl;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hugo.zxh
 * @date: 2023/09/28 15:45
 */
public class Test {

    public static void main(String[] args) {

        ClientConfiguration clientConfiguration = new ClientConfiguration();
        clientConfiguration.setMaxConnections(100);

        AwsClientBuilder.EndpointConfiguration endpointConfiguration = new AwsClientBuilder.EndpointConfiguration("http://192.168.5.220:9000","shanghai");
        AWSCredentials awsCredentials = new BasicAWSCredentials("EVZAX8MYH5MH21NM593L","gqPu1l0PXPy3bU+imKSS27OS1oTy9AwaHjJQe+Pk");
        AWSCredentialsProvider awsCredentialsProvider = new AWSStaticCredentialsProvider(awsCredentials);

        AmazonS3 amazonS3 = AmazonS3Client.builder()
                .withEndpointConfiguration(endpointConfiguration)
                .withClientConfiguration(clientConfiguration)
                .withCredentials(awsCredentialsProvider)
                .disableChunkedEncoding()
                .build();

        OssService ossService = new S3OssServiceImpl(amazonS3,"test1");
        ossService.putObject("123.docx",new File("D:\\Downloads\\日志链路追踪 一   .docx"));
    }

}
