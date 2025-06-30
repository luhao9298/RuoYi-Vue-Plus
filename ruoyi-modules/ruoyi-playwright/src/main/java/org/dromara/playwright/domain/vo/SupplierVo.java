package org.dromara.playwright.domain.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.playwright.domain.Supplier;

import java.io.Serial;
import java.io.Serializable;


/**
 * 供应商视图对象 supplier
 *
 * @author Ethan Lu
 * @date 2025-06-30
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = Supplier.class)
public class SupplierVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ExcelProperty(value = "主键")
    private Long id;

    /**
     * 供应商名称
     */
    @ExcelProperty(value = "供应商名称")
    private String name;

    /**
     * 供应商网站
     */
    @ExcelProperty(value = "供应商网站")
    private String website;

    /**
     * 登录用户名
     */
    @ExcelProperty(value = "登录用户名")
    private String username;

    /**
     * 登录密码
     */
    @ExcelProperty(value = "登录密码")
    private String password;

    /**
     * 供应商类型/站点标识
     */
    @ExcelProperty(value = "供应商类型/站点标识")
    private String type;


}
