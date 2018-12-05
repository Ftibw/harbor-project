package com.whxm.harbor.service.impl;

import com.whxm.harbor.bean.BizBuilding;
import com.whxm.harbor.bean.Result;
import com.whxm.harbor.enums.ResultEnum;
import com.whxm.harbor.exception.BusinessException;
import com.whxm.harbor.mapper.BizBuildingMapper;
import com.whxm.harbor.mapper.MapEdgeMapper;
import com.whxm.harbor.service.BuildingService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;


@Service
@Transactional
public class BuildingServiceImpl implements BuildingService {

    @Resource
    private BizBuildingMapper bizBuildingMapper;
    @Resource
    private MapEdgeMapper mapEdgeMapper;

    @Override
    public List<BizBuilding> listBuildings(Integer floor, Integer type) {

        return bizBuildingMapper.getBuildingList(floor, type);
    }

    @CacheEvict(cacheNames = {"bizBuilding", "bizEdge"}, allEntries = true)
    @Override
    public Result saveBizBuildings(List<BizBuilding> list) {
        int affectRow = bizBuildingMapper.batchReplace(list);
        return 0 == affectRow ? Result.failure(ResultEnum.OPERATION_LOGIC_ERROR, "建筑列表无法添加")
                : Result.success(list);
    }

    @CacheEvict(cacheNames = {"bizBuilding", "bizEdge"}, allEntries = true)
    @Override
    public Result batchDelete(List<Integer> idList) {
        int affectRow = bizBuildingMapper.batchDelete(idList);
        if (0 == affectRow) {
            return Result.failure(ResultEnum.OPERATION_LOGIC_ERROR, "建筑删除失败");
        }
        affectRow = mapEdgeMapper.batchDeleteByTailListOrHeadList(idList);
        if (0 == affectRow) {
            throw new BusinessException("建筑有关边删除失败");
        }
        return Result.success(ResultEnum.SUCCESS_DELETED);
    }
}
