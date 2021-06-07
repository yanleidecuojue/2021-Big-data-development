package plj.liocna.club.util;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;

import java.io.File;
import java.util.ArrayList;

public class MultipartUpload {
    public static void multipartUpload(AmazonS3 s3, String bucketName, String filePath, String keyName, long partSize) {
        File file = new File(filePath);
        if (file.length() <= partSize) {
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, keyName, file);
            s3.putObject(putObjectRequest);
        } else {
            // Create a list of UploadPartResponse objects. You get one of these
            // for each part upload.
            ArrayList<PartETag> partETags = new ArrayList<PartETag>();

            long contentLength = file.length();
            String uploadId = null;
            try {
                // Step 1: Initialize.
                InitiateMultipartUploadRequest initRequest = new InitiateMultipartUploadRequest(bucketName, keyName);
                uploadId = s3.initiateMultipartUpload(initRequest).getUploadId();
                System.out.format("Created upload ID was %s\n", uploadId);

                // Step 2: Upload parts.
                long filePosition = 0;
                for (int i = 1; filePosition < contentLength; i++) {
                    // Last part can be less than 5 MB. Adjust part size.
                    partSize = Math.min(partSize, contentLength - filePosition);

                    // Create request to upload a part.
                    UploadPartRequest uploadRequest = new UploadPartRequest().withBucketName(bucketName).withKey(keyName)
                            .withUploadId(uploadId).withPartNumber(i).withFileOffset(filePosition).withFile(file)
                            .withPartSize(partSize);

                    // Upload part and add response to our list.
                    System.out.format("Uploading part %d\n", i);
                    partETags.add(s3.uploadPart(uploadRequest).getPartETag());

                    filePosition += partSize;
                }

                // Step 3: Complete.
                System.out.println("Completing upload");
                CompleteMultipartUploadRequest compRequest = new CompleteMultipartUploadRequest(bucketName, keyName,
                        uploadId, partETags);

                s3.completeMultipartUpload(compRequest);

                for (int i = 0; i < partETags.size(); i++) {
                    System.out.println(partETags.get(i).getETag());
                }
            } catch (Exception e) {
                System.err.println(e.toString());
                if (uploadId != null && !uploadId.isEmpty()) {
                    // Cancel when error occurred
                    System.out.println("Aborting upload");
                    s3.abortMultipartUpload(new AbortMultipartUploadRequest(bucketName, keyName, uploadId));
                }
                System.exit(1);
            }
            System.out.println("Done!");
        }
    }
}
