package pers.hubery.filecomponent;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.FileOutputStream;
import java.io.IOException;

public class FtpDownloader {
    public static void main(String[] args) {
        String server = "127.0.0.1";
        int port = 21;
        String user = "hubery";
        String pass = "123456";


        FTPClient ftpClient = new FTPClient();
        try {
            // 连接到FTP服务器
            ftpClient.connect(server, port);
            ftpClient.login(user, pass);
            ftpClient.enterLocalPassiveMode();

            // 设置文件类型为二进制
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            // 下载文件
            FileOutputStream outputStream = new FileOutputStream("local-file.txt");
            String remoteFileName = "remote-file.txt";
            boolean success = ftpClient.retrieveFile(remoteFileName, outputStream);
            outputStream.close();

            if (success) {
                System.out.println("文件下载成功");
            } else {
                System.out.println("文件下载失败");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // 断开与FTP服务器的连接
                ftpClient.logout();
                ftpClient.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}