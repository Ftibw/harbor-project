package com.whxm.harbor.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.whxm.harbor.annotation.MyApiResponses;
import com.whxm.harbor.bean.*;
import com.whxm.harbor.service.BuildingService;
import com.whxm.harbor.utils.JacksonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

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
            @ApiParam(name = "floor", value = "楼层ID")
            @RequestParam(value = "floor", required = false)
                    Integer floor) {

        List<BizBuilding> list = buildingService.getBizBuildingList(floor);

        return Result.ok(list);
    }

    @ApiOperation("批量添加建筑")
    @PostMapping
    public Result addBizBuilding(@RequestParam("buildings") String buildings) {

        Assert.notNull(buildings, "建筑数据不能为空");

        List<BizBuilding> list = JacksonUtils.readGenericTypeValue(buildings, new TypeReference<List<BizBuilding>>() {
        });

        return buildingService.addBizBuildings(list);
    }

    @ApiOperation("修改建筑")
    @PutMapping
    public Result updateBizBuilding(BizBuilding bizBuilding) {

        Assert.notNull(bizBuilding, "建筑数据不能为空");

        Assert.notNull(bizBuilding.getId(), "建筑ID不能为空");

        return buildingService.updateBizBuilding(bizBuilding);
    }


    @ApiOperation("删除建筑")
    @DeleteMapping
    public Result delBizBuilding(
            @ApiParam(name = "ID", value = "建筑ID", required = true)
            @RequestParam("id") Integer id) {

        Assert.notNull(id, "建筑ID不能为空");

        return buildingService.deleteBizBuilding(id);
    }
}
