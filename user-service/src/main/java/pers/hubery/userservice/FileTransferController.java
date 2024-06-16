package pers.hubery.userservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.hubery.filecomponent.config.FileTransferConfigs;
import pers.hubery.filecomponent.service.FileTransferService;

import java.io.File;

@RestController
@RequestMapping("/file")
public class FileTransferController {


    @Autowired
    private FileTransferConfigs fileTransferConfigs;

    @Autowired
    private FileTransferService fileTransferService;

    @GetMapping("/hello")
    public String hello(String name) {
        return "hello " + name;
    }

    @GetMapping("/config")
    public FileTransferConfigs config() {
        return fileTransferConfigs;
    }

    @PutMapping("/upload")
    public String upload(String localFilePath, String sceneName, String remoteFileName, String bucket) throws Throwable {
        File localFile = new File(localFilePath);
        fileTransferService.uploadFile(localFile, sceneName, bucket, remoteFileName);
        return "success";
    }

}
