package org.dromara.playwright.domain.vo;


import java.math.BigDecimal;
import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.playwright.domain.PriceHistory;

import java.io.Serial;
import java.io.Serializable;



/**
 * 价格历史视图对象 price_history
 *
 * @author Ethan Lu
 * @date 2025-07-04
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = PriceHistory.class)
public class PriceHistoryVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ExcelProperty(value = "主键")
    private Long id;

    /**
     * 产品id
     */
    @ExcelProperty(value = "产品id")
    private Long productId;

    /**
     * 产品名称
     */
    @ExcelProperty(value = "产品名称")
    private String productName;

    /**
     * 供应商名称
     */
    @ExcelProperty(value = "供应商名称")
    private String supplierName;

    /**
     * 成本价
     */
    @ExcelProperty(value = "成本价")
    private BigDecimal costPrice;

    /**
     * 销售价
     */
    @ExcelProperty(value = "销售价")
    private BigDecimal retailPrice;

    /**
     * ISO货币代码
     */
    @ExcelProperty(value = "ISO货币代码")
    private String currency;


}

