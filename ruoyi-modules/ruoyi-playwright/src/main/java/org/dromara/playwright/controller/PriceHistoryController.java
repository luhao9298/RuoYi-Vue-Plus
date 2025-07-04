package org.dromara.playwright.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.*;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.web.core.BaseController;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.excel.utils.ExcelUtil;
import org.dromara.playwright.domain.vo.PriceHistoryVo;
import org.dromara.playwright.domain.bo.PriceHistoryBo;
import org.dromara.playwright.service.IPriceHistoryService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 价格历史
 *
 * @author Ethan Lu
 * @date 2025-07-04
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/history")
public class PriceHistoryController extends BaseController {

    private final IPriceHistoryService priceHistoryService;

    /**
     * 查询价格历史列表
     */
    @SaCheckPermission("system:history:list")
    @GetMapping("/list")
    public TableDataInfo<PriceHistoryVo> list(PriceHistoryBo bo, PageQuery pageQuery) {
        return priceHistoryService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出价格历史列表
     */
    @SaCheckPermission("system:history:export")
    @Log(title = "价格历史", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(PriceHistoryBo bo, HttpServletResponse response) {
        List<PriceHistoryVo> list = priceHistoryService.queryList(bo);
        ExcelUtil.exportExcel(list, "价格历史", PriceHistoryVo.class, response);
    }

    /**
     * 获取价格历史详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("system:history:query")
    @GetMapping("/{id}")
    public R<PriceHistoryVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(priceHistoryService.queryById(id));
    }

    /**
     * 新增价格历史
     */
    @SaCheckPermission("system:history:add")
    @Log(title = "价格历史", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody PriceHistoryBo bo) {
        return toAjax(priceHistoryService.insertByBo(bo));
    }

    /**
     * 修改价格历史
     */
    @SaCheckPermission("system:history:edit")
    @Log(title = "价格历史", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody PriceHistoryBo bo) {
        return toAjax(priceHistoryService.updateByBo(bo));
    }

    /**
     * 删除价格历史
     *
     * @param ids 主键串
     */
    @SaCheckPermission("system:history:remove")
    @Log(title = "价格历史", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(priceHistoryService.deleteWithValidByIds(List.of(ids), true));
    }
}
