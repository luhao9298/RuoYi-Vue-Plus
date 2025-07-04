package org.dromara.playwright.service.impl;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.dromara.playwright.domain.Supplier;
import org.dromara.playwright.service.SupplierLoginStrategy;
import org.springframework.stereotype.Component;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import com.microsoft.playwright.options.WaitUntilState;


@Component
@RequiredArgsConstructor
public class OnshopfrontLoginStrategy implements SupplierLoginStrategy {

    private final PlaywrightContextHelper playwrightContextHelper;

    @Override
    public boolean supports(String supplierName) {
        return StrUtil.equalsIgnoreCase("onshopfront", supplierName);
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
            page.waitForSelector("#shopfront-field-u", new Page.WaitForSelectorOptions().setTimeout(20000));
            page.fill("#shopfront-field-u", supplier.getUsername());
            page.fill("#shopfront-field-p", supplier.getPassword());
            page.click("#login-actions button");
            page.waitForTimeout(3000);
            if (page.locator(".z-1.fixed.inset-0").count() > 0) {
                Locator notUsingRegister = page.locator(":text('Not using a register')");
                if (notUsingRegister.count() > 0) {
                    notUsingRegister.first().click();
                }
            }
            page.waitForTimeout(3000);
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
