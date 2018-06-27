package com.whxm.harbor.service;

import com.whxm.harbor.bean.*;
import com.whxm.harbor.vo.BizShopVo;

import java.util.List;
import java.util.Map;

/**
 * 商铺服务
 */
public interface ShopService {
    /**
     * 根据商铺ID获取商铺数据
     *
     * @param bizShopId 商铺ID
     * @return 商铺数据
     */
    BizShopVo getBizShop(String bizShopId);

    /**
     * 获取商铺列表
     *
     * @param pageQO
     * @param condition
     * @return list
     */
    PageVO<BizShopVo> getBizShopList(PageQO pageQO, BizShop condition);


    /**
     *根据{业态ID/楼层ID/商铺名称首字母}获取店铺列表
     */
    List<BizShopVo> getBizShopListOptional(Map<String, Object> params);

    /**
     * 根据ID停用/启用商铺
     *
     * @param bizShopId 商铺ID
     * @return ret
     */
    Result triggerBizShop(String bizShopId);

    /**
     * 修改商铺数据
     *
     * @param bizShop 商铺数据新值
     * @param pictureList
     * @return ret
     */
    Result updateBizShop(BizShop bizShop, List<Map<String, Object>> pictureList);

    /**
     * 新增商铺数据
     *
     * @param bizShop 商铺数据新值
     * @return ret
     */
    Result addBizShop(BizShop bizShop, List<Map<String, Object>> pictureList);

    /**
     * @param bizShopId 商铺ID
     * @return 商铺的图片路径集合
     */
    List<ShopPicture> getShopPicturesById(String bizShopId);
}
