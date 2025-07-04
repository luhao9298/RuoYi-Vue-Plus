package org.dromara.playwright.service;


import org.dromara.playwright.domain.Supplier;
import org.dromara.playwright.service.impl.PlaywrightContextHelper;

public interface SupplierProductCrawlerStrategy {
    /**
     * 用登录后的上下文抓取商品信息
     */
    void crawlAndSaveProducts(Supplier supplier, PlaywrightContextHelper.BrowserContextHolder holder);

    /**
     * 是否支持该名称
     */
    boolean supports(String supplierName);
}
