package org.dromara.playwright.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.playwright.domain.Supplier;
import org.dromara.playwright.domain.bo.SupplierBo;
import org.dromara.playwright.domain.vo.SupplierVo;
import org.dromara.playwright.mapper.SupplierMapper;
import org.dromara.playwright.service.ISupplierService;
import org.dromara.playwright.service.SupplierLoginStrategy;
import org.dromara.playwright.service.SupplierProductCrawlerStrategy;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 供应商Service业务层处理
 *
 * @author Ethan Lu
 * @date 2025-06-30
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class SupplierServiceImpl implements ISupplierService {

    private final SupplierMapper baseMapper;
    private final SupplierStrategyRegistry strategyRegistry;

    /**
     * 查询供应商
     *
     * @param id 主键
     * @return 供应商
     */
    @Override
    public SupplierVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询供应商列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 供应商分页列表
     */
    @Override
    public TableDataInfo<SupplierVo> queryPageList(SupplierBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<Supplier> lqw = buildQueryWrapper(bo);
        Page<SupplierVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的供应商列表
     *
     * @param bo 查询条件
     * @return 供应商列表
     */
    @Override
    public List<SupplierVo> queryList(SupplierBo bo) {
        LambdaQueryWrapper<Supplier> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<Supplier> buildQueryWrapper(SupplierBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<Supplier> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(Supplier::getId);
        lqw.like(StringUtils.isNotBlank(bo.getName()), Supplier::getName, bo.getName());
        lqw.eq(StringUtils.isNotBlank(bo.getWebsite()), Supplier::getWebsite, bo.getWebsite());
        lqw.like(StringUtils.isNotBlank(bo.getUsername()), Supplier::getUsername, bo.getUsername());
        lqw.eq(StringUtils.isNotBlank(bo.getPassword()), Supplier::getPassword, bo.getPassword());
        lqw.eq(StringUtils.isNotBlank(bo.getType()), Supplier::getType, bo.getType());
        return lqw;
    }

    /**
     * 新增供应商
     *
     * @param bo 供应商
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(SupplierBo bo) {
        Supplier add = MapstructUtils.convert(bo, Supplier.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改供应商
     *
     * @param bo 供应商
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(SupplierBo bo) {
        Supplier update = MapstructUtils.convert(bo, Supplier.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(Supplier entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除供应商信息
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

    @Override
    public void crawlSupplierProducts(Long supplierId) {
        Supplier supplier = baseMapper.selectById(supplierId);
        if (supplier == null) {
            throw new IllegalArgumentException("供应商不存在: " + supplierId);
        }
        SupplierLoginStrategy loginStrategy = strategyRegistry.getLoginStrategy(supplier.getName());
        SupplierProductCrawlerStrategy crawlerStrategy = strategyRegistry.getCrawlerStrategy(supplier.getName());
        var session = loginStrategy.login(supplier);
        crawlerStrategy.crawlAndSaveProducts(supplier, session);
    }
}
