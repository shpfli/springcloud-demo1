package pers.hubery.filecomponent;

import org.apache.commons.net.ftp.FTPClient;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;

public class FtpFileService implements FileService {

    /** ftp 服务器 hostname */
    private final String serverHostname;

    /** ftp 服务器端口 */
    private final int serverPort;

    /** 用户 */
    private final String username;

    /** 密码 */
    private final String password;

    private FTPClient ftpClient;

    /**
     * 构造方法
     *
     * @param serverHostname ftp server host
     * @param port           ftp server port
     * @param username       ftp username
     * @param password       ftp password
     */
    public FtpFileService(String serverHostname, int port, String username, String password) {
        this.serverHostname = serverHostname;
        this.serverPort = port;
        this.username = username;
        this.password = password;
    }

    private FTPClient getFTPClient() {

        if (ftpClient == null) {

            ftpClient = new FTPClient();
            try {
                ftpClient.connect(serverHostname, serverPort);

                boolean loginSuccess = ftpClient.login(username, password);
                if (!loginSuccess) {
                    throw new RuntimeException("登录失败");
                }

                ftpClient.enterLocalPassiveMode();

                // 设置文件类型为二进制
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);

                System.out.println("Login to ftp server success!");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return ftpClient;
    }


    @Override
    public void uploadFile(String localFilePath, String bucket, String remoteFileName) throws Exception {

        try (FileInputStream inputStream = new FileInputStream(localFilePath)) {

            boolean success = getFTPClient().storeFile(remoteFileName, inputStream);

            if (!success) {
                throw new RuntimeException("文件上传失败");
            } else {
                System.out.println(
                        MessageFormat.format("文件上传成功，本地文件路径：{0}, 服务器文件路径：{1}",
                                localFilePath, remoteFileName));
            }
        }
    }

    @Override
    public void downloadFile(String bucket, String remoteFilePath, String localFilePath) throws Exception {

        try (FileOutputStream outputStream = new FileOutputStream(localFilePath)) {

            boolean success = getFTPClient().retrieveFile(remoteFilePath, outputStream);

            if (!success) {
                throw new RuntimeException("文件下载失败");
            } else {
                System.out.println(
                        MessageFormat.format("文件下载成功，本地文件路径：{0}, 服务器文件路径：{1}",
                                localFilePath, remoteFilePath));
            }
        }
    }

    @Override
    protected void finalize() throws Throwable {
        System.out.println("finalize of MyFtpClient called");
        if (ftpClient != null) {
            ftpClient.logout();
            ftpClient.disconnect();
            System.out.println("Disconnected from ftp server!");
        }

        super.finalize();
    }
}
