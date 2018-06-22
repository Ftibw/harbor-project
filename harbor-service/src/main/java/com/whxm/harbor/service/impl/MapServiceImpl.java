package com.whxm.harbor.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.whxm.harbor.bean.BizMap;
import com.whxm.harbor.bean.PageQO;
import com.whxm.harbor.bean.PageVO;
import com.whxm.harbor.bean.Result;
import com.whxm.harbor.constant.Constant;
import com.whxm.harbor.mapper.BizMapMapper;
import com.whxm.harbor.service.MapService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
@Transactional
public class MapServiceImpl implements MapService {

    private final Logger logger = LoggerFactory.getLogger(MapServiceImpl.class);

    @Resource
    private BizMapMapper bizMapMapper;

    @Override
    public BizMap getBizMap(Integer bizMapId) {

        BizMap bizMap;

        try {
            bizMap = bizMapMapper.selectByPrimaryKey(bizMapId);

            if (this.logger.isDebugEnabled()) {
                if (null == bizMap) logger.info("ID为{}的地图不存在", bizMapId);
            }

        } catch (Exception e) {

            logger.error("ID为{}的地图 获取报错", bizMapId, e);

            throw new RuntimeException();
        }

        return bizMap;
    }

    @Override
    public PageVO<BizMap> getBizMapList(PageQO<BizMap> pageQO) {

        PageVO<BizMap> pageVO;

        try {
            Page page = PageHelper.startPage(pageQO.getPageNum(), pageQO.getPageSize());

            pageVO = new PageVO<>(pageQO);

            pageVO.setList(bizMapMapper.getBizMapList(pageQO.getCondition()));

            pageVO.setTotal(page.getTotal());

        } catch (Exception e) {

            logger.error("地图列表获取报错", e);

            throw new RuntimeException();
        }

        return pageVO;
    }

    @Override
    public List<BizMap> getBizMapList() {

        List<BizMap> list;

        try {
            list = bizMapMapper.getBizMapList((BizMap) Constant.DEFAULT_QUERY_CONDITION);

        } catch (Exception e) {

            logger.error("地图数据列表 获取报错", e);

            throw new RuntimeException();
        }

        return list;
    }

    @Override
    public Result deleteBizMap(Integer bizMapId) {

        Result ret;

        try {

            int affectRow = bizMapMapper.deleteByPrimaryKey(bizMapId);

            if (this.logger.isDebugEnabled()) {
                logger.info(0 == affectRow ? "ID为{}的地图删除失败" : "ID为{}的地图删除成功", bizMapId);
            }

            ret = new Result("ID为" + bizMapId + "的地图 删除了" + affectRow + "行");

        } catch (Exception e) {

            logger.error("ID为{}的地图 删除报错", bizMapId, e);

            throw new RuntimeException();
        }

        return ret;
    }

    @Override
    public Result updateBizMap(BizMap bizMap) {

        Result ret;

        if (null == bizMap) {

            return new Result("地图数据为空");

        } else if (null == bizMap.getMapId()) {

            return new Result("该地图不存在");
        }

        try {
            int affectRow = bizMapMapper.updateByPrimaryKeySelective(bizMap);

            if (this.logger.isDebugEnabled()) {
                logger.debug(1 == affectRow ?
                        "ID为{}的地图数据修改成功" : "ID为{}的地图数据修改失败", bizMap.getMapId());
            }

            ret = new Result(1 == affectRow ? bizMap : "地图数据 修改了0行");

        } catch (Exception e) {

            logger.error("ID为{}的地图修改 报错", bizMap.getMapId(), e);

            throw new RuntimeException();
        }

        return ret;
    }

    @Override
    public Result addBizMap(BizMap bizMap) {

        Result ret;

        try {
            int affectRow = bizMapMapper.insert(bizMap);

            if (this.logger.isDebugEnabled()) {
                logger.debug(1 == affectRow ? "地图数据添加成功" : "地图数据添加失败");
            }

            ret = new Result(1 == affectRow ? bizMap : "地图数据 添加0行");

        } catch (Exception e) {

            logger.error("地图数据 添加报错", e);

            throw new RuntimeException();
        }

        return ret;
    }
}
