package org.dromara.test;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitUntilState;
import com.microsoft.playwright.options.LoadState;
import jakarta.annotation.Resource;
import org.dromara.system.mapper.ProductInfoMapper;
import org.dromara.system.domain.ProductInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.nio.file.Paths;
import java.util.Arrays;


/**
 * 瓶子单元测试案例
 *
 * @author Ethan Lu
 */
@DisplayName("瓶子单元测试案例")
@SpringBootTest
@ActiveProfiles("dev")
public class BottleUnitTest {

    @Resource
    private ProductInfoMapper productInfoMapper;

    // 判断是否为静态资源
    private static boolean isStaticResource(String url) {
        return url.matches(".*\\.(js|css|png|jpg|jpeg|gif|svg|woff2?|ttf|eot|ico|map)(\\?.*)?$");
    }

    // 判断是否为网页内容
    private static boolean isHtmlContent(String contentType) {
        return contentType != null && contentType.contains("text/html");
    }

    // 只打印主业务域名的接口
    private static boolean isBusinessApi(String url) {
        // 只保留 plazacellars.onshopfront.com 相关
        return url.contains("plazacellars.onshopfront.com");
    }

    // 判断是否为 notifications 接口
    private static boolean isNotificationsApi(String url) {
        return url.contains("plazacellars.onshopfront.com/notifications");
    }

    @Test
    @DisplayName("测试爬取并保存瓶子产品信息")
    public void testCrawlAndSaveProducts() {
        try (Playwright playwright = Playwright.create()) {
            // 配置浏览器，增加稳定性
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                .setHeadless(false)
                .setSlowMo(1000) // 添加延迟，模拟真实用户操作
                .setArgs(Arrays.asList(
                    "--disable-blink-features=AutomationControlled",
                    "--disable-web-security",
                    "--disable-features=VizDisplayCompositor"
                )));

            // 配置浏览器上下文
            BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                .setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                .setViewportSize(1512, 982)); // 14英寸 MacBook Pro 的典型分辨率

            Page page = context.newPage();

            // 添加页面关闭监听
            page.onClose(page1 -> System.out.println("页面被关闭"));

            // 添加页面崩溃监听
            page.onCrash(page1 -> System.out.println("页面崩溃"));

            // 监听请求
//            page.onRequest(request -> {
//                String url = request.url();
//                // 只打印主业务域名且不是 blob 协议且不是静态资源且不是 notifications 的请求
//                if (isBusinessApi(url) && !url.startsWith("blob:") && !isStaticResource(url) && !isNotificationsApi(url)) {
//                    System.out.println("请求: " + request.method() + " " + url);
//                    if (request.postData() != null) {
//                        System.out.println("请求体: " + request.postData());
//                    }
//                }
//            });
//
//            // 监听响应
//            page.onResponse(response -> {
//                String url = response.url();
//                String contentType = response.headers().getOrDefault("content-type", "");
//                // 只打印主业务域名且不是 blob 协议且不是静态资源且不是 notifications 的响应
//                if (isBusinessApi(url) && !url.startsWith("blob:") && !isStaticResource(url) && !isHtmlContent(contentType) && !isNotificationsApi(url)) {
//                    System.out.println("响应: " + response.status() + " " + url);
//                    try {
//                        System.out.println("响应内容: " + response.text());
//                    } catch (Exception e) {
//                        System.out.println("响应内容获取失败: " + e.getMessage());
//                    }
//                }
//            });

            int maxRetry = 3;
            for (int attempt = 1; attempt <= maxRetry; attempt++) {
                try {
                    page.navigate("https://plazacellars.onshopfront.com/",
                        new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED).setTimeout(30000));
                    break; // 成功就退出循环
                } catch (PlaywrightException e) {
                    System.out.println("第" + attempt + "次加载失败: " + e.getMessage());
                    if (attempt == maxRetry) throw e; // 最后一次还失败就抛出
                    try {
                        Thread.sleep(3000); // 等3秒重试
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt(); // 保持中断状态
                        throw new RuntimeException("Sleep被中断", ie);
                    }
                }
            }

            page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("debug.png")));

            // 2. 等待输入框出现再填充
            page.waitForSelector("#shopfront-field-u", new Page.WaitForSelectorOptions().setTimeout(20000));
            page.fill("#shopfront-field-u", "luhao");
            page.fill("#shopfront-field-p", "luhao888");
            page.click("#login-actions button");

            // 等待登录完成，直接处理"Not using a register"选项
            try {
                // 等待页面稳定
                page.waitForTimeout(3000);

                // 检查是否有模态对话框
                boolean hasModal = page.locator(".z-1.fixed.inset-0").count() > 0;

                // 如果有模态对话框，点击"Not using a register"
                if (hasModal) {
                    // 先确认弹窗里有"Not using a register"再点
                    Locator notUsingRegister = page.locator(":text('Not using a register')");
                    if (notUsingRegister.count() > 0) {
                        notUsingRegister.first().click();
                    } else {
                        System.out.println("弹窗出现但没找到 Not using a register");
                        page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("not_using_register_not_found.png")));
                    }
                }

                // 等待页面稳定
                page.waitForTimeout(3000);

            } catch (Exception e) {
                System.out.println("处理登录后步骤时出错: " + e.getMessage());
                // 截图调试
                page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("login_error.png")));
                throw e;
            }

            // 尝试导航到库存管理页面
            try {
                // 先尝试点击菜单项
                try {
                    page.waitForSelector("li.cursor-pointer:has-text('Stock Management')",
                        new Page.WaitForSelectorOptions().setTimeout(10000));
                    page.locator("li.cursor-pointer:has-text('Stock Management')").click();
                    page.waitForTimeout(2000);
                } catch (Exception e) {
                    System.out.println("未找到 'Stock Management' 菜单，尝试直接导航: " + e.getMessage());
                }

                // 尝试点击产品链接
                try {
                    page.waitForSelector("a[href='/products']",
                        new Page.WaitForSelectorOptions().setTimeout(10000));
                    page.locator("a[href='/products']").click();
                    page.waitForTimeout(3000);
                } catch (Exception e) {
                    System.out.println("未找到产品链接，尝试直接访问: " + e.getMessage());
                    // 直接导航到产品页面
                    try {
                        page.navigate("https://plazacellars.onshopfront.com/products");
                        try {
                            page.waitForLoadState(LoadState.DOMCONTENTLOADED, new Page.WaitForLoadStateOptions().setTimeout(15000));
                        } catch (Exception loadError) {
                            System.out.println("产品页面DOM加载超时，继续执行: " + loadError.getMessage());
                        }
                    } catch (Exception navError) {
                        System.out.println("直接导航失败: " + navError.getMessage());
                        // 尝试使用不同的等待策略
                        page.navigate("https://plazacellars.onshopfront.com/products",
                            new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED).setTimeout(30000));
                    }
                }

            } catch (Exception e) {
                System.out.println("导航到产品页面失败: " + e.getMessage());
                // 截图调试
                page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("navigation_error.png")));
                throw e;
            }

            // 等待至少有一个产品行出现
            try {
                page.locator(".index-page-body-row").first().waitFor(new Locator.WaitForOptions().setTimeout(20000));
            } catch (Exception e) {
                System.out.println("等待产品行出现失败: " + e.getMessage());
                // 截图调试
                page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("after_login.png")));
                throw e;
            }

            Set<String> seen = new HashSet<>();
            List<String> allProducts = new ArrayList<>();
            List<ProductInfo> productInfoList = new ArrayList<>(); // 用于批量插入的产品信息列表

            // 智能滚动，直到真正到达底部
            int consecutiveNoNewProducts = 0;
            int maxConsecutiveNoNewProducts = 5; // 连续5次没有新产品就认为到底了
            Set<String> allSeenProductIds = new HashSet<>();

            System.out.println("开始滚动并收集产品信息...");

            while (consecutiveNoNewProducts < maxConsecutiveNoNewProducts) {
                // 获取当前可见的产品行
                Locator currentRows = page.locator(".index-page-body-row");
                int currentRowCount = currentRows.count();
                int newProductsFound = 0;

                // 遍历当前可见的所有产品行
                for (int i = 0; i < currentRowCount; i++) {
                    try {
                        Locator row = currentRows.nth(i);

                        // 从href中提取产品ID
                        String href = row.locator(".product-name .product-column-name").getAttribute("href");
                        String productId = "";
                        if (href != null && href.contains("/product/")) {
                            productId = href.substring(href.lastIndexOf("/") + 1);
                        }

                        // 如果是新产品ID，立即收集详细信息
                        if (!productId.isEmpty() && allSeenProductIds.add(productId)) {
                            newProductsFound++;

                            // 立即收集产品详细信息
                            try {
                                // 产品名称
                                String name = row.locator(".product-name .product-column-name").textContent();
                                // 状态
                                String status = row.locator(".product-status p").textContent();
                                // 类别
                                String category = row.locator(".product-category p").textContent();
                                // 成本
                                List<String> costs = row.locator(".product-cost span").allTextContents();
                                String cost = costs.isEmpty() ? "" : costs.get(0);
                                // 库存
                                List<String> inventories = row.locator(".product-inventory .inventory-amount-number").allTextContents();
                                String inventory = inventories.isEmpty() ? "" : inventories.get(0);
                                // 售价
                                List<String> prices = row.locator(".product-price span").allTextContents();
                                String price = prices.isEmpty() ? "" : prices.get(0);

                                // 创建ProductInfo对象
                                ProductInfo productInfo = new ProductInfo();
                                productInfo.setShopfrontId(productId);
                                productInfo.setProductName(name != null ? name.trim() : "");
                                productInfo.setStatus(status != null ? status.trim() : "");
                                productInfo.setCategory(category != null ? category.trim() : "");

                                // 处理库存数量
                                if (inventory != null && !inventory.trim().isEmpty()) {
                                    try {
                                        // 移除可能的非数字字符
                                        String inventoryStr = inventory.replaceAll("[^0-9]", "");
                                        if (!inventoryStr.isEmpty()) {
                                            productInfo.setInventory(Long.parseLong(inventoryStr));
                                        } else {
                                            productInfo.setInventory(0L);
                                        }
                                    } catch (NumberFormatException e) {
                                        productInfo.setInventory(0L);
                                    }
                                } else {
                                    productInfo.setInventory(0L);
                                }

                                // 设置产品URL
                                productInfo.setProductUrl("https://plazacellars.onshopfront.com/product/" + productId);

                                // 添加到列表
                                productInfoList.add(productInfo);

                                allProducts.add(String.format("产品ID: %s, 产品: %s, 状态: %s, 类别: %s, 成本: %s, 库存: %s, 售价: %s",
                                    productId, name, status, category, cost, inventory, price));

                                // 每收集100个产品打印一次进度
                                if (allProducts.size() % 100 == 0) {
                                    System.out.println("已收集 " + allProducts.size() + " 个产品");
                                }
                            } catch (Exception detailError) {
                                // 如果详细信息获取失败，至少记录产品ID
                                allProducts.add(String.format("产品ID: %s, 详细信息获取失败: %s", productId, detailError.getMessage()));

                                // 创建基本信息的产品对象
                                ProductInfo productInfo = new ProductInfo();
                                productInfo.setShopfrontId(productId);
                                productInfo.setProductName("获取失败");
                                productInfo.setStatus("Unknown");
                                productInfo.setInventory(0L);
                                productInfoList.add(productInfo);
                            }
                        }
                    } catch (Exception e) {
                        // 忽略获取失败的产品行
                    }
                }

                if (newProductsFound > 0) {
                    System.out.println("发现 " + newProductsFound + " 个新产品，累计总产品数: " + allSeenProductIds.size());
                    consecutiveNoNewProducts = 0; // 重置计数器
                } else {
                    consecutiveNoNewProducts++;
                    System.out.println("连续 " + consecutiveNoNewProducts + " 次没有新产品");
                }

                // 滚动16个产品的高度，提高效率
                page.mouse().wheel(0, 120 * 16);
                page.waitForTimeout(200); // 增加等待时间，确保内容加载

                // 检查是否到达页面底部
                try {
                    Long scrollHeight = (Long) page.evaluate("document.documentElement.scrollHeight");
                    Long scrollTop = (Long) page.evaluate("window.pageYOffset");
                    Long clientHeight = (Long) page.evaluate("document.documentElement.clientHeight");

                    // 如果已经到达底部，多滚动几次确保加载完成
                    if (scrollTop + clientHeight >= scrollHeight - 100) {
                        System.out.println("已到达页面底部，继续滚动确保加载完成...");
                        page.waitForTimeout(500);

                        // 再滚动几次确保所有内容都加载
                        for (int j = 0; j < 3; j++) {
                            page.mouse().wheel(0, 120);
                            page.waitForTimeout(200);

                            // 每次滚动后都检查是否有新产品
                            Locator checkRows = page.locator(".index-page-body-row");
                            int checkCount = checkRows.count();
                            int additionalNewProducts = 0;

                            for (int k = 0; k < checkCount; k++) {
                                try {
                                    String href = checkRows.nth(k).locator(".product-name .product-column-name").getAttribute("href");
                                    if (href != null && href.contains("/product/")) {
                                        String productId = href.substring(href.lastIndexOf("/") + 1);
                                        if (allSeenProductIds.add(productId)) {
                                            additionalNewProducts++;

                                            // 立即收集详细信息
                                            Locator row = checkRows.nth(k);
                                            try {
                                                String name = row.locator(".product-name .product-column-name").textContent();
                                                String status = row.locator(".product-status p").textContent();
                                                String category = row.locator(".product-category p").textContent();
                                                List<String> costs = row.locator(".product-cost span").allTextContents();
                                                String cost = costs.isEmpty() ? "" : costs.get(0);
                                                List<String> inventories = row.locator(".product-inventory .inventory-amount-number").allTextContents();
                                                String inventory = inventories.isEmpty() ? "" : inventories.get(0);
                                                List<String> prices = row.locator(".product-price span").allTextContents();
                                                String price = prices.isEmpty() ? "" : prices.get(0);

                                                // 创建ProductInfo对象
                                                ProductInfo productInfo = new ProductInfo();
                                                productInfo.setShopfrontId(productId);
                                                productInfo.setProductName(name != null ? name.trim() : "");
                                                productInfo.setStatus(status != null ? status.trim() : "");
                                                productInfo.setCategory(category != null ? category.trim() : "");

                                                // 处理库存数量
                                                if (inventory != null && !inventory.trim().isEmpty()) {
                                                    try {
                                                        String inventoryStr = inventory.replaceAll("[^0-9]", "");
                                                        if (!inventoryStr.isEmpty()) {
                                                            productInfo.setInventory(Long.parseLong(inventoryStr));
                                                        } else {
                                                            productInfo.setInventory(0L);
                                                        }
                                                    } catch (NumberFormatException e) {
                                                        productInfo.setInventory(0L);
                                                    }
                                                } else {
                                                    productInfo.setInventory(0L);
                                                }

                                                // 设置产品URL
                                                productInfo.setProductUrl("https://plazacellars.onshopfront.com/product/" + productId);

                                                // 添加到列表
                                                productInfoList.add(productInfo);

                                                allProducts.add(String.format("产品ID: %s, 产品: %s, 状态: %s, 类别: %s, 成本: %s, 库存: %s, 售价: %s",
                                                    productId, name, status, category, cost, inventory, price));
                                            } catch (Exception detailError) {
                                                allProducts.add(String.format("产品ID: %s, 详细信息获取失败: %s", productId, detailError.getMessage()));

                                                // 创建基本信息的产品对象
                                                ProductInfo productInfo = new ProductInfo();
                                                productInfo.setShopfrontId(productId);
                                                productInfo.setProductName("获取失败");
                                                productInfo.setStatus("Unknown");
                                                productInfo.setInventory(0L);
                                                productInfoList.add(productInfo);
                                            }
                                        }
                                    }
                                } catch (Exception e) {
                                    // 忽略获取失败的产品
                                }
                            }

                            if (additionalNewProducts > 0) {
                                System.out.println("底部滚动发现 " + additionalNewProducts + " 个新产品");
                                consecutiveNoNewProducts = 0;
                            }
                        }

                        // 最后检查一次
                        if (consecutiveNoNewProducts >= maxConsecutiveNoNewProducts) {
                            System.out.println("确认已到达底部，总产品数: " + allSeenProductIds.size());
                            break;
                        }
                    }
                } catch (Exception e) {
                    // 忽略JavaScript执行错误
                }
            }

            System.out.println("收集完成，总产品数: " + allProducts.size());

            // 保存到数据库
            System.out.println("开始保存产品信息到数据库...");
            try {
                productInfoMapper.insertOrUpdateBatch(productInfoList);
            } catch (Exception e) {
                System.out.println("保存到数据库时出错: " + e.getMessage());
                e.printStackTrace();
            }

        }
    }
}
