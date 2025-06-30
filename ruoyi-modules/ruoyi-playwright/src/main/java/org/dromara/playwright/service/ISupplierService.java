package org.dromara.playwright.service;

import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.playwright.domain.bo.SupplierBo;
import org.dromara.playwright.domain.vo.SupplierVo;

import java.util.Collection;
import java.util.List;

/**
 * 供应商Service接口
 *
 * @author Ethan Lu
 * @date 2025-06-30
 */
public interface ISupplierService {

    /**
     * 查询供应商
     *
     * @param id 主键
     * @return 供应商
     */
    SupplierVo queryById(Long id);

    /**
     * 分页查询供应商列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 供应商分页列表
     */
    TableDataInfo<SupplierVo> queryPageList(SupplierBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的供应商列表
     *
     * @param bo 查询条件
     * @return 供应商列表
     */
    List<SupplierVo> queryList(SupplierBo bo);

    /**
     * 新增供应商
     *
     * @param bo 供应商
     * @return 是否新增成功
     */
    Boolean insertByBo(SupplierBo bo);

    /**
     * 修改供应商
     *
     * @param bo 供应商
     * @return 是否修改成功
     */
    Boolean updateByBo(SupplierBo bo);

    /**
     * 校验并批量删除供应商信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    /**
     * 登录并抓取供应商商品信息（策略模式入口）
     */
    void crawlSupplierProducts(Long supplierId);
}
