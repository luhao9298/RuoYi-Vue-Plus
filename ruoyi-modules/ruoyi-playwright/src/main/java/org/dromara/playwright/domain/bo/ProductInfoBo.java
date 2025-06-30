package org.dromara.playwright.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.playwright.domain.ProductInfo;

/**
 * 产品信息业务对象 product_info
 *
 * @author Lion Li
 * @date 2025-06-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = ProductInfo.class, reverseConvertGenerate = false)
public class ProductInfoBo extends BaseEntity {

    /**
     * 主键
     */
    @NotNull(message = "主键不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 产品shopfront对应id
     */
    private String shopfrontId;

    /**
     * 产品名称
     */
    @NotBlank(message = "产品名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String productName;

    /**
     * 状态
     */
    private String status;

    /**
     * 类别
     */
    private String category;

    /**
     * 库存
     */
    private Long inventory;

    /**
     * 产品详情页URL
     */
    private String productUrl;


}
