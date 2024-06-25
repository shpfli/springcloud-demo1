package pers.hubery.filecomponent;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import pers.hubery.filecomponent.util.FileSpliter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class FtpUploader {
    private static String server = "127.0.0.1";
    private static int port = 21;
    private static String user = "hubery";
    private static String pass = "123456";
    private static final int CHUNK_SIZE = 1024 * 1024; // 1MB per chunk
    private static final String REMOTE_DIRECTORY = "data";

    private static FTPClient ftpClient;

    private static void initClient() throws IOException {
        ftpClient = new FTPClient();
        // 连接到FTP服务器
        ftpClient.connect(server, port);
        int replyCode = ftpClient.getReplyCode();
        if (!FTPReply.isPositiveCompletion(replyCode)) {
            System.out.println("Connect failed");
            return;
        }

        boolean loginSuccess = ftpClient.login(user, pass);
        if (loginSuccess) {
            System.out.println("登录成功");
        } else {
            System.out.println("登录失败");
        }

        ftpClient.enterLocalPassiveMode();

        // 设置文件类型为二进制
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
    }

    public static void main(String[] args) {
//        simpleUpload();
        uploadFileByChunks();
    }

    private static void simpleUpload() {
        try {
            initClient();

            // 上传文件
            FileInputStream inputStream = new FileInputStream("/Users/hubery/git/file-component/src/test/resources/to-upload.data");
            String remoteFileName = "remote-file.txt";
            boolean success = ftpClient.storeFile(remoteFileName, inputStream);
            inputStream.close();

            if (success) {
                System.out.println("文件上传成功");
            } else {
                System.out.println("文件上传失败");
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

    public static void uploadFileByChunks() {
//        String remoteFile = "data/gradle-2.14-all.zip";
        File localFile = new File("src/test/resources/gradle-2.14-all.zip");

        try {
            initClient();

            //拆分文件
            File tmpDirectory = new File(localFile.getParentFile(), "tmp");
            List<File> chunks = FileSpliter.split(localFile, CHUNK_SIZE, tmpDirectory);

            //逐个上传
            for (File chunk : chunks) {
                try (InputStream inputStream = new FileInputStream(chunk)) {
                    boolean result = ftpClient.storeFile(REMOTE_DIRECTORY + "/" + chunk.getName(), inputStream);
                    if (result) {
                        System.out.println("文件上传成功 : " + chunk.getName());
                    } else {
                        System.out.println("文件上传失败 : " + chunk.getName());
                        throw new RuntimeException("文件上传失败 : " + chunk.getName());
                    }
                }
            }

            // 通过重命名合并文件
            // 假设最后一个分片是 file.partN
            String lastPartName = chunks.get(chunks.size() - 1).getName();
            String finalFileName = localFile.getName();

            // 测试证明重命名并不能合并文件分片为一个完整文件
            // 重命名最后一个分片为最终的文件名
            boolean renameSuccess = ftpClient.rename(REMOTE_DIRECTORY + "/" + lastPartName, REMOTE_DIRECTORY + "/" + finalFileName);
            if (renameSuccess) {
                System.out.println("File assembly completed successfully.");
            } else {
                System.err.println("Failed to assemble the file.");
            }

            //删除临时文件
            for (File chunk : chunks) {
                chunk.delete();
            }

            // 关闭FTP连接
            ftpClient.logout();
            ftpClient.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
