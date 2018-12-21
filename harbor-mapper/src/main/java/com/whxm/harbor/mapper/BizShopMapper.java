package com.whxm.harbor.mapper;

import com.whxm.harbor.bean.BizShop;
import com.whxm.harbor.bean.ShopPicture;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

public interface BizShopMapper {
    int deleteByPrimaryKey(String shopId);

    int insert(BizShop record);

    int insertShopPictures(
            @Param("shopId") String shopId,
            @Param("pictures") List<ShopPicture> pictures
    );

    int insertSelective(BizShop record);

    BizShop selectByPrimaryKey(String shopId);

    int updateByPrimaryKeySelective(BizShop record);

    int updateByPrimaryKey(BizShop record);

    List<BizShop> getBizShopList(BizShop condition);

    List<ShopPicture> selectShopPicturesById(String bizShopId);

    List<BizShop> getBizShopListOptional(Map<String, Object> params);

    BizShop selectIdByNumber(String shopNumber);

    @Delete("delete from shop_picture_relation where shop_id=#{shopId}")
    int deleteShopPictures(@Param("shopId") String shopId);

    //int delShopPicturesRelation(String shopId);

    Integer couldUpdateUniqueNumber(BizShop bizShop);

    @Insert("INSERT INTO biz_shop_visit(shop_number,shop_visit_amount,shop_visit_time) VALUES(#{shopNumber},0,CURRENT_TIMESTAMP)")
    int insertShopVisit(String shopNumber);

    @Delete("DELETE FROM biz_shop_visit WHERE shop_number=#{shopNumber}")
    int deleteShopVisit(String shopNumber);
}