package pers.hubery.filecomponent.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "comet.file-component")
@Setter
@Getter
public class FileTransferConfigs {

    private static final String DEFAULT_CLIENT_NAME = "default";

    private Map<String, FileTransferConfig> clients;

    /**
     * 根据客户端名称获取客户端配置
     *
     * @param name 客户端名称
     * @return 客户端配置
     */
    public FileTransferConfig getClient(String name) {
        if (clients == null) {
            throw new RuntimeException("No valid file clients config exist. please check 'comet.file-component.clients' in config file");
        }

        // 只配置一个客户端的情况下，始终返回这个客户端配置
        if (clients.size() == 1) {
            return clients.values().iterator().next();
        }

        if (StringUtils.isEmpty(name)) {
            return clients.get(DEFAULT_CLIENT_NAME);
        }

        return clients.get(name);
    }
}
