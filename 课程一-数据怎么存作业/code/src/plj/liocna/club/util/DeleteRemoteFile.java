package plj.liocna.club.util;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;

/**
 * @author licona
 * @date 2021/6/3
 */
public class DeleteRemoteFile {
    public static void deleteRemoteFile(AmazonS3 s3, String bucketName, String keyName) {
        DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucketName, keyName);
        s3.deleteObject(deleteObjectRequest);
    }
}
