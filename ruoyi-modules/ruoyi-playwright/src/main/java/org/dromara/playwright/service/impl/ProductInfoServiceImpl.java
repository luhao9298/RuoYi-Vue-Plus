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
import org.dromara.playwright.domain.ProductInfo;
import org.dromara.playwright.domain.bo.ProductInfoBo;
import org.dromara.playwright.domain.vo.ProductInfoVo;
import org.dromara.playwright.mapper.ProductInfoMapper;
import org.dromara.playwright.service.IProductInfoService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 产品信息Service业务层处理
 *
 * @author Lion Li
 * @date 2025-06-30
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class ProductInfoServiceImpl implements IProductInfoService {

    private final ProductInfoMapper baseMapper;

    /**
     * 查询产品信息
     *
     * @param id 主键
     * @return 产品信息
     */
    @Override
    public ProductInfoVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询产品信息列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 产品信息分页列表
     */
    @Override
    public TableDataInfo<ProductInfoVo> queryPageList(ProductInfoBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ProductInfo> lqw = buildQueryWrapper(bo);
        Page<ProductInfoVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的产品信息列表
     *
     * @param bo 查询条件
     * @return 产品信息列表
     */
    @Override
    public List<ProductInfoVo> queryList(ProductInfoBo bo) {
        LambdaQueryWrapper<ProductInfo> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<ProductInfo> buildQueryWrapper(ProductInfoBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<ProductInfo> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(ProductInfo::getId);
        lqw.eq(StringUtils.isNotBlank(bo.getShopfrontId()), ProductInfo::getShopfrontId, bo.getShopfrontId());
        lqw.like(StringUtils.isNotBlank(bo.getProductName()), ProductInfo::getProductName, bo.getProductName());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), ProductInfo::getStatus, bo.getStatus());
        lqw.eq(StringUtils.isNotBlank(bo.getCategory()), ProductInfo::getCategory, bo.getCategory());
        lqw.eq(bo.getInventory() != null, ProductInfo::getInventory, bo.getInventory());
        lqw.eq(StringUtils.isNotBlank(bo.getProductUrl()), ProductInfo::getProductUrl, bo.getProductUrl());
        return lqw;
    }

    /**
     * 新增产品信息
     *
     * @param bo 产品信息
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(ProductInfoBo bo) {
        ProductInfo add = MapstructUtils.convert(bo, ProductInfo.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改产品信息
     *
     * @param bo 产品信息
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(ProductInfoBo bo) {
        ProductInfo update = MapstructUtils.convert(bo, ProductInfo.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(ProductInfo entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除产品信息信息
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
