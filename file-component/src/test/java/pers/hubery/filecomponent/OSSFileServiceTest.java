package pers.hubery.filecomponent;

import pers.hubery.filecomponent.oss.OSSFileService;

public class OSSFileServiceTest {

    private static final String bucketName = "hubery-oss-demo";

    public static void main(String[] args) throws Throwable {

        FileService fileService = new OSSFileService();


//        String remoteFileName = System.currentTimeMillis() + ".data";
//        String remoteFileKey = "test-data/" + remoteFileName;

//        fileService.uploadFile("src/test/resources/to-upload.data", bucketName, remoteFileKey);
//        fileService.downloadFile(bucketName, remoteFileKey, "src/test/resources/downloads/" + remoteFileName);

        fileService.uploadFile("src/test/resources/gradle-2.14-all.zip", bucketName, "test-data/gradle-2.14-all.zip");
        fileService.downloadFile(bucketName, "test-data/gradle-2.14-all.zip", "src/test/resources/downloads/gradle-2.14-all.zip");


    }
}
