package com.whxm.harbor.controller;

import com.whxm.harbor.annotation.MyApiResponses;
import com.whxm.harbor.bean.BizScreensaver;
import com.whxm.harbor.bean.PageQO;
import com.whxm.harbor.bean.PageVO;
import com.whxm.harbor.bean.Result;
import com.whxm.harbor.exception.DataNotFoundException;
import com.whxm.harbor.service.ScreensaverService;
import com.whxm.harbor.utils.Assert;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@Api(description = "屏保服务")
@RestController
@RequestMapping("/screensaver")
@MyApiResponses
public class ScreensaverController {

    @Autowired
    private ScreensaverService screensaverService;

    @ApiOperation("获取屏保列表(需授权)")
    @GetMapping("/bizScreensavers")
    public Result bizScreensaverList(PageQO pageQO, BizScreensaver condition) {

        PageVO<BizScreensaver> pageVO = screensaverService.getBizScreensaverList(pageQO, condition);

        return Result.success(pageVO);
    }

    @ApiOperation("获取屏保(需授权)")
    @GetMapping("/bizScreensaver")
    public Result getBizScreensaver(
            @ApiParam(name = "ID", value = "屏保的ID", required = true)
            @RequestParam("id") Integer id
    ) {

        Assert.notNull(id, "屏保的ID不能为空");

        BizScreensaver screensaver = screensaverService.getBizScreensaver(id);

        if (null == screensaver)
            throw new DataNotFoundException();

        return Result.success(screensaver);
    }

    @ApiOperation("修改屏保(需授权)")
    @PutMapping("/bizScreensaver")
    public Result updateBizScreensaver(@RequestBody BizScreensaver bizScreensaver) {

        Assert.notNull(bizScreensaver, "屏保数据不能为空");
        Assert.notNull(bizScreensaver.getScreensaverId(), "屏保ID不能为空");

        return screensaverService.updateBizScreensaver(bizScreensaver);
    }

    @ApiOperation("删除屏保(需授权)")
    @DeleteMapping("/bizScreensaver")
    public Result delBizScreensaver(
            @ApiParam(name = "ID", value = "屏保的ID", required = true)
                    Integer id
    ) {
        Assert.notNull(id, "屏保的ID不能为空");

        return screensaverService.deleteBizScreensaver(id);
    }

    @ApiOperation("添加屏保(需授权)")
    @PostMapping("/bizScreensaver")
    public Result addBizScreensaver(@RequestBody ScreensaverParam param) {

        Assert.notNull(param, "参数不能为空");
        Assert.notNull(param.bizScreensaver, "屏保数据不能为空");
        Assert.isNull(param.bizScreensaver.getScreensaverId(), "屏保ID必须为空");
        Assert.notEmpty(param.screensaverMaterialIds, "屏保素材ID集合不能为空");
        Assert.notRepeat(param.screensaverMaterialIds, "屏保素材ID集合不能重复");
        /*Assert.notEmpty(param.terminalIds, "终端ID集合不能为空");
        Assert.notRepeat(param.terminalIds, "终端ID集合不能重复");*/

        return screensaverService.addBizScreensaver(param.bizScreensaver, param.screensaverMaterialIds, param.terminalIds);
    }

    @ApiOperation(value = "发布屏保(需授权)",
            notes = "Integer screensaverId 屏保ID,String[] terminalIds 终端ID数组")
    @PostMapping("/publishedScreensaver")
    public Result publishScreensaver(@RequestBody PublishedScreensaverParam param) {

        Assert.notNull(param, "参数不能为空");
        Assert.notNull(param.screensaverId, "屏保ID不能为空");
        Assert.notEmpty(param.terminalIds, "终端ID集合不能为空");
        Assert.notRepeat(param.terminalIds, "终端ID集合不能重复");

        return screensaverService.publishScreensaver(param.screensaverId, param.terminalIds);
    }
}

class PublishedScreensaverParam {

    public Integer screensaverId;

    public String[] terminalIds;
}


class ScreensaverParam {

    public BizScreensaver bizScreensaver;

    public Integer[] screensaverMaterialIds;

    public String[] terminalIds;
}

