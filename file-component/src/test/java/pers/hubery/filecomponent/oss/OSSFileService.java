package pers.hubery.filecomponent.oss;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.model.*;
import pers.hubery.filecomponent.FileService;

import java.io.IOException;
import java.util.Properties;

public class OSSFileService implements FileService {

    private static final String OSS_CONFIG_FILE = "oss-config.properties";

    // Part size, by default it's 100KB.
    private long partSize = 1024 * 1024 * 4;
    // Concurrent parts upload thread count. By default it's 1.
    private int taskNum = 10;

    private OSS client;

    private OSS getClient() {
        if (client == null) {
            Properties properties = new Properties();
            try {
                properties.load(ClassLoader.getSystemClassLoader().getResourceAsStream(OSS_CONFIG_FILE));

                String accessKeyId = properties.getProperty("oss.AccessKeyID");
                String accessKeySecret = properties.getProperty("oss.AccessKeySecret");
                String endpoint = properties.getProperty("oss.Endpoint");

                client = new OSSClientBuilder().build(endpoint, new DefaultCredentialProvider(accessKeyId, accessKeySecret));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return client;
    }

    @Override
    public void uploadFile(String localFilePath, String bucket, String remoteFilePath) throws Throwable {
        System.out.println("uploading file : " + localFilePath + " to " + remoteFilePath + " in bucket " + bucket);

        long start = System.currentTimeMillis();

//        getClient().putObject(bucket, remoteFilePath, new File(localFilePath));

        UploadFileRequest request = new UploadFileRequest(bucket, remoteFilePath, localFilePath, partSize, taskNum);

        UploadFileResult result = getClient().uploadFile(request.withProgressListener(new FileUploadProgressListener()));
        System.out.println("upload file success in " + (System.currentTimeMillis() - start) + "ms");

        CompleteMultipartUploadResult r = result.getMultipartUploadResult();
        System.out.println("Client CRC : " + r.getClientCRC());
        System.out.println("Location : " + r.getLocation());
    }

    @Override
    public void downloadFile(String bucket, String remoteFilePath, String localFilePath) throws Throwable {

        System.out.println("downloading file : " + remoteFilePath + " in bucket " + bucket + " to " + localFilePath);

        long start = System.currentTimeMillis();

//        GetObjectRequest request = new GetObjectRequest(bucket, remoteFilePath);
//        ObjectMetadata objectMetadata = getClient().getObject(request, new File(localFilePath));

        DownloadFileRequest request = new DownloadFileRequest(bucket, remoteFilePath, localFilePath, partSize, taskNum, false);
        DownloadFileResult result = getClient().downloadFile(request.withProgressListener(new FileDownloadProgressListener()));

        System.out.println("download file success in " + (System.currentTimeMillis() - start) + "ms");
        System.out.println("Server CRC : " + result.getObjectMetadata().getServerCRC());
    }

    @Override
    protected void finalize() throws Throwable {
        if (client != null) {
            this.client.shutdown();
        }
        super.finalize();
    }
}