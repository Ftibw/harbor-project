package com.whxm.harbor.controller;

import com.whxm.harbor.annotation.MyApiResponses;
import com.whxm.harbor.bean.*;
import com.whxm.harbor.exception.DataNotFoundException;
import com.whxm.harbor.service.BuildingService;
import com.whxm.harbor.utils.Assert;
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
            @ApiParam(name = "floor", value = "楼层ID")
            @RequestParam(value = "floor", required = false)
                    Integer floor,
            @ApiParam(name = "type", value = "建筑类型")
            @RequestParam(value = "type", required = false)
                    Integer type) {

        List<BizBuilding> list = buildingService.getBizBuildingList(floor, type);

        if (null == list || list.isEmpty())
            throw new DataNotFoundException();

        return Result.success(list);
    }

    @ApiOperation("批量保存建筑")
    @PostMapping("/oneBuilding")
    public Result addOneBizBuilding(@RequestBody BizBuilding building) {
        Assert.notNull(building, "建筑数据不能为空");
        Assert.isNull(building.getId(), "建筑ID必须为空");
        String number = building.getNumber();
        if (null == number || "".equals(number)) {
            building.setNumber(building.getDx() + "_" + building.getDy());
        }
        return buildingService.saveBizBuildings(Collections.singletonList(building));
    }

    @ApiOperation("批量保存建筑")
    @PostMapping
    public Result addBizBuilding(@RequestBody List<BizBuilding> list) {

        Assert.notNull(list, "建筑数据不能为空");

        Assert.notRepeat(list, "建筑数据不能重复");

        list.forEach(item -> {
            Assert.isNull(item.getId(), "建筑ID必须为空");
            String number = item.getNumber();
            if (null == number || "".equals(number)) {
                item.setNumber(item.getDx() + "_" + item.getDy());
            }
        });

        return buildingService.saveBizBuildings(list);
    }

    @ApiOperation("删除建筑")
    @DeleteMapping
    public Result delBizBuilding(
            @ApiParam(name = "id", value = "建筑ID", required = true)
            @RequestParam("id") Integer id) {

        Assert.notNull(id, "建筑ID不能为空");

        return buildingService.deleteBizBuilding(id);
    }
}
