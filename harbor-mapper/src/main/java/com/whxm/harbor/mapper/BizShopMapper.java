package com.whxm.harbor.mapper;

import com.whxm.harbor.utils.BizShop;
import com.whxm.harbor.bean.ShopPicture;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface BizShopMapper {
    int deleteByPrimaryKey(String shopId);

    int insert(BizShop record);

    int insertShopPictures(
            @Param("shopId") String shopId,
            @Param("pictures") List<Map<String, Object>> pictures
    );

    int insertSelective(BizShop record);

    BizShop selectByPrimaryKey(String shopId);

    int updateByPrimaryKeySelective(BizShop record);

    int updateByPrimaryKey(BizShop record);

    List<BizShop> getBizShopList(BizShop condition);

    List<ShopPicture> selectShopPicturesById(String bizShopId);

//    List<String> selectShopIdListByBizType(String bizFormatType);

    List<BizShop> getBizShopListOptional(Map<String, Object> params);

    BizShop selectIdByNumber(String shopNumber);

    int batchSelectiveInsert(List<BizShop> shops);

    //int delShopPicturesRelation(String shopId);
}