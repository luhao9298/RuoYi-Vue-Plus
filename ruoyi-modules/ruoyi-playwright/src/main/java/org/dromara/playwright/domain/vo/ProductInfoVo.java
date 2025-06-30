package org.dromara.playwright.domain.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.playwright.domain.ProductInfo;

import java.io.Serial;
import java.io.Serializable;


/**
 * 产品信息视图对象 product_info
 *
 * @author Lion Li
 * @date 2025-06-30
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = ProductInfo.class)
public class ProductInfoVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ExcelProperty(value = "主键")
    private Long id;

    /**
     * 产品shopfront对应id
     */
    @ExcelProperty(value = "产品shopfront对应id")
    private String shopfrontId;

    /**
     * 产品名称
     */
    @ExcelProperty(value = "产品名称")
    private String productName;

    /**
     * 状态
     */
    @ExcelProperty(value = "状态")
    private String status;

    /**
     * 类别
     */
    @ExcelProperty(value = "类别")
    private String category;

    /**
     * 库存
     */
    @ExcelProperty(value = "库存")
    private Long inventory;

    /**
     * 产品详情页URL
     */
    @ExcelProperty(value = "产品详情页URL")
    private String productUrl;


}
