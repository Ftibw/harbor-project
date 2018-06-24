package com.whxm.harbor.service.impl;

import com.whxm.harbor.bean.BizBuilding;
import com.whxm.harbor.bean.Result;
import com.whxm.harbor.constant.Constant;
import com.whxm.harbor.mapper.BizBuildingMapper;
import com.whxm.harbor.service.BuildingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;


@Service
@Transactional
public class BuildingServiceImpl implements BuildingService {

    private final Logger logger = LoggerFactory.getLogger(BuildingServiceImpl.class);

    @Resource
    private BizBuildingMapper bizBuildingMapper;

    @Override
    public BizBuilding getBizBuilding(Integer bizBuildingId) {

        BizBuilding bizBuilding = null;

        try {

            bizBuilding = bizBuildingMapper.selectByPrimaryKey(bizBuildingId);

        } catch (Exception e) {

            logger.error("查询ID为{}的建筑数据报错", bizBuildingId, e);

            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }

        return bizBuilding;
    }

    @Override
    public List<BizBuilding> getBizBuildingList() {

        List<BizBuilding> list = null;

        try {

            list = bizBuildingMapper.getBuildingList();

        } catch (Exception e) {

            logger.error("查询建筑数据列表报错", e);

            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }

        return list;
    }

    @Override
    public Result deleteBizBuilding(Integer bizBuildingId) {

        Result ret = null;

        try {

            int affectRow = bizBuildingMapper.deleteByPrimaryKey(bizBuildingId);

            ret = Result.ok("建筑数据删除" + affectRow + "行");

        } catch (Exception e) {

            logger.error("ID为{}的建筑数据删除报错", bizBuildingId, e);

            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

            ret = Result.build(HttpStatus.INTERNAL_SERVER_ERROR.value(), "ID为" + bizBuildingId + "的建筑数据删除报错");
        }

        return ret;
    }

    @Override
    public Result updateBizBuilding(BizBuilding bizBuilding) {

        Result ret = null;

        try {

            int affectRow = bizBuildingMapper.updateByPrimaryKeySelective(bizBuilding);

            ret = Result.ok(1 == affectRow ? bizBuilding : "建筑数据修改0行");

        } catch (Exception e) {

            logger.error("建筑数据修改报错", e);

            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

            ret = Result.build(HttpStatus.INTERNAL_SERVER_ERROR.value(), "建筑数据修改报错");

        }

        return ret;
    }

    @Override
    public Result addBizBuilding(BizBuilding bizBuilding) {

        Result ret = null;

        Object exist = null;

        int affectRow = 0;

        try {
            synchronized (this) {
                exist = bizBuildingMapper.selectByNumber(bizBuilding.getNumber());

                if (Objects.isNull(exist)) {
                    //仅为了避免重复索引抛异常,就多查一次,贼浪费
                    affectRow = bizBuildingMapper.insert(bizBuilding);
                }
            }

            if (Objects.nonNull(exist)) {
                return Result.build(HttpStatus.NOT_ACCEPTABLE.value(), "建筑编号不能重复", bizBuilding);
            }

            ret = Result.ok(1 == affectRow ? bizBuilding : "建筑数据添加0行");

        } catch (Exception e) {

            logger.error("建筑数添加报错", e);

            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

            ret = Result.build(HttpStatus.INTERNAL_SERVER_ERROR.value(), "建筑数添加报错");

        }

        return ret;
    }

    @Override
    public Result addBizBuildings(List<BizBuilding> list) {

        Result ret;

        try {
            
            int affectRow = bizBuildingMapper.batchInsert(list);

            logger.info(0 == affectRow ?
                    "建筑数据 添加失败" :
                    "建筑数据 成功添加" + affectRow + "行"
            );

            ret = new Result("建筑数据数据添加了" + affectRow + "行");

        } catch (Exception e) {

            logger.error("建筑数据数据 添加报错", e);

            throw new RuntimeException(e);
        }

        return ret;
    }
}
