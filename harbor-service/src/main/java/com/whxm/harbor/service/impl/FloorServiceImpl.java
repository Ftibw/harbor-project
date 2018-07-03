package com.whxm.harbor.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.whxm.harbor.bean.BizFloor;
import com.whxm.harbor.bean.PageQO;
import com.whxm.harbor.bean.PageVO;
import com.whxm.harbor.bean.Result;
import com.whxm.harbor.cache.CacheService;
import com.whxm.harbor.constant.Constant;
import com.whxm.harbor.enums.ResultEnum;
import com.whxm.harbor.exception.DataNotFoundException;
import com.whxm.harbor.service.FloorService;
import com.whxm.harbor.mapper.BizFloorMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class FloorServiceImpl implements FloorService {

    private final Logger logger = LoggerFactory.getLogger(FloorServiceImpl.class);

    @Resource
    private BizFloorMapper bizFloorMapper;

    @Override
    public BizFloor getBizFloor(Integer bizFloorId) {

        BizFloor bizFloor;

        try {
            bizFloor = bizFloorMapper.selectByPrimaryKey(bizFloorId);

            if (null == bizFloor) logger.info("ID为{}的楼层不存在", bizFloorId);

        } catch (Exception e) {

            logger.error("ID为{}的楼层 获取报错", bizFloorId, e);

            throw new RuntimeException(e);
        }

        return bizFloor;
    }

    @Override
    public PageVO<BizFloor> getBizFloorList(PageQO pageQO, BizFloor condition) {

        PageVO<BizFloor> pageVO;

        Page page = PageHelper.startPage(pageQO.getPageNum(), pageQO.getPageSize());

        pageVO = new PageVO<>(pageQO);

        List<BizFloor> list = bizFloorMapper.getBizFloorList(condition);

        /*if (null == list || list.isEmpty())
            throw new DataNotFoundException();*/

        pageVO.setList(list);

        pageVO.setTotal(page.getTotal());

        return pageVO;
    }

    @Autowired
    private CacheService cacheService;

    @Override
    public List<BizFloor> getBizFloorList() {

        return cacheService.getFloorList();
    }

    @CacheEvict(cacheNames = "bizFloor", allEntries = true)
    @Override
    public Result deleteBizFloor(Integer bizFloorId) {

        int affectRow = bizFloorMapper.deleteByPrimaryKey(bizFloorId);

        return 0 == affectRow ?
                Result.failure(ResultEnum.OPERATION_LOGIC_ERROR, String.format("ID为%s的楼层,无法删除", bizFloorId))
                : Result.success(ResultEnum.SUCCESS_DELETED);
    }

    @CacheEvict(cacheNames = "bizFloor", allEntries = true)
    @Override
    public Result updateBizFloor(BizFloor bizFloor) {

        int affectRow = bizFloorMapper.updateByPrimaryKeySelective(bizFloor);

        return 0 == affectRow ?
                Result.failure(ResultEnum.OPERATION_LOGIC_ERROR, String.format("ID为%s的楼层,无法修改", bizFloor.getFloorId()))
                : Result.success(bizFloor);
    }

    @CacheEvict(cacheNames = "bizFloor", allEntries = true)
    @Override
    public Result addBizFloor(BizFloor bizFloor) {

        Object exist = null;

        int affectRow = 0;

        //已经做了编号的唯一索引,这里真浪费,暂时这样,优先保证状态正确性
        synchronized (this) {

            exist = bizFloorMapper.selectIdByNumber(bizFloor.getFloorNumber());

            if (Objects.isNull(exist)) {
                affectRow = bizFloorMapper.insert(bizFloor);
            }
        }

        if (Objects.nonNull(exist))
            return Result.failure(ResultEnum.OPERATION_LOGIC_ERROR, String.format("ID为%s的楼层编号%s重复", bizFloor.getFloorId(), bizFloor.getFloorNumber()));

        return 0 == affectRow ?
                Result.failure(ResultEnum.OPERATION_LOGIC_ERROR, String.format("ID为%s的建筑,无法添加", bizFloor.getFloorId()))
                : Result.success(bizFloor);
    }
}
