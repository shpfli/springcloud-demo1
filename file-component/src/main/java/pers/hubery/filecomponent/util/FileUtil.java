package pers.hubery.filecomponent.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class FileUtil {

    /**
     * 复制文件
     * <p>
     * 如果目标文件已经存在，则会被覆盖
     *
     * @param srcPath  源文件 Path
     * @param distPath 目标文件 Path
     */
    public static void copyFile(Path srcPath, Path distPath) {
        try {
            Files.copy(srcPath, distPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 复制文件
     * <p>
     * 如果目标文件已经存在，则会被覆盖
     *
     * @param src  源文件
     * @param dist 目标文件
     */
    public static void copyFile(File src, File dist) {
        copyFile(src.toPath(), dist.toPath());
    }


    /**
     * 复制文件
     * <p>
     * 如果目标文件已经存在，则会被覆盖
     *
     * @param src      源文件
     * @param distPath 目标文件路径
     */
    public static void copyFile(File src, String distPath) {
        copyFile(src, new File(distPath));
    }


}
