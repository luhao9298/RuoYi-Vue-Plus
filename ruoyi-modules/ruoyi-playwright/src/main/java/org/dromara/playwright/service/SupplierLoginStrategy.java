package org.dromara.playwright.service;


import org.dromara.playwright.domain.Supplier;
import org.dromara.playwright.domain.SupplierSession;

public interface SupplierLoginStrategy {
    /**
     * 登录并返回会话信息
     */
    SupplierSession login(Supplier supplier);

    /**
     * 是否支持该名称
     */
    boolean supports(String supplierName);
}
