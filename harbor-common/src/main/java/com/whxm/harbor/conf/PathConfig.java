package com.whxm.harbor.conf;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PathConfig {

    @Value("${resource.path}")
    private String resourcePath;
    @Value("${log.parent-path}")
    private String logParentPath;
    @Value("${log.bug-path}")
    private String bugPath;

    public String getResourcePath() {
        return resourcePath;
    }

    public String getAbsoluteBugFilePath() {
        return logParentPath + bugPath;
    }

    public String getBugDetailURL() {
        return resourcePath + bugPath;
    }
}
