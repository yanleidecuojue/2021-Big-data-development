package plj.licona.club;

import org.junit.Test;
import plj.liocna.club.util.MultipartFileEtag;

import java.io.FileNotFoundException;

/**
 * @author licona
 * @date 2021/6/3
 */
public class MultipartFileEtagTest {
    @Test
    public void multipartFileEtagTest() throws FileNotFoundException {
        String a = MultipartFileEtag.getMultipartFileEtag("liyuming-repository/test/test-multipartupload", 7 << 20);
        System.out.println(a);
    }
}
