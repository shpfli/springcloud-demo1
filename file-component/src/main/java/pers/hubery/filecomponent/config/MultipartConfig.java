package pers.hubery.filecomponent.config;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MultipartConfig {

    /** 分片大小，单位：字节，默认100K，即102400，可以通过将分片大小设的足够大来禁用分片 */
    private int partSize;

    /** 分片上传或下载时的并发线程数，默认为 1 */
    private int taskNum;
}
