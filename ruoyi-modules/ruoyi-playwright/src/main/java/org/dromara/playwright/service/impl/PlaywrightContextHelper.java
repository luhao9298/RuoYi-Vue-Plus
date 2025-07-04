package org.dromara.playwright.service.impl;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.Cookie;
import lombok.RequiredArgsConstructor;
import org.dromara.playwright.config.PlaywrightProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PlaywrightContextHelper {
    private final PlaywrightProperties properties;

    /**
     * 创建 Playwright、Browser、BrowserContext，可选注入 cookies
     * @param cookies 可选 cookies
     * @return BrowserContextHolder
     */
    public BrowserContextHolder createContext(List<Cookie> cookies) {
        Playwright playwright = Playwright.create();
        Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                .setHeadless(properties.isHeadless())
                .setSlowMo((double) properties.getSlowMo())
                .setArgs(properties.getBrowserArgs()));
        BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                .setUserAgent(properties.getUserAgent())
                .setViewportSize(properties.getViewport().getWidth(), properties.getViewport().getHeight()));
        if (cookies != null && !cookies.isEmpty()) {
            context.addCookies(cookies);
        }
        return new BrowserContextHolder(playwright, browser, context);
    }

    /**
     * 无 cookies 创建 context
     */
    public BrowserContextHolder createContext() {
        return createContext(null);
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
