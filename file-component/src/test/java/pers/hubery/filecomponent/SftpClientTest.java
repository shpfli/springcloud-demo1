package pers.hubery.filecomponent;

public class SftpClientTest {

    public static void main(String[] args) {
        String host = "localhost";
        int port = 22;
        String username = "sftp";
        String password = "123456";

        SftpFileService mySftpClient = new SftpFileService(host, port, username, password);

        try {
            // 上传文件
            String localFilePath = "/Users/hubery/git/file-component/src/test/resources/to-upload.data";
            String remoteFilePath = "upload/20240604.data";
            mySftpClient.uploadFile(localFilePath, null, remoteFilePath);

            // 下载文件
            String remoteFilePathToDownload = "upload/20240604.data";
            String localFilePathToDownload = "/Users/hubery/git/file-component/src/test/resources/download.data";
            mySftpClient.downloadFile(null, remoteFilePathToDownload, localFilePathToDownload);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
