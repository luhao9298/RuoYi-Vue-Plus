package org.dromara.playwright.service;


import org.dromara.playwright.domain.Supplier;
import org.dromara.playwright.service.impl.PlaywrightContextHelper;

public interface SupplierLoginStrategy {
    /**
     * 登录并返回上下文
     */
    PlaywrightContextHelper.BrowserContextHolder loginAndGetContext(Supplier supplier);

    /**
     * 是否支持该名称
     */
    boolean supports(String supplierName);
}
