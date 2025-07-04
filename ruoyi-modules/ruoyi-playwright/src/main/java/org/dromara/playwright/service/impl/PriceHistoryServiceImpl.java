package org.dromara.playwright.service.impl;

import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.dromara.playwright.domain.bo.PriceHistoryBo;
import org.dromara.playwright.domain.vo.PriceHistoryVo;
import org.dromara.playwright.domain.PriceHistory;
import org.dromara.playwright.mapper.PriceHistoryMapper;
import org.dromara.playwright.service.IPriceHistoryService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 价格历史Service业务层处理
 *
 * @author Ethan Lu
 * @date 2025-07-04
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class PriceHistoryServiceImpl implements IPriceHistoryService {

    private final PriceHistoryMapper baseMapper;

    /**
     * 查询价格历史
     *
     * @param id 主键
     * @return 价格历史
     */
    @Override
    public PriceHistoryVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询价格历史列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 价格历史分页列表
     */
    @Override
    public TableDataInfo<PriceHistoryVo> queryPageList(PriceHistoryBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<PriceHistory> lqw = buildQueryWrapper(bo);
        Page<PriceHistoryVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的价格历史列表
     *
     * @param bo 查询条件
     * @return 价格历史列表
     */
    @Override
    public List<PriceHistoryVo> queryList(PriceHistoryBo bo) {
        LambdaQueryWrapper<PriceHistory> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<PriceHistory> buildQueryWrapper(PriceHistoryBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<PriceHistory> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(PriceHistory::getId);
        lqw.eq(bo.getProductId() != null, PriceHistory::getProductId, bo.getProductId());
        lqw.like(StringUtils.isNotBlank(bo.getProductName()), PriceHistory::getProductName, bo.getProductName());
        lqw.like(StringUtils.isNotBlank(bo.getSupplierName()), PriceHistory::getSupplierName, bo.getSupplierName());
        lqw.eq(bo.getCostPrice() != null, PriceHistory::getCostPrice, bo.getCostPrice());
        lqw.eq(bo.getRetailPrice() != null, PriceHistory::getRetailPrice, bo.getRetailPrice());
        lqw.eq(StringUtils.isNotBlank(bo.getCurrency()), PriceHistory::getCurrency, bo.getCurrency());
        return lqw;
    }

    /**
     * 新增价格历史
     *
     * @param bo 价格历史
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(PriceHistoryBo bo) {
        PriceHistory add = MapstructUtils.convert(bo, PriceHistory.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改价格历史
     *
     * @param bo 价格历史
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(PriceHistoryBo bo) {
        PriceHistory update = MapstructUtils.convert(bo, PriceHistory.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(PriceHistory entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除价格历史信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteByIds(ids) > 0;
    }
}
