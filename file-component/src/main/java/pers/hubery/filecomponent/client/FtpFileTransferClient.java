package pers.hubery.filecomponent.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.hubery.filecomponent.config.FileTransferConfig;
import org.apache.commons.net.ftp.FTPClient;
import pers.hubery.filecomponent.config.FtpConfig;
import pers.hubery.filecomponent.util.FilePathUtil;

import java.io.*;
import java.lang.ref.PhantomReference;
import java.lang.ref.WeakReference;
import java.nio.file.Files;
import java.text.MessageFormat;

public class FtpFileTransferClient implements FileTransferClient {

    /** 日志 */
    private static final Logger LOGGER = LoggerFactory.getLogger(FtpFileTransferClient.class);


    /** FTP 客户端 */
    private final FTPClient ftpClient;

    /**
     * 构造器
     *
     * @param config 文件传输配置
     */
    FtpFileTransferClient(FileTransferConfig config) {

        // 工作目录
        String workingDirectoryInServer = config.getWorkingDirectoryInServer();

        FtpConfig ftpConfig = config.getFtp();

        if (ftpConfig == null) {
            throw new RuntimeException("Ftp config is null");
        }

        this.ftpClient = new FTPClient();

        try {
            ftpClient.connect(ftpConfig.getHost(), ftpConfig.getPort());

            boolean loginSuccess = ftpClient.login(ftpConfig.getUsername(), ftpConfig.getPassword());
            if (!loginSuccess) {
                throw new RuntimeException("登录失败");
            }

            ftpClient.enterLocalPassiveMode();

            // 设置文件类型为二进制
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Login to ftp server success!");
            }

            if (workingDirectoryInServer != null) {
                if (!ftpClient.changeWorkingDirectory(workingDirectoryInServer)) {
                    LOGGER.error("切换当前工作目录失败！workingDirectoryInServer=" + workingDirectoryInServer);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 使用指定场景的客户端上传本地文件到远程服务路径。
     *
     * @param toUploadFile   要上传的文件或文件夹，如果 toUploadFile 是文件夹，将递归上传文件夹下的所有文件到 remoteFilePath文件夹下
     * @param bucket         要上传的bucket，不是对象存储服务（如OSS、COS等）时，该参数无效
     * @param remoteFilePath 文件服务器上的目标文件路径/文件Key
     */
    @Override
    public void uploadFile(File toUploadFile, String bucket, String remoteFilePath) {
        uploadFile(toUploadFile, bucket, remoteFilePath, true);
    }

    /**
     * 使用指定场景的客户端上传本地文件到远程服务路径。
     *
     * @param toUploadFile       要上传的文件或文件夹，如果 toUploadFile 是文件夹，将递归上传文件夹下的所有文件到 remoteFilePath文件夹下
     * @param bucket             要上传的bucket，不是对象存储服务（如OSS、COS等）时，该参数无效
     * @param remoteFilePath     文件服务器上的目标文件路径/文件Key
     * @param autoMakeParentDirs 是否检查服务器上上级文件夹是否存在，如果不存在则自动创建。递归的时候，会设为false，避免不断检查上级目录，造成额外的交互开销
     */
    private void uploadFile(File toUploadFile, String bucket, String remoteFilePath, boolean autoMakeParentDirs) {
        try {
            if (toUploadFile.isDirectory()) {
                //如果是文件夹上传，先判断远程目录是否存在，如果不存在则创建。

                //先检查远程目录是否存在，如果不存在则创建
                if (autoMakeParentDirs) {
                    mkdirs(remoteFilePath);
                } else {
                    // 递归的时候，无需检查上级目录，这样可以避免掉和服务器的多次无效交互
                    mkdir(remoteFilePath);
                }

                // 然后递归上传整个文件夹
                File[] files = toUploadFile.listFiles();
                for (File file : files) {
                    uploadFile(file, bucket, remoteFilePath + "/" + file.getName(), false);
                }
            } else {
                //如果是文件上传，先检查目标上级目录是否存在，如果不存在则自动创建
                if (autoMakeParentDirs) {
                    String parentDir = FilePathUtil.getParentPath(remoteFilePath);
                    if (parentDir != null) {
                        // 先创建上级目录，FTP 不支持直接创建所有目录，只能一级一级创建
                        mkdirs(parentDir);
                    }
                }

                // 开始文件上传
                try (InputStream in = new BufferedInputStream(Files.newInputStream(toUploadFile.toPath()))) {
                    if (ftpClient.storeFile(remoteFilePath, in)) {
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("文件上传成功！ remoteFilePath={}", remoteFilePath);
                        }
                    } else {
                        throw new RuntimeException(MessageFormat.format("文件上传失败！remoteFilePath={0}", remoteFilePath));
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 在远程服务器上传创建文件夹，如有必要回递归创建父文件夹
     *
     * @param remoteDir 远程文件夹
     * @throws IOException IO 异常
     */
    private void mkdirs(String remoteDir) throws IOException {
        //备份当前的工作目录
        String currentWorkingDirectory = ftpClient.printWorkingDirectory();

        if (!this.changeWorkingDirectory(remoteDir)) {
            // 如果文件夹不存在则创建

            // 先创建上级目录，FTP 不支持直接创建所有目录，只能一级一级创建
            String parentDir = FilePathUtil.getParentPath(remoteDir);
            if (parentDir != null) {
                // 递归创建上级目录
                mkdirs(parentDir);
            }

            // 创建当前目录
            if (ftpClient.makeDirectory(remoteDir)) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("成功创建远程目录：{}", remoteDir);
                }
            } else {
                throw new RuntimeException("创建远程目录失败！remoteDir=" + remoteDir);
            }
        } else {
            // 还原当前工作目录，防止使用的是相对路径
            this.changeWorkingDirectory((currentWorkingDirectory));
        }
    }

    /**
     * 在远程服务器上传创建文件夹，不检查上级目录是否存在
     *
     * @param remoteDir 远程文件夹路径
     * @throws IOException IO 异常
     */
    private void mkdir(String remoteDir) throws IOException {
        //备份当前的工作目录
        String currentWorkingDirectory = ftpClient.printWorkingDirectory();

        if (!this.changeWorkingDirectory(remoteDir)) {
            // 如果文件夹不存在则创建
            // 创建当前目录
            if (ftpClient.makeDirectory(remoteDir)) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("成功创建远程目录：{}", remoteDir);
                }
            } else {
                throw new RuntimeException("创建远程目录失败！remoteDir=" + remoteDir);
            }
        } else {
            // 还原当前工作目录，防止使用的是相对路径
            this.changeWorkingDirectory((currentWorkingDirectory));
        }
    }

    /**
     * 切换远程服务器当前工作目录，可以用于检查远程目录是否存在
     *
     * @param remoteDir 远程目录
     * @return 是否切换成功
     * @throws IOException IO 异常
     */
    private boolean changeWorkingDirectory(String remoteDir) throws IOException {
        if (ftpClient.changeWorkingDirectory(remoteDir)) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("成功切换远程服务器当前工作目录到：{}", remoteDir);
            }
            return true;
        } else {
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn("切换远程服务器当前工作目录失败：{}", remoteDir);
            }
            return false;
        }
    }


    /**
     * 关闭客户端
     */
    public void close() {
        try {

            this.ftpClient.logout();

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("disconnecting from ftp server ...");
            }

            this.ftpClient.disconnect();
        } catch (IOException e) {
            throw new RuntimeException(e);
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
