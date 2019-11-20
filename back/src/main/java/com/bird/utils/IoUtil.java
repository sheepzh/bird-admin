package com.bird.utils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author zhyyy
 **/
public class IoUtil {
    private static int BYTE_BUFFER_LENGTH = 2048;

    /**
     * 输入流转字符串，且保证原输入流可再次读取
     *
     * @param inputStream 输入流
     * @return 字符串
     */
    public static String stream2String(InputStream inputStream) {
        if (!inputStream.markSupported()) {
            return "Can't translate this stream, unless it can be closed in this method.";
        }
        StringBuilder sb = new StringBuilder();
        inputStream.mark(0);
        try (InputStreamReader inReader = new InputStreamReader(inputStream);
             BufferedReader reader = new BufferedReader(inReader)) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.reset();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    /**
     * IO模式复制文件
     *
     * @param src 源文件地址
     * @param dst 目标文件地址
     * @throws IOException 复制失败时抛出
     */
    public static void copyFileIoMode(String src, String dst) throws IOException {
        try (
                InputStream is = new FileInputStream(new File(src));
                OutputStream os = new FileOutputStream(new File(dst))
        ) {
            byte[] buffer = new byte[BYTE_BUFFER_LENGTH];
            int readLength;
            while ((readLength = is.read(buffer)) != -1) {
                os.write(buffer, 0, readLength);
            }
        } catch (IOException e) {
            throw e;
        }
    }

    /**
     * NIO复制文件
     *
     * @param src 源文件地址
     * @param dst 目标文件地址
     * @throws IOException 复制失败时抛出
     */
    public static void copyFileNioMode(String src, String dst) throws IOException {
        // 新建Buffer
        ByteBuffer buffer = ByteBuffer.allocate(BYTE_BUFFER_LENGTH);
        try (
                FileInputStream fis = new FileInputStream(new File(src));
                FileOutputStream fos = new FileOutputStream(new File(dst));
                FileChannel ic = fis.getChannel();
                FileChannel oc = fos.getChannel()
        ) {
            while (ic.read(buffer) != -1) {
                // 进入写模式，写入channel
                buffer.flip();
                oc.write(buffer);
                // 重置buffer
                buffer.clear();
            }
        } catch (IOException e) {
            throw e;
        }
    }


    public static void main(String[] arg) throws IOException {
    }
}
