package com.whxm.harbor.service.impl;

import com.whxm.harbor.bean.BizBuilding;
import com.whxm.harbor.bean.Result;
import com.whxm.harbor.constant.Constant;
import com.whxm.harbor.enums.ResultEnum;
import com.whxm.harbor.mapper.BizBuildingMapper;
import com.whxm.harbor.service.BuildingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;


@Service
@Transactional
public class BuildingServiceImpl implements BuildingService {

    @Resource
    private BizBuildingMapper bizBuildingMapper;

    @Override
    public BizBuilding getBizBuilding(Integer bizBuildingId) {

        return bizBuildingMapper.selectByPrimaryKey(bizBuildingId);
    }

    @Override
    public List<BizBuilding> getBizBuildingList(Integer floor) {

        return bizBuildingMapper.getBuildingList(floor);
    }

    @Override
    public Result deleteBizBuilding(Integer bizBuildingId) {

        int affectRow = bizBuildingMapper.deleteByPrimaryKey(bizBuildingId);

        return 0 == affectRow ?
                Result.failure(ResultEnum.OPERATION_LOGIC_ERROR, String.format("ID为%s的建筑,无法删除", bizBuildingId))
                : Result.success(ResultEnum.SUCCESS_DELETED);

    }

    @Override
    public Result updateBizBuilding(BizBuilding bizBuilding) {

        int affectRow = bizBuildingMapper.updateByPrimaryKeySelective(bizBuilding);

        return 0 == affectRow ?
                Result.failure(ResultEnum.OPERATION_LOGIC_ERROR, String.format("ID为%s的建筑,无法修改", bizBuilding.getId()))
                : Result.success(bizBuilding);
    }

    @Override
    public Result addBizBuilding(BizBuilding bizBuilding) {

        Object exist = null;

        int affectRow = 0;

        synchronized (this) {
            exist = bizBuildingMapper.selectByNumber(bizBuilding.getNumber());

            if (Objects.isNull(exist)) {
                //仅为了避免重复索引抛异常,就多查一次,贼浪费
                affectRow = bizBuildingMapper.insert(bizBuilding);
            }
        }

        if (Objects.nonNull(exist)) {
            return Result.failure(ResultEnum.OPERATION_LOGIC_ERROR, String.format("ID为%s的建筑编号重复", bizBuilding.getId()));
        }

        return 0 == affectRow ?
                Result.failure(ResultEnum.OPERATION_LOGIC_ERROR, String.format("ID为%s的建筑,无法添加", bizBuilding.getId()))
                : Result.success(bizBuilding);
    }

    @Override
    public Result addBizBuildings(List<BizBuilding> list) {

        int affectRow = bizBuildingMapper.batchReplace(list);

        //仅为了避免重复索引抛异常,就多查一次,贼浪费
        /*synchronized (this) {
            exist = bizBuildingMapper.isExistsDuplicateNumber(list);
            if (null == exist || exist.isEmpty()) {
                affectRow = bizBuildingMapper.batchInsert(list);
            }
        }*/
        /*if (null != exist && !exist.isEmpty())
            return Result.failure(ResultEnum.OPERATION_LOGIC_ERROR, String.format("业态列表中编号为{%s}的数据重复", exist));
        */
        return 0 == affectRow ? Result.failure(ResultEnum.OPERATION_LOGIC_ERROR, "业态列表无法添加")
                : Result.success(list);
    }

    @Override
    public Result updateBizBuildings(List<BizBuilding> list) {

        int affectRow = bizBuildingMapper.batchReplace(list);

        return 0 == affectRow ? Result.failure(ResultEnum.OPERATION_LOGIC_ERROR, "业态列表无法修改")
                : Result.success(list);
    }
}
