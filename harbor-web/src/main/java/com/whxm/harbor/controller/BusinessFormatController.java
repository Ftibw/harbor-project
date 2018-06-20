package com.whxm.harbor.controller;

import com.whxm.harbor.annotation.MyApiResponses;
import com.whxm.harbor.bean.*;
import com.whxm.harbor.constant.Constant;
import com.whxm.harbor.service.BusinessFormatService;
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

@Api(description = "业态服务")
@RestController
@RequestMapping("/bizFormat")
@MyApiResponses
public class BusinessFormatController {
    private final Logger logger = LoggerFactory.getLogger(BusinessFormatController.class);

    @Autowired
    private BusinessFormatService businessFormatService;

    @ApiOperation("获取全部业态数据")
    @GetMapping("/businessFormats")
    public Map<String, Object> getBizFormats() {

        ResultMap<String, Object> ret = new ResultMap<>(2);

        try {
            List<BizFormat> list = businessFormatService.getBizFormatList();

            ret.build("data", list);

            ret = list.isEmpty() ? ret.build("success", false) : ret.build("success", true);

        } catch (Exception e) {

            logger.error("业态列表 获取报错", e);

            ret.build("data", new Object[]{}).build("success", false);
        }

        return ret;
    }

    //==========================以下均被拦截============================

    @ApiOperation("获取后台业态列表(需授权)")
    @GetMapping("/bizFormats")
    public Result getBizFormats(PageQO<BizFormat> pageQO, BizFormat condition) {
        Result ret = null;

        try {
            pageQO.setCondition(condition);

            PageVO<BizFormat> pageVO = businessFormatService.getBizFormatList(pageQO);

            ret = new Result(pageVO);

        } catch (Exception e) {

            logger.error("业态列表 获取报错", e);

            ret = new Result(HttpStatus.INTERNAL_SERVER_ERROR.value(), "业态列表 获取报错", pageQO);
        }

        return ret;
    }

    @ApiOperation("获取业态(需授权)")
    @GetMapping("/bizFormat/{ID}")
    public Result getBizFormat(@PathVariable("ID") Integer bizFormatId) {
        Result ret = null;

        try {
            BizFormat bizFormat = businessFormatService.getBizFormat(bizFormatId);

            ret = new Result(bizFormat);

        } catch (Exception e) {

            logger.error("ID为{}的业态数据 获取报错", bizFormatId, e);

            ret = new Result(HttpStatus.INTERNAL_SERVER_ERROR.value(), "ID为" + bizFormatId + "的业态数据 获取报错", Constant.NO_DATA);
        }

        return ret;
    }

    @ApiOperation("新增业态(需授权)")
    @PostMapping("/bizFormat")
    public Result addBizFormat(@RequestBody BizFormat bizFormat) {
        Result ret = null;
        try {
            ret = businessFormatService.addBizFormat(bizFormat);

        } catch (Exception e) {

            logger.error("业态数据 添加报错", e);

            ret = new Result(HttpStatus.INTERNAL_SERVER_ERROR.value(), "业态数据 添加报错", bizFormat);

        }
        return ret;
    }

    @ApiOperation("修改业态(需授权)")
    @PutMapping("/bizFormat")
    public Result updateBizFormat(@RequestBody BizFormat bizFormat) {
        Result ret = null;
        try {
            ret = businessFormatService.updateBizFormat(bizFormat);

        } catch (Exception e) {

            logger.error("", e);

            ret = new Result(HttpStatus.INTERNAL_SERVER_ERROR.value(), "业态数据 修改报错", bizFormat);
        }
        return ret;
    }

    @ApiOperation("删除业态(需授权)")
    @DeleteMapping("/bizFormat")
    public Result delBizFormat(@ApiParam(name = "ID", value = "业态ID", required = true)
                                       Integer id) {
        Result ret = null;
        try {
            ret = businessFormatService.deleteBizFormat(id);
        } catch (Exception e) {

            logger.error("ID为{}的业态数据 删除报错", e);

            ret = new Result(HttpStatus.INTERNAL_SERVER_ERROR.value(), "ID为" + id + "的业态数据 删除报错", Constant.NO_DATA);
        }
        return ret;
    }
}
