package org.dromara.playwright.service.impl;

import org.dromara.playwright.domain.Supplier;
import org.dromara.playwright.domain.SupplierSession;
import org.dromara.playwright.service.SupplierLoginStrategy;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class OnshopfrontLoginStrategy implements SupplierLoginStrategy {

    @Override
    public SupplierSession login(Supplier supplier) {
        // 这里用伪代码，实际应用Playwright/Selenium/HttpClient等登录逻辑
        // 假设登录后获得token和cookie
        String token = "模拟token";
        String cookie = "cookie1=xxx;cookie2=yyy";

        SupplierSession session = new SupplierSession();
        session.setToken(token);
        session.setCookie(cookie);
        session.setLoginTime(new Date());
        session.setUsername(supplier.getUsername());
        session.setPassword(supplier.getPassword());
        // 其他信息可放入extra
        return session;
    }

    @Override
    public boolean supports(String supplierName) {
        return "onshopfront".equalsIgnoreCase(supplierName);
    }
}
