package pers.hubery.filecomponent.util;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.region.Region;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.time.Duration;

public class CosClientPool {

    private static final int MAX_ACTIVE = 10;
    private static final int MAX_IDLE = 5;
    private static final int MIN_IDLE = 2;
    private static final long MAX_WAIT_MILLIS = 1000L;

    private final ObjectPool<COSClient> pool;

    /**
     * 构造函数
     *
     * @param secretId  secretId
     * @param secretKey secretKey
     * @param region    region
     */
    public CosClientPool(String secretId, String secretKey, String region) {

        ClientConfig clientConfig = new ClientConfig();
        clientConfig.setRegion(new Region(region));

        BasicCOSCredentials credentials = new BasicCOSCredentials(secretId, secretKey);

        GenericObjectPoolConfig<COSClient> config = new GenericObjectPoolConfig<>();
        config.setMaxTotal(MAX_ACTIVE);
        config.setMaxIdle(MAX_IDLE);
        config.setMinIdle(MIN_IDLE);
        config.setMaxWait(Duration.ofMillis(MAX_WAIT_MILLIS));

        pool = new GenericObjectPool<>(new CosClientFactory(credentials, clientConfig), config);
    }

    public COSClient getConnection() throws Exception {
        return pool.borrowObject();
    }

    public void returnConnection(COSClient connection) {
        try {
            pool.returnObject(connection);
        } catch (Exception e) {
            // 忽略返回连接时的异常，例如连接已经关闭
        }
    }

    private static class CosClientFactory extends BasePooledObjectFactory<COSClient> {

        private COSCredentials cred;
        private ClientConfig clientConfig;

        public CosClientFactory(COSCredentials cred, ClientConfig clientConfig) {
            this.cred = cred;
            this.clientConfig = clientConfig;
        }

        @Override
        public COSClient create() throws Exception {
            return new COSClient(cred, clientConfig);
        }

        @Override
        public boolean validateObject(PooledObject<COSClient> p) {
            return super.validateObject(p);
        }

        @Override
        public void destroyObject(PooledObject<COSClient> p) throws Exception {
            p.getObject().shutdown();
        }

        @Override
        public PooledObject<COSClient> wrap(COSClient obj) {
            return new DefaultPooledObject<>(obj);
        }
    }
}
