package com.whxm.harbor.conf;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PathConfig {

    @Value("${resource.path}")
    private String resourcePath;
    @Value("${log.web-path}")
    private String logUri;

    public String getResourcePath() {
        return resourcePath;
    }

    public String getLogUri() {
        return logUri;
    }
}
