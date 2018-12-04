package com.whxm.harbor.service;

import com.whxm.harbor.bean.*;

import java.util.List;

/**
 * 地图服务
 */
public interface MapService {
    /**
     * 根据楼层ID获取地图数据
     *
     * @param floorId 楼层ID
     * @return 地图数据
     */
    BizMap getBizMap(Integer floorId);

    /**
     * 获取地图列表
     *
     * @param pageQO
     * @param condition
     * @return list
     */
    PageVO<BizMap> getBizMapList(PageQO pageQO, BizMap condition);

    /**
     * 获取全部地图数据
     *
     * @return 全部地图数据
     */
    List<BizMap> getBizMapList();

    /**
     * 根据ID删除地图
     *
     * @param bizMapId 地图ID
     * @return ret
     */
    Result deleteBizMap(Integer bizMapId);

    /**
     * 修改地图数据
     *
     * @param bizMap 地图数据新值
     * @return ret
     */
    Result updateBizMap(BizMap bizMap);

    /**
     * 新增地图数据
     *
     * @param bizMap 新地图数据
     * @return 添加操作结果
     */
    Result addBizMap(BizMap bizMap);

    //====================================================================

    Result saveEdges(List<MapEdge> edges);//添加导航图中边关系

    Result delEdgeByIdList(List<Integer> list);//根据ID删除一条边

    Result delEdgesByPartKey(MapEdge key);//根据1点ID删除有关边

    List<MapEdge> getEdgesByFid(Integer mapId);//获取所有边数据
}
