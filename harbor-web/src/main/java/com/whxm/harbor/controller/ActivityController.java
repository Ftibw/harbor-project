package com.whxm.harbor.controller;

import com.whxm.harbor.annotation.MyApiResponses;
import com.whxm.harbor.exception.DataNotFoundException;
import com.whxm.harbor.exception.ParameterInvalidException;
import com.whxm.harbor.service.ActivityService;
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

@Api(description = "活动服务")
@RestController
@RequestMapping("/activity")
@MyApiResponses
public class ActivityController {

    private final Logger logger = LoggerFactory.getLogger(ActivityController.class);

    @Autowired
    private ActivityService activityService;

    @ApiOperation("终端获取全部活动数据")
    @GetMapping("/activities")
    public ResultMap<String, Object> getActivities() {

        ResultMap<String, Object> ret = new ResultMap<>(2);

        try {
            List<BizActivity> list = activityService.getBizActivityList();

            if (null == list || list.isEmpty())
                throw new DataNotFoundException();

            ret.build("data", list).build("success", true);

        } catch (Exception e) {

            logger.error("活动列表 获取报错", e);

            ret.build("data", new Object[]{}).build("success", false);
        }

        return ret;
    }


    @ApiOperation("上传logo")
    @PostMapping("/logo")
    public Result uploadLogo(@RequestParam("file") MultipartFile file) {

        if (null == file || file.isEmpty()) {
            throw new ParameterInvalidException("上传的文件是空的");
        }

        return FileUtils.upload(file, Result::success);
    }

    //==========================以下均被拦截============================

    @ApiOperation("获取活动列表(需授权)")
    @GetMapping("/bizActivities")
    public Result getBizActivities(PageQO pageQO, BizActivity condition) {

        return Result.success(activityService.getBizActivityList(pageQO, condition));
    }

    @ApiOperation("修改活动(需授权)")
    @PutMapping("/bizActivity")
    public Result updateBizActivity(@RequestBody BizActivity bizActivity) {

        Assert.notNull(bizActivity, "活动数据不能为空");

        Assert.notNull(bizActivity.getActivityId(), "活动ID不能为空");

        return activityService.updateBizActivity(bizActivity);
    }

    @ApiOperation("删除活动(需授权)")
    @DeleteMapping("/bizActivity")
    public Result delBizActivity(
            @ApiParam(name = "ID", value = "活动的ID", required = true)
                    Integer id
    ) {
        Assert.notNull(id, "活动ID不能为空");

        return activityService.deleteBizActivity(id);
    }

    @ApiOperation("添加活动(需授权)")
    @PostMapping(value = "/bizActivity")
    public Result addBizActivity(@RequestBody BizActivity bizActivity) {

        Assert.notNull(bizActivity, "活动数据不能为空");

        Assert.isNull(bizActivity.getActivityId(), "活动ID必须为空");

        return activityService.addBizActivity(bizActivity);
    }
}
