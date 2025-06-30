package org.dromara.playwright.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.excel.utils.ExcelUtil;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.web.core.BaseController;
import org.dromara.playwright.domain.bo.ProductInfoBo;
import org.dromara.playwright.domain.vo.ProductInfoVo;
import org.dromara.playwright.service.IProductInfoService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 产品信息
 *
 * @author Ethan Lu
 * @date 2025-06-30
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/info")
public class ProductInfoController extends BaseController {

    private final IProductInfoService productInfoService;

    /**
     * 查询产品信息列表
     */
    @SaCheckPermission("system:info:list")
    @GetMapping("/list")
    public TableDataInfo<ProductInfoVo> list(ProductInfoBo bo, PageQuery pageQuery) {
        return productInfoService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出产品信息列表
     */
    @SaCheckPermission("system:info:export")
    @Log(title = "产品信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(ProductInfoBo bo, HttpServletResponse response) {
        List<ProductInfoVo> list = productInfoService.queryList(bo);
        ExcelUtil.exportExcel(list, "产品信息", ProductInfoVo.class, response);
    }

    /**
     * 获取产品信息详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("system:info:query")
    @GetMapping("/{id}")
    public R<ProductInfoVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(productInfoService.queryById(id));
    }

    /**
     * 新增产品信息
     */
    @SaCheckPermission("system:info:add")
    @Log(title = "产品信息", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody ProductInfoBo bo) {
        return toAjax(productInfoService.insertByBo(bo));
    }

    /**
     * 修改产品信息
     */
    @SaCheckPermission("system:info:edit")
    @Log(title = "产品信息", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody ProductInfoBo bo) {
        return toAjax(productInfoService.updateByBo(bo));
    }

    /**
     * 删除产品信息
     *
     * @param ids 主键串
     */
    @SaCheckPermission("system:info:remove")
    @Log(title = "产品信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(productInfoService.deleteWithValidByIds(List.of(ids), true));
    }
}
