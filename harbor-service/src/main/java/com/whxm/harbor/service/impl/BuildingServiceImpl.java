package com.whxm.harbor.service.impl;

import com.whxm.harbor.bean.BizBuilding;
import com.whxm.harbor.bean.MapEdge;
import com.whxm.harbor.bean.Result;
import com.whxm.harbor.enums.ResultEnum;
import com.whxm.harbor.mapper.BizBuildingMapper;
import com.whxm.harbor.service.BuildingService;
import com.whxm.harbor.service.MapService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;


@Service
@Transactional
public class BuildingServiceImpl implements BuildingService {

    private final Logger logger = LoggerFactory.getLogger(BuildingServiceImpl.class);

    @Resource
    private BizBuildingMapper bizBuildingMapper;

    @Autowired
    private MapService mapService;


    @Override
    public BizBuilding getBizBuilding(Integer id) {

        return bizBuildingMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<BizBuilding> getBizBuildingList(Integer floor, Integer type) {

        return bizBuildingMapper.getBuildingList(floor, type);
    }

    @CacheEvict(cacheNames = "bizBuilding", allEntries = true)
    @Override
    public Result deleteBizBuilding(Integer id) {
        MapEdge key = new MapEdge();
        key.setTail(id);
        key.setHead(id);
        Result result = mapService.delEdgesByPartKey(key);
        if (0 == result.getCode()) {
            logger.info("ID为{}的点依附的边删除失败", id);
        }
        int affectRow = bizBuildingMapper.deleteByPrimaryKey(id);

        return 0 == affectRow ?
                Result.failure(ResultEnum.OPERATION_LOGIC_ERROR, String.format("ID为%s的建筑,无法删除", id))
                : Result.success(ResultEnum.SUCCESS_DELETED);
    }

    @CacheEvict(cacheNames = "bizBuilding", allEntries = true)
    @Override
    public Result saveBizBuildings(List<BizBuilding> list) {

        int affectRow = bizBuildingMapper.batchReplace(list);

        //仅为了避免重复索引抛异常,就多查一次,贼浪费
        /*synchronized (this) {
            exist = bizBuildingMapper.isExistsDuplicateNumber(list);
            if (null == exist || exist.isEmpty()) {
                affectRow = bizBuildingMapper.addShopWithPoint(list);
            }
        }*/
        /*if (null != exist && !exist.isEmpty())
            return Result.failure(ResultEnum.OPERATION_LOGIC_ERROR, String.format("业态列表中编号为{%s}的数据重复", exist));
        */
        return 0 == affectRow ? Result.failure(ResultEnum.OPERATION_LOGIC_ERROR, "业态列表无法添加")
                : Result.success(list);
    }
}
