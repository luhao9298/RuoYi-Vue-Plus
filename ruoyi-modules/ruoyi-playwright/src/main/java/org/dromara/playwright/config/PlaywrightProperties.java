package org.dromara.playwright.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "playwright")
public class PlaywrightProperties {
    private boolean headless = true;
    private int slowMo = 1000;
    private String userAgent;
    private Viewport viewport = new Viewport();
    private List<String> browserArgs;

    @Data
    public static class Viewport {
        private int width = 1512;
        private int height = 982;
    }
} 