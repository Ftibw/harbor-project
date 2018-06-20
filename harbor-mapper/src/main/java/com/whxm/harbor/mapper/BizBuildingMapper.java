package com.whxm.harbor.mapper;

import com.whxm.harbor.bean.BizBuilding;

public interface BizBuildingMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(BizBuilding record);

    int insertSelective(BizBuilding record);

    BizBuilding selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BizBuilding record);

    int updateByPrimaryKey(BizBuilding record);
}