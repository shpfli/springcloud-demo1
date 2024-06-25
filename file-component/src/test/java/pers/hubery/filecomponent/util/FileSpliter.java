package pers.hubery.filecomponent.util;


import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class FileSpliter {

    public static void main(String[] args) throws IOException {
        File originalFile = new File("src/test/resources/gradle-2.14-all.zip"); //原文件
        long perSize = 1024 * 1024; //指定拆分后每个文件的大小
        File dir = new File("src/test/resources/tmp"); //拆分后文件地址

        List<File> chunks = split(originalFile, perSize, dir); //计算拆分后文件数量
        System.out.println(chunks.size());

        File targetFile = new File(dir, originalFile.getName());

        mergeFiles(chunks, targetFile.getAbsolutePath());
        System.out.println("原文件大小：" + originalFile.length());
        System.out.println("最终文件大小：" + targetFile.length());

        chunks.forEach(File::delete);

    }

    public static List<File> split(final File original, final long perSize, final File directory) {
        // 参数校验
        if (original == null || !original.isFile()) {
            return null;
        }

        // 输出文件夹是否存在
        if (!directory.exists()) {
            directory.mkdirs();
        }

        int count = 0; // 拆分文件的数量

        String filename = original.getName();

        long fileSize = original.length(); // 原始文件大小

        List<File> results = new ArrayList<>((int) (original.length() / perSize + (fileSize % perSize == 0 ? 0 : 1)));

        try (FileInputStream fis = new FileInputStream(original)) {
            // 单文件大小一个数组能读取到范围
            if (perSize <= Integer.MAX_VALUE - 8) {
                //最大数组大小定义为Integer.MAX_VALUE - 8，作为自己需要8 bytes存储大小
                byte[] bytes = new byte[(int) perSize];
                int readSize;   // readSize 读取的字节数
                while (fis.available() != 0) {//可读取字节数
                    readSize = fis.read(bytes);
                    File outFile = new File(directory, filename + ".part" + (++count));
                    // 用输出流创建子文件
                    FileOutputStream fos = new FileOutputStream(outFile);

                    fos.write(bytes, 0, readSize);//这么做防止写入重复的字节

                    System.out.println("分隔文件:" + outFile);
                    fos.close();

                    results.add(outFile);
                }

            } else {
                // 一次读取不能完整读取一个文件  size > Integer.MAX_VALUE
                // 当前被分隔的文件是否以分隔完毕
                while (fis.available() != 0) { // 每循环一个会创建一个子文件
                    // 用输出流创建子文件
                    File outFile = new File(directory, filename + ".part" + (++count));
                    FileOutputStream fos = new FileOutputStream(outFile);

                    long sum = perSize; // 单文件大小

                    while (sum > Integer.MAX_VALUE - 8 && fis.available() != 0) {
                        byte[] bytes = new byte[Integer.MAX_VALUE - 8];
                        int readSize = fis.read(bytes);
                        fos.write(bytes, 0, readSize);
                        sum -= readSize;
                    }
                    // 没有满足单文件大小, 还有内容没读取
                    if (fis.available() != 0) {
                        byte[] bytes = new byte[(int) sum];
                        int readSize = fis.read(bytes);
                        fos.write(bytes, 0, readSize);
                    }
                    fos.close();

                    results.add(outFile);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }


    public static void mergeFiles(List<File> fileList, String targetFilePath) throws IOException {
        if (fileList.isEmpty()) {
            System.out.println("没有找到任何文件！");
            return;
        }

        File outputFile = new File(targetFilePath);

        try (BufferedOutputStream output = new BufferedOutputStream(Files.newOutputStream(outputFile.toPath()))) {
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];

            for (File file : fileList) {
                try (BufferedInputStream input = new BufferedInputStream(Files.newInputStream(file.toPath()))) {
                    int length;
                    while ((length = input.read(buffer)) != -1) {
                        output.write(buffer, 0, length);
                    }
                }
            }
        }

        System.out.println("文件合并完成！filepath: " + targetFilePath);
    }

}