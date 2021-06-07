package plj.liocna.club.util;

import com.amazonaws.util.Md5Utils;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import com.google.common.io.BaseEncoding;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author licona
 * @date 2021/6/3
 */
public class MultipartFileEtag {
    public static String getMultipartFileEtag(String inputFile, int bufferSize) {
        long contentLength = new File(inputFile).length();
        FileInputStream fis = null;
        List<String> md5s = new ArrayList<>();
        try {
            fis = new FileInputStream(inputFile);
            // 文件读取缓存
            byte[] buffer = new byte[bufferSize];
            int fileNum = 0;
            // 大文件切割成小文件
            while ((fis.read(buffer, 0, bufferSize)) != -1) {
                md5s.add(ByteToHex.bytesToHex(Md5Utils.computeMD5Hash(subBytes(buffer, 0, bufferSize))));
                fileNum++;
                bufferSize = Math.toIntExact(Math.min(bufferSize, contentLength - (long) fileNum * bufferSize));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (String md5 : md5s) {
            stringBuilder.append(md5);
        }
        String hex = stringBuilder.toString();
        byte[] raw = BaseEncoding.base16().decode(hex.toUpperCase());
        Hasher hasher = Hashing.md5().newHasher();
        hasher.putBytes(raw);
        String digest = hasher.hash().toString();

        return digest + "-" + md5s.size();
    }

    public static byte[] subBytes(byte[] src, int begin, int count) {
        byte[] bs = new byte[count];
        System.arraycopy(src, begin, bs, 0, count);
        return bs;
    }
}

