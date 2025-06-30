package org.dromara.playwright.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.playwright.domain.Supplier;

/**
 * 供应商业务对象 supplier
 *
 * @author Ethan Lu
 * @date 2025-06-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = Supplier.class, reverseConvertGenerate = false)
public class SupplierBo extends BaseEntity {

    /**
     * 主键
     */
    @NotNull(message = "主键不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 供应商名称
     */
    @NotBlank(message = "供应商名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String name;

    /**
     * 供应商网站
     */
    @NotBlank(message = "供应商网站不能为空", groups = { AddGroup.class, EditGroup.class })
    private String website;

    /**
     * 登录用户名
     */
    @NotBlank(message = "登录用户名不能为空", groups = { AddGroup.class, EditGroup.class })
    private String username;

    /**
     * 登录密码
     */
    @NotBlank(message = "登录密码不能为空", groups = { AddGroup.class, EditGroup.class })
    private String password;

    /**
     * 供应商类型/站点标识
     */
    private String type;


}
