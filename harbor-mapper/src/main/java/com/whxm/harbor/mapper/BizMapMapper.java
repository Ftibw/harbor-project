package com.whxm.harbor.mapper;

import com.whxm.harbor.bean.BizMap;

import java.util.List;

public interface BizMapMapper {
    int deleteByPrimaryKey(Integer mapId);

    int insert(BizMap record);

    int insertSelective(BizMap record);

    BizMap selectByPrimaryKey(Integer mapId);

    int updateByPrimaryKeySelective(BizMap record);

    int updateByPrimaryKey(BizMap record);

    List<BizMap> getBizMapList(BizMap condition);
}