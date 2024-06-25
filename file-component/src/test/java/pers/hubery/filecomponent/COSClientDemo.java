package pers.hubery.filecomponent;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.BasicSessionCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.COSObjectInputStream;
import com.qcloud.cos.model.GetObjectRequest;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.region.Region;
import org.apache.commons.io.FileUtils;
import pers.hubery.filecomponent.util.CRC64Util;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class COSClientDemo {

    private static final String COS_CONFIG_FILE = "cos-config.properties";

    private static final String COS_REGION = "ap-chengdu";
    private static final String COS_BUCKET = "my-cos-demo-1258205246";
    private COSClient client;

    private static void testUpload(COSClient client) {

        client.putObject(COS_BUCKET, "test.txt", "test11");

        File toUpload = new File("src/test/resources/to-upload.data");
        client.putObject(COS_BUCKET, "to-upload.data", toUpload);
    }

    private static void testDownload(COSClient client) {

        // 方法1 获取下载输入流
        COSObject object = client.getObject(COS_BUCKET, "test.txt");
        try (COSObjectInputStream objectInputStream = object.getObjectContent()) {

            File toDownload = new File("src/test/resources/test.txt");

            FileUtils.copyInputStreamToFile(objectInputStream, toDownload);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 方法2 下载文件到本地的路径，例如 把文件下载到本地的 /path/to/路径下的localFile文件中
        File outputFile = new File("src/test/resources/test2.txt");
        GetObjectRequest getObjectRequest = new GetObjectRequest(COS_BUCKET, "test.txt");

        ObjectMetadata objectMetadata = client.getObject(getObjectRequest, outputFile);
        // 下载对象的 CRC64
        String crc64Ecma = objectMetadata.getCrc64Ecma();

        System.out.println(crc64Ecma);


        String crc64Ecma2 = null;
        try {
            crc64Ecma2 = String.valueOf(CRC64Util.calculateCRC64(outputFile));
            System.out.println(crc64Ecma2);
            System.out.println("校验通过：" + crc64Ecma.equals(crc64Ecma2));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static COSClient initClientByTmpSecret() {
        // 1 传入获取到的临时密钥 (tmpSecretId, tmpSecretKey, sessionToken)
        String tmpSecretId = "AKIDV_MFTHAOCP3sK7HvaExilfAzHpSO4cwfAXvLOVnm2qyO8mAvMG-geZFNmdzChBqx";
        String tmpSecretKey = "26p8tEwkpwi0sQgdEcOpJ+1K6Wx4tWwAN0JjSJfckVI=";
        String sessionToken = "ruhqF6PYo36fGFMzoNH7CJVE79UJzf7ab4d43bdd6b2fe7985d93239950e21a23hcCtItSkBxF4UrDGN1fob7YkDCcdvvQTIZPtGF687I0yvHjUFLgpMVD8Huy09uJ_WlTyF_Z59NRO-qhZPoxv0m6thSoVb1wUaTGF8n7LrJ2aD6BtDZvqvAZMGX6NmfN3Rm9dmY-lthypcE2KF_JNybxWzOyvigaDt8ilDmMzCSGwDI8OPaC7Ut82G-w218X2V_GOrzO0jvkMGvFhw2PxtAMDweNX3CDUMxKhxvxfpb1mMRywrlDRJ_AJAH-xr8iftMD3dI28eIYjnxtxUBPeCTBqQkp_9MPrfqhwupGwDCDG1DEd4UtUpkrDSfNaZitpVQpi3e1dJotheXEZcH1ZwZ2TwXLtcSzEXfd2ycO_4KEbxKWfWrGNNl9qj1W2nPyydycZ9eOXQDYq-8ycQStOOOzdPe6WyiqcOj-edzUMqcZ24wAVIX8uN92xCeh9eZp2FSgy6OimF_4EonVr0ZRfh_OZ95w8lJ_clLnP6mhYlQs0MYZBg9PYGA9JNGH8X7ICUDCQ2IrY_LuuuVw6NfI4MmEyaxc2DZr0_kcZvjsSgDhUE_Xqt3U283kWMWT_8ol2euseVXNIsc43stFOpWmzkQ";

        BasicSessionCredentials cred = new BasicSessionCredentials(tmpSecretId, tmpSecretKey, sessionToken);

        // 2 设置 bucket 的地域
        // clientConfig 中包含了设置 region, https(默认 http), 超时, 代理等 set 方法, 使用可参见源码或者常见问题 Java SDK 部分
        //COS_REGION 参数：配置成存储桶 bucket 的实际地域，例如 ap-beijing，更多 COS 地域的简称请参见 https://cloud.tencent.com/document/product/436/6224
        Region region = new Region(COS_REGION);
        ClientConfig clientConfig = new ClientConfig(region);

        // 3 生成 cos 客户端
        return new COSClient(cred, clientConfig);
    }

    /**
     * 初始化客户端
     *
     * @return 客户端
     */
    private COSClient getClient() {

        Properties properties = new Properties();
        try {
            properties.load(ClassLoader.getSystemClassLoader().getResourceAsStream(COS_CONFIG_FILE));

            String secretId = properties.getProperty("secretId");
            String secretKey = properties.getProperty("secretKey");
            String region = properties.getProperty("region");

            if (secretId == null || secretKey == null) {
                throw new RuntimeException("secretId or secretKey in cos-config.properties is null");
            }
            if (region == null) {
                region = COS_REGION;
            }

            COSCredentials credentials = new BasicCOSCredentials(secretId, secretKey);

            // 2 设置 bucket 的地域
            // clientConfig 中包含了设置 region, https(默认 http), 超时, 代理等 set 方法, 使用可参见源码或者常见问题 Java SDK 部分
            ClientConfig clientConfig = new ClientConfig(new Region(region));
            // 这里建议设置使用 https 协议
            // 从 5.6.54 版本开始，默认使用了 https
            clientConfig.setHttpProtocol(HttpProtocol.https);

            // 3 生成 cos 客户端
            return new COSClient(credentials, clientConfig);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
