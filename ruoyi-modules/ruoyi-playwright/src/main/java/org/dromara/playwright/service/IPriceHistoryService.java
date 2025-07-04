package org.dromara.playwright.service;

import org.dromara.playwright.domain.vo.PriceHistoryVo;
import org.dromara.playwright.domain.bo.PriceHistoryBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 价格历史Service接口
 *
 * @author Ethan Lu
 * @date 2025-07-04
 */
public interface IPriceHistoryService {

    /**
     * 查询价格历史
     *
     * @param id 主键
     * @return 价格历史
     */
    PriceHistoryVo queryById(Long id);

    /**
     * 分页查询价格历史列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 价格历史分页列表
     */
    TableDataInfo<PriceHistoryVo> queryPageList(PriceHistoryBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的价格历史列表
     *
     * @param bo 查询条件
     * @return 价格历史列表
     */
    List<PriceHistoryVo> queryList(PriceHistoryBo bo);

    /**
     * 新增价格历史
     *
     * @param bo 价格历史
     * @return 是否新增成功
     */
    Boolean insertByBo(PriceHistoryBo bo);

    /**
     * 修改价格历史
     *
     * @param bo 价格历史
     * @return 是否修改成功
     */
    Boolean updateByBo(PriceHistoryBo bo);

    /**
     * 校验并批量删除价格历史信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
