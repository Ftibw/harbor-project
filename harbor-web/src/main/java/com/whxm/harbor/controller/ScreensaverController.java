package com.whxm.harbor.controller;

import com.whxm.harbor.annotation.MyApiResponses;
import com.whxm.harbor.bean.BizScreensaver;
import com.whxm.harbor.bean.PageQO;
import com.whxm.harbor.bean.PageVO;
import com.whxm.harbor.bean.Result;
import com.whxm.harbor.constant.Constant;
import com.whxm.harbor.service.ScreensaverService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Objects;

@Api(description = "屏保服务")
@RestController
@RequestMapping("/screensaver")
@MyApiResponses
public class ScreensaverController {

    private final Logger logger = LoggerFactory.getLogger(ScreensaverController.class);

    @Autowired
    private ScreensaverService screensaverService;

    @ApiOperation("获取屏保列表(需授权)")
    @GetMapping("/bizScreensavers")
    public Result bizScreensaverList(PageQO<BizScreensaver> pageQO, BizScreensaver condition) {

        Result ret = null;

        try {
            pageQO.setCondition(condition);

            PageVO<BizScreensaver> pageVO = screensaverService.getBizScreensaverList(pageQO);

            ret = new Result(pageVO);

        } catch (Exception e) {
            logger.error("屏保列表 获取报错", e);

            ret = new Result(HttpStatus.INTERNAL_SERVER_ERROR.value(), "屏保列表 获取报错", pageQO);
        }

        return ret;
    }

    @ApiOperation("获取屏保(需授权)")
    @GetMapping("/bizScreensaver")
    public Result getBizScreensaver(
            @ApiParam(name = "ID", value = "屏保的ID", required = true)
            @RequestParam("id") Integer id
    ) {
        Result ret;

        try {
            BizScreensaver screensaver = screensaverService.getBizScreensaver(id);

            ret = new Result(screensaver);

        } catch (Exception e) {

            logger.error("ID为{}的屏保数据 获取报错", id, e);

            ret = new Result(HttpStatus.INTERNAL_SERVER_ERROR.value(), "ID为" + id + "的屏保数据 获取报错", Constant.NO_DATA);
        }

        return ret;
    }

    @ApiOperation("修改屏保(需授权)")
    @PutMapping("/bizScreensaver")
    public Result updateBizScreensaver(@RequestBody BizScreensaver bizScreensaver) {

        Result ret = null;
        try {
            ret = screensaverService.updateBizScreensaver(bizScreensaver);
        } catch (Exception e) {
            logger.error("屏保数据 修改报错", e);

            ret = new Result(HttpStatus.INTERNAL_SERVER_ERROR.value(), "屏保数据 修改报错", bizScreensaver);
        }

        return ret;
    }

    @ApiOperation("删除屏保(需授权)")
    @DeleteMapping("/bizScreensaver")
    public Result delBizScreensaver(
            @ApiParam(name = "ID", value = "屏保的ID", required = true)
                    Integer id
    ) {
        Result ret = null;

        try {
            ret = screensaverService.deleteBizScreensaver(id);
        } catch (Exception e) {
            logger.error("ID{}的屏保数据 删除报错", id, e);

            ret = new Result(HttpStatus.INTERNAL_SERVER_ERROR.value(), "ID为" + id + "的屏保 删除报错", Constant.NO_DATA);
        }

        return ret;
    }

    @ApiOperation("添加屏保(需授权)")
    @PostMapping("/bizScreensaver")
    public Result addBizScreensaver(@RequestBody ScreensaverParam param) {

        if (Objects.isNull(param.bizScreensaver) || Objects.isNull(param.screensaverMaterialIds)) {

            return new Result(HttpStatus.NOT_ACCEPTABLE.value(),
                    "参数不能为null", Constant.NO_DATA);
        }

        Result ret = null;
        try {
            ret = screensaverService.addBizScreensaver(param.bizScreensaver, param.screensaverMaterialIds);

        } catch (Exception e) {
            logger.error("屏保 添加报错", e);

            ret = new Result(HttpStatus.INTERNAL_SERVER_ERROR.value(), "屏保 添加报错", param);
        }
        return ret;
    }

    /*@ApiOperation(value = "发布屏保(需授权)",
            notes = "Integer screensaverId 屏保ID,String[] terminalIds 终端ID数组")*/
    @PostMapping("/publishedScreensaver")
    public Result publishScreensaver(@RequestBody PublishedScreensaverParam param) {

        if (Objects.isNull(param.screensaverId) || Objects.isNull(param.terminalIds)) {

            logger.info("输入参数不能为null,param:{{}}",
                    "screensaverId:" + param.screensaverId + "," +
                            "terminalIds:" + Arrays.toString(param.terminalIds));

            return new Result(HttpStatus.NOT_ACCEPTABLE.value(),
                    "参数不能为null", param);
        }

        Result ret = null;

        try {
            ret = screensaverService.publishScreensaver(param.screensaverId, param.terminalIds);

        } catch (Exception e) {

            logger.error("ID为{}的屏保 发布报错", param.screensaverId);

            ret = new Result(HttpStatus.INTERNAL_SERVER_ERROR.value(), "屏保 发布报错", param);
        }
        return ret;
    }

}

class PublishedScreensaverParam {

    public Integer screensaverId;

    public String[] terminalIds;
}


class ScreensaverParam {

    public BizScreensaver bizScreensaver;

    public Integer[] screensaverMaterialIds;
}

