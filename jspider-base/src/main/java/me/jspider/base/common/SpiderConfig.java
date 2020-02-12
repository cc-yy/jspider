package me.jspider.base.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpiderConfig {

    @Value("${spider.chrome.driver}")
    private String chromeDriver;

    public String getChromeDriver() {
        return chromeDriver;
    }
}
