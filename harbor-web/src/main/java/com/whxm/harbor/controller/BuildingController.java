package com.whxm.harbor.controller;

import com.whxm.harbor.annotation.MyApiResponses;
import com.whxm.harbor.bean.*;
import com.whxm.harbor.exception.DataNotFoundException;
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

        List<BizBuilding> list = buildingService.listBuildings(floor, typeList);

        if (null == list || list.isEmpty())
            throw new DataNotFoundException();

        return Result.success(list);
    }

    @ApiOperation("保存一个建筑")
    @PostMapping("/oneBuilding")
    public Result addOneBizBuilding(@RequestBody BizBuilding building) {
        return addBizBuilding(Collections.singletonList(building));
    }

    @ApiOperation("批量保存建筑")
    @PostMapping
    public Result addBizBuilding(@RequestBody List<BizBuilding> list) {

        Assert.notEmpty(list, "建筑数据不能为空");

        Assert.notRepeat(list, "建筑数据不能重复");

        list.forEach(item -> {
            Assert.isNull(item.getId(), "建筑ID必须为空");
            Double dx = item.getDx();
            Double dy = item.getDy();
            Assert.notNull(dx, "建筑dx不能为空");
            Assert.notNull(dy, "建筑dy不能为空");
            if (null == item.getArea() || null == JacksonUtils.readValue(item.getArea(), List.class)) {
                item.setArea("[]");
            }
            if (null == item.getName()) {
                item.setName("");
            }
            //终端或商铺类型的建筑编号使用终端或商铺的编号
            Integer type = item.getType();
            if (null == type) {
                item.setType(BizBuilding.TYPE_LINE);
            }
            if (!BizBuilding.TYPE_TERMINAL.equals(type) && !BizBuilding.TYPE_SHOP.equals(type)) {
                item.setNumber(dx.intValue() + "_" + dy.intValue());
            }
        });

        return buildingService.saveBizBuildings(list);
    }

    @ApiOperation("删除建筑")
    @DeleteMapping
    public Result delBizBuilding(
            @ApiParam(value = "建筑ID列表", required = true)
            @RequestBody List<Integer> idList) {

        Assert.notEmpty(idList, "建筑ID不能为空");
        Assert.notRepeat(idList, "建筑ID不能重复");

        return buildingService.batchDelete(idList);
    }
}
