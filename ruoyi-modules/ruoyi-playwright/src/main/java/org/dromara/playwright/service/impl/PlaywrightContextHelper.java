package org.dromara.playwright.service.impl;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Playwright;

import java.util.Arrays;

public class PlaywrightContextHelper {
    /**
     * 创建默认的Playwright、Browser、BrowserContext
     * @return BrowserContextHolder，包含Playwright、Browser、BrowserContext
     */
    public static BrowserContextHolder createDefaultContext() {
        Playwright playwright = Playwright.create();
        Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                .setHeadless(false)
                .setSlowMo(1000)
                .setArgs(Arrays.asList(
                        "--disable-blink-features=AutomationControlled",
                        "--disable-web-security",
                        "--disable-features=VizDisplayCompositor"
                )));
        BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                .setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                .setViewportSize(1512, 982));
        return new BrowserContextHolder(playwright, browser, context);
    }

    /**
     * 资源持有对象，便于统一关闭
     */
    public static class BrowserContextHolder implements AutoCloseable {
        public final Playwright playwright;
        public final Browser browser;
        public final BrowserContext context;
        public BrowserContextHolder(Playwright playwright, Browser browser, BrowserContext context) {
            this.playwright = playwright;
            this.browser = browser;
            this.context = context;
        }
        @Override
        public void close() {
            if (context != null) context.close();
            if (browser != null) browser.close();
            if (playwright != null) playwright.close();
        }
    }
}
