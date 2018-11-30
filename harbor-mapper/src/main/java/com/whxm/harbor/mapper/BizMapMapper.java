package com.whxm.harbor.mapper;

import com.whxm.harbor.bean.BizMap;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface BizMapMapper {
    int deleteByPrimaryKey(Integer mapId);

    int insert(BizMap record);

    int updateByPrimaryKeySelective(BizMap record);

    List<BizMap> getBizMapList(BizMap condition);

    BizMap selectMapByFloorId(Integer floorId);
}