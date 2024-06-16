package pers.hubery.filecomponent.client;

import java.io.Closeable;
import java.io.File;

public interface FileTransferClient extends Closeable {

    /**
     * 使用指定场景的客户端上传本地文件到远程服务路径。
     *
     * @param toUploadFile   要上传的文件或文件夹，如果 toUploadFile 是文件夹，将递归上传文件夹下的所有文件到 remoteFilePath文件夹下
     * @param bucket         要上传的bucket，不是对象存储服务（如OSS、COS等）时，该参数无效
     * @param remoteFilePath 文件服务器上的目标文件路径/文件Key。推荐远程文件路径使用相对路径，PS: OSS 不支持以“/”开头的路径，如果以“/”开头，将自动去掉。另外，sftp等权限管理严格，根目录通常都没有权限，请谨慎使用。
     */
    void uploadFile(File toUploadFile, String bucket, String remoteFilePath);
}
