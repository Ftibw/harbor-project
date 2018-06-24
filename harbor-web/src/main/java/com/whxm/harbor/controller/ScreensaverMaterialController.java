package com.whxm.harbor.controller;

import com.whxm.harbor.annotation.MyApiResponses;
import com.whxm.harbor.bean.*;
import com.whxm.harbor.conf.FileDir;
import com.whxm.harbor.conf.UrlConfig;
import com.whxm.harbor.constant.Constant;
import com.whxm.harbor.service.ScreensaverMaterialService;
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
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Api(description = "屏保素材服务")
@RestController
@MyApiResponses
@RequestMapping("/screensaverMaterial")
public class ScreensaverMaterialController {

    private final Logger logger = LoggerFactory.getLogger(ScreensaverMaterialController.class);

    @Autowired
    private ScreensaverMaterialService screensaverMaterialService;

    @Autowired
    private FileDir fileDir;

    @Autowired
    private UrlConfig urlConfig;

    @SuppressWarnings("unchecked")
    @ApiOperation("上传屏保素材图片")
    @PostMapping("/picture")
    public Result uploadPicture(@RequestParam("file") MultipartFile file, HttpServletRequest request) {

        return FileUtils.upload(file, request);
    }

    //===============================以下均被拦截===============================

    @ApiOperation("获取屏保素材列表(需授权)")
    @GetMapping("/bizScreensaverMaterials")
    public Result getBizScreensaverMaterials(PageQO<BizScreensaverMaterial> pageQO, BizScreensaverMaterial condition) {

        Result ret = null;

        try {
            pageQO.setCondition(condition);

            PageVO<BizScreensaverMaterial> pageVO = screensaverMaterialService.getBizScreensaverMaterialList(pageQO);

            ret = new Result(pageVO);

        } catch (Exception e) {
            logger.error("屏保素材列表 获取报错", e);
            ret = new Result(HttpStatus.INTERNAL_SERVER_ERROR.value(), "屏保素材列表 获取报错", pageQO);
        }

        return ret;
    }

    @ApiOperation("获取屏保素材(需授权)")
    @GetMapping("/bizScreensaverMaterial/{ID}")
    public Result getBizScreensaverMaterial(
            @ApiParam(name = "ID", value = "屏保素材的ID", required = true)
            @PathVariable("ID") Integer screensaverMaterialId
    ) {
        Result ret = null;
        BizScreensaverMaterial screensaverMaterial = null;
        try {
            screensaverMaterial = screensaverMaterialService.getBizScreensaverMaterial(screensaverMaterialId);

            ret = new Result(screensaverMaterial);

        } catch (Exception e) {

            logger.error("ID为{}的屏保素材数据 获取报错", screensaverMaterialId, e);

            ret = new Result(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "ID为" + screensaverMaterialId + "的屏保素材数据 获取报错", Constant.NO_DATA);
        }

        return ret;
    }

    @ApiOperation("修改屏保素材(需授权)")
    @PutMapping("/bizScreensaverMaterial")
    public Result updateBizScreensaverMaterial(@RequestBody BizScreensaverMaterial bizScreensaverMaterial) {

        Result ret = null;

        Assert.notNull(bizScreensaverMaterial.getScreensaverMaterialId(), "屏保素材ID不能为null");

        try {
            ret = screensaverMaterialService.updateBizScreensaverMaterial(bizScreensaverMaterial);

        } catch (Exception e) {

            logger.error("屏保素材数据 修改报错", e);

            ret = new Result(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "屏保素材数据 修改报错", bizScreensaverMaterial);
        }

        return ret;
    }

    @ApiOperation("删除屏保素材(需授权)")
    @DeleteMapping("/bizScreensaverMaterial")
    public Result delBizScreensaverMaterial(
            @ApiParam(name = "id", value = "屏保素材的ID", required = true)
                    Integer id
    ) {
        Result ret = null;
        try {
            ret = screensaverMaterialService.deleteBizScreensaverMaterial(id);
        } catch (Exception e) {

            logger.error("ID为{}的屏保素材数据 删除报错", id, e);

            ret = new Result(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "ID为" + id + "的屏保素材数据 删除报错", Constant.NO_DATA);
        }

        return ret;
    }

    @ApiOperation("添加屏保素材(需授权)")
    @PostMapping("/bizScreensaverMaterial")
    public Result addBizScreensaverMaterial(@RequestBody List<BizScreensaverMaterial> list) {
        //BizScreensaverMaterial bizScreensaverMaterial
        Result ret = null;
        try {
            ret = screensaverMaterialService.addBizScreensaverMaterial(list);

        } catch (Exception e) {
            logger.error("屏保素材 添加报错", e);

            ret = new Result(HttpStatus.INTERNAL_SERVER_ERROR.value(), "屏保素材 添加报错", list);
        }
        return ret;
    }
}
