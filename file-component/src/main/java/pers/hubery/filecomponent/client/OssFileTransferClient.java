package pers.hubery.filecomponent.client;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.UploadFileRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import pers.hubery.filecomponent.config.FileTransferConfig;
import pers.hubery.filecomponent.config.MultipartConfig;
import pers.hubery.filecomponent.config.OssConfig;
import pers.hubery.filecomponent.listener.OssFileUploadProgressListener;

import java.io.File;
import java.lang.ref.PhantomReference;
import java.lang.ref.WeakReference;
import java.text.MessageFormat;

public class OssFileTransferClient implements FileTransferClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(OssFileTransferClient.class);

    private final OSS client;

    /** 默认的bucket，绝大部分情况下，一个业务系统只需要访问一个bucket，调用文件上传和下载接口时，可以不指定 bucket，会自动使用这里配置的默认 bucket。如果一个业务系统需要访问多个不同的bucket，那么需要在调用接口时指定 bucket。 */
    private final String defaultBucket;

    /** 是否启用断点续传，默认关闭 */
    private boolean enableCheckpoint = false;

    /** 分片大小，单位：字节，默认100K，即102400，可以通过将分片大小设的足够大来禁用分片 */
    private int partSize = 102400;

    /** 分片上传或下载时的并发线程数，默认为 1 */
    private int taskNum = 1;

    public OssFileTransferClient(FileTransferConfig config) {

        OssConfig ossConfig = config.getOss();

        if (ossConfig == null) {
            throw new RuntimeException("No valid oss config exist. please check 'comet.file-component.<sceneName>.oss' in config file");
        }

        this.defaultBucket = ossConfig.getDefaultBucket();
        this.enableCheckpoint = ossConfig.isEnableCheckpoint();

        MultipartConfig multipartConfig = ossConfig.getMultipart();
        if (ossConfig.getMultipart() != null) {
            if (multipartConfig.getPartSize() > 0) {
                this.partSize = multipartConfig.getPartSize();
            }
            if (multipartConfig.getTaskNum() > 0) {
                this.taskNum = multipartConfig.getTaskNum();
            }
        }

        client = new OSSClientBuilder().build(ossConfig.getEndpoint(), ossConfig.getAccessKeyId(), ossConfig.getAccessKeySecret());
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("OSS 客户端初始化成功！default bucket:{}, enableCheckpoint:{}, partSize{}, taskNum:{}",
                    defaultBucket, enableCheckpoint, partSize, taskNum);
        }
    }

    /**
     * 使用指定场景的客户端上传本地文件到远程服务路径。
     *
     * @param toUploadFile   要上传的文件或文件夹，如果 toUploadFile 是文件夹，将递归上传文件夹下的所有文件到 remoteFilePath文件夹下
     * @param bucket         要上传的bucket，不是对象存储服务（如OSS、COS等）时，该参数无效。为空时，取默认 bucket
     * @param remoteFilePath 文件服务器上的目标文件路径/文件Key。推荐远程文件路径使用相对路径，PS: OSS 不支持以“/”开头的路径，如果以“/”开头，将自动去掉。
     */
    @Override
    public void uploadFile(File toUploadFile, String bucket, String remoteFilePath) {

        if (StringUtils.isEmpty(bucket)) {
            // 如果bucket为空，则使用配置的默认buck
            bucket = this.defaultBucket;
        }

        if (StringUtils.startsWithIgnoreCase(remoteFilePath, "/")) {
            // 如果remoteFilePath以“/”开头，去掉“/”，OSS 的路径不支持以“/”或“\”开头
            remoteFilePath = remoteFilePath.substring(1);
        }


        if (toUploadFile.isDirectory()) {
            // 递归上传整个文件夹
            File[] files = toUploadFile.listFiles();
            if (files != null) {
                for (File file : files) {
                    uploadFile(file, bucket, remoteFilePath + "/" + file.getName());
                }
            }
        } else {

            UploadFileRequest request = new UploadFileRequest(bucket, remoteFilePath, toUploadFile.getAbsolutePath(), this.partSize, this.taskNum, this.enableCheckpoint);

            try {
                // 上传文件
                client.uploadFile(request.withProgressListener(new OssFileUploadProgressListener()));

                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("使用OSS客户端上传文件成功！toUploadFile={}, bucket={}, remoteFilePath={}",
                            toUploadFile.getAbsoluteFile(), bucket, remoteFilePath);
                }

            } catch (Throwable e) {
                throw new RuntimeException(
                        MessageFormat.format("使用OSS客户端上传文件异常！toUploadFile={0}, bucket={1}, remoteFilePath={2}",
                                toUploadFile.getAbsoluteFile(), bucket, remoteFilePath),
                        e);
            }
        }
    }

    /**
     * 关闭客户端
     */
    @Override
    public void close() {
        client.shutdown();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("OSS 客户端关闭成功！");
        }
    }

    /**
     * Called by the garbage collector on an object when garbage collection
     * determines that there are no more references to the object.
     * A subclass overrides the {@code finalize} method to dispose of
     * system resources or to perform other cleanup.
     * <p>
     * The general contract of {@code finalize} is that it is invoked
     * if and when the Java&trade; virtual
     * machine has determined that there is no longer any
     * means by which this object can be accessed by any thread that has
     * not yet died, except as a result of an action taken by the
     * finalization of some other object or class which is ready to be
     * finalized. The {@code finalize} method may take any action, including
     * making this object available again to other threads; the usual purpose
     * of {@code finalize}, however, is to perform cleanup actions before
     * the object is irrevocably discarded. For example, the finalize method
     * for an object that represents an input/output connection might perform
     * explicit I/O transactions to break the connection before the object is
     * permanently discarded.
     * <p>
     * The {@code finalize} method of class {@code Object} performs no
     * special action; it simply returns normally. Subclasses of
     * {@code Object} may override this definition.
     * <p>
     * The Java programming language does not guarantee which thread will
     * invoke the {@code finalize} method for any given object. It is
     * guaranteed, however, that the thread that invokes finalize will not
     * be holding any user-visible synchronization locks when finalize is
     * invoked. If an uncaught exception is thrown by the finalize method,
     * the exception is ignored and finalization of that object terminates.
     * <p>
     * After the {@code finalize} method has been invoked for an object, no
     * further action is taken until the Java virtual machine has again
     * determined that there is no longer any means by which this object can
     * be accessed by any thread that has not yet died, including possible
     * actions by other objects or classes which are ready to be finalized,
     * at which point the object may be discarded.
     * <p>
     * The {@code finalize} method is never invoked more than once by a Java
     * virtual machine for any given object.
     * <p>
     * Any exception thrown by the {@code finalize} method causes
     * the finalization of this object to be halted, but is otherwise
     * ignored.
     *
     * @throws Throwable the {@code Exception} raised by this method
     * @jls 12.6 Finalization of Class Instances
     * @see WeakReference
     * @see PhantomReference
     */
    @Override
    protected void finalize() throws Throwable {
        this.close();
        super.finalize();
    }
}
