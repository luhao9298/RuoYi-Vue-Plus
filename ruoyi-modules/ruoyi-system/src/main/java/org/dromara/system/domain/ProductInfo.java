package org.dromara.system.domain;

import org.dromara.common.tenant.core.TenantEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 产品信息对象 product_info
 *
 * @author Lion Li
 * @date 2025-06-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("product_info")
public class ProductInfo extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 产品shopfront对应id
     */
    private String shopfrontId;

    /**
     * 产品名称
     */
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

    /**
     * 版本
     */
    @Version
    private Long version;

    /**
     * 删除标志
     */
    @TableLogic
    private Long delFlag;


}
