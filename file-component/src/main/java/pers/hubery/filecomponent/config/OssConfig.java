package pers.hubery.filecomponent.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OssConfig {

    /** OSS endpoint */
    private String endpoint;

    /** OSS access key id */
    private String accessKeyId;

    /** OSS access key secret */
    private String accessKeySecret;

    /** 默认的bucket，绝大部分情况下，一个业务系统只需要访问一个bucket，调用文件上传和下载接口时，可以不指定 bucket，会自动使用这里配置的默认 bucket。如果一个业务系统需要访问多个不同的bucket，那么需要在调用接口时指定 bucket。 */
    private String defaultBucket;

    /** 是否启用断点续传，默认关闭 */
    private boolean enableCheckpoint = false;

    /** 分片上传和下载配置，可以通过将分片大小设的足够大来禁用分片 */
    private MultipartConfig multipart;
}