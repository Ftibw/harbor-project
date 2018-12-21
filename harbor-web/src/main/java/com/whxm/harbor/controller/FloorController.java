package com.whxm.harbor.controller;

import com.whxm.harbor.annotation.MyApiResponses;
import com.whxm.harbor.bean.*;
import com.whxm.harbor.exception.DataNotFoundException;
import com.whxm.harbor.service.FloorService;
import com.whxm.harbor.utils.Assert;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Api(description = "楼层服务")
@RestController
@RequestMapping("/floor")
@MyApiResponses
public class FloorController {
    private final Logger logger = LoggerFactory.getLogger(FloorController.class);

    @Autowired
    private FloorService floorService;

    @ApiOperation("获取全部楼层数据")
    @GetMapping("/floors")
    public Map<String, Object> getBizFloors() {
        ResultMap<String, Object> ret = new ResultMap<>(2);
        try {
            List<BizFloor> list = floorService.getBizFloorList();
            ret.build("data", list).build("success", true);
        } catch (Exception e) {
            logger.error("楼层列表 获取报错", e);
            ret.build("data", new Object[]{}).build("success", false);
        }
        return ret;
    }

    //==========================以下均被拦截============================

    @ApiOperation("获取楼层列表(需授权)")
    @GetMapping("/bizFloors")
    public Result getBizFloors(PageQO pageQO, BizFloor condition) {
        PageVO<BizFloor> pageVO = floorService.getBizFloorList(pageQO, condition);
        return Result.success(pageVO);
    }

    @ApiOperation("添加楼层(需授权)")
    @PostMapping("/bizFloor")
    public Result addBizFloor(@RequestBody BizFloor bizFloor) {
        Assert.notNull(bizFloor, "楼层数据不能为空");
        Assert.isNull(bizFloor.getFloorId(), "楼层ID必须为空");
        return floorService.addBizFloor(bizFloor);
    }

    @ApiOperation("修改楼层(需授权)")
    @PutMapping("/bizFloor")
    public Result updateBizFloor(@RequestBody BizFloor bizFloor) {
        Assert.notNull(bizFloor, "楼层数据不能为空");
        Assert.notNull(bizFloor.getFloorId(), "楼层ID不能为空");
        return floorService.updateBizFloor(bizFloor);
    }


    @ApiOperation("删除楼层(需授权)")
    @DeleteMapping("/bizFloor")
    public Result delBizFloor(
            @ApiParam(name = "ID", value = "楼层ID", required = true)
                    Integer id) {
        Assert.notNull(id, "楼层ID不能为空");
        return floorService.deleteBizFloor(id);
    }
}
