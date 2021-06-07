package plj.licona.club;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.junit.Test;
import plj.liocna.club.util.MultipartUpload;

/**
 * @author licona
 * @date 2021/6/3
 */
public class MultipartUploadTest {
    private final static String bucketName = "liyuming-repository";
    private final static String filePath = "C:/liyuming-repository/test/test-multipartupload";
    private final static String accessKey = "9955B832123E755E5E98";
    private final static String secretKey = "WzI3RTA1RDEyRjc1NDRERjU4NDQwNDg4MzVBNTcz";
    private final static String serviceEndpoint = "http://10.16.0.1:81";
    private final static String signingRegion = "";

    private static final long partSize = 20 << 20;

    @Test
    public void multipartUploadTest() {
        final BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        final ClientConfiguration ccfg = new ClientConfiguration().withUseExpectContinue(true);

        final AwsClientBuilder.EndpointConfiguration endpoint = new AwsClientBuilder.EndpointConfiguration(serviceEndpoint, signingRegion);

        final AmazonS3 s3 = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials)).withClientConfiguration(ccfg)
                .withEndpointConfiguration(endpoint).withPathStyleAccessEnabled(true).build();

        MultipartUpload.multipartUpload(s3, bucketName, filePath,"test/test-multipartupload", partSize);
    }
}
