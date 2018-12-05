package com.whxm.harbor.mapper;

import com.whxm.harbor.bean.MapEdge;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MapEdgeMapper {
    int deleteByPartKey(MapEdge key);

    int deleteByPrimaryKey(Integer id);

    int insert(MapEdge record);

    int insertSelective(MapEdge record);

    MapEdge selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(MapEdge record);

    int updateByPrimaryKey(MapEdge record);

    List<MapEdge> selectAll(@Param("floorId") Integer floorId);

    int batchReplace(List<MapEdge> list);

    int batchDelete(List<Integer> list);

    /**
     * 用于批量删除建筑时,批量删除建筑有关的边
     *
     * @param list 建筑的ID列表
     * @return 被删除边的行数
     */
    int batchDeleteByTailListOrHeadList(List<Integer> list);
}