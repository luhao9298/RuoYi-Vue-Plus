package org.dromara.playwright.service.impl;

import cn.hutool.core.collection.CollUtil;
import org.dromara.playwright.domain.ProductInfo;
import org.dromara.playwright.domain.Supplier;
import org.dromara.playwright.mapper.ProductInfoMapper;
import org.dromara.playwright.service.SupplierProductCrawlerStrategy;
import org.springframework.stereotype.Component;
import com.microsoft.playwright.*;
import java.util.*;
import lombok.RequiredArgsConstructor;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class OnshopfrontProductCrawlerStrategy implements SupplierProductCrawlerStrategy {

    private final ProductInfoMapper productInfoMapper;
    private final PlaywrightContextHelper playwrightContextHelper;

    @Override
    public void crawlAndSaveProducts(Supplier supplier, PlaywrightContextHelper.BrowserContextHolder holder) {
        List<ProductInfo> productList = fetchProductsFromOnshopfront(supplier, holder);
        if (CollUtil.isNotEmpty(productList)) {
            productInfoMapper.insertOrUpdateBatch(productList);
        }
    }

    @Override
    public boolean supports(String supplierName) {
        return StrUtil.equalsIgnoreCase("onshopfront", supplierName);
    }

    private List<ProductInfo> fetchProductsFromOnshopfront(Supplier supplier, PlaywrightContextHelper.BrowserContextHolder holder) {
        List<ProductInfo> productList = new ArrayList<>();
        try {
            Page page = holder.context.pages().get(0);
            String productUrl = supplier.getWebsite().replaceAll("/$", "") + "/products";
            page.navigate(productUrl);
            page.waitForTimeout(1000);

            Set<String> allSeenProductIds = new HashSet<>();
            int consecutiveNoNewProducts = 0;
            int maxConsecutiveNoNewProducts = 5;
            int maxScrollTimes = 100; // 防止死循环
            int scrollCount = 0;

            while (consecutiveNoNewProducts < maxConsecutiveNoNewProducts && scrollCount < maxScrollTimes) {
                Locator rows = page.locator(".index-page-body-row");
                int count = rows.count();
                int newFound = 0;

                for (int i = 0; i < count; i++) {
                    try {
                        Locator row = rows.nth(i);
                        String href = row.locator(".product-name .product-column-name").getAttribute("href");
                        String productId = "";
                        if (href != null && href.contains("/product/")) {
                            productId = href.substring(href.lastIndexOf("/") + 1);
                        }
                        if (!productId.isEmpty() && allSeenProductIds.add(productId)) {
                            newFound++;
                            productList.add(parseProductInfo(row, productId, href));
                        }
                    } catch (Exception ex) {
                        log.warn("解析单个商品信息失败: {}", ex.getMessage());
                    }
                }

                if (newFound == 0) {
                    consecutiveNoNewProducts++;
                } else {
                    consecutiveNoNewProducts = 0;
                }

                // 滚动到底部
                page.mouse().wheel(0, 120 * 16);
                page.waitForTimeout(100);
                scrollCount++;
            }
            log.info("采集完成，商品总数: {}", productList.size());
        } catch (Exception e) {
            log.error("采集商品信息失败: {}", e.getMessage(), e);
        }
        return productList;
    }

    private ProductInfo parseProductInfo(Locator row, String productId, String href) {
        ProductInfo info = new ProductInfo();
        info.setShopfrontId(productId);
        info.setProductName(row.locator(".product-name .product-column-name").textContent());
        info.setStatus(row.locator(".product-status").textContent());
        info.setCategory(row.locator(".product-category").textContent());
        String inventoryText = row.locator(".product-inventory").textContent();
        try {
            info.setInventory(Long.parseLong(inventoryText.replaceAll("\\D", "")));
        } catch (Exception e) {
            info.setInventory(null);
        }
        info.setProductUrl(href);
        return info;
    }

}
