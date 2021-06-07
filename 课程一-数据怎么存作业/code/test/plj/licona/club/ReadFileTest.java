package plj.licona.club;

import org.junit.Test;
import plj.liocna.club.util.ReadFiles;

import java.io.IOException;
import java.util.List;

/**
 * @author licona
 * @date 2021/6/3
 */
public class ReadFileTest {
    @Test
    public void readFileTest() throws IOException {
        List<String> strings = new ReadFiles().readFiles("./liyuming-repository", "./liyuming-repository");
        for (int i = 0; i<strings.size(); i++) {
            System.out.println(strings.get(i));
        }
    }
}
