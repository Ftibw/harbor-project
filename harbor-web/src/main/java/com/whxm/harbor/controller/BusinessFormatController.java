package com.whxm.harbor.controller;

import com.whxm.harbor.annotation.MyApiResponses;
import com.whxm.harbor.bean.*;
import com.whxm.harbor.exception.DataNotFoundException;
import com.whxm.harbor.service.BusinessFormatService;
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
            ret.build("data", list).build("success", true);
        } catch (Exception e) {
            logger.error("业态列表 获取报错", e);
            ret.build("data", new Object[]{}).build("success", false);
        }
        return ret;
    }

    //==========================以下均被拦截============================

    @ApiOperation("获取后台业态列表(需授权)")
    @GetMapping("/bizFormats")
    public Result getBizFormats(PageQO pageQO, BizFormat condition) {
        PageVO<BizFormat> pageVO = businessFormatService.getBizFormatList(pageQO, condition);
        return Result.success(pageVO);
    }

    @ApiOperation("新增业态(需授权)")
    @PostMapping("/bizFormat")
    public Result addBizFormat(@RequestBody BizFormat bizFormat) {
        Assert.notNull(bizFormat, "业态数据不能为空");
        Assert.isNull(bizFormat.getBizFormatId(), "业态ID必须为空");
        return businessFormatService.addBizFormat(bizFormat);
    }

    @ApiOperation("修改业态(需授权)")
    @PutMapping("/bizFormat")
    public Result updateBizFormat(@RequestBody BizFormat bizFormat) {
        Assert.notNull(bizFormat, "业态数据不能为空");
        Assert.notNull(bizFormat.getBizFormatId(), "业态ID不能为空");
        return businessFormatService.updateBizFormat(bizFormat);
    }

    @ApiOperation("删除业态(需授权)")
    @DeleteMapping("/bizFormat")
    public Result delBizFormat(@ApiParam(name = "ID", value = "业态ID", required = true)
                                       Integer id) {
        Assert.notNull(id, "业态ID不能为空");
        return businessFormatService.deleteBizFormat(id);
    }
}
