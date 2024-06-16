package pers.hubery.filecomponent.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FtpConfig {

    /** ftp 服务器 hostname */
    private String host;

    /** ftp 服务器端口 */
    private int port;

    /** 用户名 */
    private String username;

    /** 密码 */
    private String password;
}
