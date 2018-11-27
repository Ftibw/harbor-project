package com.whxm.harbor.mapper;

import com.whxm.harbor.bean.MapEdge;
import com.whxm.harbor.bean.MapEdgeKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MapEdgeMapper {
    int deleteByPrimaryKey(MapEdgeKey key);

    int insert(MapEdge record);

    int insertSelective(MapEdge record);

    MapEdge selectByPrimaryKey(MapEdgeKey key);

    int updateByPrimaryKeySelective(MapEdge record);

    int updateByPrimaryKey(MapEdge record);

    int deleteByPartKey(MapEdgeKey key);

    List<MapEdge> selectAll(@Param("floorId") Integer floorId);

    int batchReplace(List<MapEdge> list);
}