package com.whxm.harbor.controller;

import com.whxm.harbor.annotation.MyApiResponses;
import com.whxm.harbor.bean.*;
import com.whxm.harbor.exception.DataNotFoundException;
import com.whxm.harbor.exception.ParameterInvalidException;
import com.whxm.harbor.service.ScreensaverMaterialService;
import com.whxm.harbor.utils.Assert;
import com.whxm.harbor.utils.FileUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Api(description = "屏保素材服务")
@RestController
@MyApiResponses
@RequestMapping("/screensaverMaterial")
public class ScreensaverMaterialController {


    @Autowired
    private ScreensaverMaterialService screensaverMaterialService;

    @ApiOperation("上传屏保素材图片")
    @PostMapping("/picture")
    public Result uploadPicture(@RequestParam("file") MultipartFile file) {

        if (null == file || file.isEmpty()) {
            throw new ParameterInvalidException("上传的文件是空的");
        }

        return FileUtils.upload(file, Result::success);
    }

    //===============================以下均被拦截===============================

    @ApiOperation("获取首页素材列表(需授权)")
    @GetMapping("/bizScreensaverMaterials/first")
    public Result getFirstPageMaterials(PageQO pageQO, BizScreensaverMaterial condition) {

        PageVO<BizScreensaverMaterial> pageVO = screensaverMaterialService.getFirstPageMaterials(pageQO, condition);

        return Result.success(pageVO);
    }


    @ApiOperation("获取屏保素材列表(需授权)")
    @GetMapping("/bizScreensaverMaterials")
    public Result getBizScreensaverMaterials(PageQO pageQO, BizScreensaverMaterial condition) {

        PageVO<BizScreensaverMaterial> pageVO = screensaverMaterialService.getBizScreensaverMaterialList(pageQO, condition);

        return Result.success(pageVO);
    }

    @ApiOperation("获取指定屏保绑定的屏保素材(需授权)")
    @GetMapping("/bizScreensaverMaterialsBound")
    public Result getMaterialsByScreensaverId(PageQO pageQO, BizScreensaverMaterial condition) {

        PageVO<BizScreensaverMaterial> pageVO = screensaverMaterialService.getMaterialsByScreensaverId(pageQO, condition);

        return Result.success(pageVO);
    }

    @ApiOperation("获取屏保素材(需授权)")
    @GetMapping("/bizScreensaverMaterial/{ID}")
    public Result getBizScreensaverMaterial(
            @ApiParam(name = "ID", value = "屏保素材的ID", required = true)
            @PathVariable("ID") Integer screensaverMaterialId
    ) {

        Assert.notNull(screensaverMaterialId, "屏保素材的ID不能为空");

        BizScreensaverMaterial screensaverMaterial = screensaverMaterialService.getBizScreensaverMaterial(screensaverMaterialId);

        if (null == screensaverMaterial)
            throw new DataNotFoundException();

        return Result.success(screensaverMaterial);
    }

    @ApiOperation("修改屏保素材(需授权)")
    @PutMapping("/bizScreensaverMaterial")
    public Result updateBizScreensaverMaterial(@RequestBody BizScreensaverMaterial bizScreensaverMaterial) {

        Assert.notNull(bizScreensaverMaterial, "屏保素材不能为null");
        Assert.notNull(bizScreensaverMaterial.getScreensaverMaterialId(), "屏保素材ID不能为null");
        Assert.notNull(bizScreensaverMaterial.getIsFirstPage(), "素材是否为首页素材必须填");

        return screensaverMaterialService.updateBizScreensaverMaterial(bizScreensaverMaterial);
    }

    @ApiOperation("删除屏保素材(需授权)")
    @DeleteMapping("/bizScreensaverMaterial")
    public Result delBizScreensaverMaterial(
            @ApiParam(name = "id", value = "屏保素材的ID", required = true)
                    Integer id
    ) {
        Assert.notNull(id, "屏保素材ID不能为null");

        return screensaverMaterialService.deleteBizScreensaverMaterial(id);
    }


    @ApiOperation("添加屏保素材(需授权)")
    @PostMapping(value = "/bizScreensaverMaterial", consumes = "application/json")
    public Result addBizScreensaverMaterial(@RequestBody List<BizScreensaverMaterial> list) {

        Assert.notNull(list, "屏保数据不能为空");

        list.forEach(item -> {
            Assert.isNull(item.getScreensaverMaterialId(), "屏保素材ID必须为null");
            Assert.notNull(item.getIsFirstPage(), "素材是否为首页素材必须填");
        });

        return screensaverMaterialService.addBizScreensaverMaterial(list);
    }
}
