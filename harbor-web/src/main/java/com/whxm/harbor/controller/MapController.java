package com.whxm.harbor.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.whxm.harbor.annotation.MyApiResponses;
import com.whxm.harbor.bean.*;
import com.whxm.harbor.cache.CacheService;
import com.whxm.harbor.enums.ResultEnum;
import com.whxm.harbor.exception.ParameterInvalidException;
import com.whxm.harbor.graph.PathFinder;
import com.whxm.harbor.graph.WeightImpl;
import com.whxm.harbor.vo.BuildingVo;
import com.whxm.harbor.service.BuildingService;
import com.whxm.harbor.service.MapService;
import com.whxm.harbor.utils.Assert;
import com.whxm.harbor.utils.FileUtils;
import com.whxm.harbor.utils.JacksonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Api(description = "地图服务")
@RestController
@RequestMapping("/map")
@MyApiResponses
public class MapController {

    private final Logger logger = LoggerFactory.getLogger(MapController.class);

    @Autowired
    private MapService mapService;
    @Autowired
    private BuildingService buildingService;

    //###########################################  地图导航  ############################################
    @Autowired
    private CacheService cacheService;

    /**
     * 根据起点和终点寻找地图最短路径,
     * 目前起点和终点只包含终端和非路径点的建筑
     *
     * @param startId 起点ID
     * @param endId   终点ID
     * @return 最短路径点集合
     */
    @ApiOperation("根据起止点ID寻找最短路线")
    @GetMapping(value = "/path")
    public Result findPath(@ApiParam(value = "导航起点ID", required = true) @RequestParam("startId") String startId,
                           @ApiParam(value = "导航终点ID", required = true) @RequestParam("endId") String endId) {
        long start = System.currentTimeMillis();
        Assert.notNull(startId, "起点ID不能为空");
        Assert.notNull(endId, "终点ID不能为空");
        if (startId.equals(endId)) {
            return Result.failure(ResultEnum.DATA_IS_WRONG, "起点与终点不能相同");
        }
        String buildingJson = cacheService.listBuildings();
        String edgeJson = cacheService.listEdges();
        if (null == buildingJson || null == edgeJson)
            return Result.failure(ResultEnum.INTERFACE_INNER_INVOKE_ERROR.setMessage("缓存数据读取为空"));
        //从缓存,获取指定楼层的所有building
        List<BizBuilding> buildings = JacksonUtils.readGenericTypeValue(buildingJson, new TypeReference<List<BizBuilding>>() {
        });
        //从缓存,根据指定楼层ID获取边集
        List<MapEdge> edges = JacksonUtils.readGenericTypeValue(edgeJson, new TypeReference<List<MapEdge>>() {
        });
        if (null == buildings || null == edges) {
            return Result.failure(ResultEnum.INTERFACE_INNER_INVOKE_ERROR.setMessage("缓存数据解析失败"));
        }
        //根据顶点集合和邻接出边表寻找最短路径
        Map<String, BizBuilding> vertices = new HashMap<>(buildings.size());//顶点集
        Map<String, List<MapEdge>> adjacencyTable = new HashMap<>();//邻接出边表
        for (BizBuilding b : buildings) {
            vertices.put(b.getId(), b);
        }

        if (null == vertices.get(startId) || null == vertices.get(endId)) {
            return Result.failure(ResultEnum.DATA_IS_WRONG, "起点或终点数据无效");
        }

        for (MapEdge edge : edges) {
            buildAdjacencyTable(adjacencyTable, edge);

            if (MapEdge.IS_DOUBLE_DIRECT.equals(edge.getIsDirected())) {
                //边反转头尾
                buildAdjacencyTable(adjacencyTable, new MapEdge(edge.getHead(), edge.getTail(), edge.getDistance(), edge.getTime()));
            }
        }

        //TODO find path using vertices and adjacencyTable by PathFinder
        PathFinder<String, BizBuilding, MapEdge, WeightImpl> pathFinder = new PathFinder<>(
                vertices, adjacencyTable, startId, endId, new WeightImpl(0.0, 0.0),
                (point, end) -> {
                    Double h = Math.abs(end.getDx() - point.getDx())
                            + Math.abs(end.getDy() - point.getDy());
                    return new WeightImpl(h, 0.0);
                }
        );
        Map<String, Object> info = pathFinder.findPath(MapEdge::getHead,
                (edge) -> new WeightImpl(edge.getDistance(), edge.getTime()));
        if (null == info)
            return Result.failure(ResultEnum.RESULT_DATA_NONE, "路径不存在");
        //将路径点按起点--->终点的次序以数组返回前端
        info.put("latency(ms)", System.currentTimeMillis() - start);
        return Result.success(info);
    }

    /**
     * 根据图顶点集合与边集合构造图的邻接出边表
     *
     * @param adjacencyTable 邻接出边表
     * @param edge           边
     */
    private void buildAdjacencyTable(Map<String, List<MapEdge>> adjacencyTable, MapEdge edge) {
        String tailId = edge.getTail();
        List<MapEdge> adjacencyList = adjacencyTable.get(tailId);
        if (null != adjacencyList) {
            adjacencyList.add(edge);
        } else {
            //顶点的出度默认值设为4(上下左右)
            List<MapEdge> newAdjacencyList = new ArrayList<>(4);
            adjacencyTable.put(tailId, newAdjacencyList);
            newAdjacencyList.add(edge);
        }
    }

    //#################################################################################################

    @ApiOperation("保存地图边关系")
    @PostMapping(value = "/edges")
    public Result saveMapEdges(@RequestBody List<MapEdge> edges) {
        Assert.notEmpty(edges, "边数据不能为空");
        for (MapEdge e : edges) {
            Assert.notNull(e.getTail(), "边起点ID不能为空");
            Assert.notNull(e.getHead(), "边终点ID不能为空");
            if (null == e.getDistance())
                e.setDistance(Double.MAX_VALUE);
            if (null == e.getTime())
                e.setTime(Double.MAX_VALUE);
        }
        return mapService.saveEdges(edges);
    }

    @ApiOperation("删除地图边关系")
    @PostMapping(value = "/delEdges")
    public Result delEdges(
            @ApiParam(value = "要删除的边集的ID列表", required = true)
            @RequestBody List<Integer> list) {
        Assert.notEmpty(list, "边不能为空");
        return mapService.delEdgeByIdList(list);
    }

    @ApiOperation("获取全部边关系")
    @GetMapping(value = "/edges")
    public Result getAllEdges(@ApiParam("楼层ID(传参时获取头点或者尾点在该楼层的边,空参时获取所有楼层的边)")
                              @RequestParam(name = "fid", required = false) Integer fid) {

        List<BuildingVo> buildings = buildingService.listBuildings(null, null);
        if (null == buildings || buildings.size() == 0) {
            return Result.failure(ResultEnum.RESULT_DATA_NONE, "建筑数据为空");
        }

        List<MapEdge> edges = mapService.getEdgesByFid(fid);
        List<Map<String, Object>> edgeList = JacksonUtils.readGenericTypeValue(JacksonUtils.toJson(edges),
                new TypeReference<List<Map<String, Object>>>() {
                });
        if (null == edgeList)
            return Result.failure(ResultEnum.RESULT_DATA_NONE);

        Map<Object, BizBuilding> buildingMap = new HashMap<>();
        for (BizBuilding b : buildings) {
            buildingMap.put(b.getId(), b);
        }
        for (Map<String, Object> edge : edgeList) {
            BizBuilding tailPoint = buildingMap.get(edge.get("tail"));
            BizBuilding headPoint = buildingMap.get(edge.get("head"));
            if (null != tailPoint)
                edge.put("tail", tailPoint);
            if (null != headPoint)
                edge.put("head", headPoint);
        }
        return Result.success(edgeList);
    }

    //#################################################################################################

    @ApiOperation("终端获取全部地图数据")
    @GetMapping("/maps")
    public ResultMap<String, Object> getBizMaps() {
        ResultMap<String, Object> ret = new ResultMap<>(2);
        try {
            List<BizMap> list = mapService.getBizMapList();
            ret.build("data", list).build("success", true);
        } catch (Exception e) {
            logger.error("地图列表 获取报错", e);
            ret.build("data", new Object[]{}).build("success", false);
        }
        return ret;
    }


    @ApiOperation("上传地图")
    @PostMapping("/picture")
    public Result uploadMap(@RequestParam("file") MultipartFile file) {
        if (null == file || file.isEmpty()) {
            throw new ParameterInvalidException("上传的文件是空的");
        }
        return FileUtils.upload(file, Result::success);
    }

    @ApiOperation("根据楼层ID获取地图数据")
    @GetMapping("/map")
    public Result getBizMap(
            @ApiParam(name = "floor", value = "楼层ID", required = true)
            @RequestParam("floor") Integer floor
    ) {
        Assert.notNull(floor, "楼层ID不能为空");
        BizMap bizMap = mapService.getBizMap(floor);
        return Result.success(bizMap);
    }

    //==========================以下均被拦截============================

    @ApiOperation("获取地图列表(需授权)")
    @GetMapping("/bizMaps")
    public Result getBizMaps(PageQO pageQO, BizMap condition) {
        PageVO<BizMap> pageVO = mapService.getBizMapList(pageQO, condition);
        return Result.success(pageVO);
    }

    @ApiOperation("修改地图(需授权)")
    @PutMapping("/bizMap")
    public Result updateBizMap(@RequestBody BizMap bizMap) {
        Assert.notNull(bizMap, "地图数据不能为空");
        Assert.notNull(bizMap.getMapId(), "地图ID不能为空");
        return mapService.updateBizMap(bizMap);
    }

    @ApiOperation("删除地图(需授权)")
    @DeleteMapping("/bizMap")
    public Result delBizMap(
            @ApiParam(name = "ID", value = "地图的ID", required = true)
                    Integer id
    ) {
        Assert.notNull(id, "地图ID不能为空");
        return mapService.deleteBizMap(id);
    }

    @ApiOperation("添加地图(需授权)")
    @PostMapping(value = "/bizMap")
    public Result addBizMap(@RequestBody BizMap bizMap) {
        Assert.notNull(bizMap, "地图数据不能为空");
        Assert.isNull(bizMap.getMapId(), "ID必须为空");
        return mapService.addBizMap(bizMap);
    }
}
