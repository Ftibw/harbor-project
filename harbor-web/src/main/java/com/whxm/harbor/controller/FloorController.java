package com.whxm.harbor.controller;

import com.whxm.harbor.annotation.MyApiResponses;
import com.whxm.harbor.bean.*;
import com.whxm.harbor.service.FloorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Api(description = "楼层服务")
@RestController
@RequestMapping("/floor")
@MyApiResponses
public class FloorController {
    private static final Logger logger = LoggerFactory.getLogger(FloorController.class);

    @Autowired
    private FloorService floorService;

    @ApiOperation("获取全部楼层数据")
    @GetMapping("/floors")
    public Map<String, Object> getBizFormats() {

        ResultMap<String, Object> ret = new ResultMap<>(2);

        try {
            List<BizFloor> list = floorService.getBizFloorList();

            ret.build("data", list);

            ret = list.isEmpty() ? ret.build("success", false) : ret.build("success", true);

        } catch (Exception e) {

            logger.error("楼层列表 获取报错", e);

            ret.build("data", new Object[]{}).build("success", false);
        }

        return ret;
    }

    //==========================以下均被拦截============================

    @ApiOperation("获取楼层列表(需授权)")
    @GetMapping("/bizFloors")
    public Result getBizFloors(PageQO<BizFloor> pageQO, BizFloor condition) {
        PageVO<BizFloor> pageVO = null;

        Result ret = null;
        try {
            pageQO.setCondition(condition);

            pageVO = floorService.getBizFloorList(pageQO);

            ret = new Result(pageVO);

        } catch (Exception e) {
            logger.error("楼层列表 获取报错", e);

            ret = new Result(HttpStatus.INTERNAL_SERVER_ERROR.value(), "楼层列表 获取报错", null);
        }

        return ret;
    }

    @ApiOperation("添加楼层(需授权)")
    @PostMapping("/bizFloor")
    public Result addBizFloor(@RequestBody BizFloor bizFloor) {
        Result ret = null;

        try {
            ret = floorService.addBizFloor(bizFloor);
        } catch (Exception e) {

            logger.error("楼层数据 添加报错", e);

            ret = new Result(HttpStatus.INTERNAL_SERVER_ERROR.value(), "楼层数据 添加报错", null);
        }

        return ret;
    }

    @ApiOperation("修改楼层(需授权)")
    @PutMapping("/bizFloor")
    public Result updateBizFloor(@RequestBody BizFloor bizFloor) {

        Result ret = null;

        try {
            ret = floorService.updateBizFloor(bizFloor);
        } catch (Exception e) {

            logger.error("楼层数据 修改报错", e);

            ret = new Result(HttpStatus.INTERNAL_SERVER_ERROR.value(), "楼层数据 修改报错", null);
        }

        return ret;
    }


    @ApiOperation("删除楼层(需授权)")
    @DeleteMapping("/bizFloor/{ID}")
    public Result delBizFloor(@ApiParam(name = "ID", value = "楼层ID", required = true)
                              @PathVariable("ID") Integer bizFloorId) {
        Result ret = null;

        try {
            ret = floorService.deleteBizFloor(bizFloorId);
        } catch (Exception e) {

            logger.error("楼层数据 删除报错", e);

            ret = new Result(HttpStatus.INTERNAL_SERVER_ERROR.value(), "楼层数据 删除报错", null);
        }

        return ret;
    }
}
