package com.whxm.harbor.controller;

import com.whxm.harbor.annotation.MyApiResponses;
import com.whxm.harbor.bean.*;
import com.whxm.harbor.conf.FileDir;
import com.whxm.harbor.conf.FtpConfig;
import com.whxm.harbor.constant.Constant;
import com.whxm.harbor.ftp.FtpSession;
import com.whxm.harbor.service.MapService;
import com.whxm.harbor.utils.FileUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Api(description = "地图服务")
@RestController
@RequestMapping("/map")
@MyApiResponses
public class MapController {

    private final Logger logger = LoggerFactory.getLogger(MapController.class);

    @Autowired
    private MapService mapService;

    @Autowired
    private FileDir fileDir;

    @ApiOperation("终端获取全部地图数据")
    @GetMapping("/maps")
    public ResultMap<String, Object> getBizFormats() {

        ResultMap<String, Object> ret = new ResultMap<>(2);

        try {
            List<BizMap> list = mapService.getBizMapList();

            ret.build("data", list);

            ret = list.isEmpty() ? ret.build("success", false) : ret.build("success", true);

        } catch (Exception e) {

            logger.error("地图列表 获取报错", e);

            ret.build("data", new Object[]{}).build("success", false);
        }

        return ret;
    }


    @ApiOperation("上传地图")
    @PostMapping("/picture")
    public Result uploadMap(@RequestParam("file") MultipartFile file, HttpServletRequest request) {

        return FileUtils.upload(file, request);
    }

    //==========================以下均被拦截============================

    @ApiOperation("获取地图列表(需授权)")
    @GetMapping("/bizMaps")
    public Result getBizMaps(PageQO<BizMap> pageQO, BizMap condition) {
        Result ret = null;

        PageVO<BizMap> pageVO = null;

        try {
            pageQO.setCondition(condition);

            pageVO = mapService.getBizMapList(pageQO);

            ret = new Result(pageVO);

        } catch (Exception e) {

            logger.error("地图列表 获取报错", e);

            ret = new Result(HttpStatus.INTERNAL_SERVER_ERROR.value(), "地图列表 获取报错", pageQO);
        }

        return ret;
    }

    @ApiOperation("获取地图(需授权)")
    @GetMapping("/bizMap/{ID}")
    public Result getBizMap(
            @ApiParam(name = "ID", value = "地图的ID", required = true)
            @PathVariable("ID") Integer mapId
    ) {
        Result ret = null;
        BizMap map = null;
        try {
            map = mapService.getBizMap(mapId);

            ret = new Result(map);

        } catch (Exception e) {

            logger.error("ID为{}的地图数据 获取报错", mapId, e);

            ret = new Result(HttpStatus.INTERNAL_SERVER_ERROR.value(), "ID为" + mapId + "的地图数据 获取报错", Constant.NO_DATA);
        }

        return ret;
    }

    @ApiOperation("修改地图(需授权)")
    @PutMapping("/bizMap")
    public Result updateBizMap(@RequestBody BizMap bizMap) {
        Result result = null;
        try {
            result = mapService.updateBizMap(bizMap);
        } catch (Exception e) {

            logger.error("地图数据 修改报错", e);

            result = new Result(HttpStatus.INTERNAL_SERVER_ERROR.value(), "地图数据 修改报错", bizMap);
        }
        return result;
    }

    @ApiOperation("删除地图(需授权)")
    @DeleteMapping("/bizMap")
    public Result delBizMap(
            @ApiParam(name = "ID", value = "地图的ID", required = true)
                    Integer id
    ) {

        Assert.notNull(id, "地图ID不能为空");

        Result result = null;

        try {
            result = mapService.deleteBizMap(id);

        } catch (Exception e) {

            logger.error("ID为{}的地图数据 删除报错", id, e);

            result = new Result(HttpStatus.INTERNAL_SERVER_ERROR.value(), "ID为" + id + "的地图数据 删除报错", Constant.NO_DATA);
        }
        return result;
    }

    @Autowired
    private FtpConfig ftpConfig;

    @ApiOperation("添加地图(需授权)")
    @PostMapping(value = "/bizMap")
    public Result addBizMap(@RequestBody BizMap bizMap, List<BizMap> list, HttpServletRequest request) {

        //---------------------------------------------------------------------------
        FtpSession ftpSession = ftpConfig.openSession(true);

        list.forEach(item -> item
                .setMapImgPath(ftpSession.clearLocalFileAfterUpload(item.getMapImgPath(), fileDir.getMapPictureDir(), request)));

        ftpConfig.closeSession(ftpSession);
        //---------------------------------------------------------------------------

        Result result = null;
        try {
            result = mapService.addBizMap(bizMap);

        } catch (Exception e) {

            logger.error("地图数据 添加报错", e);

            result = new Result(HttpStatus.INTERNAL_SERVER_ERROR.value(), "地图数据 添加报错", bizMap);
        }
        return result;
    }
}
