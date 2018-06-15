package com.whxm.harbor.controller;

import com.whxm.harbor.annotation.MyApiResponses;
import com.whxm.harbor.bean.*;
import com.whxm.harbor.constant.Constant;
import com.whxm.harbor.service.TerminalService;
import com.whxm.harbor.utils.IPv4Util;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Api(description = "终端服务")
@RestController
@RequestMapping("/terminal")
@MyApiResponses
public class TerminalController {

    private static final Logger logger = LoggerFactory.getLogger(TerminalController.class);

    @Autowired
    private TerminalService terminalService;

    @ApiOperation("终端注册")
    @PostMapping(value = "/register", consumes = "application/x-www-form-urlencoded")
    public Map<String, Boolean> register(
            @ApiParam(name = "sn", value = "终端编号", required = true)
                    String sn,
            @ApiParam(name = "os", value = "终端类型(1=android,2=windows)", required = true)
                    Integer os
    ) {

        Map<String, Boolean> ret = new HashMap<>(1);

        try {
            //终端注册然后获取注册状态
            if (HttpStatus.OK.value() ==
                    terminalService
                            .register(
                                    new ResultMap<String, Object>(3)
                                            .build("terminalNumber", sn)
                                            .build("terminalPlatform", os)
                                            .build("registerTerminalTime", new Date())
                            ).getStatus()) {
                ret.put("success", true);

            } else {

                ret.put("success", false);
            }
        } catch (Exception e) {

            logger.error("编号为{}的终端注册检测报错", sn, e);

            ret.put("success", false);
        }

        return ret;
    }

    @ApiOperation("获取终端的屏保节目")
    @PostMapping(value = "/program", consumes = "application/x-www-form-urlencoded")
    public Map<String, Object> program(
            @ApiParam(name = "sn", value = "终端编号", required = true)
                    String sn,
            @ApiParam(name = "prog", value = "前正在播放的屏保编号", required = true)
                    Integer prog
    ) {

        ResultMap<String, Object> convert = new ResultMap<>(4);

        try {
            convert.build("terminalNumber", sn).build("screensaverId", prog);

            convert = terminalService.getTerminalScreensaverProgram(convert);

        } catch (Exception e) {

            logger.error("编号为{}的终端的屏保数据 获取报错", sn, e);

            convert.clean()
                    .build("code", 0)
                    .build("prog", prog)
                    .build("on_off", Constant.NO_DATA)
                    .build("data", new Object[]{});
        }
        return convert;
    }

    //==========================以下均被拦截============================

    @ApiOperation("获取终端列表(需授权)")
    @GetMapping("/bizTerminals")
    public Result getBizTerminals(PageQO<BizTerminal> pageQO, BizTerminal condition) {

        Result ret = null;

        try {
            pageQO.setCondition(condition);

            PageVO<BizTerminal> pageVO = terminalService.getBizTerminalList(pageQO);

            ret = new Result(pageVO);

        } catch (Exception e) {
            logger.error("终端列表 获取报错", e);
            ret = new Result(HttpStatus.INTERNAL_SERVER_ERROR.value(), "终端列表 获取报错", pageQO);
        }

        return ret;
    }

    @ApiOperation("获取终端(需授权)")
    @GetMapping("/bizTerminal/{ID}")
    public Result getBizTerminal(
            @ApiParam(name = "ID", value = "终端的ID", required = true)
            @PathVariable("ID") String terminalId
    ) {
        Result ret = null;
        BizTerminal terminal = null;
        try {
            terminal = terminalService.getBizTerminal(terminalId);

            ret = new Result(terminal);

        } catch (Exception e) {

            logger.error("ID为{}的终端数据 获取报错", terminalId, e);

            ret = new Result(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "ID为" + terminalId + "的终端数据 获取报错", Constant.NO_DATA);
        }

        return ret;
    }

    @ApiOperation("修改终端(需授权)")
    @PutMapping("/bizTerminal")
    public Result updateBizTerminal(@RequestBody BizTerminal bizTerminal) {
        Result ret = null;
        try {
            ret = terminalService.updateBizTerminal(bizTerminal);
        } catch (Exception e) {

            logger.error("终端数据 修改报错", e);
            ret = new Result(HttpStatus.INTERNAL_SERVER_ERROR.value(), "终端数据 修改报错", bizTerminal);
        }

        return ret;
    }

    @ApiOperation("删除终端(需授权)")
    @DeleteMapping("/bizTerminal")
    public Result delBizTerminal(
            @ApiParam(name = "ID", value = "终端的ID", required = true)
                    String id
    ) {
        Result ret = null;
        try {
            ret = terminalService.deleteBizTerminal(id);
        } catch (Exception e) {

            logger.error("ID为{}的终端数据 删除报错", id, e);

            ret = new Result(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "ID为" + id + "的终端数据 删除报错", Constant.NO_DATA);
        }

        return ret;
    }

    @ApiOperation("添加终端(需授权)")
    @PostMapping("/bizTerminal")
    public Result addBizTerminal(@RequestBody BizTerminal bizTerminal, HttpServletRequest request) {
        Result ret = null;
        try {
            bizTerminal.setTerminalIp(IPv4Util.getIpAddress(request));

            ret = terminalService.addBizTerminal(bizTerminal);

        } catch (Exception e) {
            logger.error("终端 添加报错", e);

            ret = new Result(HttpStatus.INTERNAL_SERVER_ERROR.value(), "终端 添加报错", bizTerminal);
        }
        return ret;
    }

}
