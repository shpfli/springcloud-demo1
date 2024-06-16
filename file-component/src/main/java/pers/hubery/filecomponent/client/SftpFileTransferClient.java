package pers.hubery.filecomponent.client;

import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.hubery.filecomponent.config.FileTransferConfig;
import pers.hubery.filecomponent.config.SftpConfig;
import pers.hubery.filecomponent.util.FilePathUtil;

import java.io.File;
import java.lang.ref.PhantomReference;
import java.lang.ref.WeakReference;
import java.text.MessageFormat;

public class SftpFileTransferClient implements FileTransferClient {

    /** 日志 */
    private static final Logger LOGGER = LoggerFactory.getLogger(SftpFileTransferClient.class);


    /** SFTP Session */
    private Session session = null;

    /** SFTP Channel */
    private final ChannelSftp channelSftp;


    /**
     * 构造器
     *
     * @param config 文件传输配置
     */
    SftpFileTransferClient(FileTransferConfig config) {

        //默认 SFTP 工作目录
        String workingDirectoryInServer = config.getWorkingDirectoryInServer();

        SftpConfig sftpConfig = config.getSftp();

        if (sftpConfig == null) {
            throw new RuntimeException("Sftp config is null");
        }

        String username = sftpConfig.getUsername();
        String hostname = sftpConfig.getHost();
        int port = sftpConfig.getPort();
        String password = sftpConfig.getPassword();

        try {

            JSch jsch = new JSch();
            session = jsch.getSession(username, hostname, port);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("sftp session connected!");
            }

            channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("sftp channel opened!");
            }
        } catch (JSchException e) {
            throw new RuntimeException("connect to sftp server failed", e);
        }

        // 切换默认工作目录
        if (workingDirectoryInServer != null) {
            try {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("默认工作目录：{}", channelSftp.pwd());
                }
                channelSftp.cd(workingDirectoryInServer);

                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("已切换当前工作目录到:{}", workingDirectoryInServer);
                }
            } catch (SftpException e) {
                LOGGER.error("切换当前工作目录失败！workingDirectoryInServer=" + workingDirectoryInServer);
                throw new RuntimeException(e);
            }
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

        try {
            this.uploadFile(toUploadFile, bucket, remoteFilePath, true);
        } catch (SftpException e) {
            throw new RuntimeException(
                    MessageFormat.format("上传文件到sftp服务器时发生异常！toUploadFilePath={0}, remoteFilePath={1}",
                            toUploadFile.getAbsoluteFile(), remoteFilePath),
                    e);
        }
    }

    private void uploadFile(File toUploadFile, String bucket, String remoteFilePath, boolean autoMakeParentDirs) throws SftpException {

        if (toUploadFile.isDirectory()) {
            // 检查远程文件夹是否存在，不存在则自动创建
            this.mkdirs(remoteFilePath, autoMakeParentDirs);

            File[] files = toUploadFile.listFiles();
            if (files != null) {
                //遍历上传
                for (File file : files) {
                    uploadFile(file, bucket, remoteFilePath + "/" + file.getName(), false);
                }
            }
        } else {

            if (autoMakeParentDirs) {
                // 递归创建父文件夹
                String parentDirPath = FilePathUtil.getParentPath(remoteFilePath);
                if (parentDirPath != null) {
                    this.mkdirs(parentDirPath, true);
                }
            }

            // 上传文件
            channelSftp.put(toUploadFile.getAbsolutePath(), remoteFilePath);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("文件{}上传成功！", remoteFilePath);
            }
        }
    }

    /**
     * 创建文件夹，如果父文件夹不存在，则递归创建父文件夹。
     *
     * @param remoteDirPath      远程文件夹
     * @param autoMakeParentDirs 是否检查父文件夹，并自动创建。递归时，无需重复检查，应设为false。
     */
    private void mkdirs(String remoteDirPath, boolean autoMakeParentDirs) {

        if (!this.isDirExist(remoteDirPath)) {

            if (autoMakeParentDirs) {
                // 递归创建父文件夹
                String parentDirPath = FilePathUtil.getParentPath(remoteDirPath);
                if (parentDirPath != null) {
                    this.mkdirs(parentDirPath, true);
                }
            }

            // 创建当前文件夹
            try {
                channelSftp.mkdir(remoteDirPath);

                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("远程文件夹{}创建成功！", remoteDirPath);
                }
            } catch (SftpException e) {
                throw new RuntimeException("远程文件夹创建失败。remoteDirPath=" + remoteDirPath, e);
            }
        }
    }

    /**
     * 判断远程文件夹是否存在
     *
     * @param remoteDirPath 远程文件夹路径
     * @return 是否存在
     */
    private boolean isDirExist(String remoteDirPath) {
        try {
            SftpATTRS attrs = channelSftp.lstat(remoteDirPath);
            if (attrs != null) {
                if (attrs.isDir()) {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("远程文件夹{}已经存在！", remoteDirPath);
                    }
                    return true;
                } else {
                    throw new RuntimeException("远程路径" + remoteDirPath + "不是文件夹！");
                }
            }
        } catch (SftpException e) {
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn("远程文件夹{}不存在", remoteDirPath);
            }
        }

        return false;
    }

    /**
     * 关闭客户端
     */
    @Override
    public void close() {

        if (channelSftp != null) {
            channelSftp.disconnect();

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("closed sftp channel to server");
            }
        }

        if (session != null) {
            session.disconnect();

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("closed sftp session to server");
            }
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("disconnected success from sftp server");
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
