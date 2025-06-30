package org.dromara.playwright.service.impl;

import org.dromara.playwright.service.SupplierLoginStrategy;
import org.dromara.playwright.service.SupplierProductCrawlerStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SupplierStrategyRegistry {
    @Autowired
    private List<SupplierLoginStrategy> loginStrategies;
    @Autowired
    private List<SupplierProductCrawlerStrategy> crawlerStrategies;

    public SupplierLoginStrategy getLoginStrategy(String name) {
        return loginStrategies.stream()
                .filter(s -> s.supports(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("不支持的供应商名称: " + name));
    }

    public SupplierProductCrawlerStrategy getCrawlerStrategy(String name) {
        return crawlerStrategies.stream()
                .filter(s -> s.supports(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("不支持的供应商名称: " + name));
    }
}
