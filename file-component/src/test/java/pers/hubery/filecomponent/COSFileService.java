package pers.hubery.filecomponent;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.GetObjectRequest;
import com.qcloud.cos.model.PutObjectRequest;
import pers.hubery.filecomponent.util.CosClientPool;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class COSFileService implements FileService {

    private static final String COS_CONFIG_FILE = "cos-config.properties";

    private static final String COS_BUCKET = "my-cos-demo-1258205246";

    private CosClientPool pool;

    private COSClient getClient() throws Exception {

        if (pool == null) {
            initPool();
        }

        return pool.getConnection();
    }

    private void initPool() {

        Properties properties = new Properties();
        try {
            properties.load(ClassLoader.getSystemClassLoader().getResourceAsStream(COS_CONFIG_FILE));

            String secretId = properties.getProperty("cos.secretId");
            String secretKey = properties.getProperty("cos.secretKey");
            String region = properties.getProperty("cos.region");

            if (secretId == null || secretKey == null || region == null) {
                throw new RuntimeException("secretId or secretKey or region in cos-config.properties is null");
            }

            pool = new CosClientPool(secretId, secretKey, region);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void uploadFile(String localFilePath, String bucket, String remoteFileName) throws Exception {

        PutObjectRequest request = new PutObjectRequest(COS_BUCKET, remoteFileName, new File(localFilePath));

        COSClient client = getClient();
        try {
            client.putObject(request);
        } finally {
            pool.returnConnection(client);
        }
    }

    @Override
    public void downloadFile(String bucket, String remoteFilePath, String localFilePath) throws Exception {

        GetObjectRequest request = new GetObjectRequest(COS_BUCKET, remoteFilePath);

        COSClient client = getClient();
        try {
            client.getObject(request, new File(localFilePath));
        } finally {
            pool.returnConnection(client);
        }
    }

}
