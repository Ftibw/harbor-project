package com.whxm.harbor.conf;

import com.whxm.harbor.constant.Constant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PathConfig {

    @Value("${resource.path}")
    private String resourcePath;
    @Value("${log."+ Constant.PLATFORM+"-parent-path}")
    private String logParentPath;
    @Value("${log.web-path}")
    private String logUri;

    public String getResourcePath() {
        return resourcePath;
    }

    public String getLogParentPath() {
        return logParentPath;
    }

    public String getLogUri() {
        return logUri;
    }
}
