package pers.hubery.filecomponent;

public interface FileService {

    /**
     * 上传文件
     *
     * @param localFilePath  本地文件路径
     * @param bucket         存储桶或目标文件夹
     * @param remoteFileName 目标文件名称或文件key
     */
    void uploadFile(String localFilePath, String bucket, String remoteFileName) throws Throwable;

    /**
     * 下载文件
     *
     * @param bucket         存储桶
     * @param remoteFilePath 要下载的文件路径
     * @param localFilePath  本地文件路径
     */
    void downloadFile(String bucket, String remoteFilePath, String localFilePath) throws Throwable;
}
