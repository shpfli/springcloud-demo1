package pers.hubery.filecomponent.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileTransferConfig {

    private String protocol;

    private String workingDirectoryInServer;

    private FtpConfig ftp;

    private SftpConfig sftp;

    private OssConfig oss;
}
