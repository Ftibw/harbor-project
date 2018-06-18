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

@Service
@Transactional
public class FloorServiceImpl implements FloorService {

    private static final Logger logger = LoggerFactory.getLogger(FloorServiceImpl.class);

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

            throw new RuntimeException();
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

            throw new RuntimeException();
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

            throw new RuntimeException();
        }

        return list;
    }

    @Override
    public Result deleteBizFloor(Integer bizFloorId) {

        Result ret;

        try {

            int affectRow = bizFloorMapper.deleteByPrimaryKey(bizFloorId);

            logger.info(1 == affectRow ? "ID为{}的楼层删除失败" : "ID为{}的楼层删除成功", bizFloorId);

            ret = new Result("ID为" + bizFloorId + "的楼层 删除了" + affectRow + "行");

        } catch (Exception e) {

            logger.error("ID为{}的楼层 删除报错", bizFloorId, e);

            throw new RuntimeException();
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

            logger.info(1 == affectRow ?
                    "ID为{}的楼层数据修改成功" : "ID为{}的楼层数据修改失败", bizFloor.getFloorId());

            ret = new Result("楼层数据 修改了" + affectRow + "行");

        } catch (Exception e) {

            logger.error("ID为{}的楼层修改 报错", bizFloor.getFloorId(), e);

            throw new RuntimeException();
        }

        return ret;
    }

    @Override
    public Result addBizFloor(BizFloor bizFloor) {

        Result ret;

        try {
            synchronized (this) {
                if (null != bizFloorMapper.selectIdByNumber(bizFloor.getFloorNumber())) {

                    return new Result(HttpStatus.NOT_ACCEPTABLE.value(), "业态编号重复", Constant.NO_DATA);
                }
            }

            int affectRow = bizFloorMapper.insert(bizFloor);

            logger.info(1 == affectRow ? "楼层数据添加成功" : "楼层数据添加失败");

            ret = new Result("楼层数据 添加了" + affectRow + "条数据");

        } catch (Exception e) {

            logger.error("楼层数据 添加报错", e);

            throw new RuntimeException();
        }

        return ret;
    }
}
