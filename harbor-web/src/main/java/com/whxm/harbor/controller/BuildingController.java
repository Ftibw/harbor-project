package com.whxm.harbor.controller;

import com.whxm.harbor.annotation.MyApiResponses;
import com.whxm.harbor.bean.*;
import com.whxm.harbor.service.BuildingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(description = "建筑服务")
@RestController
@RequestMapping("/building")
@MyApiResponses
public class BuildingController {

    @Autowired
    private BuildingService buildingService;

    @ApiOperation("获取建筑列表(需授权)")
    @GetMapping("/bizBuildings")
    public Result getBizBuildings() {

        List<BizBuilding> list = buildingService.getBizBuildingList();

        return Result.ok(list);
    }

    @ApiOperation("添加建筑(需授权)")
    @PostMapping("/bizBuilding")
    public Result addBizBuilding(@RequestBody BizBuilding bizBuilding) {

        Assert.notNull(bizBuilding,"建筑数据不能为空");

        return buildingService.addBizBuilding(bizBuilding);
    }

    @ApiOperation("修改建筑(需授权)")
    @PutMapping("/bizBuilding")
    public Result updateBizBuilding(@RequestBody BizBuilding bizBuilding) {

        Assert.notNull(bizBuilding,"建筑数据不能为空");

        Assert.notNull(bizBuilding.getId(),"建筑ID不能为空");

        return buildingService.updateBizBuilding(bizBuilding);
    }


    @ApiOperation("删除建筑(需授权)")
    @DeleteMapping("/bizBuilding")
    public Result delBizBuilding(@ApiParam(name = "ID", value = "建筑ID", required = true)
                                         Integer id) {

        Assert.notNull(id,"建筑ID不能为空");

        return buildingService.deleteBizBuilding(id);
    }
}
