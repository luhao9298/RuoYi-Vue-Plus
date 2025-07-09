package org.dromara.playwright.service.impl;

import cn.hutool.core.util.StrUtil;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitUntilState;
import lombok.RequiredArgsConstructor;
import org.dromara.playwright.domain.Supplier;
import org.dromara.playwright.service.SupplierLoginStrategy;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AlmliquorLoginStrategy implements SupplierLoginStrategy {

    private final PlaywrightContextHelper playwrightContextHelper;

    @Override
    public boolean supports(String supplierName) {
        return StrUtil.equalsIgnoreCase("almliquor", supplierName);
    }

    @Override
    public PlaywrightContextHelper.BrowserContextHolder loginAndGetContext(Supplier supplier) {
        if (supplier == null || StrUtil.isBlank(supplier.getUsername()) || StrUtil.isBlank(supplier.getPassword())) {
            throw new IllegalArgumentException("供应商信息或账号密码不能为空");
        }
        PlaywrightContextHelper.BrowserContextHolder holder = playwrightContextHelper.createContext();
        Page page = holder.context.newPage();
        try {
            page.navigate(supplier.getWebsite(), new Page.NavigateOptions().setTimeout(60000).setWaitUntil(WaitUntilState.DOMCONTENTLOADED));
            page.locator("li.member-login > a").hover();
            page.waitForSelector("li.member-login .dropdown-menu", new Page.WaitForSelectorOptions().setTimeout(20000));
            page.locator("li.member-login .dropdown-menu a:has-text('Retailer')").click();
            page.waitForSelector("#input-3", new Page.WaitForSelectorOptions().setTimeout(30000));
            page.fill("#input-3", supplier.getUsername());
            page.fill("#input-5", supplier.getPassword());
            page.click("button.logInButton");
            page.waitForTimeout(3000);

            // 监听并获取新开的窗口（弹窗）
            Page popup = page.waitForPopup(() -> {
                page.locator("span:has-text('Click here')").click();
            });

            // 等待 popup 跳转到首页
            popup.waitForURL("https://www.almliquor.com.au/", new Page.WaitForURLOptions().setTimeout(60000));

            // 登录成功后直接返回 holder
            return holder;
        } catch (Exception e) {
            holder.close();
            String msg = e.getMessage();
            if (msg != null && msg.contains("Timeout")) {
                throw new RuntimeException("登录超时，请检查网络或目标网站是否可访问。", e);
            } else {
                throw new RuntimeException("登录失败，原因：" + msg, e);
            }
        }
    }

}
