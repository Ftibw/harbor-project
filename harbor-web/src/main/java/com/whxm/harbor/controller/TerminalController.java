package com.whxm.harbor.controller;

import com.whxm.harbor.annotation.KeepAliveDetect;
import com.whxm.harbor.annotation.MyApiResponses;
import com.whxm.harbor.annotation.VisitLogger;
import com.whxm.harbor.bean.*;
import com.whxm.harbor.conf.TerminalConfig;
import com.whxm.harbor.enums.ResultEnum;
import com.whxm.harbor.service.TerminalService;
import com.whxm.harbor.service.TerminalVisitService;
import com.whxm.harbor.utils.Assert;
import com.whxm.harbor.utils.IPv4Utils;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Api(description = "终端服务")
@RestController
@RequestMapping("/terminal")
@MyApiResponses
public class TerminalController {

    private final Logger logger = LoggerFactory.getLogger(TerminalController.class);

    @Autowired
    private TerminalService terminalService;

    @Autowired
    private TerminalVisitService terminalVisitService;

    @Autowired
    private TerminalConfig terminalConfig;

    @ApiOperation("终端注册")
    @PostMapping(value = "/register")
    public Map<String, Object> register(
            @ApiParam(name = "sn", value = "终端编号", required = true)
                    String sn,
            @ApiParam(name = "os", value = "终端类型(1=android,2=windows)", required = true)
                    Integer os
    ) {

        Assert.notNull(sn, "终端编号不能为空");
        Assert.notNull(os, "终端类型不能为空");

        ResultMap<String, Object> ret = new ResultMap<>(3);

        try {
            //"floor":"楼层编号","rotate":45
            //终端注册然后获取注册状态
            BizTerminal terminal = terminalService.register(
                    new ResultMap<String, Object>(3)
                            .build("terminalNumber", sn)
                            .build("terminalPlatform", os)
                            .build("registerTerminalTime", new Date())
            );
            if (null != terminal) {

                ret.build("success", true)
                        .build("floor", terminal.getFloorId())
                        .build("rotate", terminal.getTerminalRotationAngle());

            } else {

                ret.put("success", false);
            }
        } catch (Exception e) {

            logger.error("编号为{}的终端注册检测报错", sn, e);

            ret.put("success", false);
        }

        return ret;
    }

    @KeepAliveDetect
    @ApiOperation("获取终端的屏保节目")
    @RequestMapping(value = "/program", method = {RequestMethod.POST, RequestMethod.GET})
    public Map<String, Object> program(
            @ApiParam(name = "sn", value = "终端编号", required = true)
                    String sn,
            @ApiParam(name = "prog", value = "前正在播放的屏保编号")
                    Integer prog
    ) {

        Assert.notNull(sn, "终端编号不能为空");

        ResultMap<String, Object> convert = new ResultMap<>(6);

        try {
            convert.build("terminalNumber", sn).build("screensaverId", prog);

            convert = terminalService.getTerminalScreensaverProgram(convert);

        } catch (Exception e) {

            logger.error("编号为{}的终端的屏保数据 获取报错", sn, e);

            convert.clean()
                    .build("code", 0)
                    .build("prog", 0)
                    .build("data", new Object[]{})
                    .build("on_off", terminalConfig.getOnOff())
                    .build("delay", terminalConfig.getDelay())
                    .build("protect", terminalConfig.getProtect());
        }
        return convert;
    }

    @ApiOperation("获取终端的首页轮播图")
    @PostMapping("/firstPage")
    public Map<String, Object> getTerminalFirstPage(
            @ApiParam(name = "sn", value = "终端编号", required = true)
                    String sn
    ) {
        ResultMap<String, Object> ret = new ResultMap<String, Object>(2)
                .build("success", false)
                .build("data", new Object[]{});
        if (null == sn) return ret;

        try {
            return terminalService.getTerminalFirstPage(sn, ret);

        } catch (Exception e) {

            logger.error("终端首页轮播图 获取报错", e);
            return ret;
        }
    }

    @VisitLogger
    @ApiOperation(value = "访问终端")
    @PostMapping("/visit")
    public ResultMap<String, Object> updateTerminalVisit(
            @ApiParam(name = "sn", value = "终端编号")
            @RequestParam("sn") String terminalNumber) {

        Assert.notNull(terminalNumber, "终端编号不能为空");

        return terminalVisitService.updateTerminalVisit(terminalNumber);
    }

    @ApiOperation(value = "获取终端访问数据列表")
    @GetMapping("/visits")
    public Result getTerminalVisitList(PageQO pageQO, BizTerminal condition) {

        PageVO<TerminalVisit> pageVO = terminalVisitService.getTerminalVisitList(pageQO, condition);

        return Result.success(pageVO);
    }

    //==========================以下均被拦截============================

    @ApiOperation("终端绑定首页轮播图(需授权)")
    @PostMapping("/boundFirstPage")
    public Result terminalBindFirstPage(@RequestBody TerminalFirstPageParam param) {

        Assert.notNull(param, "参数不能为空");
        Assert.notNull(param.terminalId, "终端ID不能为空[param:{}]", param);
        Assert.notEmpty(param.firstPageIds, "首页轮播图的ID集合不能为空[param:{}]", param);
        Assert.notRepeat(param.firstPageIds, "首页轮播图的ID集合不能重复");

        return terminalService.bindFirstPage(param.terminalId, param.firstPageIds);
    }

    @ApiOperation("指定ID的屏保发布前,查询全部终端,并将该屏保发布过的终端标记为checked(需授权)")
    @GetMapping("/bizTerminalsWithPublishedFlag")
    public Result getNotPublishedTerminals(PageQO pageQO, BizTerminal condition) {

        PageVO<BizTerminal> pageVO = terminalService.getBizTerminalListWithPublishedFlag(pageQO, condition);

        return Result.success(pageVO);
    }

    @ApiOperation("获取终端列表(需授权)")
    @GetMapping("/bizTerminals")
    public Result getBizTerminals(PageQO pageQO, BizTerminal condition) {

        PageVO<BizTerminal> pageVO = terminalService.getBizTerminalList(pageQO, condition);

        return Result.success(pageVO);
    }

    @ApiOperation("获取终端(需授权)")
    @GetMapping("/bizTerminal/{ID}")
    public Result getBizTerminal(
            @ApiParam(name = "ID", value = "终端的ID", required = true)
            @PathVariable("ID") String terminalId
    ) {
        Assert.notNull(terminalId, "终端的ID不能为空");

        BizTerminal terminal = terminalService.getBizTerminal(terminalId);

        if (null == terminal)
            return Result.failure(ResultEnum.RESULT_DATA_NONE);

        return Result.success(terminal);
    }

    @ApiOperation("修改终端(需授权)")
    @PutMapping("/bizTerminal")
    public Result updateBizTerminal(@RequestBody BizTerminal bizTerminal) {

        Assert.notNull(bizTerminal, "终端的数据不能为空");
        Assert.notNull(bizTerminal.getTerminalId(), "终端的ID不能为空");

        return terminalService.updateBizTerminal(bizTerminal);
    }

    @ApiOperation("删除终端(需授权)")
    @DeleteMapping("/bizTerminal")
    public Result delBizTerminal(
            @ApiParam(name = "ID", value = "终端的ID", required = true)
                    String id
    ) {

        Assert.notNull(id, "终端的ID不能为空");

        return terminalService.deleteBizTerminal(id);
    }

    @ApiOperation("添加终端(需授权)")
    @PostMapping("/bizTerminal")
    public Result addBizTerminal(@RequestBody BizTerminal bizTerminal, HttpServletRequest request) {

        Assert.notNull(bizTerminal, "终端的数据不能为空");
        Assert.isNull(bizTerminal.getTerminalId(), "终端的ID必须为空");

        bizTerminal.setTerminalIp(IPv4Utils.getIpAddress(request));

        return terminalService.addBizTerminal(bizTerminal);
    }

    @PostMapping("/config")
    public Result updateTerminalConfig(@RequestBody TerminalConfig config) {

        Assert.notNull(config, "参数不能为空");
        Assert.notNull(config.getOnOff(), "终端开关机时间不能为空[params:{}]", config);
        Assert.notNull(config.getDelay(), "终端延时不能为空[params:{}]", config);
        Assert.notNull(config.getProtect(), "终端保护时间不能为空[params:{}]", config);

        TerminalConfig terminalConfig = terminalService.updateTerminalConfig(config);

        return Result.success(terminalConfig);
    }

    @GetMapping("/config")
    public Result getTerminalConfig() {

        TerminalConfig terminalConfig = terminalService.getTerminalConfig(TerminalConfig.cacheKey);

        return Result.success(terminalConfig);
    }
}

class TerminalFirstPageParam {

    public String terminalId;

    public Integer[] firstPageIds;
}
