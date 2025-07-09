package org.dromara.playwright.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.microsoft.playwright.options.LoadState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.playwright.domain.PriceHistory;
import org.dromara.playwright.domain.ProductInfo;
import org.dromara.playwright.domain.Supplier;
import org.dromara.playwright.mapper.PriceHistoryMapper;
import org.dromara.playwright.mapper.ProductInfoMapper;
import org.dromara.playwright.service.SupplierProductCrawlerStrategy;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.net.URLEncoder;

@Slf4j
@Component
@RequiredArgsConstructor
public class AlmliquorLoginCrawlerStrategy implements SupplierProductCrawlerStrategy {

    private final ProductInfoMapper productInfoMapper;
    private final PriceHistoryMapper priceHistoryMapper;

    public static final String ALMLIQUOR_SEARCH_PREFIX = "https://www.almliquor.com.au/search/?isGridView=false&text=";

    @Override
    public boolean supports(String supplierName) {
        return StrUtil.equalsIgnoreCase("almliquor", supplierName);
    }

    @Override
    public void crawlAndSaveProducts(Supplier supplier, PlaywrightContextHelper.BrowserContextHolder holder) {
        try {
            Page page = holder.context.pages().get(1);
            List<ProductInfo> productInfoList = productInfoMapper.selectList(new LambdaQueryWrapper<ProductInfo>());

            int matchedCount = 0;
            int unmatchedCount = 0;
            List<String> unmatchedNames = new ArrayList<>();
            List<PriceHistory> priceHistoryList = new ArrayList<>();
            for (ProductInfo productInfo : productInfoList) {
                // 强制用全称产品名做比对
                String targetName = productInfo.getProductName();
                String targetNorm = targetName.trim().replace("\u00A0", "").replace(" ", "").toUpperCase();
                System.out.println("数据库产品名(原始): [" + targetName + "]");
                String encodedProductName = URLEncoder.encode(targetName, StandardCharsets.UTF_8);
                String url = ALMLIQUOR_SEARCH_PREFIX + encodedProductName + "&typeahead=false";

                page.navigate(url);
                page.waitForLoadState(LoadState.NETWORKIDLE);
                try {
                    page.waitForSelector("ul#resultsList", new Page.WaitForSelectorOptions().setTimeout(3000).setState(WaitForSelectorState.ATTACHED));
                } catch (Exception e) {
                    System.out.println("产品名: " + targetName + "，未找到匹配的产品！（页面无结果列表）");
                    unmatchedCount++;
                    unmatchedNames.add(targetName);
                    continue;
                }
                // 再查找商品行
                Locator productRows = page.locator("ul#resultsList > li.table-tr.item-list-item");
                int count = productRows.count();
                System.out.println("商品行数: " + count);
                String price = null;
                boolean found = false;

                if (count == 0) {
                    System.out.println("产品名: " + targetName + "，未找到匹配的产品！");
                    unmatchedCount++;
                    unmatchedNames.add(targetName);
                    continue;
                }

                for (int i = 0; i < count; i++) {
                    Locator row = productRows.nth(i);
                    boolean visible = row.isVisible();
                    List<String> descs = row.locator(".description-price.product-description").allInnerTexts();
                    if (descs.isEmpty()) {
                        System.out.println("第" + i + "行 isVisible: " + visible + ", 未找到商品名元素");
                        continue;
                    }
                    // 取所有商品名，逐个比对
                    for (String desc : descs) {
                        String name = desc.trim().replace("\u00A0", "").replace(" ", "").toUpperCase();
                        System.out.println("第" + i + "行 isVisible: " + visible + ", 商品名: " + name);
                        if (name.equals(targetNorm)) {
                            // 找到匹配商品名，提取价格
                            List<String> prices = row.locator(".description-price").allInnerTexts();
                            for (String p : prices) {
                                p = p.trim();
                                if (p.equalsIgnoreCase(desc)) continue;
                                if (p.startsWith("$") || p.matches("\\$\\d+.*")) {
                                    System.out.println("匹配到价格: " + p);
                                    price = p;
                                    found = true;
                                    break;
                                }
                            }
                            if (!found) {
                                System.out.println("产品名: " + targetName + "，未找到匹配的价格！");
                            }
                            break;
                        }
                    }
                    if (found) break;
                }

                if (price == null) {
                    System.out.println("产品名: " + targetName + "，未找到匹配的价格！");
                    unmatchedCount++;
                    unmatchedNames.add(targetName);
                } else {
                    System.out.println("产品名: " + targetName + "，价格: " + price);
                    matchedCount++;

                    // 解析价格，移除货币符号和其他非数字字符（保留小数点）
                    String cleanPrice = price.replaceAll("[^\\d.]", "");
                    BigDecimal retailPrice;
                    try {
                        retailPrice = new BigDecimal(cleanPrice);
                    } catch (NumberFormatException e) {
                        System.out.println("价格解析失败: " + price + "，跳过此商品");
                        unmatchedCount++;
                        unmatchedNames.add(targetName);
                        continue;
                    }

                    PriceHistory priceHistory = new PriceHistory();
                    priceHistory.setProductId(productInfo.getId());
                    priceHistory.setProductName(targetName);
                    priceHistory.setSupplierName(supplier.getName());
                    priceHistory.setCostPrice(retailPrice);
                    priceHistory.setRetailPrice(BigDecimal.ZERO); // 设置默认零售价为0
                    priceHistoryList.add(priceHistory);
                }
            }
            System.out.println("\n采集统计：");
            System.out.println("匹配到价格的商品数: " + matchedCount);
            System.out.println("未匹配到价格的商品数: " + unmatchedCount);
            if (!unmatchedNames.isEmpty()) {
                System.out.println("未匹配到价格的商品名列表:");
                for (String name : unmatchedNames) {
                    System.out.println("- " + name);
                }
            }
            // 批量插入价格历史 - 使用 MyBatis-Plus 的 insertOrUpdateBatch
            if (!priceHistoryList.isEmpty()) {
                priceHistoryMapper.insertOrUpdateBatch(priceHistoryList);
                System.out.println("批量处理完成，共处理 " + priceHistoryList.size() + " 条价格记录");
            }
            log.info("采集完成，商品总数: {}", productInfoList.size());
        } catch (Exception e) {
            log.error("采集商品信息失败: {}", e.getMessage(), e);
        }
    }
}
