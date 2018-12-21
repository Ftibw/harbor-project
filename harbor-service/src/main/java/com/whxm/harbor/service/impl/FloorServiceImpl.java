package com.whxm.harbor.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.whxm.harbor.bean.BizFloor;
import com.whxm.harbor.bean.PageQO;
import com.whxm.harbor.bean.PageVO;
import com.whxm.harbor.bean.Result;
import com.whxm.harbor.constant.Constant;
import com.whxm.harbor.service.FloorService;
import com.whxm.harbor.mapper.BizFloorMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
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
    public PageVO<BizFloor> getBizFloorList(PageQO<BizFloor> pageQO) {

        PageVO<BizFloor> pageVO;

        try {
            Page page = PageHelper.startPage(pageQO.getPageNum(), pageQO.getPageSize());

            pageVO = new PageVO<>(pageQO);

            pageVO.setList(bizFloorMapper.getBizFloorList(pageQO.getCondition()));

            pageVO.setTotal(page.getTotal());

        } catch (Exception e) {

            logger.error("楼层列表获取报错", e);

            throw new RuntimeException(e);
        }

        return pageVO;
    }

    @Override
    public List<BizFloor> getBizFloorList() {

        List<BizFloor> list;

        try {
            list = bizFloorMapper.getBizFloorList((BizFloor) Constant.DEFAULT_QUERY_CONDITION);

        } catch (Exception e) {

            logger.error("楼层数据列表 获取报错", e);

            throw new RuntimeException(e);
        }

        return list;
    }

    @Override
    public Result deleteBizFloor(Integer bizFloorId) {

        Result ret;

        try {

            int affectRow = bizFloorMapper.deleteByPrimaryKey(bizFloorId);

            logger.info(1 == affectRow ? "ID为{}的楼层删除成功" : "ID为{}的楼层删除失败", bizFloorId);

            ret = new Result(String.format("ID为%d的楼层 删除了%d行", bizFloorId, affectRow));

        } catch (Exception e) {

            logger.error("ID为{}的楼层 删除报错", bizFloorId, e);

            throw new RuntimeException(e);
        }

        return ret;
    }

    @Override
    public Result updateBizFloor(BizFloor bizFloor) {

        Result ret;

        if (null == bizFloor) {

            return new Result("楼层数据为空");

        } else if (null == bizFloor.getFloorId()) {

            return new Result("该楼层不存在");
        }

        try {
            int affectRow = bizFloorMapper.updateByPrimaryKeySelective(bizFloor);

            if (this.logger.isDebugEnabled()) {
                logger.debug(1 == affectRow ?
                        "ID为{}的楼层数据修改成功" : "ID为{}的楼层数据修改失败", bizFloor.getFloorId());
            }

            ret = new Result(1 == affectRow ? bizFloor : "楼层数据修改0行");

        } catch (Exception e) {

            logger.error("ID为{}的楼层修改 报错", bizFloor.getFloorId(), e);

            throw new RuntimeException(e);
        }

        return ret;
    }

    @Override
    public Result addBizFloor(BizFloor bizFloor) {

        Result ret;

        Object exist = null;

        int affectRow = 0;

        try {
            //已经做了编号的唯一索引,这里真浪费,暂时这样,优先保证状态正确性
            synchronized (this) {

                exist = bizFloorMapper.selectIdByNumber(bizFloor.getFloorNumber());

                if (Objects.isNull(exist)) {
                    affectRow = bizFloorMapper.insert(bizFloor);
                }
            }

            if (Objects.nonNull(exist))
                return new Result(HttpStatus.NOT_ACCEPTABLE.value(), "楼层编号重复", bizFloor.getFloorName());

            if (this.logger.isDebugEnabled()) {

                logger.debug(1 == affectRow ? "楼层数据添加成功" : "楼层数据添加失败");
            }

            ret = new Result(1 == affectRow ? bizFloor : "楼层数据添加0行");

        } catch (Exception e) {

            logger.error("楼层数据 添加报错", e);

            throw new RuntimeException(e);
        }

        return ret;
    }
}
