package plj.liocna.club.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author licona
 * @date 2021/6/3
 */
public class ReadFiles {
    List<String> files = new ArrayList<>();
    /**
     * 读取某个文件夹下的所有文件
     */
    public List<String> readFiles(String path, String prefix) {
        File file = new File(path);
        File[] tempList = file.listFiles();

        for (File value : tempList) {
            if (value.isFile()) {
                files.add(value.getPath().substring(prefix.length()).replaceAll("\\\\", "/"));
            }
            if (value.isDirectory()) {
                readFiles(value.getPath(), prefix);
//                files.add(value.getPath() + "/");
            }
        }
        return files;
    }
}
