package org.dromara.playwright.domain;

import org.dromara.common.tenant.core.TenantEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;

import java.io.Serial;

/**
 * 价格历史对象 price_history
 *
 * @author Ethan Lu
 * @date 2025-07-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("price_history")
public class PriceHistory extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 产品id
     */
    private Long productId;

    /**
     * 产品名称
     */
    private String productName;

    /**
     * 供应商名称
     */
    private String supplierName;

    /**
     * 成本价
     */
    private BigDecimal costPrice;

    /**
     * 销售价
     */
    private BigDecimal retailPrice;

    /**
     * ISO货币代码
     */
    private String currency;

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

