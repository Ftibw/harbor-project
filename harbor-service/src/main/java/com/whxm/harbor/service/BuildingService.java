package com.whxm.harbor.service;

import com.whxm.harbor.bean.BizBuilding;
import com.whxm.harbor.bean.Result;

import java.util.List;

/**
 * 建筑服务
 */
public interface BuildingService {
    /**
     * 根据建筑ID获取建筑数据
     *
     * @param id 建筑ID
     * @return 建筑数据
     */
    BizBuilding getBizBuilding(Integer id);

    /**
     * 获取全部建筑数据
     *
     * @param floor 楼层ID
     * @return 全部建筑数据
     */
    List<BizBuilding> getBizBuildingList(Integer floor);

    /**
     * 根据ID删除建筑
     *
     * @param id 建筑ID
     * @return ret
     */
    Result deleteBizBuilding(Integer id);

    /**
     * 批量新增建筑数据
     *
     * @param list 新建筑数据
     * @return 添加操作结果
     */
    Result saveBizBuildings(List<BizBuilding> list);
}
