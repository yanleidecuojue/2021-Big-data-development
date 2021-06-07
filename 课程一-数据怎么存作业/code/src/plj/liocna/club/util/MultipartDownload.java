package plj.liocna.club.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;

/**
 * @author licona
 * @date 2021/6/3
 */
public class MultipartDownload {
    public static void multipartDownload(AmazonS3 s3, String bucketName, String filePath, String keyName, long partSize) {

        File file = new File(filePath);
        ObjectMetadata oMetaData = s3.getObjectMetadata(bucketName, keyName);

        S3Object o = null;
        S3ObjectInputStream s3is = null;
        FileOutputStream fos = null;
        try {
            if (oMetaData.getContentLength() <= partSize) {
                fos = new FileOutputStream(file);
                s3is = s3.getObject(new GetObjectRequest(bucketName, keyName)).getObjectContent();
                byte[] read_buf = new byte[64 * 1024];
                int read_len = 0;
                while ((read_len = s3is.read(read_buf)) > 0) {
                    fos.write(read_buf, 0, read_len);
                }
            } else {
                // Step 1: Initialize.
                final long contentLength = oMetaData.getContentLength();
                final GetObjectRequest downloadRequest =
                        new GetObjectRequest(bucketName, keyName);

                fos = new FileOutputStream(file);

                // Step 2: Download parts.
                long filePosition = 0;
                for (int i = 1; filePosition < contentLength; i++) {
                    // Last part can be less than 5 MB. Adjust part size.
                    partSize = Math.min(partSize, contentLength - filePosition);

                    // Create request to download a part.
                    downloadRequest.setRange(filePosition, filePosition + partSize);
                    o = s3.getObject(downloadRequest);

                    // download part and save to local file.
                    System.out.format("Downloading part %d\n", i);

                    filePosition += partSize + 1;
                    s3is = o.getObjectContent();
                    byte[] read_buf = new byte[64 * 1024];
                    int read_len = 0;
                    while ((read_len = s3is.read(read_buf)) > 0) {
                        fos.write(read_buf, 0, read_len);
                    }
                }
                // Step 3: Complete.
                System.out.println("Completing download");

                System.out.format("save %s to %s\n", keyName, filePath);
            }
        } catch (Exception e) {
            System.err.println(e.toString());

            System.exit(1);
        } finally {
            if (s3is != null) try {
                s3is.close();
            } catch (IOException e) {
            }
            if (fos != null) try {
                fos.close();
            } catch (IOException e) {
            }
        }
        System.out.println("Done!");
    }
}
