package com.whxm.harbor.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.whxm.harbor.annotation.VisitLogger;
import com.whxm.harbor.bean.*;
import com.whxm.harbor.constant.Constant;
import com.whxm.harbor.mapper.ShopVisitMapper;
import com.whxm.harbor.service.ShopVisitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
@Transactional
public class ShopVisitServiceImpl implements ShopVisitService {

    private final Logger logger = LoggerFactory.getLogger(ShopVisitServiceImpl.class);

    @Resource
    private ShopVisitMapper shopVisitMapper;

    @Override
    public ShopVisit getShopVisit(String ShopNumber) {

        ShopVisit shopVisit;

        try {
            shopVisit = shopVisitMapper.selectByPrimaryKey(ShopNumber);

            if (null == shopVisit) {
                logger.info("编号为{}的商铺访问数据不存在", ShopNumber);
            }
        } catch (Exception e) {

            logger.error("商铺访问数据 获取报错", e);

            throw new RuntimeException();
        }

        return shopVisit;
    }

    //如果是POST+JSON就会被防重复提交了...
    @Override
    public ResultMap<String, Object> updateShopVisit(String shopNumber) {

        ResultMap<String, Object> ret;

        try {

            int affectRow = shopVisitMapper.updateAmountByID(shopNumber);

            if (this.logger.isDebugEnabled()) {

                this.logger.debug(1 == affectRow ?
                        "编号为{}的商铺访问数据更新成功" : "编号为{}的商铺访问数据更新失败", shopNumber);
            }

            ret = new ResultMap<String, Object>(1).build("success", 1 == affectRow);

        } catch (Exception e) {

            logger.error("编号为{}的商铺访问更新 报错", shopNumber, e);

            throw new RuntimeException();
        }

        return ret;
    }

    @Override
    public PageVO<ShopVisit> getShopVisitList(PageQO<BizShop> pageQO) {

        PageVO<ShopVisit> pageVO;

        try {
            Page page = PageHelper.startPage(pageQO.getPageNum(), pageQO.getPageSize());

            pageVO = new PageVO<>(pageQO);

            pageVO.setList(shopVisitMapper.getShopVisitList(pageQO.getCondition()));

            pageVO.setTotal(page.getTotal());

        } catch (Exception e) {

            logger.error("商铺访问列表获取报错", e);

            throw new RuntimeException();
        }

        return pageVO;
    }

    @Override
    public List<ShopVisit> getShopVisitList() {

        List<ShopVisit> list;

        try {
            list = shopVisitMapper.getShopVisitList((BizShop) Constant.DEFAULT_QUERY_CONDITION);

        } catch (Exception e) {

            logger.error("商铺访问数据列表 获取报错", e);

            throw new RuntimeException();
        }

        return list;
    }

}
