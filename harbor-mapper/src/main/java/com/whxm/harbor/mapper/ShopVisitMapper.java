package com.whxm.harbor.mapper;

import com.whxm.harbor.bean.ShopVisit;

public interface ShopVisitMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ShopVisit record);

    int insertSelective(ShopVisit record);

    ShopVisit selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ShopVisit record);

    int updateByPrimaryKey(ShopVisit record);
}