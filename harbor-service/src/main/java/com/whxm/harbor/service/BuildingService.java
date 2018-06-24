package com.whxm.harbor.service;

import com.whxm.harbor.bean.BizBuilding;
import com.whxm.harbor.bean.PageQO;
import com.whxm.harbor.bean.PageVO;
import com.whxm.harbor.bean.Result;

import java.util.List;
import java.util.Map;

/**
 * 建筑服务
 */
public interface BuildingService {
    /**
     * 根据建筑ID获取建筑数据
     *
     * @param bizBuildingId 建筑ID
     * @return 建筑数据
     */
    BizBuilding getBizBuilding(Integer bizBuildingId);

    /**
     * 获取全部建筑数据
     *
     * @return 全部建筑数据
     */
    List<BizBuilding> getBizBuildingList();

    /**
     * 根据ID删除建筑
     *
     * @param bizBuildingId 建筑ID
     * @return ret
     */
    Result deleteBizBuilding(Integer bizBuildingId);

    /**
     * 修改建筑数据
     *
     * @param bizBuilding 建筑数据新值
     * @return ret
     */
    Result updateBizBuilding(BizBuilding bizBuilding);

    /**
     * 新增建筑数据
     *
     * @param bizBuilding 新建筑数据
     * @return 添加操作结果
     */
    Result addBizBuilding(BizBuilding bizBuilding);

    /**
     * 批量新增建筑数据
     *
     * @param list 新建筑数据
     * @return 添加操作结果
     */
    Result addBizBuildings(List<BizBuilding> list);
}
