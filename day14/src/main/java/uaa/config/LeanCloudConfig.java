package uaa.config;

import cn.leancloud.core.AVOSCloud;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
@RequiredArgsConstructor
public class LeanCloudConfig {
    private final AppProperties appProperties;

    @PostConstruct
    public void initialize(){
        AVOSCloud.initialize(appProperties.getLeanCloud().getAppId(),appProperties.getLeanCloud().getAppKey());
    }
}
