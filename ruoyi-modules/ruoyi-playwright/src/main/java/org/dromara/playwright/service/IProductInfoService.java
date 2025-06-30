package org.dromara.playwright.service;

import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.playwright.domain.bo.ProductInfoBo;
import org.dromara.playwright.domain.vo.ProductInfoVo;

import java.util.Collection;
import java.util.List;

/**
 * 产品信息Service接口
 *
 * @author Lion Li
 * @date 2025-06-30
 */
public interface IProductInfoService {

    /**
     * 查询产品信息
     *
     * @param id 主键
     * @return 产品信息
     */
    ProductInfoVo queryById(Long id);

    /**
     * 分页查询产品信息列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 产品信息分页列表
     */
    TableDataInfo<ProductInfoVo> queryPageList(ProductInfoBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的产品信息列表
     *
     * @param bo 查询条件
     * @return 产品信息列表
     */
    List<ProductInfoVo> queryList(ProductInfoBo bo);

    /**
     * 新增产品信息
     *
     * @param bo 产品信息
     * @return 是否新增成功
     */
    Boolean insertByBo(ProductInfoBo bo);

    /**
     * 修改产品信息
     *
     * @param bo 产品信息
     * @return 是否修改成功
     */
    Boolean updateByBo(ProductInfoBo bo);

    /**
     * 校验并批量删除产品信息信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
