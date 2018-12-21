package com.whxm.harbor.service.impl;

import com.whxm.harbor.bean.BizBuilding;
import com.whxm.harbor.bean.Result;
import com.whxm.harbor.enums.ResultEnum;
import com.whxm.harbor.mapper.BizBuildingMapper;
import com.whxm.harbor.mapper.MapEdgeMapper;
import com.whxm.harbor.vo.BuildingVo;
import com.whxm.harbor.service.BuildingService;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;


@Service
@Transactional
public class BuildingServiceImpl implements BuildingService {

    @Resource
    private BizBuildingMapper bizBuildingMapper;
    @Resource
    private MapEdgeMapper mapEdgeMapper;

    @Override
    public List<BuildingVo> listBuildings(Integer floorId, List<Integer> typeList) {

        return bizBuildingMapper.listBuildings(floorId, typeList);
    }

    @CacheEvict(cacheNames = {"bizBuilding", "bizEdge"}, allEntries = true)
    @Override
    public Result saveBizBuildings(List<BuildingVo> list) {
        List<BizBuilding> buildings = new ArrayList<>();
        for (BuildingVo vo : list) {
            BizBuilding building = new BizBuilding();
            BeanUtils.copyProperties(vo, building);
            buildings.add(building);
            String sid = vo.getSid();
            String bid = vo.getId();
            if (null != sid) {
                bizBuildingMapper.setShopBuildingId(sid, bid);
            }
            String tid = vo.getTid();
            if (null != tid) {
                bizBuildingMapper.setTerminalBuildingId(tid, bid);
            }
        }
        int affectRow = bizBuildingMapper.batchReplace(buildings);
        return 0 == affectRow ? Result.failure(ResultEnum.OPERATION_LOGIC_ERROR, "建筑列表无法添加")
                : Result.success(list);
    }

    @CacheEvict(cacheNames = {"bizBuilding", "bizEdge"}, allEntries = true)
    @Override
    public Result batchDelete(List<String> idList) {
        int affectRow = bizBuildingMapper.batchDelete(idList);
        if (0 == affectRow) {
            return Result.failure(ResultEnum.OPERATION_LOGIC_ERROR, "建筑删除失败");
        }
        affectRow = mapEdgeMapper.batchDeleteByTailListOrHeadList(idList);
        if (0 == affectRow) {
            Result ret = Result.success(ResultEnum.SUCCESS_DELETED);
            ret.setMsg("建筑删除成功,建筑有关边删除行数为0");
            return ret;
        }
        return Result.success(ResultEnum.SUCCESS_DELETED);
    }
}
