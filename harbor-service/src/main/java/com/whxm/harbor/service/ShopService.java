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
     * 根据商铺编号获取商铺数据
     *
     * @param shopNumber 商铺编号
     * @return 商铺数据
     */
    BizShopVo getBizShopByNumber(String shopNumber);

    /**
     * 获取商铺列表
     *
     * @param pageQO
     * @param condition
     * @return list
     */
    PageVO<BizShopVo> getBizShopList(PageQO pageQO, BizShop condition);


    /**
     * 根据{业态ID/楼层ID/商铺名称首字母}获取店铺列表
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
     * @param shopVo 商铺数据新值
     * @return ret
     */
    Result updateBizShop(BizShopVo shopVo);

    /**
     * 新增商铺数据
     *
     * @param shopVo 商铺数据新值
     * @return ret
     */
    Result addBizShop(BizShopVo shopVo);

    /**
     * @param bizShopId 商铺ID
     * @return 商铺的图片路径集合
     */
    List<ShopPicture> getShopPicturesById(String bizShopId);

    /**
     * 删除商铺数据
     *
     * @param bizShopId 商铺ID
     * @return 商铺的图片路径集合
     */
    Result deleteBizShop(String bizShopId);

    /**
     * 批量添加商铺数据
     */
    Result addShopWithPoint(BizShopVo vo);

    List<BizShopVo> listAllShopInfo();
}
