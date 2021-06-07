package plj.liocna.club;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.Md5Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import plj.liocna.club.util.*;
import plj.liocna.club.util.MultipartUpload;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author licona
 * @date 2021/6/3
 */
public class Main {
    private final static String bucketName = "liyuming-repository";
    private final static String filePath = "C:/liyuming-repository/";
    private final static String accessKey = "9955B832123E755E5E98";
    private final static String secretKey = "WzI3RTA1RDEyRjc1NDRERjU4NDQwNDg4MzVBNTcz";
    private final static String serviceEndpoint = "http://10.16.0.1:81";
    private final static String signingRegion = "";

    private static final long partSize = 20 << 20;

    public static void main(String[] args) throws IOException, InterruptedException {
        final BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        final ClientConfiguration ccfg = new ClientConfiguration().withUseExpectContinue(true);


        final EndpointConfiguration endpoint = new EndpointConfiguration(serviceEndpoint, signingRegion);

        final AmazonS3 s3 = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials)).withClientConfiguration(ccfg)
                .withEndpointConfiguration(endpoint).withPathStyleAccessEnabled(true).build();


        System.out.println("----------------------------首先我们处理之前程序运行时候断点下载问题-------------------------------");
        BreakpointDownload.breakpointContinuation(s3, bucketName, filePath, partSize);
        System.out.println("----------------------------断点续传之下载问题处理完成------------------------------");

        // 程序启动时候首先处理断点续传
        System.out.println("----------------------------首先我们处理之前程序运行时候断点上传问题-------------------------------");
        BreakpointUpload.breakpointContinuation(s3, bucketName, filePath, partSize);
        System.out.println("----------------------------断点上传问题处理完成------------------------------");

        // 获取到远程文件列表和文件ETag值
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest().withBucketName(bucketName);
        ObjectListing objects = s3.listObjects(listObjectsRequest);
        Map<String, String> remoteFiles = new HashMap<>();
        do {
            for (S3ObjectSummary objectSummary : objects.getObjectSummaries()) {
                if((objectSummary.getKey().lastIndexOf("/") != objectSummary.getKey().length() - 1)) {
                    remoteFiles.put(objectSummary.getKey(), objectSummary.getETag());
                }
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
        System.out.println("---------------首先我们将Bucket文件同步到本地并解决相关冲突-----------------");
        for(Map.Entry<String, String> entry: remoteFiles.entrySet()) {
            //远程多余或修改的文件
            if(entry.getValue() != null && localFiles.get(entry.getKey())  == null) {
                System.out.println(entry.getKey());
                MultipartDownload.multipartDownload(s3, bucketName,filePath +entry.getKey(), entry.getKey(), partSize);
            }
            // 对于本地多余的文件，我们暂时不做任何操作，在之后向服务器同步时候将其上传到服务器即可
        }
        System.out.println("--------------------------同步到本地已经完成----------------------------");

        while (true) {
            // 获取到远程文件列表和文件ETag值
            listObjectsRequest = new ListObjectsRequest().withBucketName(bucketName);
            objects = s3.listObjects(listObjectsRequest);
            remoteFiles = new HashMap<>();
            do {
                for (S3ObjectSummary objectSummary : objects.getObjectSummaries()) {
                    if((objectSummary.getKey().lastIndexOf("/") != objectSummary.getKey().length() - 1)) {
                        remoteFiles.put(objectSummary.getKey(), objectSummary.getETag());
                    }
                }
                objects = s3.listNextBatchOfObjects(objects);
            } while (objects.isTruncated());
            //获取本地文件列表并计算哈希值
            localFiles = new HashMap<>();
            strings = new ReadFiles().readFiles(filePath, filePath);
            for (int i = 0; i < strings.size(); i++) {
                File file = new File(filePath + strings.get(i));
                if (file.length() > partSize) {
                    localFiles.put(strings.get(i), MultipartFileEtag.getMultipartFileEtag(filePath + strings.get(i), (int) partSize));
                } else {
                    localFiles.put(strings.get(i), ByteToHex.bytesToHex(Md5Utils.computeMD5Hash(file)));
                }
            }

            System.out.println("---------------开始执行同步本地文件到远程操作-----------------");
            /**
             * 之后我们将实时监控本地文件，每三十秒对本地文件进行扫描
             * 1.对于本地添加的文件，我们将执行上传到远程库
             * 2.对于本地修改的文件，我们也将上传到远程库
             * 3.对于本地删除的文件，我们将删除远程库中此类文件
             */
            System.out.println("首先，我们上传本地添加或修改的文件到远程库-----------------");
            for(Map.Entry<String, String> entry: localFiles.entrySet()) {
                // 本地添加的文件
                if(entry.getKey() != null && remoteFiles.get(entry.getKey()) == null) {
                    MultipartUpload.multipartUpload(s3, bucketName, filePath + entry.getKey(), entry.getKey(), partSize);
                    System.out.println("本地添加的文件处理成功，文件名称为: ");
                    System.out.println(entry.getKey());
                }

                // 本地修改的文件
                if(entry.getKey() != null && remoteFiles.get(entry.getKey()) != null && !remoteFiles.get(entry.getKey()).equals(entry.getValue())) {
                    MultipartUpload.multipartUpload(s3, bucketName, filePath + entry.getKey(), entry.getKey(), partSize);
                    System.out.println("本地修改的文件处理成功，文件名称为: ");
                    System.out.println(entry.getKey());
                }
            }

            System.out.println("之后，我们处理本地删除的文件------------------");
            for(Map.Entry<String, String> entry: remoteFiles.entrySet()) {
                // 本地删除的文件
                if(entry.getKey() != null && localFiles.get(entry.getKey()) == null) {
                    DeleteRemoteFile.deleteRemoteFile(s3, bucketName, entry.getKey());
                    System.out.println("本地删除的文件处理成功，文件名称为: ");
                    System.out.println(entry.getKey());
                }
            }
            System.out.println("---------------同步本地文件到远程已经完成，程序将睡眠30s，之后会对文件再次扫描-----------------");
            // 休眠三十秒
            try {
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
