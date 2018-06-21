package com.whxm.harbor.service.impl;

import com.whxm.harbor.bean.BizBuilding;
import com.whxm.harbor.bean.Result;
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

            ret = Result.ok(1 == affectRow ? bizBuilding : "更新失败");

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

        try {

            int affectRow = bizBuildingMapper.insert(bizBuilding);

            ret = Result.ok(1 == affectRow ? bizBuilding : "添加失败");

        } catch (Exception e) {

            logger.error("建筑数添加报错", e);

            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

            ret = Result.build(HttpStatus.INTERNAL_SERVER_ERROR.value(), "建筑数添加报错");

        }

        return ret;
    }
}
