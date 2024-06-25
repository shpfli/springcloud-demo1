package pers.hubery.filecomponent;

public class MyFtpClientTest {

    public static void main(String[] args) throws Exception {
        String serverHostname = "127.0.0.1";
        int serverPort = 21;
        String username = "hubery";
        String password = "123456";

        String toUpload = "/Users/hubery/git/file-component/src/test/resources/to-upload.data";
        String remoteFilePath = "20240604.data";
        String downloadFilePath = "src/test/resources/20240604.data";

        FtpFileService ftpFileService = new FtpFileService(serverHostname, serverPort, username, password);

        ftpFileService.uploadFile(toUpload, null, remoteFilePath);

        ftpFileService.downloadFile(null, remoteFilePath, downloadFilePath);

    }
}
