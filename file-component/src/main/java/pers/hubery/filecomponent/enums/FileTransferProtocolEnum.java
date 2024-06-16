package pers.hubery.filecomponent.enums;

import lombok.Getter;

@Getter
public enum FileTransferProtocolEnum {

    LOCAL("local", "本地文件传输"),
    SHARE("share", "共享存储"),
    FTP("ftp", "FTP 文件传输协议"),
    SFTP("sftp", "SFTP 文件传输协议"),
    OSS("oss", "阿里云对象存储服务");

    private final String protocolCode;

    private final String description;

    FileTransferProtocolEnum(String protocolCode, String description) {
        this.protocolCode = protocolCode;
        this.description = description;
    }

    /**
     * 根据协议 code 获取协议枚举
     *
     * @param protocolCode 协议 code
     * @return 协议枚举
     */
    public static FileTransferProtocolEnum getByCode(String protocolCode) {
        for (FileTransferProtocolEnum protocolEnum : FileTransferProtocolEnum.values()) {
            if (protocolEnum.getProtocolCode().equals(protocolCode)) {
                return protocolEnum;
            }
        }
        return null;
    }
}
