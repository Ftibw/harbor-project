package com.whxm.harbor.controller;

import com.whxm.harbor.annotation.MyApiResponses;
import com.whxm.harbor.bean.*;
import com.whxm.harbor.cache.CacheService;
import com.whxm.harbor.enums.ResultEnum;
import com.whxm.harbor.exception.DataNotFoundException;
import com.whxm.harbor.exception.ParameterInvalidException;
import com.whxm.harbor.graph.PathFinder;
import com.whxm.harbor.graph.Weight;
import com.whxm.harbor.graph.WeightImpl;
import com.whxm.harbor.service.MapService;
import com.whxm.harbor.utils.Assert;
import com.whxm.harbor.utils.FileUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(description = "地图服务")
@RestController
@RequestMapping("/map")
@MyApiResponses
public class MapController {

    private final Logger logger = LoggerFactory.getLogger(MapController.class);

    @Autowired
    private MapService mapService;

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
    public Result findPath(Integer startId, Integer endId) {
        //从缓存,获取指定楼层的所有building
        List<BizBuilding> buildings = cacheService.getBuildingList();
        //从缓存,根据指定楼层ID获取边集
        List<MapEdge> edges = cacheService.getEdgesByFid();
        //根据顶点集合和邻接出边表寻找最短路径
        Map<Integer, BizBuilding> vertices = new HashMap<>(buildings.size());//顶点集
        Map<Integer, List<MapEdge>> adjacencyTable = new HashMap<>();//邻接出边表
        for (BizBuilding b : buildings) {
            vertices.put(b.getId(), b);
        }
        for (MapEdge edge : edges) {
            buildAdjacencyTable(adjacencyTable, edge);
        }
        //TODO find path using vertices and adjacencyTable by PathFinder
        PathFinder<Integer, BizBuilding, MapEdge, WeightImpl> pathFinder = new PathFinder<>(
                vertices, adjacencyTable, startId, endId, WeightImpl.newInstance(0.0, 0.0),
                (point, end) -> {
                    Double h = Math.abs(Double.parseDouble(end.getDx()) - Double.parseDouble(point.getDx()))
                            + Math.abs(Double.parseDouble(end.getDy()) - Double.parseDouble(point.getDy()));
                    return WeightImpl.newInstance(h, 0.0);
                }
        );
        Map<String, Object> pathInfo = pathFinder.findPath(MapEdgeKey::getHead,
                (edge) -> WeightImpl.newInstance(edge.getDistance(), edge.getTime()));
        //将路径点按起点--->终点的次序以数组返回前端
        return null == pathInfo ? Result.failure(ResultEnum.RESULT_DATA_NONE, "路径不存在") : Result.success(pathInfo);
    }

    /**
     * 根据图顶点集合与边集合构造图的邻接出边表
     *
     * @param adjacencyTable 邻接出边表
     * @param edge           边
     */
    private void buildAdjacencyTable(Map<Integer, List<MapEdge>> adjacencyTable, MapEdge edge) {
        Integer tailId = edge.getTail();
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

        Assert.notNull(edges, "边数据不能为空");
        for (MapEdge e : edges) {
            Assert.notNull(e.getTail(), "边起点ID不能为空");
            Assert.notNull(e.getHead(), "边终点ID不能为空");
        }
        return mapService.saveEdges(edges);
    }

    @ApiOperation("删除地图边关系")
    @DeleteMapping(value = "/edges")
    public Result delEdges(MapEdgeKey key) {
        Result ret;
        Integer tail = key.getTail();
        Integer head = key.getHead();
        if (null != tail && null != head) {
            if (tail.equals(head)) {
                ret = mapService.delEdgesByPartKey(key);
            } else {
                ret = mapService.delEdgeByPK(key);
            }
        } else
            ret = Result.failure(ResultEnum.PARAM_IS_BLANK, "ID不能为空");
        return ret;
    }

    @ApiOperation("获取全部边关系")
    @GetMapping(value = "/edges")
    public Result getAllEdges(Integer fid) {
        return Result.success(mapService.getEdgesByFid(fid));
    }

    //#################################################################################################

    @ApiOperation("终端获取全部地图数据")
    @GetMapping("/maps")
    public ResultMap<String, Object> getBizFormats() {

        ResultMap<String, Object> ret = new ResultMap<>(2);

        try {
            List<BizMap> list = mapService.getBizMapList();

            if (null == list || list.isEmpty())
                throw new DataNotFoundException();

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

        if (null == bizMap)
            throw new DataNotFoundException();

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
