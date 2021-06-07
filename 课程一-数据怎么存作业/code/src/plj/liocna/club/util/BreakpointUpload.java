package plj.liocna.club.util;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.services.s3.model.MultipartUpload;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author licona
 * @date 2021/6/3
 */
public class BreakpointUpload {
    public static void breakpointContinuation(AmazonS3 s3, String bucketName, String filePath, long partSize) {
        ArrayList<PartETag> partETags = new ArrayList<PartETag>();
        MultipartUploadListing multipartUploadListing = s3.listMultipartUploads(new ListMultipartUploadsRequest(bucketName));
        List<MultipartUpload> multipartUploads = multipartUploadListing.getMultipartUploads();

        for (int i = 0; i < multipartUploads.size(); i++) {
            System.out.println(multipartUploads.get(i).getKey());
            List<PartSummary> parts = s3.listParts(new ListPartsRequest(bucketName, multipartUploads.get(i).getKey(), multipartUploads.get(i).getUploadId())).getParts();
            for (int j = 0; j < parts.size(); j++) {
                partETags.add(new PartETag(parts.get(j).getPartNumber(), parts.get(j).getETag()));
            }
            File file = new File(filePath + multipartUploads.get(i).getKey());

            long contentLength = file.length();
            String uploadId = multipartUploads.get(i).getUploadId();
            try {
                // Step 1: Initialize.
                System.out.format("Created upload ID was %s\n", uploadId);

                // Step 2: Upload parts.
                long filePosition = parts.size() * partSize;
                for (int k = parts.size() + 1; filePosition < contentLength; k++) {
                    // Last part can be less than 5 MB. Adjust part size.
                    partSize = Math.min(partSize, contentLength - filePosition);

                    // Create request to upload a part.
                    UploadPartRequest uploadRequest = new UploadPartRequest().withBucketName(bucketName).withKey(multipartUploads.get(i).getKey())
                            .withUploadId(uploadId).withPartNumber(k).withFileOffset(filePosition).withFile(file)
                            .withPartSize(partSize);

                    // Upload part and add response to our list.
                    System.out.format("Uploading part %d\n", k);
                    partETags.add(s3.uploadPart(uploadRequest).getPartETag());

                    filePosition += partSize;
                }

                // Step 3: Complete.
                System.out.println("Completing upload");
                CompleteMultipartUploadRequest compRequest = new CompleteMultipartUploadRequest(bucketName, multipartUploads.get(i).getKey(),
                        multipartUploads.get(i).getUploadId(), partETags);

                s3.completeMultipartUpload(compRequest);
            } catch (Exception e) {
                System.err.println(e.toString());
                if (uploadId != null && !uploadId.isEmpty()) {
                    // Cancel when error occurred
                    System.out.println("Aborting upload");
                    s3.abortMultipartUpload(new AbortMultipartUploadRequest(bucketName, multipartUploads.get(i).getKey(), uploadId));
                }
                System.exit(1);
            }
            System.out.println("Done!");
        }
    }
}
