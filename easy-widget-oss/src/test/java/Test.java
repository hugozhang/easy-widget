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

//        AwsClientBuilder.EndpointConfiguration endpointConfiguration = new AwsClientBuilder.EndpointConfiguration("http://113.138.242.248:9000","shanghai");
//        AWSCredentials awsCredentials = new BasicAWSCredentials("P94IF096RD3CMFGVRAVB","Bx5yLfax6eSDxgZllTNKC+IcOZTIhXde2CjU8RWM");

        AwsClientBuilder.EndpointConfiguration endpointConfiguration = new AwsClientBuilder.EndpointConfiguration("http://192.168.5.220:9000","shanghai");
        AWSCredentials awsCredentials = new BasicAWSCredentials("6Q6TGG2Q90DH7JJW6JD4","ze9g04fS6mmFnQ+nj1f95zQKCBn0JLAhovDb1b6r");
        AWSCredentialsProvider awsCredentialsProvider = new AWSStaticCredentialsProvider(awsCredentials);

        AmazonS3 amazonS3 = AmazonS3Client.builder()
                .withEndpointConfiguration(endpointConfiguration)
                .withClientConfiguration(clientConfiguration)
                .withCredentials(awsCredentialsProvider)
                .disableChunkedEncoding()
                .build();

        OssService ossService = new S3OssServiceImpl(amazonS3,"hmap-web");
        ossService.putObject("123.docx",new File("D:\\Downloads\\日志链路追踪 一   .docx"));
    }

}
