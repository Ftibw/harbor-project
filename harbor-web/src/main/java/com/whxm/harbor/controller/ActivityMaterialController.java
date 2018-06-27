package com.whxm.harbor.controller;

import com.whxm.harbor.annotation.MyApiResponses;
import com.whxm.harbor.exception.DataNotFoundException;
import com.whxm.harbor.exception.ParameterInvalidException;
import com.whxm.harbor.service.ActivityMaterialService;
import com.whxm.harbor.bean.*;
import com.whxm.harbor.utils.Assert;
import com.whxm.harbor.utils.FileUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Api(description = "活动素材服务")
@RestController
@RequestMapping("/activityMaterial")
@MyApiResponses
public class ActivityMaterialController {

    private final Logger logger = LoggerFactory.getLogger(ActivityMaterialController.class);

    @Autowired
    private ActivityMaterialService activityMaterialService;

    @ApiOperation(value = "根据活动ID获取活动素材列表")
    @PostMapping(value = "/activityMaterials")
    public Map<String, Object> getBizActivities(
            @ApiParam(name = "activity", value = "活动ID", required = true)
                    Integer activity) {

        ResultMap<String, Object> ret = new ResultMap<String, Object>(2);

        try {
            Integer activityId;

            activityId = activity;

            List<BizActivityMaterial> list = activityMaterialService.getMaterialListByActivityId(activityId);

            /*if (null == list || list.isEmpty())
                throw new DataNotFoundException();*/

            ret.build("data", list).build("success", true);

        } catch (Exception e) {

            logger.error("活动素材列表 获取报错", e);

            ret.build("data", new Object[]{}).build("success", false);
        }

        return ret;
    }

    @ApiOperation("上传活动素材图片")
    @PostMapping("/picture")
    public Result uploadPicture(@RequestParam("file") MultipartFile file) {

        if (null == file || file.isEmpty()) {
            throw new ParameterInvalidException("上传的文件是空的");
        }

        return FileUtils.upload(file, Result::success);
    }

    //==========================以下均被拦截============================

    @ApiOperation("获取活动素材列表(需授权)")
    @GetMapping("/bizActivityMaterials")
    public Result getBizActivityMaterials(PageQO pageQO, BizActivityMaterial condition) {

        PageVO<BizActivityMaterial> pageVO = activityMaterialService.getBizActivityMaterialList(pageQO, condition);

        return Result.success(pageVO);
    }

    @ApiOperation("修改活动素材(需授权)")
    @PutMapping("/bizActivityMaterial")
    public Result updateBizActivityMaterial(@RequestBody BizActivityMaterial bizActivityMaterial) {

        Assert.notNull(bizActivityMaterial, "活动材料不能为空");
        Assert.notNull(bizActivityMaterial.getActivityMaterialId(), "活动材料ID不能为空");

        return activityMaterialService.updateBizActivityMaterial(bizActivityMaterial);
    }

    @ApiOperation("删除活动素材(需授权)")
    @DeleteMapping("/bizActivityMaterial")
    public Result delBizActivityMaterial(
            @ApiParam(name = "ID", value = "活动素材的ID", required = true)
                    Integer id
    ) {
        Assert.notNull(id, "活动材料ID不能为空");

        return activityMaterialService.deleteBizActivityMaterial(id);
    }

    @ApiOperation("添加活动素材(需授权)")
    @PostMapping("/bizActivityMaterial")
    public Result addBizActivityMaterial(@RequestBody List<BizActivityMaterial> list) {

        Assert.notNull(list, "活动素材数据不能为空");

        list.forEach(item -> Assert.isNull(item.getActivityMaterialId(), "活动素材ID必须为空"));

        return activityMaterialService.addBizActivityMaterials(list);
    }

}
