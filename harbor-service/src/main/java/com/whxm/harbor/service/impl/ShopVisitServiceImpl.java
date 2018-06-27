package com.whxm.harbor.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.whxm.harbor.bean.*;
import com.whxm.harbor.exception.DataNotFoundException;
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

        ShopVisit shopVisit = shopVisitMapper.selectByPrimaryKey(ShopNumber);

        if (null == shopVisit) {
            throw new DataNotFoundException();
        }

        return shopVisit;
    }

    //如果是POST+JSON就会被防重复提交了...
    @Override
    public ResultMap<String, Object> updateShopVisit(String shopNumber) {

        int affectRow = shopVisitMapper.updateAmountByID(shopNumber);

        if (this.logger.isDebugEnabled()) {

            this.logger.debug(1 == affectRow ?
                    "编号为{}的商铺访问数据更新成功" : "编号为{}的商铺访问数据更新失败", shopNumber);
        }

        return new ResultMap<String, Object>(1).build("success", 1 == affectRow);
    }

    @Override
    public PageVO<ShopVisit> getShopVisitList(PageQO pageQO, BizShop condition) {

        PageVO<ShopVisit> pageVO = new PageVO<>(pageQO);

        Page page = PageHelper.startPage(pageQO.getPageNum(), pageQO.getPageSize());

        List<ShopVisit> list = shopVisitMapper.getShopVisitList(condition);

        /*if (null == list || list.isEmpty())
            throw new DataNotFoundException();*/

        pageVO.setList(list);

        pageVO.setTotal(page.getTotal());

        return pageVO;
    }

    @Override
    public List<ShopVisit> getShopVisitList() {

        return shopVisitMapper.getShopVisitList(null);
    }

}
