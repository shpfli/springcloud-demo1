package pers.hubery.filecomponent.client;

import pers.hubery.filecomponent.config.FileTransferConfig;
import pers.hubery.filecomponent.enums.FileTransferProtocolEnum;

public class FileTransferClientFactory {

    /**
     * 创建文件传输客户端
     *
     * @param config 文件传输配置
     * @return 文件传输客户端
     */
    public static FileTransferClient createClient(FileTransferConfig config) {

        FileTransferProtocolEnum protocol = FileTransferProtocolEnum.getByCode(config.getProtocol());
        if (protocol == null) {
            throw new UnsupportedOperationException("Unsupported file transfer protocol: " + config.getProtocol());
        }

        switch (protocol) {
            case LOCAL:
            case SHARE:
                return new ShareFileTransferClient(config);
            case FTP:
                return new FtpFileTransferClient(config);
            case SFTP:
                return new SftpFileTransferClient(config);
            case OSS:
                return new OssFileTransferClient(config);
            default:
                throw new UnsupportedOperationException("Unsupported file transfer protocol: " + protocol);
        }
    }

}
