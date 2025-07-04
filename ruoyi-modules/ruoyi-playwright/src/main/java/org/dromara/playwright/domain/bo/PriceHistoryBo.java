package org.dromara.playwright.domain.bo;

import org.dromara.playwright.domain.PriceHistory;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * 价格历史业务对象 price_history
 *
 * @author Ethan Lu
 * @date 2025-07-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = PriceHistory.class, reverseConvertGenerate = false)
public class PriceHistoryBo extends BaseEntity {

    /**
     * 主键
     */
    @NotNull(message = "主键不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 产品id
     */
    @NotNull(message = "产品id不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long productId;

    /**
     * 产品名称
     */
    @NotBlank(message = "产品名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String productName;

    /**
     * 供应商名称
     */
    @NotBlank(message = "供应商名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String supplierName;

    /**
     * 成本价
     */
    @NotNull(message = "成本价不能为空", groups = { AddGroup.class, EditGroup.class })
    private BigDecimal costPrice;

    /**
     * 销售价
     */
    @NotNull(message = "销售价不能为空", groups = { AddGroup.class, EditGroup.class })
    private BigDecimal retailPrice;

    /**
     * ISO货币代码
     */
    private String currency;


}
