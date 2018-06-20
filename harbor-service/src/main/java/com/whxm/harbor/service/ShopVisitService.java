package com.whxm.harbor.service;

import com.whxm.harbor.bean.*;

import java.util.List;

/**
 * 商铺访问服务
 */
public interface ShopVisitService {
    
    /**
     * 根据商铺编号获取商铺访问数据
     *
     * @param ShopNumber 商铺编号
     * @return 商铺访问数据
     */
    ShopVisit getShopVisit(String  ShopNumber);

    /**
     * 获取商铺访问列表
     *
     * @param pageQO
     * @return list
     */
    PageVO<ShopVisit> getShopVisitList(PageQO<BizShop> pageQO);

    /**
     * 获取全部商铺访问数据
     * @return 全部商铺访问数据
     */
    List<ShopVisit> getShopVisitList();
    
    /**
     * 更新商铺访问数据
     *
     * @param shopNumber 商铺访问数据新值
     * @return ret
     */
    Result updateShopVisit(String shopNumber);


}
