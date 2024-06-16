package pers.hubery.filecomponent.service;

import java.io.File;

public interface FileTransferService {

    /**
     * 使用默认客户端上传本地文件到远程服务路径。
     *
     * @param toUploadFile   要上传的文件或文件夹，如果 toUploadFile 是文件夹，将递归上传文件夹下的所有文件到 remoteFilePath文件夹下
     * @param remoteFilePath 文件服务器上的目标文件路径/文件Key。推荐远程文件路径使用相对路径，PS: OSS 不支持以“/”开头的路径，如果以“/”开头，将自动去掉。另外，sftp等权限管理严格，根目录通常都没有权限，请谨慎使用。
     */
    void uploadFile(File toUploadFile, String remoteFilePath);

    /**
     * 使用指定场景的客户端上传本地文件到远程服务路径
     *
     * @param toUploadFile   要上传的文件或文件夹，如果 toUploadFile 是文件夹，将递归上传文件夹下的所有文件到 remoteFilePath文件夹下
     * @param sceneName      场景名称，系统会根据场景名称匹配对应的客户端，该参数为空并且只有一份客户端配置时，将使用默认客户端
     * @param remoteFilePath 文件服务器上的目标文件路径/文件Key。推荐远程文件路径使用相对路径，PS: OSS 不支持以“/”开头的路径，如果以“/”开头，将自动去掉。另外，sftp等权限管理严格，根目录通常都没有权限，请谨慎使用。，如果客户端配置的是对象存储服务（如OSS、COS等），将使用默认的bucket。例如：OSS的默认bucket配置key为：comet.file-component.clients.${sceneName}.oss.default-bucket
     */
    void uploadFile(File toUploadFile, String sceneName, String remoteFilePath);


    /**
     * 使用指定场景的客户端上传本地文件到远程服务路径。
     *
     * @param toUploadFile   要上传的文件或文件夹，如果 toUploadFile 是文件夹，将递归上传文件夹下的所有文件到 remoteFilePath文件夹下
     * @param sceneName      场景名称，系统会根据场景名称匹配对应的客户端，该参数为空并且只有一份客户端配置时，将使用默认客户端
     * @param bucket         要上传的bucket，不是对象存储服务（如OSS、COS等）时，该参数无效
     * @param remoteFilePath 文件服务器上的目标文件路径/文件Key。推荐远程文件路径使用相对路径，PS: OSS 不支持以“/”开头的路径，如果以“/”开头，将自动去掉。另外，sftp等权限管理严格，根目录通常都没有权限，请谨慎使用。
     */
    void uploadFile(File toUploadFile, String sceneName, String bucket, String remoteFilePath);

    /**
     * 使用默认客户端下载远程文件到本地路径。
     * <p>
     * 如果远程服务是对象存储服务（如OSS、COS等），将使用默认的bucket。例如：OSS的默认bucket配置key为：comet.file-component.clients.${sceneName}.oss.default-bucket
     *
     * @param remoteFilePath 文件服务器上的远程文件路径
     * @param localFilePath  本地文件路径
     * @return 下载的文件
     */
    File downloadFile(String remoteFilePath, String localFilePath);

    /**
     * 使用指定场景的客户端下载远程文件到本地路径。
     * <p>
     * * 如果远程服务是对象存储服务（如OSS、COS等），将使用默认的bucket。例如：OSS的默认bucket配置key为：comet.file-component.clients.${sceneName}.oss.default-bucket
     *
     * @param sceneName      场景名称，系统会根据场景名称匹配对应的客户端，该参数为空并且只有一份客户端配置时，将使用默认客户端
     * @param remoteFilePath 文件服务器上的远程文件路径
     * @param localFilePath  本地文件路径
     * @return 下载的文件
     */
    File downloadFile(String sceneName, String remoteFilePath, String localFilePath);

    /**
     * 使用指定场景的客户端下载远程文件到本地路径。
     *
     * @param sceneName      场景名称，系统会根据场景名称匹配对应的客户端，该参数为空并且只有一份客户端配置时，将使用默认客户端
     * @param bucket         要下载的文件所在对象存储服务上的bucket，如果使用的不是文件存储服务，该参数将不起作用
     * @param remoteFilePath 要下载的文件在服务器上的文件路径或文件Key
     * @param localFilePath  要下载到的本地文件路径
     * @return 下载的文件
     */
    File downloadFile(String sceneName, String bucket, String remoteFilePath, String localFilePath);


    /**
     * 使用默认客户端下载远程文件到本地文件夹下
     * <p>
     * 如果远程服务是对象存储服务（如OSS、COS等），将使用默认的bucket。例如：OSS的默认bucket配置key为：comet.file-component.clients.${sceneName}.oss.default-bucket
     *
     * @param remoteFilePath  要下载的文件在服务器上的文件路径或文件Key
     * @param targetDirectory 要下载到的目标文件夹
     * @return 下载的文件
     */
    File downloadFileToDirectory(String remoteFilePath, File targetDirectory);

    /**
     * 使用指定场景的客户端下载远程文件到本地文件夹下
     *
     * @param sceneName       场景名称，系统会根据场景名称匹配对应的客户端，该参数为空并且只有一份客户端配置时，将使用默认客户端
     * @param remoteFilePath  要下载的文件在服务器上的文件路径或文件Key
     * @param targetDirectory 要下载到的目标文件夹
     * @return 下载的文件
     */
    File downloadFileToDirectory(String sceneName, String remoteFilePath, File targetDirectory);

    /**
     * 使用指定场景的客户端下载远程文件到本地文件夹下
     *
     * @param sceneName       场景名称，系统会根据场景名称匹配对应的客户端，该参数为空并且只有一份客户端配置时，将使用默认客户端
     * @param bucket          要下载的文件所在对象存储服务上的bucket，如果使用的不是文件存储服务，该参数将不起作用
     * @param remoteFilePath  要下载的文件在服务器上的文件路径或文件Key
     * @param targetDirectory 要下载到的目标文件夹
     * @return 下载的文件
     */
    File downloadFileToDirectory(String sceneName, String bucket, String remoteFilePath, File targetDirectory);


    /**
     * 使用默认客户端判断远程文件是否在远程服务器上存在
     * <p>
     * * 如果远程服务是对象存储服务（如OSS、COS等），将使用默认的bucket。例如：OSS的默认bucket配置key为：comet.file-component.clients.${sceneName}.oss.default-bucket
     *
     * @param remoteFilePath 远程文件路径
     * @return 是否存在
     */
    boolean isFileExistInServer(String remoteFilePath);

    /**
     * 使用指定场景的客户端判断远程文件是否在远程服务器上存在
     * <p>
     * * * 如果远程服务是对象存储服务（如OSS、COS等），将使用默认的bucket。例如：OSS的默认bucket配置key为：comet.file-component.clients.${sceneName}.oss.default-bucket
     *
     * @param sceneName      场景名称，系统会根据场景名称匹配对应的客户端，该参数为空并且只有一份客户端配置时，将使用默认客户端
     * @param remoteFilePath 远程文件路径
     * @return 是否存在
     */
    boolean isFileExistInServer(String sceneName, String remoteFilePath);

    /**
     * 使用指定场景的客户端判断远程文件是否在远程服务器上存在
     *
     * @param sceneName      场景名称，系统会根据场景名称匹配对应的客户端，该参数为空并且只有一份客户端配置时，将使用默认客户端
     * @param bucket         要下载的文件所在对象存储服务上的bucket，如果使用的不是文件存储服务，该参数将不起作用，如果使用的不是文件存储服务，该参数将不起作用
     * @param remoteFilePath 远程文件路径
     * @return 是否存在
     */
    boolean isFileExistInServer(String sceneName, String bucket, String remoteFilePath);
}
