package com.whxm.harbor.mapper;

import com.whxm.harbor.bean.BizShop;
import com.whxm.harbor.bean.ShopVisit;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Date;
import java.util.List;

public interface ShopVisitMapper {
    int deleteByPrimaryKey(String ShopNumber);

    int insert(ShopVisit record);

    int insertSelective(ShopVisit record);

    ShopVisit selectByPrimaryKey(String ShopNumber);

    int updateByPrimaryKeySelective(ShopVisit record);

    int updateByPrimaryKey(ShopVisit record);

    List<ShopVisit> getShopVisitList(BizShop condition);

    int updateAmountByID(@Param("shopNumber") String shopNumber);
}