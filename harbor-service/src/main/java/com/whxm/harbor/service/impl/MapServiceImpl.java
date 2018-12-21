package com.whxm.harbor.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.whxm.harbor.bean.*;
import com.whxm.harbor.conf.PathConfig;
import com.whxm.harbor.enums.ResultEnum;
import com.whxm.harbor.mapper.BizMapMapper;
import com.whxm.harbor.mapper.MapEdgeMapper;
import com.whxm.harbor.service.MapService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
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
    @Resource
    private MapEdgeMapper mapEdgeMapper;

    @Autowired
    private PathConfig pathConfig;

    @Override
    public BizMap getBizMap(Integer floorId) {

        BizMap bizMap;

        try {
            bizMap = bizMapMapper.selectMapByFloorId(floorId);


            if (this.logger.isDebugEnabled()) {
                if (null == bizMap) logger.info("楼层ID为{}的地图不存在", floorId);
            }
            if (null != bizMap)
                bizMap.setMapImgPath(pathConfig.getResourceURLWithPost() + bizMap.getMapImgPath());

        } catch (Exception e) {

            logger.error("楼层ID为{}的地图 获取报错", floorId, e);

            throw new RuntimeException(e);
        }

        return bizMap;
    }

    @Override
    public PageVO<BizMap> getBizMapList(PageQO pageQO, BizMap condition) {

        PageVO<BizMap> pageVO = new PageVO<>(pageQO);

        Page page = PageHelper.startPage(pageQO.getPageNum(), pageQO.getPageSize());

        List<BizMap> list = bizMapMapper.getBizMapList(condition);

        if (null != list && !list.isEmpty())
            list.forEach(item -> item.setMapImgPath(pathConfig.getResourceURLWithPost() + item.getMapImgPath()));

        pageVO.setList(list);

        pageVO.setTotal(page.getTotal());

        return pageVO;
    }

    @Override
    public List<BizMap> getBizMapList() {

        List<BizMap> list = bizMapMapper.getBizMapList(null);

        if (null != list && !list.isEmpty()) {
            list.forEach(item -> item.setMapImgPath(pathConfig.getResourceURLWithPost() + item.getMapImgPath()));
        }

        return list;
    }

    @Override
    public Result deleteBizMap(Integer bizMapId) {

        int affectRow = bizMapMapper.deleteByPrimaryKey(bizMapId);

        return 0 == affectRow ?
                Result.failure(ResultEnum.OPERATION_LOGIC_ERROR, String.format("ID为%s的地图,无法删除", bizMapId))
                : Result.success(ResultEnum.SUCCESS_DELETED);
    }

    @Override
    public Result updateBizMap(BizMap bizMap) {


        bizMap.setMapImgPath(bizMap.getMapImgPath().replaceAll("^" + pathConfig.getResourceURLWithPost() + "(.*)$", "$1"));

        int affectRow = bizMapMapper.updateByPrimaryKeySelective(bizMap);

        return 0 == affectRow ?
                Result.failure(ResultEnum.OPERATION_LOGIC_ERROR, String.format("ID为%s的地图,无法修改", bizMap.getMapId()))
                : Result.success(bizMap);
    }

    @Override
    public Result addBizMap(BizMap bizMap) {

        int affectRow = bizMapMapper.insert(bizMap);

        //楼层ID唯一判断

        return 0 == affectRow ?
                Result.failure(ResultEnum.OPERATION_LOGIC_ERROR, String.format("ID为%s的地图,无法添加", bizMap.getMapId()))
                : Result.success(bizMap);
    }

    //导航图边集CRUD
    @CacheEvict(cacheNames = "bizEdge", allEntries = true)
    @Override
    public Result saveEdges(List<MapEdge> edges) {
        int i = mapEdgeMapper.batchReplace(edges);
        return 0 == i ? Result.failure(ResultEnum.OPERATION_LOGIC_ERROR, "边集合保存失败")
                : Result.success(edges);
    }

    @CacheEvict(cacheNames = "bizEdge", allEntries = true)
    @Override
    public Result delEdgeByIdList(List<Integer> list) {
        int i = mapEdgeMapper.batchDelete(list);
        return 0 == i ?
                Result.failure(ResultEnum.OPERATION_LOGIC_ERROR, String.format("边集%s,无法全部删除", list))
                : Result.success(ResultEnum.SUCCESS_DELETED);
    }

    @CacheEvict(cacheNames = "bizEdge", allEntries = true)
    @Override
    public Result delEdgesByTailOrHead(MapEdge edge) {
        int i = mapEdgeMapper.delEdgesByTailOrHead(edge);
        return 0 == i ?
                Result.failure(ResultEnum.OPERATION_LOGIC_ERROR, String.format("点ID为%s吸附的边,无法删除", edge))
                : Result.success(ResultEnum.SUCCESS_DELETED);
    }

    @Override
    public List<MapEdge> getEdgesByFid(Integer fid) {
        return mapEdgeMapper.selectAll(fid);
    }
}
