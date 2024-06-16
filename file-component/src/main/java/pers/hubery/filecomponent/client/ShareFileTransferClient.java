package pers.hubery.filecomponent.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import pers.hubery.filecomponent.config.FileTransferConfig;
import pers.hubery.filecomponent.util.FileUtil;

import java.io.File;

public class ShareFileTransferClient implements FileTransferClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(ShareFileTransferClient.class);

    /** 共享存储的路径 */
    private File workingDirectoryInServer;

    /**
     * 构造器
     *
     * @param config 客户端配置
     */
    ShareFileTransferClient(FileTransferConfig config) {
        String workingDirectoryPath = config.getWorkingDirectoryInServer();

        if (!StringUtils.isEmpty(workingDirectoryPath)) {
            this.workingDirectoryInServer = new File(workingDirectoryPath);

            if (!this.workingDirectoryInServer.isDirectory()) {
                throw new RuntimeException("The working directory in server is not correct: " + workingDirectoryPath);
            }

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Working directory in server: {}", this.workingDirectoryInServer.getAbsolutePath());
            }
        }
    }

    /**
     * 使用指定场景的客户端上传本地文件到远程服务路径。
     *
     * @param toUploadFile   要上传的文件或文件夹，如果 toUploadFile 是文件夹，将递归上传文件夹下的所有文件到 remoteFilePath文件夹下
     * @param bucket         要上传的bucket，不是对象存储服务（如OSS、COS等）时，该参数无效
     * @param remoteFilePath 文件服务器上的目标文件路径/文件Key
     */
    @Override
    public void uploadFile(File toUploadFile, String bucket, String remoteFilePath) {

        if (toUploadFile.isDirectory()) {

            File remoteFile = computeFinalPath(remoteFilePath);

            // 创建目标目录
            this.mkdirs(remoteFile);

            for (File file : toUploadFile.listFiles()) {
                uploadFile(file, bucket, remoteFilePath + "/" + file.getName());
            }
        } else {
            // 上传文件
            File remoteFile = computeFinalPath(remoteFilePath);

            // 检查上级目录是否存在，不存在则创建
            this.mkdirs(remoteFile.getParentFile());

            // 上传文件
            FileUtil.copyFile(toUploadFile, remoteFile);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Uploaded file success. remoteFilePath = {}", remoteFile.getAbsolutePath());
            }
        }
    }

    /**
     * 创建目录，如果上级目录不存在，也会自动创建
     *
     * @param dir 目录
     */
    private void mkdirs(File dir) {
        if (!dir.exists()) {
            if (dir.mkdirs()) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Created directory: " + dir.getAbsolutePath());
                } else {
                    throw new RuntimeException("Failed to create directory: " + dir.getAbsolutePath());
                }
            }
        }
    }

    /**
     * 根据当前工作目录和远程文件路径计算最终的远程文件路径
     *
     * @param remoteFilePath 远程文件路径
     * @return 最终的远程文件对象
     */
    private File computeFinalPath(String remoteFilePath) {

        if (this.workingDirectoryInServer != null) {
            return new File(this.workingDirectoryInServer, remoteFilePath);
        } else {
            return new File(remoteFilePath);
        }
    }

    /**
     * 关闭客户端
     */
    public void close() {
        // do nothing
    }
}
