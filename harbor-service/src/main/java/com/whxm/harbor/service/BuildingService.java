package com.whxm.harbor.service;

import com.whxm.harbor.bean.Result;
import com.whxm.harbor.model.BuildingVo;

import java.util.List;

/**
 * 建筑服务
 */
public interface BuildingService {

    /**
     * 获取全部建筑数据
     *
     * @param floor 楼层ID
     * @param typeList  建筑类型集合
     * @return 建筑数据列表
     */
    List<BuildingVo> listBuildings(Integer floor, List<Integer> typeList);

    /**
     * 批量新增建筑数据
     *
     * @param list 新建筑数据
     * @return 添加操作结果
     */
    Result saveBizBuildings(List<BuildingVo> list);

    /**
     * 批量删除建筑数据
     * 特别说明,店铺或终端数据录入数据库时,不能保证一定有对应的建筑数据
     * 所以可以随意删除建筑而不影响商铺和终端,当需要时在为指定商铺或终端录入建筑数据
     *
     * @param idList id列表
     * @return 删除的行数
     */
    Result batchDelete(List<String> idList);
}
