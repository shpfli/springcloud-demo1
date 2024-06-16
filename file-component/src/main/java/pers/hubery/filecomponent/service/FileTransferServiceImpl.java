package pers.hubery.filecomponent.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.hubery.filecomponent.client.FileTransferClient;
import pers.hubery.filecomponent.client.FileTransferClientFactory;
import pers.hubery.filecomponent.config.FileTransferConfig;
import pers.hubery.filecomponent.config.FileTransferConfigs;

import java.io.File;
import java.io.IOException;

@Service
public class FileTransferServiceImpl implements FileTransferService {

    @Autowired
    private FileTransferConfigs fileTransferConfigs;

    public void uploadFile(File toUploadFile, String remoteFilePath) {
        uploadFile(toUploadFile, null, remoteFilePath);
    }

    public void uploadFile(File toUploadFile, String sceneName, String remoteFilePath) {
        uploadFile(toUploadFile, sceneName, null, remoteFilePath);
    }

    public void uploadFile(File toUploadFile, String sceneName, String bucket, String remoteFilePath) {

        if (toUploadFile == null) {
            throw new RuntimeException("File to upload is null!");
        }

        if (!toUploadFile.exists()) {
            throw new RuntimeException("File to upload not exist. toUploadFile=" + toUploadFile.getAbsoluteFile());
        }


        //1. 根据sceneName获取客户端配置
        FileTransferConfig clientConfig = fileTransferConfigs.getClient(sceneName);
        if (clientConfig == null) {
            throw new RuntimeException("No valid file client config exist. please check 'comet.file-component.clients' in config file");
        }

        //2. 创建客户端
        try (FileTransferClient client = FileTransferClientFactory.createClient(clientConfig)) {

            //3. 上传文件
            client.uploadFile(toUploadFile, bucket, remoteFilePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    public File downloadFile(String remoteFilePath, String localFilePath) {
        return downloadFile(null, remoteFilePath, localFilePath);
    }

    public File downloadFile(String sceneName, String remoteFilePath, String localFilePath) {
        return downloadFile(sceneName, null, remoteFilePath, localFilePath);
    }

    public File downloadFile(String sceneName, String bucket, String remoteFilePath, String localFilePath) {
        //TODO
        return null;
    }

    public File downloadFileToDirectory(String remoteFilePath, File targetDirectory) {
        return downloadFileToDirectory(null, remoteFilePath, targetDirectory);
    }

    public File downloadFileToDirectory(String sceneName, String remoteFilePath, File targetDirectory) {
        return downloadFileToDirectory(sceneName, null, remoteFilePath, targetDirectory);
    }

    public File downloadFileToDirectory(String sceneName, String bucket, String remoteFilePath, File targetDirectory) {
        //TODO
        return null;
    }

    public boolean isFileExistInServer(String remoteFilePath) {
        return isFileExistInServer(null, remoteFilePath);
    }

    public boolean isFileExistInServer(String sceneName, String remoteFilePath) {
        return isFileExistInServer(sceneName, null, remoteFilePath);
    }

    public boolean isFileExistInServer(String sceneName, String bucket, String remoteFilePath) {
        //TODO
        return false;
    }
}
