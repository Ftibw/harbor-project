package com.whxm.harbor.service.impl;

import com.whxm.harbor.bean.BizBuilding;
import com.whxm.harbor.bean.RelationShopBuilding;
import com.whxm.harbor.bean.RelationTerminalBuilding;
import com.whxm.harbor.bean.Result;
import com.whxm.harbor.enums.ResultEnum;
import com.whxm.harbor.mapper.BizBuildingMapper;
import com.whxm.harbor.mapper.MapEdgeMapper;
import com.whxm.harbor.mapper.RelationShopBuildingMapper;
import com.whxm.harbor.mapper.RelationTerminalBuildingMapper;
import com.whxm.harbor.model.BuildingVo;
import com.whxm.harbor.service.BuildingService;
import com.whxm.harbor.utils.Assert;
import com.whxm.harbor.utils.JacksonUtils;
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
    @Resource
    private RelationShopBuildingMapper relationShopBuildingMapper;
    @Resource
    private RelationTerminalBuildingMapper relationTerminalBuildingMapper;

    @Override
    public List<BuildingVo> listBuildings(Integer floorId, List<Integer> typeList) {

        return bizBuildingMapper.listBuildings(floorId, typeList);
    }

    @CacheEvict(cacheNames = {"bizBuilding", "bizEdge"}, allEntries = true)
    @Override
    public Result saveBizBuildings(List<BuildingVo> list) {
        List<BizBuilding> buildings = new ArrayList<>();
        List<RelationShopBuilding> rsbList = new ArrayList<>();
        List<RelationTerminalBuilding> rtbList = new ArrayList<>();
        List<String> sBidList = new ArrayList<>();
        List<String> tBidList = new ArrayList<>();

        for (BuildingVo vo : list) {
            BizBuilding building = new BizBuilding();
            BeanUtils.copyProperties(vo, building);
            buildings.add(building);
            String shopNumber = vo.getShopNumber();
            String id = vo.getId();
            if (null != shopNumber) {
                RelationShopBuilding rsb = new RelationShopBuilding();
                rsb.setNumber(shopNumber);
                rsb.setBid(id);
                String area = vo.getShopArea();
                List test = JacksonUtils.readValue(area, List.class);
                Assert.notNull(test, "编号为[" + shopNumber + "]的建筑区域数据错误");
                rsb.setArea(area);
                sBidList.add(id);
                rsbList.add(rsb);
            }
            String terminalNumber = vo.getTerminalNumber();
            if (null != terminalNumber) {
                RelationTerminalBuilding rtb = new RelationTerminalBuilding();
                rtb.setBid(id);
                rtb.setNumber(terminalNumber);
                tBidList.add(id);
                rtbList.add(rtb);
            }
        }
        if (sBidList.size() > 0) {
            relationShopBuildingMapper.batchDeleteByBidList(sBidList);
            relationShopBuildingMapper.batchInsert(rsbList);
        }
        if (tBidList.size() > 0) {
            relationTerminalBuildingMapper.batchDeleteByBidList(tBidList);
            relationTerminalBuildingMapper.batchInsert(rtbList);
        }

        int affectRow = bizBuildingMapper.batchReplace(buildings);
        return 0 == affectRow ? Result.failure(ResultEnum.OPERATION_LOGIC_ERROR, "建筑列表无法添加")
                : Result.success(list);
    }

    @CacheEvict(cacheNames = {"bizBuilding", "bizEdge"}, allEntries = true)
    @Override
    public Result batchDelete(List<String> idList) {
        relationShopBuildingMapper.batchDeleteByBidList(idList);
        relationTerminalBuildingMapper.batchDeleteByBidList(idList);

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
