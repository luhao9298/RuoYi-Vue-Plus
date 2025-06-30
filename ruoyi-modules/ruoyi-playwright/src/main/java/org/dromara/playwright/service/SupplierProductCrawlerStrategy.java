package org.dromara.playwright.service;


import org.dromara.playwright.domain.Supplier;
import org.dromara.playwright.domain.SupplierSession;

public interface SupplierProductCrawlerStrategy {
    /**
     * 用登录后的session抓取商品信息
     */
    void crawlAndSaveProducts(Supplier supplier, SupplierSession session);

    /**
     * 是否支持该名称
     */
    boolean supports(String supplierName);
}
