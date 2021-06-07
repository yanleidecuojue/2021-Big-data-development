package plj.liocna.club.util;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BreakpointDownload {
    public static void breakpointContinuation(AmazonS3 s3, String bucketName, String filePath, long partSize) throws IOException {
        // 获取到远程文件列表和文件ETag值
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest().withBucketName(bucketName);
        ObjectListing objects = s3.listObjects(listObjectsRequest);
        Map<String, Long> remoteFiles = new HashMap<>();
        do {
            for (S3ObjectSummary objectSummary : objects.getObjectSummaries()) {
                if((objectSummary.getKey().lastIndexOf("/") != objectSummary.getKey().length() - 1)) {
                    remoteFiles.put(objectSummary.getKey(), objectSummary.getSize());
                }
            }
            objects = s3.listNextBatchOfObjects(objects);
        } while (objects.isTruncated());
        //获取本地文件列表并计算哈希值
        Map<String, Long> localFiles = new HashMap<>();
        List<String> strings = new ReadFiles().readFiles(filePath, filePath);
        for (int i = 0; i < strings.size(); i++) {
            File file = new File(filePath + strings.get(i));
            localFiles.put(strings.get(i), file.length());
        }

        List<String> interruptedDownloadFilesKey = new ArrayList<>();
        for (Map.Entry<String,Long> entry: localFiles.entrySet()) {
            if(entry.getValue()!=null && remoteFiles.get(entry.getKey())!=null && !remoteFiles.get(entry.getKey()).equals(entry.getValue())) {
                System.out.println("被中断的文件名称: " + entry.getKey());
                interruptedDownloadFilesKey.add(entry.getKey());
            }
        }

        for (int j = 0; j < interruptedDownloadFilesKey.size(); j++) {
            breakpointDownload(s3, bucketName, filePath, interruptedDownloadFilesKey.get(j), partSize);
        }
    }

    public static void breakpointDownload(AmazonS3 s3, String bucketName, String filePath, String keyName, Long partSize) {
        // 首先读取本地已经下载下来的文件
        File localFile = new File(filePath + keyName);
        S3Object o = null;
        S3ObjectInputStream s3is = null;
        RandomAccessFile randomAccessFile = null;
        try {
            // Step 1: Initialize.
            ObjectMetadata oMetaData = s3.getObjectMetadata(bucketName, keyName);
            final long contentLength = oMetaData.getContentLength();
            final GetObjectRequest downloadRequest =
                    new GetObjectRequest(bucketName, keyName);

            randomAccessFile = new RandomAccessFile(filePath + keyName, "rw");
            // 将写文件指针置于文件尾部
            randomAccessFile.seek(randomAccessFile.length());

            // Step 2: Download parts.
            System.out.println("我们从根据本地文件大小来从未下载部分开始继续下载文件");
            System.out.println("目前已下载"+ localFile.getName() +"百分比:" + (double)localFile.length()/oMetaData.getContentLength() * 100 + "%");
            long filePosition = localFile.length();
            for (int i = 1; filePosition < contentLength; i++) {
                // Last part can be less than 5 MB. Adjust part size.
                partSize = Math.min(partSize, contentLength - filePosition);

                // Create request to download a part.
                downloadRequest.setRange(filePosition, filePosition + partSize);
                o = s3.getObject(downloadRequest);

                // download part and save to local file.
                System.out.format("Downloading part %d\n", i);

                filePosition += partSize+1;
                s3is = o.getObjectContent();
                byte[] read_buf = new byte[64 * 1024];
                int read_len = 0;
                while ((read_len = s3is.read(read_buf)) > 0) {
                    randomAccessFile.write(read_buf, 0, read_len);
                }
            }

            System.out.println(localFile.length());

            // Step 3: Complete.
            System.out.println("Completing download");

            System.out.format("save %s to %s\n", keyName, filePath);
        } catch (Exception e) {
            System.err.println(e.toString());

            System.exit(1);
        } finally {
            if (s3is != null) try { s3is.close(); } catch (IOException e) { }
            if (randomAccessFile != null) try { randomAccessFile.close(); } catch (IOException e) { }
        }

    }
}
