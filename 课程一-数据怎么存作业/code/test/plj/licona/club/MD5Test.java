package plj.licona.club;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import org.junit.Test;
import plj.liocna.club.util.ByteToHex;
import plj.liocna.club.util.MultipartFileEtag;
import plj.liocna.club.util.ReadFiles;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.util.Md5Utils;

/**
 * @author licona
 * @date 2021/6/3
 */
public class MD5Test {
    private final static String bucketName = "liyuming-repository";
    private final static String filePath = "./liyuming-repository/";
    private final static String accessKey = "9955B832123E755E5E98";
    private final static String secretKey = "WzI3RTA1RDEyRjc1NDRERjU4NDQwNDg4MzVBNTcz";
    private final static String serviceEndpoint = "http://10.16.0.1:81";
    private final static String signingRegion = "";

    private static long partSize = 5 << 20;

    @Test
    public void test() throws IOException {
        final BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        final ClientConfiguration ccfg = new ClientConfiguration().withUseExpectContinue(true);

        final AwsClientBuilder.EndpointConfiguration endpoint = new AwsClientBuilder.EndpointConfiguration(serviceEndpoint, signingRegion);

        final AmazonS3 s3 = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials)).withClientConfiguration(ccfg)
                .withEndpointConfiguration(endpoint).withPathStyleAccessEnabled(true).build();

        // 获取到远程文件列表和文件ETag值
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest().withBucketName(bucketName);
        ObjectListing objects = s3.listObjects(listObjectsRequest);
        Map<String, String> remoteFiles = new HashMap<>();
        do {
            for (S3ObjectSummary objectSummary : objects.getObjectSummaries()) {
                remoteFiles.put(objectSummary.getKey(), objectSummary.getETag());
            }
            objects = s3.listNextBatchOfObjects(objects);
        } while (objects.isTruncated());
        //获取本地文件列表并计算哈希值
        Map<String, String> localFiles = new HashMap<>();
        List<String> strings = new ReadFiles().readFiles(filePath, filePath);
        for (int i = 0; i < strings.size(); i++) {
            File file = new File(filePath + strings.get(i));
            if (file.length() > partSize) {
                localFiles.put(strings.get(i), MultipartFileEtag.getMultipartFileEtag(filePath + strings.get(i), (int) partSize));
            } else {
                localFiles.put(strings.get(i), ByteToHex.bytesToHex(Md5Utils.computeMD5Hash(file)));
            }
        }

        for (Map.Entry<String, String> entry : localFiles.entrySet()) {
            if (entry.getValue().equals(remoteFiles.get(entry.getKey()))) {
                System.out.print("same ");
            } else {
                System.out.println(entry);
                System.out.println(remoteFiles.get(entry.getKey()));
                System.out.println("本地文件与远程文件不一样");
            }
        }
    }
}
