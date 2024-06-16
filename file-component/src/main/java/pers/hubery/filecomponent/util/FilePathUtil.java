package pers.hubery.filecomponent.util;

public class FilePathUtil {

    /**
     * 获取远程文件路径的父路径，不存在“/”，则返回 null
     *
     * @param remotePath 远程文件路径
     * @return 父路径，不存在“/”，则返回 null
     */
    public static String getParentPath(String remotePath) {
        int lastIndex = remotePath.lastIndexOf("/");
        if (lastIndex > 0) {
            return remotePath.substring(0, lastIndex);
        }
        return null;
    }
}
