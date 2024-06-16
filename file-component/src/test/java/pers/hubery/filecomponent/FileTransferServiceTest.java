package pers.hubery.filecomponent;

import pers.hubery.filecomponent.service.FileTransferService;

import java.io.File;

public class FileTransferServiceTest {

    private FileTransferService fileTransferService;

    public void test() {
        //假设 root-path=/data
        //该文件最终会被上传到：/data/to-accounting/cl-20240606.data
        fileTransferService.uploadFile(new File("/home/root/tmp/cl-accounting-20240606.data"), "/to-accounting/cl-20240606.data");


        File toUploadFile = new File("./tmp/cl-accounting-20240606.data");
        String remoteFilePath = "/to-accounting/cl-20240606.data";
        String bucket = "bucket1";
        String sceneName = "scene-accounting";

        // 将文件上传到 OSS 上名为 bucket1 的 bucket 中，文件Key为 "/to-accounting/cl-20240606.data"
        fileTransferService.uploadFile(toUploadFile, null, bucket, remoteFilePath);

        // 从名为 bucket1 的 bucket 中下载"/demo/test.txt" 到本地的当前工作目录下，并重命名为test2.txt
        fileTransferService.downloadFile(null, bucket, "/demo/test.txt", "test2.txt");

        //假设 root-path=/data
        //那么这里的会下载 /data/demo/test.txt 到本地的当前工作目录下，并重命名为test2.txt
        fileTransferService.downloadFile("/demo/test.txt", "test2.txt");


        // 使用"scene-accounting"场景对应的客户端上传文件
        fileTransferService.uploadFile(toUploadFile, sceneName, remoteFilePath);

        // 使用"scene-accounting"场景对应的客户端下载文件
        fileTransferService.downloadFile(sceneName, remoteFilePath, "test2.txt");
    }
}
