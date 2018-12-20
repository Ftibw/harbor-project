package com.whxm.harbor.controller;

import com.whxm.harbor.annotation.MyApiResponses;
import com.whxm.harbor.bean.*;
import com.whxm.harbor.model.BuildingVo;
import com.whxm.harbor.service.BuildingService;
import com.whxm.harbor.utils.Assert;
import com.whxm.harbor.utils.JacksonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@Api(description = "建筑服务")
@RestController
@RequestMapping("/buildings")
@MyApiResponses
public class BuildingController {

    @Autowired
    private BuildingService buildingService;

    @ApiOperation("获取建筑列表")
    @GetMapping
    public Result getBizBuildings(
            @ApiParam("楼层ID")
            @RequestParam(value = "floor", required = false)
                    Integer floor,
            @ApiParam("建筑类型")
            @RequestParam(value = "type", required = false)
                    List<Integer> typeList) {
        List<BuildingVo> list = buildingService.listBuildings(floor, typeList);
        return Result.success(list);
    }

    @ApiOperation("保存一个建筑")
    @PostMapping("/oneBuilding")
    public Result addOneBizBuilding(@RequestBody BuildingVo building) {
        return addBizBuilding(Collections.singletonList(building));
    }

    @ApiOperation("批量保存建筑")
    @PostMapping
    public Result addBizBuilding(@RequestBody List<BuildingVo> list) {
        Assert.notEmpty(list, "建筑数据不能为空");
        list.forEach(item -> {
            Assert.notNull(item.getType(), "建筑类型不能为空");
            Double dx = item.getDx();
            Double dy = item.getDy();
            Assert.notNull(dx, "建筑dx不能为空");
            Assert.notNull(dy, "建筑dy不能为空");
            String area = item.getArea();
            if (null != area) {
                List test = JacksonUtils.readValue(area, List.class);
                Assert.notNull(test, "建筑区域数据错误");
            } else {
                item.setArea("[]");
            }
            item.setId(dx.intValue() + "_" + dy.intValue());
        });
        return buildingService.saveBizBuildings(list);
    }

    @ApiOperation("删除建筑")
    @DeleteMapping
    public Result delBizBuilding(
            @ApiParam(value = "建筑ID列表", required = true)
            @RequestBody List<String> idList) {
        Assert.notEmpty(idList, "建筑ID不能为空");
        Assert.notRepeat(idList, "建筑ID不能重复");
        return buildingService.batchDelete(idList);
    }
}
